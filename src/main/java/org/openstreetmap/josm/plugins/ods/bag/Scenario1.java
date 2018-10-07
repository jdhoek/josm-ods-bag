package org.openstreetmap.josm.plugins.ods.bag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.Scenario;
import org.openstreetmap.josm.plugins.ods.bag.mapping.duinoord.DuinoordAddressNodeMapper;
import org.openstreetmap.josm.plugins.ods.bag.mapping.duinoord.DuinoordBuildingMapper;
import org.openstreetmap.josm.plugins.ods.bag.mapping.pdok_wfs.BuildingMapper;
import org.openstreetmap.josm.plugins.ods.bag.mapping.pdok_wfs.BuildingUnitMapper;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagAddressNodeEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagBuildingEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.processing.BagBuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtilProj4j;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.processing.AddressNodeDistributor;
import org.openstreetmap.josm.plugins.ods.domains.addresses.processing.BuildingUnitToBuildingConnector;
import org.openstreetmap.josm.plugins.ods.domains.addresses.processing.OdAddressToBuildingConnector;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuildingUnit;
import org.openstreetmap.josm.plugins.ods.entities.EntityDao;
import org.openstreetmap.josm.plugins.ods.entities.EntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtDatasourceBuilder;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.io.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.OsmLayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.Task;
import org.openstreetmap.josm.plugins.ods.storage.EntityRepository;
import org.openstreetmap.josm.plugins.ods.storage.EntityStorage;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;

public class Scenario1 extends Scenario {
    private static CRSUtil crsUtil = new CRSUtilProj4j();

    private final OdsModule module;

    private WFSHost pdokWfsHost;
    private WFSHost duinoordWfsHost;

    private EntityStorage<OpenDataBuilding> downloadedBuildings;
    private EntityStorage<OpenDataBuildingUnit> downloadedBuildingUnits;
    private EntityStorage<OpenDataAddressNode> downloadedAddressNodes;
    private EntityStorage<OpenDataBuilding> odBuildingStorage;
    private EntityStorage<OpenDataBuildingUnit> odBuildingUnitStorage;
    private EntityStorage<OpenDataAddressNode> odAddressNodeStorage;
    private EntityRepository mainRepository;
    private EntityRepository downloadRepository;

    private GtFeatureSource pdokVboFeatureSource;
    private GtFeatureSource pdokPandFeatureSource;
    private GtFeatureSource pdokLigplaatsFeatureSource;
    private GtFeatureSource pdokStandplaatsFeatureSource;
    private GtFeatureSource duinoordMissingAddressFS;
    private GtFeatureSource duinoordDeletedBuildingFS;

    private GtDataSource pdokVboDataSource;
    private GtDataSource pdokPandDataSource;
    private GtDataSource pdokLigplaatsDataSource;
    private GtDataSource pdokStandplaatsDataSource;
    private GtDataSource duinoordMissingAddressDataSource;
    private GtDataSource duinoordDeletedBuildingDataSource;

    private BuildingMapper buildingMapper;
    private BuildingUnitMapper buildingUnitMapper;
    private DuinoordAddressNodeMapper duinoordAddressNodeMapper;
    private DuinoordBuildingMapper duinoordBuildingMapper;

    private List<GtDownloader<?>> featureDownloaders;
    private OpenDataLayerDownloader odLayerDownloader;
    private OsmLayerManager osmLayerManager;
    private OsmLayerDownloader osmLayerDownloader;

    private MainDownloader mainDownloader;

    public Scenario1(OdsModule module) {
        super();
        this.module = module;
    }

    @Override
    public MainDownloader getMainDownloader() {
        return mainDownloader;
    }

    @Override
    protected void createHosts() {
        pdokWfsHost = new WFSHost("BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs?request=getCapabilities&VERSION=2.0.0", 1000, 60000, 60000);
        // The Duinoord WFS doesn't support paging, because there is an issue with the primary index
        duinoordWfsHost = new WFSHost("DUINOORD WFS", "https://duinoord.xs4all.nl/geoserver/wfs?request=getCapabilities&VERSION=2.0.0", -1, 60000, 60000);
    }

    @Override
    protected void createMappers() {
        buildingMapper = new BuildingMapper();
        buildingUnitMapper = new BuildingUnitMapper();
        duinoordBuildingMapper = new DuinoordBuildingMapper();
        duinoordAddressNodeMapper = new DuinoordAddressNodeMapper();
    }

    @Override
    protected void createFeatureSources() {
        pdokVboFeatureSource = new GtFeatureSource(pdokWfsHost, "bag:verblijfsobject");
        pdokPandFeatureSource = new GtFeatureSource(pdokWfsHost, "bag:pand");
        pdokLigplaatsFeatureSource = new GtFeatureSource(pdokWfsHost, "bag:ligplaats");
        pdokStandplaatsFeatureSource = new GtFeatureSource(pdokWfsHost, "bag:standplaats");
        duinoordMissingAddressFS = new GtFeatureSource(duinoordWfsHost, "bag:Address_Missing");
        duinoordDeletedBuildingFS = new GtFeatureSource(duinoordWfsHost, "bag:Building_Destroyed");
    }

    @Override
    protected void createDataSources() {
        pdokVboDataSource = new GtDatasourceBuilder(crsUtil)
                .setFeatureSource(pdokVboFeatureSource)
                .setProperties("identificatie", "oppervlakte", "status", "gebruiksdoel",
                        "openbare_ruimte", "huisnummer", "huisletter", "toevoeging", "postcode", "woonplaats",
                        "geometrie", "pandidentificatie")
                .setPageSize(1000)
                .build();
        pdokPandDataSource = new GtDatasourceBuilder(crsUtil)
                .setFeatureSource(pdokPandFeatureSource)
                .setProperties("identificatie", "bouwjaar", "status", "geometrie")
                .setPageSize(1000)
                .build();
        pdokLigplaatsDataSource = new GtDatasourceBuilder(crsUtil)
                .setFeatureSource(pdokLigplaatsFeatureSource)
                .setProperties("identificatie", "status", "openbare_ruimte", "huisnummer",
                        "huisletter", "toevoeging", "postcode", "woonplaats", "geometrie")
                .setPageSize(1000)
                .build();
        pdokStandplaatsDataSource = new GtDatasourceBuilder(crsUtil)
                .setFeatureSource(pdokStandplaatsFeatureSource)
                .setProperties("identificatie", "status", "openbare_ruimte", "huisnummer",
                        "huisletter", "toevoeging", "postcode", "woonplaats", "geometrie")
                .setPageSize(1000)
                .build();
        duinoordMissingAddressDataSource = new GtDatasourceBuilder(crsUtil)
                .setFeatureSource(duinoordMissingAddressFS)
                .setProperties("nummeraanduiding", "postcode", "huisnummer", "huisnummertoevoeging",
                        "huisletter", "straat", "huisnummer", "woonplaats", "pandidentificatie", "geopunt")
                .build();
        duinoordDeletedBuildingDataSource = new GtDatasourceBuilder(crsUtil)
                .setFeatureSource(duinoordDeletedBuildingFS)
                .setProperties("identificatie", "pandstatus", "bouwjaar", "geovlak")
                .build();
    }

    @Override
    protected void createMainStorage() {
        odBuildingStorage = new EntityStorage<>(OpenDataBuilding.class);
        odBuildingUnitStorage = new EntityStorage<>(OpenDataBuildingUnit.class);
        odAddressNodeStorage = new EntityStorage<>(OpenDataAddressNode.class);
        List<EntityDao<?>> daos = new ArrayList<>(5);
        daos.add(odBuildingStorage);
        daos.add(odBuildingUnitStorage);
        daos.add(odAddressNodeStorage);
        mainRepository = new EntityRepository(daos);
    }

    @Override
    protected void createDownloadStorage() {
        downloadedBuildings = new EntityStorage<>(OpenDataBuilding.class);
        downloadedBuildingUnits = new EntityStorage<>(OpenDataBuildingUnit.class);
        downloadedAddressNodes = new EntityStorage<>(OpenDataAddressNode.class);
        List<EntityDao<?>> daos = new ArrayList<>(5);
        daos.add(downloadedBuildings);
        daos.add(downloadedBuildingUnits);
        daos.add(downloadedAddressNodes);
        downloadRepository = new EntityRepository(daos);
    }

    @Override
    protected void createFeatureDownloaders() {
        featureDownloaders = new LinkedList<>();
        GtDownloader<OpenDataBuilding> pandDownloader = new GtDownloader<>(pdokPandDataSource, crsUtil,
                buildingMapper, downloadedBuildings);
        pandDownloader.setNormalisation(Normalisation.CLOCKWISE);
        GtDownloader<OpenDataBuilding> ligplaatsDownloader = new GtDownloader<>(pdokLigplaatsDataSource, crsUtil,
                buildingMapper, downloadedBuildings);
        ligplaatsDownloader.setNormalisation(Normalisation.CLOCKWISE);
        GtDownloader<OpenDataBuilding> standplaatsDownloader = new GtDownloader<>(pdokStandplaatsDataSource, crsUtil,
                buildingMapper, downloadedBuildings);
        standplaatsDownloader.setNormalisation(Normalisation.CLOCKWISE);
        GtDownloader<OpenDataBuildingUnit> verblijfsobjectDownloader = new GtDownloader<>(pdokVboDataSource, crsUtil,
                buildingUnitMapper, downloadedBuildingUnits);
        GtDownloader<OpenDataAddressNode> missingAddressDownloader = new GtDownloader<>(duinoordMissingAddressDataSource, crsUtil,
                duinoordAddressNodeMapper, downloadedAddressNodes);
        GtDownloader<OpenDataBuilding> deletedBuildingDownloader = new GtDownloader<>(duinoordDeletedBuildingDataSource, crsUtil,
                duinoordBuildingMapper, downloadedBuildings);
        featureDownloaders.add(pandDownloader);
        featureDownloaders.add(ligplaatsDownloader);
        featureDownloaders.add(standplaatsDownloader);
        featureDownloaders.add(verblijfsobjectDownloader);
        featureDownloaders.add(missingAddressDownloader);
        featureDownloaders.add(deletedBuildingDownloader);
    }

    private List<Task> createOdProcessingTasks() {
        List<Task> tasks = new ArrayList<>(10);
        tasks.add(new BuildingUnitToBuildingConnector(odBuildingUnitStorage, odBuildingStorage));
        tasks.add(new OdAddressToBuildingConnector(odAddressNodeStorage, odBuildingStorage));
        //        tasks.add(new BuildingCompletenessEnricher());
        tasks.add(new BagBuildingTypeEnricher(odBuildingStorage));
        tasks.add(new PrimitiveBuilder(getPrimitiveBuilders()));
        tasks.add(new AddressNodeDistributor(odBuildingStorage));
        tasks.add(new OpenDataLayerDownloader.UpdateLayerTask());
        return tasks;
    }

    @Override
    protected void createMainDownloader() {
        List<Task> odProcessingTasks = createOdProcessingTasks();
        odLayerDownloader = new OpenDataLayerDownloader(featureDownloaders, odProcessingTasks);
        osmLayerDownloader = new OsmLayerDownloader(osmLayerManager);
        mainDownloader = new MainDownloader(odLayerDownloader, osmLayerDownloader, null);
    }

    private List<EntityPrimitiveBuilder<?>> getPrimitiveBuilders() {
        return Arrays.asList(
                new BagBuildingEntityPrimitiveBuilder(module, odBuildingStorage),
                new BagAddressNodeEntityPrimitiveBuilder(module, odAddressNodeStorage)
                );
    }
}
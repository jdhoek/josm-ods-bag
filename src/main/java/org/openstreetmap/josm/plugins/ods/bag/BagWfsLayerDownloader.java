package org.openstreetmap.josm.plugins.ods.bag;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OdsModuleConfiguration;
import org.openstreetmap.josm.plugins.ods.bag.processing.BagBuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNodeEntityType;
import org.openstreetmap.josm.plugins.ods.domains.addresses.processing.AddressNodeDistributor;
import org.openstreetmap.josm.plugins.ods.domains.addresses.processing.BuildingUnitToBuildingConnector;
import org.openstreetmap.josm.plugins.ods.domains.addresses.processing.OdAddressToBuildingConnector;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingEntityType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingUnitEntityType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.processing.BuildingCompletenessEnricher;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloaderFactory;
import org.openstreetmap.josm.plugins.ods.io.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.Task;

public class BagWfsLayerDownloader extends OpenDataLayerDownloader {
    private final static List<Class<? extends Task>> odsProcessors = Arrays.asList(
            BuildingUnitToBuildingConnector.class,
            OdAddressToBuildingConnector.class,
            BuildingCompletenessEnricher.class,
            BagBuildingTypeEnricher.class,
            OpenDataLayerDownloader.BuildPrimitivesTask.class,
            AddressNodeDistributor.class,
            OpenDataLayerDownloader.UpdateLayerTask.class);
    private final OdsModuleConfiguration configuration;

    private final GtDownloaderFactory gtDownloaderFactory;

    public BagWfsLayerDownloader(OdsModule module) {
        super(module);
        this.gtDownloaderFactory = new GtDownloaderFactory(module);
        this.configuration = module.getConfiguration();
    }

    @Override
    protected List<Class<? extends Task>> getProcessors() {
        return odsProcessors;
    }

    @Override
    public void initialize() throws OdsException {
        addFeatureDownloader(createBuildingDownloader("bag:pand"));
        addFeatureDownloader(createBuildingDownloader("bag:ligplaats"));
        addFeatureDownloader(createBuildingDownloader("bag:standplaats"));
        addFeatureDownloader(createDeletedBuildingDownloader());
        addFeatureDownloader(createMissingAddressDownloader());
        addFeatureDownloader(createVerblijfsobjectDownloader());
    }

    private FeatureDownloader createVerblijfsobjectDownloader() throws OdsException {
        GtDataSource dataSource = (GtDataSource) configuration.getDataSource("bag:verblijfsobject");
        return gtDownloaderFactory.createDownloader(dataSource, BuildingUnitEntityType.class);
    }

    private FeatureDownloader createMissingAddressDownloader() throws OdsException {
        GtDataSource dataSource = (GtDataSource) configuration.getDataSource("bag:Address_Missing");
        return gtDownloaderFactory.createDownloader(dataSource, AddressNodeEntityType.class);
    }

    private FeatureDownloader createDeletedBuildingDownloader() throws OdsException {
        GtDataSource dataSource = (GtDataSource) configuration.getDataSource("bag:Building_Destroyed");
        return gtDownloaderFactory.createDownloader(dataSource, BuildingEntityType.class);
    }

    private FeatureDownloader createBuildingDownloader(String featureType) throws OdsException {
        GtDataSource dataSource = (GtDataSource) configuration.getDataSource(featureType);
        GtDownloader<?> downloader = gtDownloaderFactory.createDownloader(dataSource, BuildingEntityType.class);
        /*
         *  The original BAG import partially normalised the building geometries,
         * by making the (outer) rings clockwise. For fast comparison of geometries,
         * I choose to override the default normalisation here.
         */
        downloader.setNormalisation(Normalisation.CLOCKWISE);
        return downloader;
    }
}

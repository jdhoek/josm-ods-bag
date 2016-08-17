package org.openstreetmap.josm.plugins.ods.bag;

import java.util.LinkedList;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OdsModuleConfiguration;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.HousingUnit;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.BuildingCompletenessEnricher;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.DistributeAddressNodes;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.matching.OpenDataHousingUnitToBuildingMatcher;
import org.openstreetmap.josm.plugins.ods.osm.OsmNeighbourFinder;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

import com.vividsolutions.jts.geom.Geometry;

public class BagWfsLayerDownloader extends OpenDataLayerDownloader {
    
    private final OdsModule module;
    private final OdsModuleConfiguration configuration;
    private OpenDataLayerManager layerManager;
    private BagPrimitiveBuilder primitiveBuilder;

    LinkedList<HousingUnit> unmatchedHousingUnits = new LinkedList<>();

    public BagWfsLayerDownloader(OdsModule module) {
        super(module);
        this.module = module;
        this.configuration = module.getConfiguration();
    }
    
    @Override
    public void initialize() throws OdsException {
        this.layerManager = module.getOpenDataLayerManager();
        addFeatureDownloader(createBuildingDownloader("bag:pand"));
        addFeatureDownloader(createBuildingDownloader("bag:ligplaats"));
        addFeatureDownloader(createBuildingDownloader("bag:standplaats"));
//      addFeatureDownloader(createDemolishedBuildingsDownloader());
        addFeatureDownloader(createVerblijfsobjectDownloader());
        this.primitiveBuilder = new BagPrimitiveBuilder(module);
    }

    @Override
    public void process() {
        Thread.currentThread().setName("BagWfsLayerDownloader process");
        try {
            super.process();
            matchHousingUnitsToBuilding();
            checkBuildingCompleteness();
            distributeAddressNodes();
            analyzeBuildingTypes();
            findBuildingNeighbours(getResponse());
            primitiveBuilder.run(getResponse());
            updateLayer();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void findBuildingNeighbours(DownloadResponse response) {
        OsmNeighbourFinder neighbourFinder = new OsmNeighbourFinder(module);
        for (Building building :layerManager.getRepository().getAll(Building.class)) {
            ManagedPrimitive<?> mPrimitive = building.getPrimitive();
            if (mPrimitive != null) {
                neighbourFinder.findNeighbours(mPrimitive);
            }
        }
    }

    private FeatureDownloader createVerblijfsobjectDownloader() throws OdsException {
        OdsDataSource dataSource = configuration.getDataSource("bag:verblijfsobject");
        return new GtDownloader<>(module, dataSource, HousingUnit.class);
    }
    
    private FeatureDownloader createBuildingDownloader(String featureType) throws OdsException {
        OdsDataSource dataSource = configuration.getDataSource(featureType);
        FeatureDownloader downloader = new GtDownloader<>(module, dataSource, Building.class);
        /*
         *  The original BAG import partially normalised the building geometries,
         * by making the (outer) rings clockwise. For fast comparison of geometries,
         * I choose to override the default normalisation here.
         */
        downloader.setNormalisation(Normalisation.CLOCKWISE);
        return downloader;
    }
    
    
    /**
     * Find a matching building for foreign addressNodes. 
     */
    private void matchHousingUnitsToBuilding() {
        OpenDataHousingUnitToBuildingMatcher matcher = new OpenDataHousingUnitToBuildingMatcher(module);
        matcher.setUnmatchedHousingUnitHandler(unmatchedHousingUnits::add);
        for(HousingUnit housingUnit : layerManager.getRepository().getAll(HousingUnit.class)) {
            matcher.matchHousingUnitToBuilding(housingUnit);
        }
    }
    
    private void checkBuildingCompleteness() {
        // TODO implement this. Move the data boundary from buildingStore 
        // to layerManager (for the whole layer) or a bounding-box aware extension of EntityRepository.
        Geometry boundary = layerManager.getBoundary();
        Consumer<Building> enricher = new BuildingCompletenessEnricher(boundary);
        for (Building building : layerManager.getRepository().getAll(Building.class)) {
            enricher.accept(building);
        }
    }
    
    private void distributeAddressNodes() {
        Consumer<Building> enricher = new DistributeAddressNodes(module.getGeoUtil());
        for (Building building : layerManager.getRepository().getAll(Building.class)) {
            enricher.accept(building);
        }
    }
    
    private void analyzeBuildingTypes() {
        Consumer<Building> enricher = new BuildingTypeEnricher();
        for (Building building : layerManager.getRepository().getAll(Building.class)) {
            enricher.accept(building);
        }
    }
}

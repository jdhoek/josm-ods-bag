package org.openstreetmap.josm.plugins.ods.bag;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OdsModuleConfiguration;
import org.openstreetmap.josm.plugins.ods.bag.processing.BagBuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.processing.AddressNodeDistributor;
import org.openstreetmap.josm.plugins.ods.domains.addresses.processing.OdAddressToBuildingConnector;
import org.openstreetmap.josm.plugins.ods.domains.addresses.processing.HousingUnitToBuildingConnector;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.domains.buildings.HousingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.processing.BuildingCompletenessEnricher;
import org.openstreetmap.josm.plugins.ods.entities.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.io.OdsProcessor;

public class BagWfsLayerDownloader extends OpenDataLayerDownloader {
    private final static List<Class<? extends OdsProcessor>> odsProcessors = Arrays.asList(
            HousingUnitToBuildingConnector.class,
            OdAddressToBuildingConnector.class,
            BuildingCompletenessEnricher.class,
            AddressNodeDistributor.class,
            BagBuildingTypeEnricher.class);
    private final OdsModuleConfiguration configuration;
    private BagPrimitiveBuilder primitiveBuilder;

    LinkedList<HousingUnit> unmatchedHousingUnits = new LinkedList<>();

    public BagWfsLayerDownloader(OdsModule module) {
        super(module);
        this.configuration = module.getConfiguration();
    }
    
    @Override
    protected PrimitiveBuilder getPrimitiveBuilder() {
        return this.primitiveBuilder;
    }
    
    @Override
    protected List<Class<? extends OdsProcessor>> getProcessors() {
        return odsProcessors;
    }
    
    @Override
    public void initialize() throws OdsException {
        addFeatureDownloader(createBuildingDownloader("bag:pand"));
        addFeatureDownloader(createBuildingDownloader("bag:ligplaats"));
        addFeatureDownloader(createBuildingDownloader("bag:standplaats"));
//        addFeatureDownloader(createMissingAddressDownloader());
        addFeatureDownloader(createVerblijfsobjectDownloader());
        this.primitiveBuilder = new BagPrimitiveBuilder(getModule());
    }

    private FeatureDownloader createVerblijfsobjectDownloader() throws OdsException {
        GtDataSource dataSource = (GtDataSource) configuration.getDataSource("bag:verblijfsobject");
        return new GtDownloader<>(getModule(), dataSource, HousingUnit.class);
    }
    
    private FeatureDownloader createMissingAddressDownloader() throws OdsException {
        GtDataSource dataSource = (GtDataSource) configuration.getDataSource("bag:Address_Missing");
        return new GtDownloader<>(getModule(), dataSource, AddressNode.class);
    }
    
    private FeatureDownloader createBuildingDownloader(String featureType) throws OdsException {
        GtDataSource dataSource = (GtDataSource) configuration.getDataSource(featureType);
        FeatureDownloader downloader = new GtDownloader<>(getModule(), dataSource, Building.class);
        /*
         *  The original BAG import partially normalised the building geometries,
         * by making the (outer) rings clockwise. For fast comparison of geometries,
         * I choose to override the default normalisation here.
         */
        downloader.setNormalisation(Normalisation.CLOCKWISE);
        return downloader;
    }
}

package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.ods.OdsModule;

public class BagWfsLayerDownloader {
    //    private final static List<Class<? extends Task>> odsProcessors = Arrays.asList(
    //            BuildingUnitToBuildingConnector.class,
    //            OdAddressToBuildingConnector.class,
    //            BuildingCompletenessEnricher.class,
    //            BagBuildingTypeEnricher.class,
    //            OpenDataLayerDownloader.BuildPrimitivesTask.class,
    //            AddressNodeDistributor.class,
    //            OpenDataLayerDownloader.UpdateLayerTask.class);
    //    private final OdsModuleConfiguration configuration;

    public BagWfsLayerDownloader(OdsModule module) {
        //        super(module);
        //        this.configuration = module.getConfiguration();
    }

    //    @Override
    //    protected List<Class<? extends Task>> getProcessors() {
    //        return odsProcessors;
    //    }

    //    @Override
    //    public void initialize() throws OdsException {
    //        addFeatureDownloader(createPandDownloader());
    //        addFeatureDownloader(createLigplaatsDownloader());
    //        addFeatureDownloader(createStandplaatsDownloader());
    //        addFeatureDownloader(createBuildingDownloader("bag:pand"));
    //        addFeatureDownloader(createBuildingDownloader("bag:ligplaats"));
    //        addFeatureDownloader(createBuildingDownloader("bag:standplaats"));
    //        addFeatureDownloader(createDeletedBuildingDownloader());
    //        addFeatureDownloader(createMissingAddressDownloader());
    //        addFeatureDownloader(createVerblijfsobjectDownloader());
    //    }
    //
    //    private FeatureDownloader createVerblijfsobjectDownloader() throws OdsException {
    //        GtDataSource dataSource = (GtDataSource) configuration.getDataSource("bag:verblijfsobject");
    //        return gtDownloaderFactory.createDownloader(dataSource);
    //    }
    //
    //    private FeatureDownloader createMissingAddressDownloader() throws OdsException {
    //        GtDataSource dataSource = (GtDataSource) configuration.getDataSource("bag:Address_Missing");
    //        return gtDownloaderFactory.createDownloader(dataSource);
    //    }
    //
    //    private FeatureDownloader createDeletedBuildingDownloader() throws OdsException {
    //        GtDataSource dataSource = (GtDataSource) configuration.getDataSource("bag:Building_Destroyed");
    //        return gtDownloaderFactory.createDownloader(dataSource);
    //    }
    //
    //    private FeatureDownloader createPandDownloader() throws OdsException {
    //        GtDataSource dataSource = (GtDataSource) configuration.getDataSource(featureType);
    //        GtDownloader downloader = gtDownloaderFactory.createDownloader(dataSource);
    //        /*
    //         *  The original BAG import partially normalised the building geometries,
    //         * by making the (outer) rings clockwise. For fast comparison of geometries,
    //         * I choose to override the default normalisation here.
    //         */
    //        downloader.setNormalisation(Normalisation.CLOCKWISE);
    //        return downloader;
    //    }
    //
    //    private FeatureDownloader createBuildingDownloader(String featureType) throws OdsException {
    //        GtDataSource dataSource = (GtDataSource) configuration.getDataSource(featureType);
    //        GtDownloader downloader = gtDownloaderFactory.createDownloader(dataSource);
    //        /*
    //         *  The original BAG import partially normalised the building geometries,
    //         * by making the (outer) rings clockwise. For fast comparison of geometries,
    //         * I choose to override the default normalisation here.
    //         */
    //        downloader.setNormalisation(Normalisation.CLOCKWISE);
    //        return downloader;
    //    }
    //
    //    private FeatureDownloader createBuildingDownloader(String featureType) throws OdsException {
    //        GtDataSource dataSource = (GtDataSource) configuration.getDataSource(featureType);
    //        GtDownloader downloader = gtDownloaderFactory.createDownloader(dataSource);
    //        /*
    //         *  The original BAG import partially normalised the building geometries,
    //         * by making the (outer) rings clockwise. For fast comparison of geometries,
    //         * I choose to override the default normalisation here.
    //         */
    //        downloader.setNormalisation(Normalisation.CLOCKWISE);
    //        return downloader;
    //    }
    //
    //    private FeatureDownloader createBuildingDownloader(String featureType) throws OdsException {
    //        GtDataSource dataSource = (GtDataSource) configuration.getDataSource(featureType);
    //        GtDownloader downloader = gtDownloaderFactory.createDownloader(dataSource);
    //        /*
    //         *  The original BAG import partially normalised the building geometries,
    //         * by making the (outer) rings clockwise. For fast comparison of geometries,
    //         * I choose to override the default normalisation here.
    //         */
    //        downloader.setNormalisation(Normalisation.CLOCKWISE);
    //        return downloader;
    //    }
}

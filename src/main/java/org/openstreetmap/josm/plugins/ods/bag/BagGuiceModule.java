package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.ods.OdsModule;

import com.google.inject.AbstractModule;

public class BagGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(OdsModule.class).toInstance(new BagImportModule());
        //        bind(OdBuildingStorage.class).in(Singleton.class);
        //        bind(OsmBuildingStorage.class).in(Singleton.class);
        //        bind(OdBuildingDao.class).to(OdBuildingStorage.Dao.class).in(Singleton.class);
        //        bind(OsmBuildingDao.class).to(OsmBuildingStorage.Dao.class).in(Singleton.class);
        //        bind(BuildingMatchFactory.class).to(BagBuildingMatchFactory.class).in(Singleton.class);
        //        bind(BuildingMatcher.class).in(Singleton.class);
    }
}

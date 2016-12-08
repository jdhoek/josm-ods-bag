package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.ods.AbstractModuleConfiguration;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagEntityMapperFactory;
import org.openstreetmap.josm.plugins.ods.geotools.GtDatasourceBuilder;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;

public class BagConfiguration extends AbstractModuleConfiguration {
    private BagEntityMapperFactory entityMapperFactory;

    public BagConfiguration() {
        WFSHost bagWfsHost = new WFSHost("BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs?request=getCapabilities&VERSION=2.0.0", 1000, 60000, 60000);
//      hosts.put(demolishedBuildingsHost.getName(), demolishedBuildingsHost);
        GtFeatureSource vboFeatureSource = new GtFeatureSource(bagWfsHost, "bag:verblijfsobject", "identificatie");
        GtFeatureSource pandFeatureSource = new GtFeatureSource(bagWfsHost, "bag:pand", "identificatie");
        GtFeatureSource ligplaatsFeatureSource = new GtFeatureSource(bagWfsHost, "bag:ligplaats", "identificatie");
        GtFeatureSource standplaatsFeatureSource = new GtFeatureSource(bagWfsHost, "bag:standplaats", "identificatie");
        addFeatureSource(vboFeatureSource);
        addFeatureSource(pandFeatureSource);
        addFeatureSource(ligplaatsFeatureSource);
        addFeatureSource(standplaatsFeatureSource);
        
        entityMapperFactory = new BagEntityMapperFactory(bagWfsHost);
        addDataSource(createVboDataSource(vboFeatureSource));
        addDataSource(createPandDataSource(pandFeatureSource));
        addDataSource(createLigplaatsDataSource(ligplaatsFeatureSource));
        addDataSource(createStandplaatsDataSource(standplaatsFeatureSource));
    }
    
    private OdsDataSource createVboDataSource(GtFeatureSource featureSource) {
        GtDatasourceBuilder builder = new GtDatasourceBuilder()
            .setFeatureSource(featureSource)
            .setProperties("identificatie", "oppervlakte", "status", "gebruiksdoel",
                "openbare_ruimte", "huisnummer", "huisletter", "toevoeging", "postcode", "woonplaats",
                "geometrie", "pandidentificatie")
            .setUniqueKey("identificatie")
            .setEntityMapperFactory(entityMapperFactory);
        return builder.build();
    }
    
    private OdsDataSource createPandDataSource(GtFeatureSource featureSource) {
        GtDatasourceBuilder builder = new GtDatasourceBuilder()
            .setFeatureSource(featureSource)
            .setProperties("identificatie", "bouwjaar", "status", "geometrie")
            .setUniqueKey("identificatie")
            .setEntityMapperFactory(entityMapperFactory);
        return builder.build();
    }
    
    private OdsDataSource createLigplaatsDataSource(GtFeatureSource featureSource) {
        GtDatasourceBuilder builder = new GtDatasourceBuilder()
            .setFeatureSource(featureSource)
            .setProperties("identificatie", "status", "openbare_ruimte", "huisnummer",
                "huisletter", "toevoeging", "postcode", "woonplaats", "geometrie")
            .setUniqueKey("identificatie")
            .setEntityMapperFactory(entityMapperFactory);
        return builder.build();
    }
    
    private OdsDataSource createStandplaatsDataSource(GtFeatureSource featureSource) {
        GtDatasourceBuilder builder = new GtDatasourceBuilder()
            .setFeatureSource(featureSource)
            .setProperties("identificatie", "status", "openbare_ruimte", "huisnummer",
                "huisletter", "toevoeging", "postcode", "woonplaats", "geometrie")
            .setUniqueKey("identificatie")
            .setEntityMapperFactory(entityMapperFactory);
        return builder.build();
    }
}

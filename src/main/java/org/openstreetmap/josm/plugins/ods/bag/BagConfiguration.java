package org.openstreetmap.josm.plugins.ods.bag;

import java.util.Arrays;
import java.util.List;

import org.geotools.data.Query;
import org.openstreetmap.josm.plugins.ods.AbstractModuleConfiguration;
import org.openstreetmap.josm.plugins.ods.DefaultOdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagEntityMapperFactory;
import org.openstreetmap.josm.plugins.ods.geotools.GroupByQuery;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;

public class BagConfiguration extends AbstractModuleConfiguration {
    private BagEntityMapperFactory entityMapperFactory;

    public BagConfiguration() {
        WFSHost bagWfsHost = new WFSHost("BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs?request=getCapabilities&VERSION=1.1.0", 15000, 60000, 60000);
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
        List<String> properties = Arrays.asList("identificatie", "oppervlakte", "status", "gebruiksdoel",
            "openbare_ruimte", "huisnummer", "huisletter", "toevoeging", "postcode", "woonplaats",
            "geometrie", "pandidentificatie");
        Query query = new GroupByQuery(featureSource, properties, Arrays.asList("identificatie"));
        return new DefaultOdsDataSource(featureSource, query, entityMapperFactory,
            Arrays.asList(new String[] {"identificatie", "openbare_ruimte", "huisnummer",
                "huisletter", "toevoeging", "postcode", "woonplaats", "bouwjaar", "geometrie",
                "gebruiksdoel", "status", "oppervlakte", "pandidentificatie"}));
    }
    
    private OdsDataSource createPandDataSource(GtFeatureSource featureSource) {
        List<String> properties = Arrays.asList("identificatie", "bouwjaar", "status", "aantal_verblijfsobjecten", "geometrie");
        Query query = new GroupByQuery(featureSource, properties, Arrays.asList("identificatie"));
        return new DefaultOdsDataSource(featureSource, query, entityMapperFactory);
    }
    
    private OdsDataSource createLigplaatsDataSource(GtFeatureSource featureSource) {
        List<String> properties = Arrays.asList("identificatie", "status", "openbare_ruimte", "huisnummer",
            "huisletter", "toevoeging", "postcode", "woonplaats", "geometrie");
        Query query = new GroupByQuery(featureSource, properties, Arrays.asList("identificatie"));
        return new DefaultOdsDataSource(featureSource, query, entityMapperFactory);
    }
    
    private OdsDataSource createStandplaatsDataSource(GtFeatureSource featureSource) {
        List<String> properties = Arrays.asList("identificatie", "status", "openbare_ruimte", "huisnummer",
            "huisletter", "toevoeging", "postcode", "woonplaats", "geometrie");
        Query query = new GroupByQuery(featureSource, properties, Arrays.asList("identificatie"));
        return new DefaultOdsDataSource(featureSource, query, entityMapperFactory);
    }
}

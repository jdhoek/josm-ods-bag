package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.geotools.data.DataStore;
import org.geotools.feature.NameImpl;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddress;
import org.openstreetmap.josm.plugins.ods.properties.SimpleEntityMapper;
import org.openstreetmap.josm.plugins.ods.test.file.wfs.TestData;
import org.openstreetmap.josm.plugins.ods.test.file.wfs.TestDataLoader;
import org.openstreetmap.josm.plugins.ods.wfs.file.FileWFSDataStore;

public class TestBagEntityMapperFactory {

    @Test
    public void testCreateAddressMapper(SimpleFeatureType simpleFeatureType) throws IOException {
        File dir = new File (FileWFSDataStore.class.getResource("/testdata/inktpot_1_1_0").getFile());
        TestData testData = TestDataLoader.loadTestData(dir, new String[] {"verblijfsobject"});
        DataStore dataStore = testData.getDataStore();
        BagEntityMapperFactory factory = new BagEntityMapperFactory(dataStore);
        SimpleEntityMapper<SimpleFeature, BagAddress> mapper = factory.createAddressMapper(simpleFeatureType);
        assertNotNull(mapper);
    }

    @Test
    public void testMapaddress() throws IOException {
        File dir = new File (getClass().getResource("/testdata/inktpot_1_1_0").getFile());
        TestData testData = TestDataLoader.loadTestData(dir, new String[] {"verblijfsobject"});
        DataStore dataStore = testData.getDataStore();
        BagEntityMapperFactory factory = new BagEntityMapperFactory(dataStore);
        SimpleFeatureType featureType = dataStore.getSchema("verblijfsobject");
        SimpleEntityMapper<SimpleFeature, BagAddress> mapper = factory.createAddressMapper(featureType);
        Name typeName = new NameImpl("http://bag.geonovum.nl", "verblijfsobject");
        SimpleFeature feature = testData.getFeature(typeName, "verblijfsobject.5880559");
        BagAddress address = mapper.map(feature);
        address.getCityName();
    }

}

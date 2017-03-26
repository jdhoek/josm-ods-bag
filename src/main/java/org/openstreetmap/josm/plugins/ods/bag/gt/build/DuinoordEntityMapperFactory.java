package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import java.math.BigDecimal;

import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddress;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNodeImpl;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.geotools.GtEntityMapperFactory;
import org.openstreetmap.josm.plugins.ods.geotools.SimpleFeatureEntityType;
import org.openstreetmap.josm.plugins.ods.properties.EntityMapperBuilder;
import org.openstreetmap.josm.plugins.ods.properties.EntityType;
import org.openstreetmap.josm.plugins.ods.properties.SimpleEntityFactory;
import org.openstreetmap.josm.plugins.ods.properties.SimpleEntityMapper;
import org.openstreetmap.josm.plugins.ods.properties.pojo.PojoEntityType;
import org.openstreetmap.josm.plugins.ods.properties.transform.CastingGeoTypeTransform;
import org.openstreetmap.josm.plugins.ods.properties.transform.SimpleTypeTransform;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;
import org.openstreetmap.josm.tools.I18n;

import com.vividsolutions.jts.geom.Point;

public class DuinoordEntityMapperFactory extends GtEntityMapperFactory {
    public DuinoordEntityMapperFactory(WFSHost host) {
        super(host);
    }
    
    public DuinoordEntityMapperFactory(DataStore dataStore) {
        super(dataStore);
    }

    @Override
    public SimpleEntityMapper<?, ?> create(OdsFeatureSource odsFeatureSource)
            throws OdsException {
        String feature = odsFeatureSource.getFeatureName();
        SimpleFeatureType featureType = (SimpleFeatureType) odsFeatureSource.getFeatureType();
        switch (feature) {
        case "bag:Address_Missing":
            return createAddressNodeMapper(featureType);
        default:
            throw new OdsException(I18n.tr("There is no entity mapper for feature ''{0}''", feature));
        }
    }

    public static SimpleEntityMapper<SimpleFeature, AddressNode> createAddressNodeMapper(SimpleFeatureType featureType) {
        CoordinateReferenceSystem crs = featureType.getCoordinateReferenceSystem();
        EntityType<SimpleFeature> sourceType = new SimpleFeatureEntityType(featureType);
        EntityType<AddressNode> targetType = new PojoEntityType<>(AddressNode.class);
        EntityMapperBuilder<SimpleFeature, AddressNode> builder =
            new EntityMapperBuilder<>(sourceType, targetType);
        builder.setFactory(new SimpleEntityFactory<>(AddressNodeImpl.class));
        builder.addAttributeMapping("#ID", "primaryId");
        builder.addAttributeMapping("nummeraanduiding", "referenceId", new SimpleTypeTransform<>(BigDecimal.class, Long.class, BigDecimal::longValue));
        builder.addAttributeMapping("geopunt", "geometry", new CastingGeoTypeTransform<>(crs, Point.class));
        builder.addConstant("status", EntityStatus.IN_USE);
        builder.addChildMapper(createAddressMapper(featureType), "address");
        return builder.build();
    }

    protected static SimpleEntityMapper<SimpleFeature, BagAddress> createAddressMapper(SimpleFeatureType featureType) {
        EntityType<SimpleFeature> sourceType = new SimpleFeatureEntityType(featureType);
        EntityType<BagAddress> targetType = new PojoEntityType<>(BagAddress.class);
        EntityMapperBuilder<SimpleFeature, BagAddress> builder =
            new EntityMapperBuilder<>(sourceType, targetType);
        builder.setFactory(new SimpleEntityFactory<>(BagAddress.class));
        builder.addAttributeMapping("huisnummer", "houseNumber", new SimpleTypeTransform<>(BigDecimal.class, Integer.class, BigDecimal::intValue));
        builder.addAttributeMapping("huisletter", "houseLetter", new HouseLetterTransform());
        builder.addAttributeMapping("huisnummertoevoeging", "houseNumberExtra");
        builder.addAttributeMapping("postcode", "postcode");
        builder.addAttributeMapping("straat", "streetName");
        builder.addAttributeMapping("woonplaats", "cityName");
        return builder.build();
    }

    private static class HouseLetterTransform extends SimpleTypeTransform<String, Character> {
        public HouseLetterTransform() {
            super(String.class, Character.class, null);
        }

        @Override
        public Character apply(String source) {
            if (source == null || source.length() == 0) {
                return null;
            }
            return source.charAt(0);
        }
    }
}

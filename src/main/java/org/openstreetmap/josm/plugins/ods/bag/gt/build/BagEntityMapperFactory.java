package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import java.math.BigDecimal;

import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressImpl;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.TypeOfBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.geotools.GtEntityMapperFactory;
import org.openstreetmap.josm.plugins.ods.geotools.SimpleFeatureEntityType;
import org.openstreetmap.josm.plugins.ods.properties.EntityMapperBuilder;
import org.openstreetmap.josm.plugins.ods.properties.OdsEntityType;
import org.openstreetmap.josm.plugins.ods.properties.SimpleEntityFactory;
import org.openstreetmap.josm.plugins.ods.properties.SimpleEntityMapper;
import org.openstreetmap.josm.plugins.ods.properties.pojo.PojoEntityType;
import org.openstreetmap.josm.plugins.ods.properties.transform.CastingGeoTypeTransform;
import org.openstreetmap.josm.plugins.ods.properties.transform.GeoTypeTransform;
import org.openstreetmap.josm.plugins.ods.properties.transform.SimpleTypeTransform;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;
import org.openstreetmap.josm.tools.I18n;

import com.vividsolutions.jts.geom.Point;

public class BagEntityMapperFactory extends GtEntityMapperFactory {
    public BagEntityMapperFactory(WFSHost host) {
        super(host);
    }

    public BagEntityMapperFactory(DataStore dataStore) {
        super(dataStore);
    }

    @Override
    public SimpleEntityMapper<?, ?> create(OdsFeatureSource odsFeatureSource)
            throws OdsException {
        String feature = odsFeatureSource.getFeatureName();
        SimpleFeatureType featureType = (SimpleFeatureType) odsFeatureSource.getFeatureType();
        switch (feature) {
        case "bag:pand":
            return createBuildingMapper(featureType);
        case "bag:verblijfsobject":
            return createHousingUnitMapper(featureType);
        case "bag:ligplaats":
            return createLigplaatsMapper(featureType);
        case "bag:standplaats":
            return createStandplaatsMapper(featureType);
        default:
            throw new OdsException(I18n.tr("There is no entity mapper for feature ''{0}''", feature));
        }
    }


    public static SimpleEntityMapper<SimpleFeature, BagBuilding> createBuildingMapper(SimpleFeatureType featureType) {
        CoordinateReferenceSystem crs = featureType.getCoordinateReferenceSystem();
        OdsEntityType<SimpleFeature> sourceType = new SimpleFeatureEntityType(featureType);
        OdsEntityType<BagBuilding> targetType = new PojoEntityType<>(BagBuilding.class);
        EntityMapperBuilder<SimpleFeature, BagBuilding> builder =
                new EntityMapperBuilder<>(sourceType, targetType);
        builder.setFactory(new SimpleEntityFactory<>(BagBuilding.class));
        builder.addAttributeMapping("#ID", "primaryId");
        builder.addAttributeMapping("identificatie", "referenceId", new SimpleTypeTransform<>(BigDecimal.class, Long.class, BigDecimal::longValue));
        builder.addAttributeMapping("status", "status", new PandStatusTransform());
        builder.addAttributeMapping("bouwjaar", "startDate", new BouwjaarTransform());
        builder.addAttributeMapping("geometrie", "geometry", new GeoTypeTransform(crs));
        builder.addConstant("buildingType", TypeOfBuilding.UNCLASSIFIED);
        builder.addConstant("source", "BAG");
        return builder.build();
    }

    public static SimpleEntityMapper<SimpleFeature, AddressNode> createAddressNodeMapper(SimpleFeatureType featureType) {
        CoordinateReferenceSystem crs = featureType.getCoordinateReferenceSystem();
        OdsEntityType<SimpleFeature> sourceType = new SimpleFeatureEntityType(featureType);
        OdsEntityType<AddressNode> targetType = new PojoEntityType<>(AddressNode.class);
        EntityMapperBuilder<SimpleFeature, AddressNode> builder =
                new EntityMapperBuilder<>(sourceType, targetType);
        builder.setFactory(new SimpleEntityFactory<>(OpenDataAddressNode.class));
        builder.addAttributeMapping("#ID", "primaryId");
        builder.addAttributeMapping("identificatie", "referenceId", new SimpleTypeTransform<>(BigDecimal.class, Long.class, BigDecimal::longValue));
        builder.addAttributeMapping("geometrie", "geometry", new CastingGeoTypeTransform<>(crs, Point.class));
        builder.addAttributeMapping("status", "status", new AddressNodeStatusTransform());
        builder.addChildMapper(createAddressMapper(featureType), "address");
        return builder.build();
    }

    public static SimpleEntityMapper<SimpleFeature, BagBuildingUnit> createHousingUnitMapper(SimpleFeatureType featureType) {
        CoordinateReferenceSystem crs = featureType.getCoordinateReferenceSystem();
        OdsEntityType<SimpleFeature> sourceType = new SimpleFeatureEntityType(featureType);
        OdsEntityType<BagBuildingUnit> targetType = new PojoEntityType<>(BagBuildingUnit.class);
        EntityMapperBuilder<SimpleFeature, BagBuildingUnit> builder =
                new EntityMapperBuilder<>(sourceType, targetType);
        builder.setFactory(new SimpleEntityFactory<>(BagBuildingUnit.class));
        builder.addAttributeMapping("#ID", "primaryId");
        builder.addAttributeMapping("identificatie", "referenceId", new SimpleTypeTransform<>(BigDecimal.class, Long.class, BigDecimal::longValue));
        builder.addAttributeMapping("gebruiksdoel", "gebruiksdoel");
        builder.addAttributeMapping("oppervlakte", "area", new SimpleTypeTransform<>(BigDecimal.class, Double.class, BigDecimal::doubleValue));
        builder.addAttributeMapping("status", "status", new AddressNodeStatusTransform());
        builder.addAttributeMapping("gebruiksdoel", "type", new BuildingTypeTransform());
        builder.addAttributeMapping("pandidentificatie", "buildingRef", new SimpleTypeTransform<>(BigDecimal.class, Long.class, BigDecimal::longValue));
        builder.addAttributeMapping("geometrie", "geometry", new CastingGeoTypeTransform<>(crs, Point.class));
        builder.addConstant("source", "BAG");
        builder.addChildMapper(createAddressNodeMapper(featureType), "mainAddressNode");
        return builder.build();
    }

    public static SimpleEntityMapper<SimpleFeature, BagBuilding> createLigplaatsMapper(SimpleFeatureType featureType) {
        CoordinateReferenceSystem crs = featureType.getCoordinateReferenceSystem();
        OdsEntityType<SimpleFeature> sourceType = new SimpleFeatureEntityType(featureType);
        OdsEntityType<BagBuilding> targetType = new PojoEntityType<>(BagBuilding.class);
        EntityMapperBuilder<SimpleFeature, BagBuilding> builder =
                new EntityMapperBuilder<>(sourceType, targetType);
        builder.setFactory(new SimpleEntityFactory<>(BagBuilding.class));
        builder.addAttributeMapping("#ID", "primaryId");
        builder.addAttributeMapping("identificatie", "referenceId", new SimpleTypeTransform<>(BigDecimal.class, Long.class, BigDecimal::longValue));
        builder.addAttributeMapping("status", "status", new PandStatusTransform());
        builder.addAttributeMapping("geometrie", "geometry", new GeoTypeTransform(crs));
        builder.addConstant("source", "BAG");
        builder.addConstant("buildingType", TypeOfBuilding.HOUSEBOAT);
        builder.addChildMapper(createAddressMapper(featureType), "address");
        return builder.build();
    }

    public static SimpleEntityMapper<SimpleFeature, BagBuilding> createStandplaatsMapper(SimpleFeatureType featureType) {
        CoordinateReferenceSystem crs = featureType.getCoordinateReferenceSystem();
        OdsEntityType<SimpleFeature> sourceType = new SimpleFeatureEntityType(featureType);
        OdsEntityType<BagBuilding> targetType = new PojoEntityType<>(BagBuilding.class);
        EntityMapperBuilder<SimpleFeature, BagBuilding> builder =
                new EntityMapperBuilder<>(sourceType, targetType);
        builder.setFactory(new SimpleEntityFactory<>(BagBuilding.class));
        builder.addAttributeMapping("#ID", "primaryId");
        builder.addAttributeMapping("identificatie", "referenceId", new SimpleTypeTransform<>(BigDecimal.class, Long.class, BigDecimal::longValue));
        builder.addAttributeMapping("status", "status", new PandStatusTransform());
        builder.addAttributeMapping("geometrie", "geometry", new GeoTypeTransform(crs));
        builder.addConstant("source", "BAG");
        builder.addConstant("buildingType", TypeOfBuilding.STATIC_CARAVAN);
        builder.addChildMapper(createAddressMapper(featureType), "address");
        return builder.build();
    }

    protected static SimpleEntityMapper<SimpleFeature, Address> createAddressMapper(SimpleFeatureType featureType) {
        OdsEntityType<SimpleFeature> sourceType = new SimpleFeatureEntityType(featureType);
        OdsEntityType<Address> targetType = new PojoEntityType<>(Address.class);
        EntityMapperBuilder<SimpleFeature, Address> builder =
                new EntityMapperBuilder<>(sourceType, targetType);
        builder.setFactory(new SimpleEntityFactory<>(AddressImpl.class));
        builder.addAttributeMapping("huisnummer", "houseNumber", new SimpleTypeTransform<>(BigDecimal.class, Integer.class, BigDecimal::intValue));
        builder.addAttributeMapping("huisletter", "houseLetter", new HouseLetterTransform());
        builder.addAttributeMapping("toevoeging", "houseNumberExtra");
        builder.addAttributeMapping("postcode", "postcode");
        builder.addAttributeMapping("openbare_ruimte", "streetName");
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

    private static class BuildingTypeTransform extends SimpleTypeTransform<String, TypeOfBuilding> {

        public BuildingTypeTransform() {
            super(String.class, TypeOfBuilding.class, null);
        }

        @Override
        public TypeOfBuilding apply(String type) {
            switch (type.toLowerCase()) {
            case "woonfunctie":
                return TypeOfBuilding.HOUSE;
            case "overige gebruiksfunctie":
                return TypeOfBuilding.UNCLASSIFIED;
            case "industriefunctie":
                return TypeOfBuilding.INDUSTRIAL;
            case "winkelfunctie":
                return TypeOfBuilding.RETAIL;
            case "kantoorfunctie":
                return TypeOfBuilding.OFFICE;
            case "celfunctie":
                return TypeOfBuilding.PRISON;
            default:
                return TypeOfBuilding.UNCLASSIFIED;
            }
        }
    }

    private static class AddressNodeStatusTransform extends SimpleTypeTransform<String, EntityStatus> {
        public AddressNodeStatusTransform() {
            super(String.class, EntityStatus.class, null);
        }

        @Override
        public EntityStatus apply(String status) {
            if (status == null) {
                return EntityStatus.UNKNOWN;
            }
            switch (status) {
            case "Verblijfsobject gevormd":
                return EntityStatus.CONSTRUCTION;
            case "Verblijfsobject in gebruik":
            case "Verblijfsobject buiten gebruik":
            case "Verblijfsobject in gebruik (niet ingemeten)":
                return EntityStatus.IN_USE;
            case "Verblijfsobject ingetrokken":
            case "Niet gerealiseerd verblijfsobject":
                return EntityStatus.REMOVED;
            default:
                return EntityStatus.IN_USE;
            }
        }
    }
}

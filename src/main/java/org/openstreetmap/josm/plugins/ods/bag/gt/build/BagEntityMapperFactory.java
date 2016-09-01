package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import java.math.BigDecimal;
import java.util.function.Function;

import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagHousingUnit;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.BuildingType;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.AddressNodeImpl;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.geotools.GtEntityMapperFactory;
import org.openstreetmap.josm.plugins.ods.geotools.SimpleFeatureEntityType;
import org.openstreetmap.josm.plugins.ods.properties.EntityMapperBuilder;
import org.openstreetmap.josm.plugins.ods.properties.EntityType;
import org.openstreetmap.josm.plugins.ods.properties.SimpleEntityFactory;
import org.openstreetmap.josm.plugins.ods.properties.SimpleEntityMapper;
import org.openstreetmap.josm.plugins.ods.properties.pojo.PojoEntityType;
import org.openstreetmap.josm.plugins.ods.properties.transform.CastingGeoTypeTransform;
import org.openstreetmap.josm.plugins.ods.properties.transform.GeoTypeTransform;
import org.openstreetmap.josm.plugins.ods.properties.transform.SimpleTypeTransform;
import org.openstreetmap.josm.plugins.ods.properties.transform.TypeTransform;
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
        switch (feature) {
        case "bag:pand":
            return createBuildingMapper();
        case "bag:verblijfsobject":
            return createHousingUnitMapper();
        case "bag:ligplaats":
            return createLigplaatsMapper();
        case "bag:standplaats":
            return createStandplaatsMapper();
        default:
            throw new OdsException(I18n.tr("There is no entity mapper for feature ''{0}''", feature));
        }
    }


    public SimpleEntityMapper<SimpleFeature, BagBuilding> createBuildingMapper() throws OdsException {
        SimpleFeatureType featureType = getFeatureType("bag:pand");
        CoordinateReferenceSystem crs = featureType.getCoordinateReferenceSystem();
        EntityType<SimpleFeature> sourceType = new SimpleFeatureEntityType(featureType);
        EntityType<BagBuilding> targetType = new PojoEntityType<>(BagBuilding.class);
        EntityMapperBuilder<SimpleFeature, BagBuilding> builder =
            new EntityMapperBuilder<>(sourceType, targetType);
        builder.setFactory(new SimpleEntityFactory<>(BagBuilding.class));
        builder.addAttributeMapping("#ID", "primaryId");
        builder.addAttributeMapping("identificatie", "referenceId", new SimpleTypeTransform<>(BigDecimal.class, Long.class, BigDecimal::longValue));
        builder.addAttributeMapping("status", "status", new PandStatusTransform());
        builder.addAttributeMapping("bouwjaar", "startDate", new BouwjaarTransform());
        builder.addAttributeMapping("geometrie", "geometry", new GeoTypeTransform(crs));
        builder.addConstant("buildingType", BuildingType.OTHER);
        builder.addConstant("source", "BAG");
        return builder.build();
    }

    public static SimpleEntityMapper<SimpleFeature, AddressNode> createAddressNodeMapper(SimpleFeatureType featureType) {
        CoordinateReferenceSystem crs = featureType.getCoordinateReferenceSystem();
        EntityType<SimpleFeature> sourceType = new SimpleFeatureEntityType(featureType);
        EntityType<AddressNode> targetType = new PojoEntityType<>(AddressNode.class);
        EntityMapperBuilder<SimpleFeature, AddressNode> builder =
            new EntityMapperBuilder<>(sourceType, targetType);
        builder.setFactory(new SimpleEntityFactory<>(AddressNodeImpl.class));
        builder.addAttributeMapping("#ID", "primaryId");
        builder.addAttributeMapping("identificatie", "referenceId", new SimpleTypeTransform<>(BigDecimal.class, Long.class, BigDecimal::longValue));
        builder.addAttributeMapping("geometrie", "geometry", new CastingGeoTypeTransform<>(crs, Point.class));
        builder.addAttributeMapping("status", "status", new AddressNodeStatusTransform());
        builder.addChildMapper(createAddressMapper(featureType), "address");
        return builder.build();
    }

    public SimpleEntityMapper<SimpleFeature, BagHousingUnit> createHousingUnitMapper() throws OdsException {
        SimpleFeatureType featureType = getFeatureType("bag:verblijfsobject");
        CoordinateReferenceSystem crs = featureType.getCoordinateReferenceSystem();
        EntityType<SimpleFeature> sourceType = new SimpleFeatureEntityType(featureType);
        EntityType<BagHousingUnit> targetType = new PojoEntityType<>(BagHousingUnit.class);
        EntityMapperBuilder<SimpleFeature, BagHousingUnit> builder =
            new EntityMapperBuilder<>(sourceType, targetType);
        builder.setFactory(new SimpleEntityFactory<>(BagHousingUnit.class));
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

    public SimpleEntityMapper<SimpleFeature, BagBuilding> createLigplaatsMapper() throws OdsException {
        SimpleFeatureType featureType = getFeatureType("bag:ligplaats");
        CoordinateReferenceSystem crs = featureType.getCoordinateReferenceSystem();
        EntityType<SimpleFeature> sourceType = new SimpleFeatureEntityType(featureType);
        EntityType<BagBuilding> targetType = new PojoEntityType<>(BagBuilding.class);
        EntityMapperBuilder<SimpleFeature, BagBuilding> builder =
            new EntityMapperBuilder<>(sourceType, targetType);
        builder.setFactory(new SimpleEntityFactory<>(BagBuilding.class));
        builder.addAttributeMapping("#ID", "primaryId");
        builder.addAttributeMapping("identificatie", "referenceId", new SimpleTypeTransform<>(BigDecimal.class, Long.class, BigDecimal::longValue));
        builder.addAttributeMapping("status", "status", new PandStatusTransform());
        builder.addAttributeMapping("geometrie", "geometry", new GeoTypeTransform(crs));
        builder.addConstant("source", "BAG");
        builder.addConstant("buildingType", BuildingType.HOUSEBOAT);
        builder.addChildMapper(createAddressMapper(featureType), "address");
        return builder.build();
    }

    public SimpleEntityMapper<SimpleFeature, BagBuilding> createStandplaatsMapper() throws OdsException {
        SimpleFeatureType featureType = getFeatureType("bag:standplaats");
        CoordinateReferenceSystem crs = featureType.getCoordinateReferenceSystem();
        EntityType<SimpleFeature> sourceType = new SimpleFeatureEntityType(featureType);
        EntityType<BagBuilding> targetType = new PojoEntityType<>(BagBuilding.class);
        EntityMapperBuilder<SimpleFeature, BagBuilding> builder =
            new EntityMapperBuilder<>(sourceType, targetType);
        builder.setFactory(new SimpleEntityFactory<>(BagBuilding.class));
        builder.addAttributeMapping("#ID", "primaryId");
        builder.addAttributeMapping("identificatie", "referenceId", new SimpleTypeTransform<>(BigDecimal.class, Long.class, BigDecimal::longValue));
        builder.addAttributeMapping("status", "status", new PandStatusTransform());
        builder.addAttributeMapping("geometrie", "geometry", new GeoTypeTransform(crs));
        builder.addConstant("source", "BAG");
        builder.addConstant("buildingType", BuildingType.STATIC_CARAVAN);
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
    
    private static class BuildingTypeTransform extends SimpleTypeTransform<String, BuildingType> {

        public BuildingTypeTransform() {
            super(String.class, BuildingType.class, null);
        }
        
        @Override
        public BuildingType apply(String type) {
            switch (type.toLowerCase()) {
            case "woonfunctie":
                return BuildingType.HOUSE;
            case "overige gebruiksfunctie":
                return BuildingType.UNCLASSIFIED;
            case "industriefunctie":
                return BuildingType.INDUSTRIAL;
            case "winkelfunctie":
                return BuildingType.RETAIL;
            case "kantoorfunctie":
                return BuildingType.OFFICE;
            case "celfunctie":
                return BuildingType.PRISON;
            default: 
                return BuildingType.UNCLASSIFIED;
            }
        }
    }
    
    private static class PandStatusTransform extends SimpleTypeTransform<String, EntityStatus> {
        public PandStatusTransform() {
            super(String.class, EntityStatus.class, null);
        }

        @Override
        public EntityStatus apply(String status) {
            if (status == null) {
                return EntityStatus.UNKNOWN;
            }
            switch (status) {
            case "Bouwvergunning verleend":
                return EntityStatus.PLANNED;
            case "Bouw gestart":
                return EntityStatus.CONSTRUCTION;
            case "Pand in gebruik":
            case "Pand in gebruik (niet ingemeten)":
            case "Pand buiten gebruik":
            case "Plaats aangewezen":
                return EntityStatus.IN_USE;
            case "Niet gerealiseerd pand":
                return EntityStatus.NOT_REALIZED;
            case "Sloopvergunning verleend":
                return EntityStatus.REMOVAL_DUE;
            case "Pand gesloopt":
                return EntityStatus.REMOVED;
            default:
                return EntityStatus.UNKNOWN;
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

    private static class BouwjaarTransform implements TypeTransform<BigDecimal, String> {
        public BouwjaarTransform() {
            super();
        }

        @Override
        public Class<BigDecimal> getSourceType() {
            return BigDecimal.class;
        }

        @Override
        public Class<String> getTargetType() {
            return String.class;
        }

        @Override
        public Function<BigDecimal, String> getFunction() {
            return null;
        }

        @Override
        public String apply(BigDecimal bouwjaar) {
            if (bouwjaar == null) {
                return null;
            }
            return ((Integer)bouwjaar.intValue()).toString();
        }
    }
}

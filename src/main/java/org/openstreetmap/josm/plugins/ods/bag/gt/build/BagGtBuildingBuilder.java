package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.BuildingType;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.properties.EntityMapper;

public class BagGtBuildingBuilder extends BagGtEntityBuilder<Building, BagBuilding> {
    private EntityMapper<SimpleFeature, BagBuilding> buildingMapper;
    private EntityMapper<SimpleFeature, BagAddress> addressMapper;
    
    public BagGtBuildingBuilder(CRSUtil crsUtil, EntityMapper<SimpleFeature, BagBuilding> buildingMapper) {
        super(crsUtil);
        this.buildingMapper = buildingMapper;
    }

//    @Override
//    protected BagBuilding newInstance() {
//        return new BagBuilding();
//    }

    @Override
    public BagBuilding build(SimpleFeature feature, DownloadResponse response) {
        BagBuilding building = buildingMapper.map(feature);
//        BagBuilding building = buildingFactory.create();
//        BagBuilding building = super.build(feature, response);
//        String type = feature.getName().getLocalPart();
//        building.setStartDate(startDateMapper.get(feature));
//        String status = statusMapper.get(feature);
//        // TODO consider moving this code to the statusMapper
//        building.setStatus(parseStatus(status));
//        if (type.equals("bag:pand") || type.equals("osm_bag:buildingdestroyed_osm")) {
//            building.setBuildingType(BuildingType.UNCLASSIFIED);
//            building.setAantalVerblijfsobjecten(FeatureUtil.getLong(feature, "aantal_verblijfsobjecten"));
//        }
        if (building.getBuildingType().equals(BuildingType.HOUSEBOAT) ||
                building.getBuildingType().equals(BuildingType.STATIC_CARAVAN)) {
            BagAddress address = addressMapper.map(feature);
//                    new BagAddress();
//            address.setHouseNumber(FeatureUtil.getInteger(feature, "huisnummer"));
//            address.setHuisletter(FeatureUtil.getString(feature, "huisletter"));
//            address.setHuisnummerToevoeging(FeatureUtil.getString(feature, "toevoeging"));
//            address.setStreetName(FeatureUtil.getString(feature, "openbare_ruimte"));
//            address.setCityName(FeatureUtil.getString(feature, "woonplaats"));
//            address.setPostcode(FeatureUtil.getString(feature, "postcode"));
            building.setAddress(address);
//            if (type.equals("bag:ligplaats")) {
//                building.setBuildingType(BuildingType.HOUSEBOAT);
//            }
//            else if (type.equals("bag:standplaats")) {
//                building.setBuildingType(BuildingType.STATIC_CARAVAN);
//            }
//            else {
//                building.setBuildingType(BuildingType.UNCLASSIFIED);
//            }
        }
        return building;
    }

    private static EntityStatus parseStatus(String status) {
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

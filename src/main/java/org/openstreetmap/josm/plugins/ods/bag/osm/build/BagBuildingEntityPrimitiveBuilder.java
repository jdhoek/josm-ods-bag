package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.time.LocalDate;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.actual.Address;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

public class BagBuildingEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<Building> {

    public BagBuildingEntityPrimitiveBuilder(LayerManager dataLayer) {
        super(dataLayer, Building.class);
    }

    @Override
    public void createPrimitive(Building building) {
        // Ignore buildings with status "Bouwvergunning verleend"
        // Make an exception for buildings that already exist in OSM. In that case, the building permit is for reconstruction
        if ("Bouwvergunning verleend".equals(building.getStatus())
                && building.getMatch() == null) {
            return;
        }
        super.createPrimitive(building);
    }


    @Override
    protected void buildTags(Building building, Map<String, String> tags) {
        Address address = building.getAddress();
        if (address != null) {
            createAddressTags(address, tags);
        }
        tags.put("source", "BAG");
        LocalDate date = building.getSourceDate();
        if (date == null) date = LocalDate.now();
        tags.put("source:date", date.toString());
        tags.put("ref:bag", building.getReferenceId().toString());
        if (building.getStartDate() != null) {
            tags.put("start_date", building.getStartDate());
        }
        if ("Sloopvergunning verleend".equals(building.getStatus())) {
            tags.put("note", "Sloopvergunning verleend");
        }
        String type = "yes";
        switch (building.getBuildingType()) {
        case APARTMENTS:
            type = "apartments";
            break;
        case GARAGE:
            type = "garage";
            break;
        case HOUSE:
            type = "house";
            break;
        case HOUSEBOAT:
            type = "houseboat";
            tags.put("floating", "yes");
            break;
        case INDUSTRIAL:
            type = "industrial";
            break;
        case OFFICE:
            type = "office";
            break;
        case PRISON:
            tags.put("amenity", "prison");
            break;
        case RETAIL:
            type = "retail";
            break;
        case STATIC_CARAVAN:
            type = "static_caravan";
            break;
        case SUBSTATION:
            tags.put("power", "substation");
            break;
        case OTHER:
            type = building.getBuildingType().getSubType();
            break;
        default:
            type = "yes";
            break;
        }
        
        if (building.getStatus().equals(EntityStatus.CONSTRUCTION) ||
                building.getStatus().equals(EntityStatus.PLANNED)) {
            tags.put("building", "construction");
            tags.put("construction", type);
        }
        else {
            tags.put("building", type);
        }
    }
}

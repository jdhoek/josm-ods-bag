package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.time.LocalDate;

import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.util.OdsTagMap;

public class BagBuildingEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<Building> {

    public BagBuildingEntityPrimitiveBuilder(LayerManager dataLayer) {
        super(dataLayer, Building.class);
    }

//    //TODO Implement this using a predicate in stead.
//    @Override
//    public void createPrimitive(Building building) {
//        // Ignore buildings with status "PLANNED"
//        // Make an exception for buildings that already exist in OSM. In that case, the building permit is for reconstruction
//        if (EntityStatus.PLANNED.equals(building.getStatus())
//                && building.getMatch(building.getBaseType()) == null) {
//            return;
//        }
//        super.createPrimitive(building);
//    }
//

    @Override
    protected void buildTags(Building building, OdsTagMap tags) {
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
            tags.put("start_date", building.getStartDate().toString());
        }
        if (EntityStatus.REMOVAL_DUE.equals(building.getStatus())) {
            tags.put("note", "Sloopvergunning verleend");
        }
        tags.putAll(building.getBuildingType().getTags());
        if (building.getStatus().equals(EntityStatus.CONSTRUCTION) ||
                building.getStatus().equals(EntityStatus.PLANNED)) {
            String type = tags.get("building");
            tags.put("building", "construction");
            tags.put("construction", type);
        }
    }
}

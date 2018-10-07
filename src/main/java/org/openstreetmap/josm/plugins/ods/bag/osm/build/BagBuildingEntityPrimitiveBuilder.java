package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.time.LocalDate;
import java.util.Optional;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityDao;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.util.OdsTagMap;

public class BagBuildingEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<OpenDataBuilding> {


    public BagBuildingEntityPrimitiveBuilder(OdsModule module,
            EntityDao<OpenDataBuilding> dao) {
        super(module, dao);
    }

    @Override
    protected void buildTags(OpenDataBuilding building,
            OdsTagMap tags) {
        //    protected void buildTags(OdEntity<BuildingEntityType> building, OdsTagMap tags) {
        Optional<Address> address = building.getAddress();
        if (address.isPresent()) {
            createAddressTags(address.get(), tags);
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
        if (building.getStatus().equals(EntityStatus.CONSTRUCTION) ||
                building.getStatus().equals(EntityStatus.PLANNED)) {
            tags.put("building", "construction");
            tags.put("construction", "yes");
        }
        else {
            tags.put("building", "yes");
        }
    }

    public static void updateBuildingTypeTags(Building building) {
        ManagedPrimitive primitive = building.getPrimitive();
        if (primitive != null) {
            primitive.putAll(building.getBuildingType().getTags());
            if (building.getStatus().equals(EntityStatus.CONSTRUCTION) ||
                    building.getStatus().equals(EntityStatus.PLANNED)) {
                String type = primitive.get("building");
                primitive.put("building", "construction");
                primitive.put("construction", type);
            }
        }
    }

    //    @Override
    //    protected <E extends OdEntity<BuildingEntityType>> void buildTags(E entity,
    //            OdsTagMap tags) {
    //        // TODO Auto-generated method stub
    //
    //    }
}

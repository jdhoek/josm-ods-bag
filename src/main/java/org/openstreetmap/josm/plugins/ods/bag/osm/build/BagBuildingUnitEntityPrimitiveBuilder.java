package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.time.LocalDate;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuildingUnit;
import org.openstreetmap.josm.plugins.ods.util.OdsTagMap;

public class BagBuildingUnitEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<OpenDataBuildingUnit> {

    public BagBuildingUnitEntityPrimitiveBuilder() {
        super(OpenDataBuildingUnit.class);
    }

    @Override
    protected void buildTags(OpenDataBuildingUnit buildingUnit, OdsTagMap tags) {
        createAddressTags(buildingUnit.getMainAddressNode().getAddress(), tags);
        tags.put("source", "BAG");
        LocalDate date = buildingUnit.getSourceDate();
        if (date == null) date = LocalDate.now();
        tags.put("source:date", date.toString());
    }
}

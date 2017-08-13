package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.time.LocalDate;

import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingUnit;
import org.openstreetmap.josm.plugins.ods.util.OdsTagMap;

public class BagHousingUnitEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<BuildingUnit> {

    public BagHousingUnitEntityPrimitiveBuilder(LayerManager dataLayer) {
        super(dataLayer, BuildingUnit.class);
    }

    @Override
    protected void buildTags(BuildingUnit housingUnit, OdsTagMap tags) {
        createAddressTags(housingUnit.getMainAddressNode().getAddress(), tags);
        tags.put("source", "BAG");
        LocalDate date = housingUnit.getSourceDate();
        if (date == null) date = LocalDate.now();
        tags.put("source:date", date.toString());
    }
}

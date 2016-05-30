package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.time.LocalDate;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.entities.actual.HousingUnit;

public class BagHousingUnitEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<HousingUnit> {

    public BagHousingUnitEntityPrimitiveBuilder(LayerManager dataLayer) {
        super(dataLayer, HousingUnit.class);
    }

    @Override
    protected void buildTags(HousingUnit housingUnit, Map<String, String> tags) {
        createAddressTags(housingUnit.getMainAddressNode().getAddress(), tags);
        tags.put("source", "BAG");
        LocalDate date = housingUnit.getSourceDate();
        if (date != null) {
            tags.put("source:date", date.toString());
        }
    }
}

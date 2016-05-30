package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.time.LocalDate;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;

public class BagAddressNodeEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<AddressNode> {

    public BagAddressNodeEntityPrimitiveBuilder(LayerManager dataLayer) {
        super(dataLayer, AddressNode.class);
    }

    @Override
    protected void buildTags(AddressNode addresNode, Map<String, String> tags) {
        createAddressTags(addresNode.getAddress(), tags);
        tags.put("source", "BAG");
        LocalDate date = addresNode.getSourceDate();
        if (date != null) {
            tags.put("source:date", date.toString());
        }
    }
}

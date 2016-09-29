package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.time.LocalDate;

import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.util.OdsTagMap;

public class BagAddressNodeEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<AddressNode> {

    public BagAddressNodeEntityPrimitiveBuilder(LayerManager dataLayer) {
        super(dataLayer, AddressNode.class);
    }

    @Override
    protected void buildTags(AddressNode addressNode, OdsTagMap tags) {
        createAddressTags(addressNode.getAddress(), tags);
        tags.put("source", "BAG");
        LocalDate date = addressNode.getSourceDate();
        if (date == null) date = LocalDate.now();
        tags.put("source:date", date.toString());
    }
}

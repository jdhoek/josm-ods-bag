package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.time.LocalDate;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.EntityDao;
import org.openstreetmap.josm.plugins.ods.util.OdsTagMap;

public class BagAddressNodeEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<OpenDataAddressNode> {

    public BagAddressNodeEntityPrimitiveBuilder(OdsModule module,
            EntityDao<OpenDataAddressNode> dao) {
        super(module, dao);
    }

    @Override
    protected void buildTags(OpenDataAddressNode addressNode, OdsTagMap tags) {
        createAddressTags(addressNode.getAddress(), tags);
        tags.put("source", "BAG");
        LocalDate date = addressNode.getSourceDate();
        if (date == null) date = LocalDate.now();
        tags.put("source:date", date.toString());
    }
}

package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.osm.build.AbstractOsmAddressNodeBuilder;

public class BagOsmAddressNodeBuilder extends AbstractOsmAddressNodeBuilder {

    public BagOsmAddressNodeBuilder(OdsModule module) {
        super(module);
    }

    @Override
    protected Long parseReferenceId(Map<String, String> tags) {
        return BagOsmEntityBuilder.getReferenceId(tags.get("ref:bag"));
    }
}

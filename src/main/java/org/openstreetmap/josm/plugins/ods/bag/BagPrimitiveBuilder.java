package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagAddressNodeEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagBuildingEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.entities.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;

public class BagPrimitiveBuilder extends PrimitiveBuilder {

    public BagPrimitiveBuilder(OdsModule module) {
        super(module);
        OpenDataLayerManager odLayerManager = module.getOpenDataLayerManager();
        register(Building.class, new BagBuildingEntityPrimitiveBuilder(odLayerManager));
        register(AddressNode.class, new BagAddressNodeEntityPrimitiveBuilder(odLayerManager));
    }
}

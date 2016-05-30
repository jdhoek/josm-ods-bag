package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagAddressNodeEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagBuildingEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.HousingUnit;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;

public class BagPrimitiveBuilder extends PrimitiveBuilder {
//    private OdsModule module;
//    private EntityPrimitiveBuilder<AddressNode> addressNodePrimitiveBuilder;
//    private EntityPrimitiveBuilder<Building> buildingPrimitiveBuilder;
//    private BuildingSegmentSimplifier segmentSimplifier;
//    private BuildingAligner buildingAligner;

    public BagPrimitiveBuilder(OdsModule module) {
        super(module);
//        this.module = module;
        OpenDataLayerManager odLayerManager = module.getOpenDataLayerManager();
        register(Building.class, new BagBuildingEntityPrimitiveBuilder(odLayerManager));
        register(AddressNode.class, new BagAddressNodeEntityPrimitiveBuilder(odLayerManager));
//        register(HousingUnit.class, new BagHousingUnitEntityPrimitiveBuilder(odLayerManager));
//        buildingPrimitiveBuilder = new BagBuildingEntityPrimitiveBuilder(odLayerManager);
//        addressNodePrimitiveBuilder = new BagAddressNodeEntityPrimitiveBuilder(odLayerManager);
        // TODO pass tolerance as a configurable parameter at a higher level.
 //       segmentSimplifier = new BuildingSegmentSimplifier(1e-5);
//        buildingAligner = new BuildingAligner(module, odLayerManager.getEntityStore(Building.class));
    }
    
//    public void run(DownloadResponse response) {
//        OpenDataLayerManager odLayerManager = module.getOpenDataLayerManager();
//        EntityStore<AddressNode> addressNodeStore = module.getOpenDataLayerManager()
//                .getEntityStore(AddressNode.class);
//        EntityStore<Building> buildingStore = module.getOpenDataLayerManager()
//                .getEntityStore(Building.class);
//        addressNodeStore.stream()
//            .filter(addressNode->addressNode.getPrimitive() == null)
////            .filter(addressNode->!addressNode.isIncomplete())
//            .forEach(addressNodePrimitiveBuilder::createPrimitive);
//        buildingStore.stream()
//            .filter(building->building.getPrimitive() == null)
////            .filter(building->!building.isIncomplete())
//            .forEach(buildingPrimitiveBuilder::createPrimitive);
////        buildingStore.stream().filter(building->building.getDownloadResponse() == response)
////            .forEach(segmentSimplifier::simplify);
//        buildingStore.forEach(buildingAligner::align);
//    }
}

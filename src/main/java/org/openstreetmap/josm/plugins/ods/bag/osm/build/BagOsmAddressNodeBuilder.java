package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddress;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.actual.Address;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.AddressNodeImpl;
import org.openstreetmap.josm.plugins.ods.entities.osm.AbstractOsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitiveFactory;

import com.vividsolutions.jts.geom.Point;

public class BagOsmAddressNodeBuilder extends AbstractOsmEntityBuilder<AddressNode> {
    private final OsmLayerManager layerManager;
    private final ManagedPrimitiveFactory factory;
    
    public BagOsmAddressNodeBuilder(OdsModule module) {
        super(module, AddressNode.class);
        layerManager = module.getOsmLayerManager();
        factory = new ManagedPrimitiveFactory(layerManager);
    }

    @Override
    public Class<AddressNode> getEntityClass() {
        return AddressNode.class;
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (AddressNode.isAddressNode(primitive)) {
            ManagedPrimitive<?> managedPrimitive = layerManager.getManagedPrimitive(primitive);
            if (managedPrimitive == null) {
                managedPrimitive = factory.createNode((Node) primitive);
            }
            Entity entity = managedPrimitive.getEntity();
            if (entity != null) {
                assert getEntityClass().isInstance(entity);
                return;
            }
            normalizeKeys(managedPrimitive);
            Address address = new BagAddress();
            AddressNodeImpl addressNode = new AddressNodeImpl();
            addressNode.setPrimaryId(managedPrimitive.getUniqueId());
            addressNode.setPrimitive(managedPrimitive);
            addressNode.setAddress(address);
            Map<String, String> tags = primitive.getKeys();
            BagOsmAddressEntityBuilder.parseKeys(address, tags);
            parseKeys(addressNode, tags);
            addressNode.setOtherTags(tags);
            addressNode.setGeometry(buildGeometry(primitive));
            managedPrimitive.setEntity(addressNode);
            layerManager.getRepository().add(addressNode);
        }
        return;
    }
    
    public static void normalizeKeys(ManagedPrimitive<?> primitive) {
        BagOsmEntityBuilder.normalizeTags(primitive);
    }
    
    private static void parseKeys(AddressNode addressNode, Map<String, String> tags) {
        BagOsmEntityBuilder.parseKeys(addressNode, tags); 
    }
    
    private Point buildGeometry(OsmPrimitive primitive) {
        if (primitive.getDisplayType() == OsmPrimitiveType.NODE) {
            return getGeoUtil().toPoint((Node) primitive);
        }
        return null;
    }
}

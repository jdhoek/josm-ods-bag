package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.util.OdsTagMap;

public abstract class BagEntityPrimitiveBuilder<E extends OdEntity<?>>
extends AbstractEntityPrimitiveBuilder<E> {

    public BagEntityPrimitiveBuilder(Class<E> clazz) {
        super(clazz);
    }

    @Override
    public void createPrimitive(E entity) {
        if (entity.getPrimitive() == null && entity.getGeometry() != null) {
            OdsTagMap tags = new OdsTagMap();
            buildTags(entity, tags);
            ManagedPrimitive primitive = getPrimitiveFactory().create(entity.getGeometry(), tags);
            entity.setPrimitive(primitive);
            primitive.setEntity(entity);
        }
    }

    protected abstract void buildTags(E entity, OdsTagMap tags);

    public static void createAddressTags(Address address, Map<String, String> tags) {
        if (address.getStreetName() != null) {
            tags.put("addr:street", address.getStreetName());
        }
        if (address.getFullHouseNumber() != null) {
            tags.put("addr:housenumber", address.getFullHouseNumber());
        }
        if (address.getPostcode() != null) {
            tags.put("addr:postcode", address.getPostcode());
        }
        if (address.getCityName() != null) {
            tags.put("addr:city", address.getCityName());
        }
    }

}

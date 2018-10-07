package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.properties.transform.SimpleTypeTransform;

public class AddressNodeStatusTransform extends SimpleTypeTransform<String, EntityStatus> {
    public AddressNodeStatusTransform() {
        super(String.class, EntityStatus.class, null);
    }

    @Override
    public EntityStatus apply(String status) {
        if (status == null) {
            return EntityStatus.UNKNOWN;
        }
        switch (status) {
        case "Verblijfsobject gevormd":
            return EntityStatus.CONSTRUCTION;
        case "Verblijfsobject in gebruik":
        case "Verblijfsobject buiten gebruik":
        case "Verblijfsobject in gebruik (niet ingemeten)":
            return EntityStatus.IN_USE;
        case "Verblijfsobject ingetrokken":
        case "Niet gerealiseerd verblijfsobject":
            return EntityStatus.REMOVED;
        default:
            return EntityStatus.IN_USE;
        }
    }
}

package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.properties.transform.SimpleTypeTransform;

class PandStatusTransform extends SimpleTypeTransform<String, EntityStatus> {
    public PandStatusTransform() {
        super(String.class, EntityStatus.class, null);
    }

    @Override
    public EntityStatus apply(String status) {
        if (status == null) {
            return EntityStatus.UNKNOWN;
        }
        switch (status) {
        case "Bouwvergunning verleend":
            return EntityStatus.PLANNED;
        case "Bouw gestart":
            return EntityStatus.CONSTRUCTION;
        case "Pand in gebruik":
        case "Pand buiten gebruik":
        case "Plaats aangewezen":
            return EntityStatus.IN_USE;
        case "Pand in gebruik (niet ingemeten)":
            return EntityStatus.IN_USE_NOT_MEASURED;
        case "Niet gerealiseerd pand":
            return EntityStatus.NOT_REALIZED;
        case "Sloopvergunning verleend":
            return EntityStatus.REMOVAL_DUE;
        case "Pand gesloopt":
            return EntityStatus.REMOVED;
        default:
            return EntityStatus.UNKNOWN;
        }
    }
}
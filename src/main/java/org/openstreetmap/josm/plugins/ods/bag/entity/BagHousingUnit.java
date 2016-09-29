package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.HousingUnitImpl;

public class BagHousingUnit extends HousingUnitImpl {
    private String gebruiksdoel;
    
    @Override
    public void setMainAddressNode(AddressNode addressNode) {
        super.setMainAddressNode(addressNode);
        addressNode.setHousingUnit(this);
    }

    @Override
    public boolean isIncomplete() {
        if (getBuilding() != null) {
            return getBuilding().isIncomplete();
        }
        return super.isIncomplete();
    }
    
    public String getGebruiksdoel() {
        return gebruiksdoel;
    }

    public void setGebruiksdoel(String gebruiksdoel) {
        this.gebruiksdoel = gebruiksdoel;
    }
}

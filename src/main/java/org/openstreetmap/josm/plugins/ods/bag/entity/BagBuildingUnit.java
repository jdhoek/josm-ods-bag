package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuildingUnit;

public class BagBuildingUnit extends OpenDataBuildingUnit {
    private String gebruiksdoel;

    @Override
    public void setMainAddressNode(OpenDataAddressNode addressNode) {
        super.setMainAddressNode(addressNode);
        addressNode.setBuildingUnit(this);
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

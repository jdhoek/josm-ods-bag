package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressImpl;

@Deprecated
public class BagAddress extends AddressImpl {

    @Override
    public String formatHouseNumber() {
        StringBuilder sb = new StringBuilder(10);
        sb.append(getHouseNumber());
        if (getHouseLetter() != null) {
            sb.append(getHouseLetter());
        }
        if (getHouseNumberExtra() != null) {
            sb.append('-').append(getHouseNumberExtra());
        }
        return sb.toString();
    }
}

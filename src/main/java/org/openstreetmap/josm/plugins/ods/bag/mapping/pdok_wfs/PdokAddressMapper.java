package org.openstreetmap.josm.plugins.ods.bag.mapping.pdok_wfs;

import java.math.BigDecimal;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressImpl;
import org.openstreetmap.josm.plugins.ods.properties.EntityMapper;

public class PdokAddressMapper implements EntityMapper<SimpleFeature, Address> {

    @Override
    public Address map(SimpleFeature feature) {
        BigDecimal houseNumber = (BigDecimal) feature.getAttribute("huisnummer");
        String houseLetter = (String) feature.getAttribute("huisletter");
        String extra = (String) feature.getAttribute("toevoeging");
        String postcode = (String) feature.getAttribute("postcode");
        String streetName = (String) feature.getAttribute("openbare_ruimte");
        String cityName = (String) feature.getAttribute("woonplaats");

        AddressImpl address = new AddressImpl();
        address.setHouseNumber(houseNumber.intValueExact());
        address.setHouseLetter(houseLetter != null ? houseLetter.charAt(0) : null);
        address.setHouseNumberExtra(extra);
        address.setPostcode(postcode);
        address.setStreetName(streetName);
        address.setCityName(cityName);
        return address;
    }

}

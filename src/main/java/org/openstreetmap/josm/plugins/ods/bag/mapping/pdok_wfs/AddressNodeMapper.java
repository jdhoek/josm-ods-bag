package org.openstreetmap.josm.plugins.ods.bag.mapping.pdok_wfs;

import java.math.BigDecimal;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.AddressNodeStatusTransform;
import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.properties.EntityMapper;

import com.vividsolutions.jts.geom.Geometry;

public class AddressNodeMapper implements EntityMapper<SimpleFeature, OpenDataAddressNode> {
    private final AddressNodeStatusTransform addressNodeStatusTransform = new AddressNodeStatusTransform();
    private final PdokAddressMapper addressMapper = new PdokAddressMapper();

    @Override
    public OpenDataAddressNode map(SimpleFeature feature) {
        String id = feature.getID();
        BigDecimal buildingUnitId = (BigDecimal) feature.getAttribute("identificatie");
        Geometry geometry = (Geometry) feature.getAttribute("geometrie");
        String status = (String) feature.getAttribute("status");
        Address address = addressMapper.map(feature);

        OpenDataAddressNode addressNode = new OpenDataAddressNode();
        addressNode.setPrimaryId(id);
        addressNode.setReferenceId(buildingUnitId);
        addressNode.setGeometry(geometry);
        addressNode.setStatus(addressNodeStatusTransform.apply(status));
        addressNode.setAddress(address);
        return addressNode;
    }

}

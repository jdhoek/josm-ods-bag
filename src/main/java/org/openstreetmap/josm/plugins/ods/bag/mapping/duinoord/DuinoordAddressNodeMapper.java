package org.openstreetmap.josm.plugins.ods.bag.mapping.duinoord;

import java.math.BigDecimal;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.AddressNodeStatusTransform;
import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.properties.EntityMapper;

import com.vividsolutions.jts.geom.Geometry;

public class DuinoordAddressNodeMapper implements EntityMapper<SimpleFeature, OpenDataAddressNode> {
    private final AddressNodeStatusTransform addressNodeStatusTransform = new AddressNodeStatusTransform();
    private final DuinoordAddressMapper addressMapper = new DuinoordAddressMapper();

    @Override
    public OpenDataAddressNode map(SimpleFeature feature) {
        String id = feature.getID();
        BigDecimal buildingUnitId = (BigDecimal) feature.getAttribute("identificatie");
        Geometry geometry = (Geometry) feature.getAttribute("geopunt");
        String status = (String) feature.getAttribute("pandstatus");
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

package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddressNode;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.geotools.AttributeMapper;
import org.openstreetmap.josm.plugins.ods.geotools.FeatureMapper;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagGtAddressNodeBuilder extends BagGtEntityBuilder<AddressNode, BagAddressNode> {
    private AttributeMapper<Integer> houseNumberMapper;
    private AttributeMapper<Character> houseLetterMapper;
    private AttributeMapper<String> houseNumberExtraMapper;
    private AttributeMapper<String> streetNameMapper;
    private AttributeMapper<String> cityNameMapper;
    private AttributeMapper<String> postCodeMapper;
    private AttributeMapper<String> gebruiksdoelMapper;
    private AttributeMapper<String> statusMapper;
    private AttributeMapper<Double> areaMapper;
    private AttributeMapper<Long> buildingRefMapper;

    
    public BagGtAddressNodeBuilder(CRSUtil crsUtil, FeatureMapper featureMapper) {
        super(crsUtil);
        houseNumberMapper = featureMapper.getAttributeMapper("houseNumber", Integer.class);
        houseLetterMapper = featureMapper.getAttributeMapper("houseLetter", Character.class);
        houseNumberExtraMapper = featureMapper.getAttributeMapper("houseNumberExtra", String.class);
        streetNameMapper = featureMapper.getAttributeMapper("streetName", String.class);
        cityNameMapper = featureMapper.getAttributeMapper("cityName", String.class);
        postCodeMapper = featureMapper.getAttributeMapper("postCode", String.class);
        gebruiksdoelMapper = featureMapper.getAttributeMapper("gebruiksdoel", String.class);
        statusMapper = featureMapper.getAttributeMapper("statusName", String.class);
        areaMapper = featureMapper.getAttributeMapper("area", Double.class);
        buildingRefMapper = featureMapper.getAttributeMapper("buildingRef", Long.class);
    }

    @Override
    protected BagAddressNode newInstance() {
        return new BagAddressNode();
    }

    @Override
    public BagAddressNode build(SimpleFeature feature, DownloadResponse response) {
        BagAddressNode addressNode = super.build(feature, response);
        BagAddress address = new BagAddress();
        address.setHouseNumber(houseNumberMapper.get(feature));
        address.setHouseLetter(houseLetterMapper.get(feature));
        address.setHouseNumberExtra(houseNumberExtraMapper.get(feature));
        address.setStreetName(streetNameMapper.get(feature));
        address.setCityName(cityNameMapper.get(feature));
        address.setPostcode(postCodeMapper.get(feature));
        addressNode.setAddress(address);
        addressNode.setStatus(parseStatus(statusMapper.get(feature)));
        addressNode.setGebruiksdoel(gebruiksdoelMapper.get(feature));
        addressNode.setArea(areaMapper.get(feature));
        addressNode.setBuildingRef(buildingRefMapper.get(feature));
        return addressNode;
    }

    private static EntityStatus parseStatus(String status) {
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

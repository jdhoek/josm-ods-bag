package org.openstreetmap.josm.plugins.ods.bag.mapping.pdok_wfs;

import java.math.BigDecimal;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuildingUnit;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.AddressNodeStatusTransform;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BuildingTypeTransform;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuildingUnit;
import org.openstreetmap.josm.plugins.ods.properties.EntityMapper;

public class BuildingUnitMapper implements EntityMapper<SimpleFeature, OpenDataBuildingUnit> {
    private final BuildingTypeTransform buildingTypeTransform = new BuildingTypeTransform();
    private final AddressNodeStatusTransform addressNodeStatusTransform = new AddressNodeStatusTransform();

    @Override
    public BagBuildingUnit map(SimpleFeature feature) {
        String id = feature.getID();
        BigDecimal buildingUnitId = (BigDecimal) feature.getAttribute("identificatie");
        String purpose = (String) feature.getAttribute("gebruiksdoel");
        Double area = (Double) feature.getAttribute("oppervlakte");
        String status = (String) feature.getAttribute("status");
        BigDecimal buildingId = (BigDecimal) feature.getAttribute("pandidentificatie");

        BagBuildingUnit housingUnit = new BagBuildingUnit();
        housingUnit.setPrimaryId(id);
        housingUnit.setReferenceId(buildingUnitId);
        housingUnit.setType(buildingTypeTransform.apply(purpose));
        housingUnit.setArea(area);
        housingUnit.setStatus(addressNodeStatusTransform.apply(status));
        housingUnit.setBuildingRef(buildingId);
        housingUnit.setSource("BAG");
        return housingUnit;
    }

}

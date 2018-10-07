package org.openstreetmap.josm.plugins.ods.bag.mapping.duinoord;

import java.math.BigDecimal;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.PandStatusTransform;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.properties.EntityMapper;

import com.vividsolutions.jts.geom.Geometry;

public class DuinoordBuildingMapper implements EntityMapper<SimpleFeature, OpenDataBuilding> {
    private final PandStatusTransform pandStatusTransform = new PandStatusTransform();

    @Override
    public BagBuilding map(SimpleFeature source) {
        String id = source.getID();
        BigDecimal buildingId = (BigDecimal) source.getAttribute("identificatie");
        String status = (String) source.getAttribute("pandstatus");
        BigDecimal constructionYear = (BigDecimal) source.getAttribute("bouwjaar");
        Geometry geometry = (Geometry) source.getAttribute("geovlak");

        BagBuilding building = new BagBuilding();
        building.setPrimaryId(id);
        building.setBuildingId(buildingId.longValueExact());
        building.setStatus(pandStatusTransform.apply(status));
        building.setConstructionYear(constructionYear.longValue());
        building.setGeometry(geometry);
        building.setSource("BAG");
        return building;
    }

}

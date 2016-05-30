package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagCity;
import org.openstreetmap.josm.plugins.ods.entities.actual.City;
import org.openstreetmap.josm.plugins.ods.entities.osm.AbstractOsmEntityBuilder;
import org.openstreetmap.josm.tools.I18n;

public class BagOsmCityBuilder extends AbstractOsmEntityBuilder<City> {
    
    public BagOsmCityBuilder(OdsModule module) {
        super(module, City.class);
    }

    @Override
    public Class<City> getEntityClass() {
        return City.class;
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (primitive.getType() == OsmPrimitiveType.RELATION &&
              "administrative".equals(primitive.get("boundary")) &&
              "8".equals(primitive.get("admin_level"))) {       
            normalizeKeys(primitive);
            BagCity city = new BagCity();
            Map<String, String> tags = primitive.getKeys();
            parseKeys(city, tags);
            city.setOtherTags(tags);
//            getEntityStore().add(city);
        }
        return;
    }
    
    public static void normalizeKeys(OsmPrimitive primitive) {
        if ("multipolygon".equals(primitive.get("type"))) {
            primitive.put("type", "boundary");
        }
    }
    
    private static void parseKeys(BagCity city, Map<String, String> tags) {
        tags.remove("boundary");
        tags.remove("admin_level");
        tags.remove("type");
        city.setName(tags.remove("name"));
        String id = tags.get("ref:woonplaatscode");
        try {
            if (id == null) {
                Main.warn(I18n.tr("The city reference code is missing for " +
                    " city '%s'.", city.getName()));
                
            }
            city.setIdentificatie(Long.parseLong(id));
            tags.remove("ref:gemeentecode");
        }
        catch (@SuppressWarnings("unused") NumberFormatException e) {
            Main.warn(I18n.tr("'%s' is not a valid value for a city reference code" +
                " (%s).", id, city.getName()));
            // Do nothing, but keep the invalid tag
        }
    }    
}

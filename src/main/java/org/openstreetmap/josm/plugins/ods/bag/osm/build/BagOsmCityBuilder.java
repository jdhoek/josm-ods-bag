package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.actual.City;
import org.openstreetmap.josm.plugins.ods.osm.build.AbstractOsmCityBuilder;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.tools.I18n;

public class BagOsmCityBuilder extends AbstractOsmCityBuilder {
    @SuppressWarnings("hiding")
    private final static Set<String> PARSED_KEYS = buildParsedKeys();
    
    public BagOsmCityBuilder(OdsModule module) {
        super(module);
    }

//    @Override
//    public Class<City> getEntityClass() {
//        return City.class;
//    }


    @Override
    public Set<String> getParsedKeys() {
        return PARSED_KEYS;
    }

    @Override
    public void parseKeys(City city, Map<String, String> tags) {
        super.parseKeys(city, tags);
        String id = tags.get("ref:woonplaatscode");
        try {
            if (id == null) {
                Main.warn(I18n.tr("The city reference code is missing for " +
                    " city '%s'.", city.getName()));
                
            }
            city.setReferenceId(Long.parseLong(id));
        }
        catch (NumberFormatException e) {
            Main.warn(I18n.tr("'%s' is not a valid value for a city reference code" +
                " (%s).", id, city.getName()));
        }
    }

    @Override
    protected Object parseReferenceId(Map<String, String> tags) {
        return BagOsmEntityBuilder.getReferenceId(tags.remove("ref:bag"));
    }

    @Override
    protected void normalizeTags(ManagedPrimitive primitive) {
        return;
    }
    
    private static Set<String> buildParsedKeys() {
        Set<String> keys = new HashSet<>(AbstractOsmCityBuilder.PARSED_KEYS);
        keys.add("ref:woonplaatscode");
        return keys;
    }
}

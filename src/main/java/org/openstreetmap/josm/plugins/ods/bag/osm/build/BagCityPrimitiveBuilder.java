package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.time.LocalDate;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.entities.actual.City;

public class BagCityPrimitiveBuilder extends BagEntityPrimitiveBuilder<City> {

    public BagCityPrimitiveBuilder(LayerManager dataLayer) {
        super(dataLayer, City.class);
    }

    @Override
    protected void buildTags(City city, Map<String, String> tags) {
        tags.put("source", "BAG");
        LocalDate date = city.getSourceDate();
        if (date != null) {
            tags.put("source:date", date.toString());
        }
        tags.put("boundary",  "administrative");
        tags.put("admin_level", "10");
        tags.put("type", "boundary");
        tags.put("name", city.getName());
        tags.put("ref:woonplaatscode", city.getReferenceId().toString());
    }
}

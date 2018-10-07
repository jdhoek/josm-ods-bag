package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.time.LocalDate;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.places.OpenDataCity;
import org.openstreetmap.josm.plugins.ods.entities.EntityDao;
import org.openstreetmap.josm.plugins.ods.util.OdsTagMap;

public class BagCityPrimitiveBuilder extends BagEntityPrimitiveBuilder<OpenDataCity> {


    public BagCityPrimitiveBuilder(OdsModule module, EntityDao<OpenDataCity> dao) {
        super(module, dao);
    }

    @Override
    protected void buildTags(OpenDataCity city, OdsTagMap tags) {
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

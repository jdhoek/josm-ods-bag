package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.osm.build.AbstractOsmBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

// TODO add code to update the entity when the primitive (tags) has been modified
public class BagOsmBuildingBuilder extends AbstractOsmBuildingBuilder {
    @SuppressWarnings("hiding")
    private static Set<String> PARSED_KEYS = buildParsedKeys();
    
    public BagOsmBuildingBuilder(OdsModule module) {
        super(module);
    }

    @Override
    public Set<String> getParsedKeys() {
        return PARSED_KEYS;
    }

    @Override
    protected void normalizeTags(ManagedPrimitive<?> primitive) {
        return;
    }

    @Override
    protected Long parseReferenceId(Map<String, String> tags) {
        return BagOsmEntityBuilder.getReferenceId(tags.get("ref:bag"));
    }
    
    private static Set<String> buildParsedKeys() {
        Set<String> keys = new HashSet<>(AbstractOsmBuildingBuilder.PARSED_KEYS);
        keys.add("ref:bag");
        return keys;
    }
}

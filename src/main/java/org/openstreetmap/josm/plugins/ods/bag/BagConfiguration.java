package org.openstreetmap.josm.plugins.ods.bag;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.AbstractModuleConfiguration;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagAddressNodeEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagBuildingEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNodeEntityType;
import org.openstreetmap.josm.plugins.ods.domains.addresses.processing.Osm_Building_AddressNode_RelationManager;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingEntityType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingUnitEntityType;
import org.openstreetmap.josm.plugins.ods.entities.EntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.processing.OsmEntityRelationManager;

public class BagConfiguration extends AbstractModuleConfiguration {
    //    private final BagEntityMapperFactory entityMapperFactory;
    //    private final DuinoordEntityMapperFactory duinoordEntityMapperFactory;
    private static List<? extends EntityType> entityTypes = Arrays.<EntityType>asList(
            BuildingEntityType.INSTANCE,
            AddressNodeEntityType.INSTANCE,
            BuildingUnitEntityType.INSTANCE);

    public BagConfiguration() {
        //        WFSHost bagWfsHost = new WFSHost("BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs?request=getCapabilities&VERSION=2.0.0", 1000, 60000, 60000);
        //        // The Duinoord WFS doesn't support paging, because there is an issue with the primary index
        //        WFSHost duinoordWfsHost = new WFSHost("DUINOORD WFS", "https://duinoord.xs4all.nl/geoserver/wfs?request=getCapabilities&VERSION=2.0.0", -1, 60000, 60000);
        //        //      hosts.put(demolishedBuildingsHost.getName(), demolishedBuildingsHost);
        //        GtFeatureSource vboFeatureSource = new GtFeatureSource(bagWfsHost, "bag:verblijfsobject", "identificatie");
        //        GtFeatureSource pandFeatureSource = new GtFeatureSource(bagWfsHost, "bag:pand", "identificatie");
        //        GtFeatureSource ligplaatsFeatureSource = new GtFeatureSource(bagWfsHost, "bag:ligplaats", "identificatie");
        //        GtFeatureSource standplaatsFeatureSource = new GtFeatureSource(bagWfsHost, "bag:standplaats", "identificatie");
        //        GtFeatureSource missingAddressFS = new GtFeatureSource(duinoordWfsHost, "bag:Address_Missing", "nummeraanduiding");
        //        GtFeatureSource deletedBuildingFS = new GtFeatureSource(duinoordWfsHost, "bag:Building_Destroyed", "identificatie");
        //        addFeatureSource(vboFeatureSource);
        //        addFeatureSource(pandFeatureSource);
        //        addFeatureSource(ligplaatsFeatureSource);
        //        addFeatureSource(standplaatsFeatureSource);
        //        addFeatureSource(missingAddressFS);
        //        addFeatureSource(deletedBuildingFS);
        //
        //        entityMapperFactory = new BagEntityMapperFactory(bagWfsHost);
        //        duinoordEntityMapperFactory = new DuinoordEntityMapperFactory(duinoordWfsHost);
        //        addDataSource(createVboDataSource(vboFeatureSource));
        //        addDataSource(createPandDataSource(pandFeatureSource));
        //        addDataSource(createLigplaatsDataSource(ligplaatsFeatureSource));
        //        addDataSource(createStandplaatsDataSource(standplaatsFeatureSource));
        //        addDataSource(createMissingAddressDataSource(missingAddressFS));
        //        addDataSource(createDeletedBuildingDataSource(deletedBuildingFS));
    }

    @Override
    public Collection<Class<? extends EntityPrimitiveBuilder<?>>> getPrimitiveBuilders() {
        return Arrays.asList(
                BagBuildingEntityPrimitiveBuilder.class,
                BagAddressNodeEntityPrimitiveBuilder.class);
    }

    @Override
    public Collection<? extends EntityType> getEntityTypes() {
        return entityTypes;
    }


    @Override
    public List<Class<? extends OsmEntityBuilder>> getOsmEntityBuilders() {
        return Arrays.asList(
                BagOsmBuildingBuilder.class,
                BagOsmAddressNodeBuilder.class);
    }

    @Override
    public List<Class<? extends OsmEntityRelationManager>> getOsmRelationManagers() {
        return Arrays.asList(Osm_Building_AddressNode_RelationManager.class);
    }

    //    private OdsDataSource createVboDataSource(GtFeatureSource featureSource) {
    //        GtDatasourceBuilder builder = new GtDatasourceBuilder()
    //                .setFeatureSource(featureSource)
    //                .setProperties("identificatie", "oppervlakte", "status", "gebruiksdoel",
    //                        "openbare_ruimte", "huisnummer", "huisletter", "toevoeging", "postcode", "woonplaats",
    //                        "geometrie", "pandidentificatie")
    //                .setUniqueKey("identificatie")
    //                .setEntityMapperFactory(entityMapperFactory);
    //        return builder.build();
    //    }
    //
    //    private OdsDataSource createPandDataSource(GtFeatureSource featureSource) {
    //        GtDatasourceBuilder builder = new GtDatasourceBuilder()
    //                .setFeatureSource(featureSource)
    //                .setProperties("identificatie", "bouwjaar", "status", "geometrie")
    //                .setUniqueKey("identificatie")
    //                .setEntityMapperFactory(entityMapperFactory);
    //        return builder.build();
    //    }
    //
    //    private OdsDataSource createLigplaatsDataSource(GtFeatureSource featureSource) {
    //        GtDatasourceBuilder builder = new GtDatasourceBuilder()
    //                .setFeatureSource(featureSource)
    //                .setProperties("identificatie", "status", "openbare_ruimte", "huisnummer",
    //                        "huisletter", "toevoeging", "postcode", "woonplaats", "geometrie")
    //                .setUniqueKey("identificatie")
    //                .setEntityMapperFactory(entityMapperFactory);
    //        return builder.build();
    //    }
    //
    //    private OdsDataSource createStandplaatsDataSource(GtFeatureSource featureSource) {
    //        GtDatasourceBuilder builder = new GtDatasourceBuilder()
    //                .setFeatureSource(featureSource)
    //                .setProperties("identificatie", "status", "openbare_ruimte", "huisnummer",
    //                        "huisletter", "toevoeging", "postcode", "woonplaats", "geometrie")
    //                .setUniqueKey("identificatie")
    //                .setEntityMapperFactory(entityMapperFactory);
    //        return builder.build();
    //    }
    //
    //    private OdsDataSource createMissingAddressDataSource(GtFeatureSource featureSource) {
    //        GtDatasourceBuilder builder = new GtDatasourceBuilder()
    //                .setFeatureSource(featureSource)
    //                .setProperties("nummeraanduiding", "postcode", "huisnummer", "huisnummertoevoeging",
    //                        "huisletter", "straat", "huisnummer", "woonplaats", "pandidentificatie", "geopunt")
    //                .setUniqueKey("nummeraanduiding")
    //                .setEntityMapperFactory(duinoordEntityMapperFactory);
    //        return builder.build();
    //    }
    //
    //    private OdsDataSource createDeletedBuildingDataSource(GtFeatureSource featureSource) {
    //        GtDatasourceBuilder builder = new GtDatasourceBuilder()
    //                .setFeatureSource(featureSource)
    //                .setProperties("identificatie", "pandstatus", "bouwjaar", "geovlak")
    //                .setUniqueKey("identificatie")
    //                .setEntityMapperFactory(duinoordEntityMapperFactory);
    //        return builder.build();
    //    }
}

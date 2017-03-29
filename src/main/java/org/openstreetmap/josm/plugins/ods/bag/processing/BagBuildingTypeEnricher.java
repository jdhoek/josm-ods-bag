package org.openstreetmap.josm.plugins.ods.bag.processing;

import static org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagBuildingEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.HousingUnit;
import org.openstreetmap.josm.plugins.ods.io.OdsProcessor;

public class BagBuildingTypeEnricher implements OdsProcessor {
    private final static List<String> trafo =
            Arrays.asList("TRAF","TRAN","TRFO","TRNS");
    private final static List<String> garage =
            Arrays.asList("GAR","GRG");
    private final OdsModule module = OdsProcessor.getModule();
    
    public BagBuildingTypeEnricher() {
        super();
    }

    @Override
    public void run() {
        LayerManager layerManager = module.getOpenDataLayerManager();
        for (Building building : layerManager.getRepository().getAll(Building.class)) {
            updateType(building);
        }
    }

    public void updateType(Building building) {
        if (BuildingType.HOUSEBOAT.equals(building.getBuildingType()) ||
                BuildingType.STATIC_CARAVAN.equals(building.getBuildingType())) {
            return;
        }
        BuildingType type = BuildingType.UNCLASSIFIED;
        switch (building.getHousingUnits().size()) {
        case 0:
            break;
        case 1:
            type = getBuildingType(building.getHousingUnits().get(0));
            break;
        default:
            type = getBuildingType(building.getHousingUnits());
        }
        building.setBuildingType(type);
        BagBuildingEntityPrimitiveBuilder.updateBuildingTypeTags(building);
    }

    private BuildingType getBuildingType(List<HousingUnit> housingUnits) {
        Statistics stats = new Statistics();
        for (HousingUnit housingUnit : housingUnits) {
            BuildingType type = getBuildingType(housingUnit);
            stats.add(type, housingUnit.getArea());
        }
        Statistics.Stat largest = stats.getLargest();
        BuildingType type = BuildingType.UNCLASSIFIED;
        if (largest.percentage > 0.75) {
            if (largest.type == BuildingType.HOUSE) {
                type = APARTMENTS;
            }
            else if (largest.type == PRISON ||
                largest.type == RETAIL ||
                largest.type == OFFICE) {
                type = largest.type;
            }
            else {
                type = BuildingType.UNCLASSIFIED;
            }
        }
        return type;
    }

    private static BuildingType getBuildingType(HousingUnit housingUnit) {
        BuildingType type = housingUnit.getType();
        // Unclassified housing units with 1 address may be a garage or a substation
        if (type == BuildingType.UNCLASSIFIED && housingUnit.getAddressNodes().size() == 1) {
            AddressNode mainNode = housingUnit.getMainAddressNode();
            String extra = mainNode.getAddress().getHouseNumberExtra();
            if (extra != null) {
                extra = extra.toUpperCase();
                if (trafo.contains(extra)) {
                    type = BuildingType.SUBSTATION;
                }
                else if (garage.contains(extra)) {
                    type = BuildingType.GARAGE;
                }
            }
        }
        return type;
    }

    class Statistics {
        private Map<BuildingType, Row> rows = new HashMap<>();
        private double totalArea = 0.0;
        
        public void add(BuildingType type, double area) {
            Row row = rows.get(type);
            if (row == null) {
                row = new Row();
                rows.put(type,  row);
            }
            row.add(area);
            totalArea += area;
        }
        
        public Stat getLargest() {
            Stat stat = new Stat();
            for (Entry<BuildingType, Row> entry : rows.entrySet()) {
                Row row = entry.getValue();
                if (row.area > stat.area) {
                    stat.type = entry.getKey();
                    stat.area = row.area;
                    stat.count = row.count; 
                }
            }
            stat.percentage = stat.area/totalArea;
            return stat;
        }
        
        class Stat {
            BuildingType type;
            int count = 0;
            double area = 0.0;
            double percentage = 0.0;
        }
        
        class Row {
            int count = 0;
            double area = 0;
            
            public void add(double a) {
                this.area += a;
                this.count++;
            }
        }
    }
}

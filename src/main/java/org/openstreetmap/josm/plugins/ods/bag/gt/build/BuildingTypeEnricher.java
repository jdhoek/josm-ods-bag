package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import static org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.bag.gt.build.BuildingTypeEnricher.Statistics.Stat;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.HousingUnit;

public class BuildingTypeEnricher implements Consumer<Building> {
    private final static List<String> trafo =
            Arrays.asList("TRAF","TRAN","TRFO","TRNS");
    private final static List<String> garage =
            Arrays.asList("GAR","GRG");
    
    public BuildingTypeEnricher() {
        super();
    }

    @Override
    public void accept(Building building) {
        if (BuildingType.HOUSEBOAT.equals(building.getBuildingType()) ||
                BuildingType.STATIC_CARAVAN.equals(building.getBuildingType())) {
            return;
        }
        BuildingType type = BuildingType.UNCLASSIFIED;
        if (building.getHousingUnits().size() == 1) {
            type = getBuildingType(building.getHousingUnits().get(0));
        }
        else {
            type = getBuildingType(building.getHousingUnits());
        }
        building.setBuildingType(type);
    }

    private BuildingType getBuildingType(List<HousingUnit> housingUnits) {
        Statistics stats = new Statistics();
        for (HousingUnit housingUnit : housingUnits) {
            BuildingType type = getBuildingType(housingUnit);
            stats.add(type, housingUnit.getArea());
        }
        Stat largest = stats.getLargest();
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
        AddressNode mainNode = housingUnit.getMainAddressNode();
        String extra = mainNode.getAddress().getHouseNumberExtra();
        if (extra != null) {
            extra = extra.toUpperCase();
            if (trafo.contains(extra)) {
                return BuildingType.SUBSTATION;
            }
            else if (garage.contains(extra)) {
                return BuildingType.GARAGE;
            }
        }
        return housingUnit.getType();
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

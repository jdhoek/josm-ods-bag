package org.openstreetmap.josm.plugins.ods.bag.processing;

import static org.openstreetmap.josm.plugins.ods.domains.buildings.TypeOfBuilding.APARTMENTS;
import static org.openstreetmap.josm.plugins.ods.domains.buildings.TypeOfBuilding.OFFICE;
import static org.openstreetmap.josm.plugins.ods.domains.buildings.TypeOfBuilding.PRISON;
import static org.openstreetmap.josm.plugins.ods.domains.buildings.TypeOfBuilding.RETAIL;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagBuildingEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.TypeOfBuilding;
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
        module.getRepository().getAll(OpenDataBuilding.class)
        .forEach(this::updateType);
    }

    public void updateType(OpenDataBuilding building) {
        if (TypeOfBuilding.HOUSEBOAT.equals(building.getBuildingType()) ||
                TypeOfBuilding.STATIC_CARAVAN.equals(building.getBuildingType())) {
            return;
        }
        TypeOfBuilding type = TypeOfBuilding.UNCLASSIFIED;
        switch (building.getBuildingUnits().size()) {
        case 0:
            break;
        case 1:
            type = getBuildingType(building.getBuildingUnits().iterator().next());
            break;
        default:
            type = getBuildingType(building.getBuildingUnits());
        }
        building.setBuildingType(type);
        BagBuildingEntityPrimitiveBuilder.updateBuildingTypeTags(building);
    }

    private TypeOfBuilding getBuildingType(Set<BuildingUnit> buildingUnits) {
        Statistics stats = new Statistics();
        for (BuildingUnit buildingUnit : buildingUnits) {
            TypeOfBuilding type = getBuildingType(buildingUnit);
            stats.add(type, buildingUnit.getArea());
        }
        Statistics.Stat largest = stats.getLargest();
        TypeOfBuilding type = TypeOfBuilding.UNCLASSIFIED;
        if (largest.percentage > 0.75) {
            if (largest.type == TypeOfBuilding.HOUSE) {
                type = APARTMENTS;
            }
            else if (largest.type == PRISON ||
                    largest.type == RETAIL ||
                    largest.type == OFFICE) {
                type = largest.type;
            }
            else {
                type = TypeOfBuilding.UNCLASSIFIED;
            }
        }
        return type;
    }

    private static TypeOfBuilding getBuildingType(BuildingUnit buildingUnit) {
        TypeOfBuilding type = buildingUnit.getType();
        // Unclassified building units with 1 address may be a garage or a substation
        if (type == TypeOfBuilding.UNCLASSIFIED && buildingUnit.getAddressNodes().size() == 1) {
            AddressNode mainNode = buildingUnit.getMainAddressNode();
            String extra = mainNode.getAddress().getHouseNumberExtra();
            if (extra != null) {
                extra = extra.toUpperCase();
                if (trafo.contains(extra)) {
                    type = TypeOfBuilding.SUBSTATION;
                }
                else if (garage.contains(extra)) {
                    type = TypeOfBuilding.GARAGE;
                }
            }
        }
        return type;
    }

    class Statistics {
        private final Map<TypeOfBuilding, Row> rows = new HashMap<>();
        private double totalArea = 0.0;

        public void add(TypeOfBuilding type, double area) {
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
            for (Entry<TypeOfBuilding, Row> entry : rows.entrySet()) {
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
            TypeOfBuilding type;
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

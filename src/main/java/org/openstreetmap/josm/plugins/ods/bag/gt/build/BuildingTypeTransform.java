package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.openstreetmap.josm.plugins.ods.domains.buildings.TypeOfBuilding;
import org.openstreetmap.josm.plugins.ods.properties.transform.SimpleTypeTransform;

public class BuildingTypeTransform extends SimpleTypeTransform<String, TypeOfBuilding> {

    public BuildingTypeTransform() {
        super(String.class, TypeOfBuilding.class, null);
    }

    @Override
    public TypeOfBuilding apply(String type) {
        switch (type.toLowerCase()) {
        case "woonfunctie":
            return TypeOfBuilding.HOUSE;
        case "overige gebruiksfunctie":
            return TypeOfBuilding.UNCLASSIFIED;
        case "industriefunctie":
            return TypeOfBuilding.INDUSTRIAL;
        case "winkelfunctie":
            return TypeOfBuilding.RETAIL;
        case "kantoorfunctie":
            return TypeOfBuilding.OFFICE;
        case "celfunctie":
            return TypeOfBuilding.PRISON;
        default:
            return TypeOfBuilding.UNCLASSIFIED;
        }
    }
}

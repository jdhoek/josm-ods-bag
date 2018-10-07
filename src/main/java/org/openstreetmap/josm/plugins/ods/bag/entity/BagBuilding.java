package org.openstreetmap.josm.plugins.ods.bag.entity;

import java.time.Year;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.entities.StartDate;

public class BagBuilding extends OpenDataBuilding {
    private Long aantalVerblijfsobjecten;
    private Long buildingId;
    private Long constructionYear;

    public void setAantalVerblijfsobjecten(Long aantalVerblijfsobjecten) {
        this.aantalVerblijfsobjecten = aantalVerblijfsobjecten;
    }

    public Long getAantal_verblijfsobjecten() {
        return aantalVerblijfsobjecten;
    }

    public void setBuildingId(Long id) {
        this.buildingId = id;
    }

    @Override
    public Object getReferenceId() {
        return buildingId;
    }

    public Long getConstructionYear() {
        return constructionYear;
    }

    public void setConstructionYear(Long constructionYear) {
        this.constructionYear = constructionYear;
        setStartDate(new StartDate(Year.of(this.constructionYear.intValue())));
    }
}

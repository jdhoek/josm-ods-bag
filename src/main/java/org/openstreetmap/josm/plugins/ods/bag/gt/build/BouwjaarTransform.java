package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import java.math.BigDecimal;
import java.time.Year;
import java.util.function.Function;

import org.openstreetmap.josm.plugins.ods.entities.StartDate;
import org.openstreetmap.josm.plugins.ods.properties.transform.TypeTransform;

class BouwjaarTransform implements TypeTransform<BigDecimal, StartDate> {
    public BouwjaarTransform() {
        super();
    }

    @Override
    public Class<BigDecimal> getSourceType() {
        return BigDecimal.class;
    }

    @Override
    public Class<StartDate> getTargetType() {
        return StartDate.class;
    }

    @Override
    public Function<BigDecimal, StartDate> getFunction() {
        return null;
    }

    @Override
    public StartDate apply(BigDecimal bouwjaar) {
        if (bouwjaar == null) {
            return null;
        }
        return new StartDate(Year.of(bouwjaar.intValue()));
    }
}
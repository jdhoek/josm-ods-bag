package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.entities.opendata.GeotoolsEntityBuilder;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public abstract class BagGtEntityBuilder<T extends Entity, T2 extends T> implements GeotoolsEntityBuilder<T> {
    private final CRSUtil crsUtil;
    
    public BagGtEntityBuilder(CRSUtil crsUtil) {
        super();
        this.crsUtil = crsUtil;
    }

    @Override
    public Object getReferenceId(SimpleFeature feature) {
        return FeatureUtil.getLong(feature, "identificatie");
    }

    @Override
    public T2 build(SimpleFeature feature, DownloadResponse response) {
        T2 entity = newInstance();
        entity.setDownloadResponse(response);
        entity.setReferenceId(getReferenceId(feature));
        entity.setPrimaryId(feature.getID());
        LocalDate date = response.getRequest().getDownloadTime().toLocalDate();
        if (date != null) {
            entity.setSourceDate(DateTimeFormatter.ISO_LOCAL_DATE.format(date));
        }
        entity.setSource("BAG");
        try {
            entity.setGeometry(crsUtil.transform(feature));
        } catch (CRSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return entity;
    }

//    protected abstract T2 newInstance();
}

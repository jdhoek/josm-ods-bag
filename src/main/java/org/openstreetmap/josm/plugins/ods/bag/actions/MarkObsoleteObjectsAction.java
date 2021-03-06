package org.openstreetmap.josm.plugins.ods.bag.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsWorkingSet;
import org.openstreetmap.josm.plugins.ods.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.builtenvironment.BuiltEnvironment;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.tools.I18n;
import org.xml.sax.SAXException;

public class MarkObsoleteObjectsAction extends JosmAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    
    public MarkObsoleteObjectsAction() {
        super(I18n.tr("Mark obsolete objects"), null, I18n.tr("Mark obsolete objects"), null, false);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        OdsWorkingSet workingSet =ODS.getModule().getWorkingSet();
        if (workingSet.getInternalDataLayer() == null) {
            new Notification(I18n.tr("The OSM datalayer is missing")).show();
            return;
        }
        if (workingSet.getExternalDataLayer() == null) {
            new Notification(I18n.tr("The BAG datalayer is missing")).show();
            return;
        }
        Main.worker.execute(new Task());
    }

    class Task extends PleaseWaitRunnable {

        public Task() {
            super(I18n.tr("Please wait"));
        }

        @Override
        protected void cancel() {
            // TODO Auto-generated method stub
            
        }

        @Override
        protected void realRun() throws SAXException, IOException,
                OsmTransferException {
            OdsWorkingSet workingSet = ODS.getModule().getWorkingSet();
            EntitySet entitySet = workingSet.getInternalDataLayer().getEntitySet();
            BuiltEnvironment be = new BuiltEnvironment(entitySet);
            Iterator<Building> it = be.getBuildings().iterator();
            while (it.hasNext()) {
                Building building = it.next();
                // TODO implement this
//                OsmPrimitive primitive = building.getPrimitive();
//                if (primitive != null) {
//                    if ("3dshapes".equals(building.getSource()) && building.getOtherTags().isEmpty()) {
//                         primitive.put("ods:obsolete", "yes");
//                     }
//                     else {
//                         primitive.put("bag", "conversie");
//                     }
//                }
            }
        }

        @Override
        protected void finish() {
            // TODO Auto-generated method stub
            
        }
        
    }
}

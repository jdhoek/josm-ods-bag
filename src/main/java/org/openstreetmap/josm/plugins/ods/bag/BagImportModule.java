package org.openstreetmap.josm.plugins.ods.bag;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.UserInfo;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.OsmServerUserInfoReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.ModuleActivationException;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OdsModuleConfiguration;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtilProj4j;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.actions.BuildingPassageAction;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.gui.AlignBuildingAction;
import org.openstreetmap.josm.plugins.ods.gui.OdsDownloadAction;
import org.openstreetmap.josm.plugins.ods.gui.OdsResetAction;
import org.openstreetmap.josm.plugins.ods.gui.OdsStatisticsAction;
import org.openstreetmap.josm.plugins.ods.gui.OdsUpdateAction;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.io.OsmLayerDownloader;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.storage.GeoRepository;
import org.openstreetmap.josm.plugins.ods.update.EntityUpdater;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

public class BagImportModule extends OdsModule {
    private final OdsModuleConfiguration configuration;
    // Boundary of the Netherlands
    private final static Bounds BOUNDS = new Bounds(50.734, 3.206, 53.583, 7.245);
    private final MainDownloader mainDownloader;
    private final GeoUtil geoUtil = new GeoUtil();
    private final CRSUtil crsUtil = new CRSUtilProj4j();
    private List<EntityUpdater> entityUpdaters;

    public BagImportModule() {
        this.configuration = new BagConfiguration();
        this.mainDownloader = createMainDownloader();
    }

    private MainDownloader createMainDownloader() {
        MainDownloader downloader = new MainDownloader(this);
        downloader.setOpenDataLayerDownloader(new BagWfsLayerDownloader(this));
        downloader.setOsmLayerDownloader(new OsmLayerDownloader(this));
        return downloader;
    }

    @Override
    public OdsModuleConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void initialize() throws OdsException {
        super.initialize();

        addAction(new OdsDownloadAction(this));
        //        addAction(new RemoveAssociatedStreetsAction(this));
        //        addAction(new OdsImportAction(this));
        addAction(new OdsUpdateAction(this));
        addAction(new BuildingPassageAction(this));
        //        addAction(new UpdateGeometryAction(this));
        addAction(new AlignBuildingAction(this));
        addAction(new OdsResetAction(this));
        String debug = System.getenv("ods-debug");
        if ("true".equals(debug)) {
            addAction(new OdsStatisticsAction(this));
        }
    }

    @Override
    protected OsmLayerManager createOsmLayerManager() {
        OsmLayerManager manager = new OsmLayerManager(this, "BAG OSM");
        GeoRepository repository = getRepository();
        repository.register(OpenDataBuilding.class, "primaryId");
        repository.addIndex(OpenDataBuilding.class, "referenceId");
        repository.addGeoIndex(OpenDataBuilding.class, "geometry");
        repository.register(OpenDataAddressNode.class, "primaryId");
        return manager;
    }

    @Override
    protected OpenDataLayerManager createOpenDataLayerManager() {
        OpenDataLayerManager manager = new OpenDataLayerManager("BAG ODS");
        GeoRepository repository = getRepository();
        repository.register(Building.class, "primaryId");
        repository.addIndex(Building.class, "referenceId");
        repository.addGeoIndex(Building.class, "geometry");
        repository.register(BuildingUnit.class, "primaryId");
        repository.register(AddressNode.class, "primaryId");
        return manager;
    }

    @Override
    public String getName() {
        return "BAG";
    }


    @Override
    public String getDescription() {
        return I18n.tr("ODS module to import buildings and addresses in the Netherlands");
    }

    @Override
    public GeoUtil getGeoUtil() {
        return geoUtil;
    }

    @Override
    public CRSUtil getCrsUtil() {
        return crsUtil;
    }

    @Override
    public Bounds getBounds() {
        return BOUNDS;
    }

    @Override
    public MainDownloader getDownloader() {
        return mainDownloader;
    }

    @Override
    public boolean usePolygonFile() {
        return true;
    }

    @SuppressWarnings("unused")
    @Override
    public void activate() throws ModuleActivationException {
        if (false && !checkUser()) { // Disabled, but kept the code in case we need it
            int answer = JOptionPane.showConfirmDialog(Main.parent,
                    "Je gebruikersnaam eindigt niet op _BAG en is daarom niet geschikt " +
                            "voor de BAG import.\nWeet je zeker dat je door wilt gaan?",
                            I18n.tr("Invalid user"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (answer == JOptionPane.CANCEL_OPTION) {
                throw ModuleActivationException.CANCELLED;
            }
        }
        super.activate();
    }

    @Override
    public Double getTolerance() {
        return 1e-5;
    }

    @Override
    public List<EntityUpdater> getUpdaters() {
        if (entityUpdaters == null) {
            entityUpdaters = new ArrayList<>(1);
            //            entityUpdaters.add(new BuildingUpdater(this));
        }
        return entityUpdaters;
    }

    private static boolean checkUser() {
        try {
            final UserInfo userInfo = new OsmServerUserInfoReader().fetchUserInfo(NullProgressMonitor.INSTANCE);
            String user = userInfo.getDisplayName();
            String suffix = "_BAG";
            return user.endsWith(suffix);
        } catch (OsmTransferException e1) {
            Logging.warn(tr("Failed to retrieve OSM user details from the server."));
            return false;
        }
    }
}

package ch.epfl.javions;

import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.gui.*;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public class Main extends Application {
    private static final String PATH_DATABASE_AIRCRAFT = "/aircraft.zip";
    private static final int INITIAL_ZOOM = 8;
    private static final int INITIAL_X_WEB_MERCATOR = 33_530;
    private static final int INITIAL_Y_WEB_MERCATOR = 23_070;
    private static final String TILE_SERVER = "tile.openstreetmap.org";
    private static final String PATH_TILE_CACHE = "tile-cache";
    public static void main(String[] args) {launch(args);}
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = buildInterface();
        scene.show()
    }
    private Scene buildInterface() throws Exception {

        //creation baseMapController
        Path tileCache = Path.of(PATH_TILE_CACHE);
        TileManager tileManager =
                new TileManager(tileCache, TILE_SERVER);
        MapParameters mapParameters =
                new MapParameters(INITIAL_ZOOM, INITIAL_X_WEB_MERCATOR, INITIAL_Y_WEB_MERCATOR);
        BaseMapController baseMapController = new BaseMapController(tileManager, mapParameters);

        // Creation of the DB
        URL u = getClass().getResource(PATH_DATABASE_AIRCRAFT);
        assert u != null;
        Path p = Path.of(u.toURI());
        AircraftDatabase db = new AircraftDatabase(p.toString());

        AircraftStateManager asm = new AircraftStateManager(db);
        ObjectProperty<ObservableAircraftState> sap =
                new SimpleObjectProperty<>();

        ChangeListener<GeoPos> centerOnSap = (o, ov, nv)->{
            baseMapController.centerOn(nv);
        };

        AircraftController ac = new AircraftController(mapParameters, asm.states(), sap);

        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        //TODO c'est un peu crado
        atc.pane().getChildren().get(0).setOnMouseClicked((mouseEvent) -> {
            if (mouseEvent.getClickCount() >= 2 && mouseEvent.getButton() == MouseButton.PRIMARY){
                atc.setOnDoubleClick(sap::set);
                baseMapController.centerOn(sap.get().getPosition());
            }
        });
        var map = new StackPane(baseMapController.pane(), ac.pane());
        var root = new SplitPane(map, atc.pane());
        root.setOrientation(Orientation.VERTICAL);
        return new Scene(root);
    }
}

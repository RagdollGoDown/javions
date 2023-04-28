package ch.epfl.javions;

import ch.epfl.javions.gui.BaseMapController;
import ch.epfl.javions.gui.MapParameters;
import ch.epfl.javions.gui.TileManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class JavionApp extends Application {
        public static void main(String[] args) {
            launch(args);
        }

        @Override
        public void start(Stage primaryStage) throws Exception{
            start_BaseMap(primaryStage);
        }

        public void start_BaseMap(Stage primaryStage) throws Exception {
            Path tileCache = Path.of("tile-cache");

            TileManager tm =
                    new TileManager(tileCache, "tile.openstreetmap.org");
            MapParameters mp =
                    new MapParameters(17, 17_389_327, 11_867_430);
            BaseMapController bmc = new BaseMapController(tm, mp);

            BorderPane root = new BorderPane(bmc.pane());
            primaryStage.setScene(new Scene(root));
            primaryStage.setWidth(256);
            primaryStage.setHeight(256);
            primaryStage.show();
        }
}


package ch.epfl.javions;

import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.gui.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class JavionApp extends Application {
        public static void main(String[] args) {
            launch(args);
        }

        @Override
        public void start(Stage primaryStage) throws Exception{
            start_AircraftController(primaryStage);
        }

        public void start_BaseMap(Stage primaryStage) {
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

        static List<RawMessage> readAllMessages(String fileName)
                throws IOException {
            List<RawMessage> rawMessages = new ArrayList<>();
            try (DataInputStream s = new DataInputStream(
                    new BufferedInputStream(
                            new FileInputStream(fileName)))){
                byte[] bytes = new byte[RawMessage.LENGTH];
                while (true) {
                    long timeStampNs = s.readLong();
                    int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                    if (bytesRead != RawMessage.LENGTH) return rawMessages;
                    rawMessages.add(RawMessage.of(timeStampNs,bytes));
                }
            } catch (EOFException e) {
                System.out.println("oups");
            }
            return rawMessages;
        }

        public void start_AircraftController(Stage primaryStage) throws Exception {
            Path tileCache = Path.of("tile-cache");
            TileManager tm =
                    new TileManager(tileCache, "tile.openstreetmap.org");
            MapParameters mp =
                    new MapParameters(17, 17_389_327, 11_867_430);
            // … à compléter (voir TestBaseMapController)
            BaseMapController bmc = new BaseMapController(tm, mp);

            // Création de la base de données
            URL dbUrl = getClass().getResource("/aircraft.zip");
            assert dbUrl != null;
            String f = Path.of(dbUrl.toURI()).toString();
            var db = new AircraftDatabase(f);

            AircraftStateManager asm = new AircraftStateManager();
            ObjectProperty<ObservableAircraftState> sap =
                    new SimpleObjectProperty<>();

            ChangeListener<GeoPos> centerOnSap = (o,ov,nv)->{
                bmc.centerOn(nv);
            };

            AircraftController ac = new AircraftController(mp, asm.states(), sap);

            AircraftTableController atc = new AircraftTableController(asm.states(), sap);

            atc.pane().setOnMouseClicked((mouseEvent) -> {
                if (mouseEvent.getClickCount() >= 2 && mouseEvent.getButton() == MouseButton.PRIMARY){
                    atc.setOnDoubleClick(sap::set);
                    bmc.centerOn(sap.get().getPosition());
                }
            });

            StatusLineController slc = new StatusLineController();
            slc.aircraftCountProperty().bind(Bindings.size(asm.states()));

            var map = new StackPane(bmc.pane(), ac.pane());
            var tableAndSlit = new BorderPane();
            tableAndSlit.bottomProperty().set(atc.pane());
            tableAndSlit.topProperty().set(slc.pane());

            var root = new SplitPane(map, tableAndSlit);
            root.setOrientation(Orientation.VERTICAL);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            var mi = readAllMessages("resources/messages_20230318_0915.bin")
                    .iterator();

            // Animation des aéronefs
            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    for (int i = 0; i < 10; i += 1) {
                        Message m = MessageParser.parse(mi.next());
                        if (m != null) asm.updateWithMessage(m);
                        asm.purge();
                    }
                }
            }.start();
    }
}


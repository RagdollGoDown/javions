package ch.epfl.javions;

import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import ch.epfl.javions.gui.*;
import javafx.animation.AnimationTimer;
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

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;

public final class Main extends Application {
    private static final String PATH_DATABASE_AIRCRAFT = "/aircraft.zip";
    private static final int INITIAL_ZOOM = 8;
    private static final int INITIAL_X_WEB_MERCATOR = 33_530;
    private static final int INITIAL_Y_WEB_MERCATOR = 23_070;
    private static final String TILE_SERVER = "tile.openstreetmap.org";
    private static final String PATH_TILE_CACHE = "tile-cache";
    private static final long PURGE_TIMER_NS = 1_000_000_000;


    // And From your main() method or any other method


    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        final ConcurrentLinkedQueue<RawMessage>  messages = new ConcurrentLinkedQueue<>();
        // Creation of the DB
        URL u = getClass().getResource(PATH_DATABASE_AIRCRAFT);
        assert u != null;
        Path p = Path.of(u.toURI());
        AircraftDatabase db = new AircraftDatabase(p.toString());
        //creation of the state manager
        AircraftStateManager asm = new AircraftStateManager(db);
        //creation of the scene
        Scene scene = buildInterface(asm);
        primaryStage.setScene(scene);
        primaryStage.show();
        //receive messages
        List<String> parameters = getParameters().getRaw();
        if (parameters.isEmpty()){
            Thread thread = new Thread(() -> {
                try {
                    fetchMessagesSystemIn(messages);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.setDaemon(true);
            thread.start();
        } else if (parameters.size() == 1) {
            if (!Files.exists(Path.of(parameters.get(0)))){
                System.out.println("Impossible to find file " + parameters.get(0) + " in " + System.getProperty("user.dir") );
                System.exit(0);
            }
            Thread thread = new Thread(() -> fetchMessagesFromFile(parameters.get(0), messages) );
            thread.setDaemon(true);
            thread.start();
        }else{
            System.out.println("To many arguments given");
            System.exit(0);
        }
        //animation timer
        new AnimationTimer() {
            private static long lasPurge;
            @Override
            public void handle(long now) {

                for (int i = 0; i < 10; i += 1) {
                    if (messages.isEmpty()) return;
                    Message m = MessageParser.parse(messages.remove());
                    if (m != null) asm.updateWithMessage(m);
                    if (now - lasPurge >= PURGE_TIMER_NS){
                        asm.purge();
                        lasPurge = now;
                    }

                }
                    /*
                    try {
                        for (int i = 0; i < 10; i += 1) {
                            Message m = MessageParser.parse(mi.next());
                            if (m != null) asm.updateWithMessage(m);
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }

                     */
            }
        }.start();


    }
    private Scene buildInterface(AircraftStateManager asm) throws Exception {

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

        ObjectProperty<ObservableAircraftState> sap =
                new SimpleObjectProperty<>();

        //center when plane is selected
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
        //TODO do the borderPane with the stateline and the table

        // creation of the scene

        var map = new StackPane(baseMapController.pane(), ac.pane());
        var root = new SplitPane(map, atc.pane());
        root.setOrientation(Orientation.VERTICAL);
        return new Scene(root);
    }

    private void fetchMessagesFromFile(String pathFile, ConcurrentLinkedQueue<RawMessage> messages){
        long timeBegin = nanoTime();
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(pathFile)))){
            byte[] bytes = new byte[RawMessage.LENGTH];
            long timeStampNs = s.readLong();
            int bytesRead = s.readNBytes(bytes, 0, bytes.length);
            while (bytesRead == RawMessage.LENGTH) {
                messages.add(RawMessage.of(timeStampNs,bytes));
                long currentTime = nanoTime();
                long sleepTime = (timeStampNs - (currentTime - timeBegin))/1000000;
                sleep((sleepTime > 0) ?  sleepTime:0);
                timeStampNs = s.readLong();
                bytesRead = s.readNBytes(bytes, 0, bytes.length);
            }
        } catch (EOFException e) {
            //TODO enlever cette saletée d'oups
            System.out.println("oups");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void fetchMessagesSystemIn(ConcurrentLinkedQueue<RawMessage> messages) throws IOException {
        AdsbDemodulator adsbDemodulator = new AdsbDemodulator(System.in);
        while (true){
            RawMessage message = adsbDemodulator.nextMessage();
            if(Objects.nonNull(message)) messages.add(message);
        }

    }

}

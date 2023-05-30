package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;

/**
 * The javion application, display aircraft on a map and there information
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public final class Main extends Application {
    private static final String PATH_DATABASE_AIRCRAFT = "/aircraft.zip";
    private static final String APP_NAME = "Javions";
    private static final double STAGE_MIN_WIDTH = 800d;
    private static final double STAGE_MIN_HEIGHT = 600d;
    private static final int INITIAL_ZOOM = 8;
    private static final int INITIAL_X_WEB_MERCATOR = 33_530;
    private static final int INITIAL_Y_WEB_MERCATOR = 23_070;
    private static final String TILE_SERVER = "tile.openstreetmap.org";
    private static final String PATH_TILE_CACHE = "tile-cache";
    private static final long PURGE_TIMER_NS = 1_000_000_000;
    private static final long NS_TO_MS = 1_000_000;

    /**
     * Launch the application
     * @param args the arguments from the command line, from the file given or from the std.in if nothing is given
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The main entry point for Javion application.
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception exceptions that occurred during the process
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        final ConcurrentLinkedQueue<RawMessage> messages = new ConcurrentLinkedQueue<>();
        // Creation of the DB
        URL u = getClass().getResource(PATH_DATABASE_AIRCRAFT);
        assert u != null;
        Path p = Path.of(u.toURI());
        AircraftDatabase db = new AircraftDatabase(p.toString());

        //creation of the state manager
        AircraftStateManager asm = new AircraftStateManager(db);

        //creation of the scene
        StatusLineController slc = new StatusLineController();
        slc.aircraftCountProperty().bind(Bindings.size(asm.states()));

        Scene scene = buildInterface(asm, slc);
        primaryStage.setTitle(APP_NAME);
        primaryStage.setMinHeight(STAGE_MIN_HEIGHT);
        primaryStage.setMinWidth(STAGE_MIN_WIDTH);
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
            private static long lastPurge;
            @Override
            public void handle(long now) {

                for (int i = 0; i < 10; i += 1) {
                    if (messages.isEmpty()) return;
                    Message m = MessageParser.parse(messages.remove());
                    if (m != null) {
                        asm.updateWithMessage(m);
                        slc.messageCountProperty().set(slc.messageCountProperty().get() + 1);
                    }
                    if (now - lastPurge >= PURGE_TIMER_NS){
                        asm.purge();
                        lastPurge = now;
                    }

                }
            }
        }.start();
    }

    /**
     * Build the interface for the application
     * @param asm instance of AircraftStateManager
     * @param slc instance of StatusLineController
     * @return the scene to display
     */
    private Scene buildInterface(AircraftStateManager asm, StatusLineController slc) {

        //creation baseMapController
        Path tileCache = Path.of(PATH_TILE_CACHE);
        TileManager tileManager =
                new TileManager(tileCache, TILE_SERVER);
        MapParameters mapParameters =
                new MapParameters(INITIAL_ZOOM, INITIAL_X_WEB_MERCATOR, INITIAL_Y_WEB_MERCATOR);
        BaseMapController bmc = new BaseMapController(tileManager, mapParameters);

        ObjectProperty<ObservableAircraftState> sap =
                new SimpleObjectProperty<>();

        AircraftController ac = new AircraftController(mapParameters, asm.states(), sap);

        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        atc.setOnDoubleClick((mouseEvent) -> bmc.centerOn(sap.get().getPosition()));

        // creation of the scene
        var map = new StackPane(bmc.pane(), ac.pane());
        var tableAndSlit = new BorderPane(atc.pane());
        tableAndSlit.setTop(slc.pane());

        var root = new SplitPane(map, tableAndSlit);
        root.setOrientation(Orientation.VERTICAL);
        return new Scene(root);
    }

    /**
     * Fetch messages from a file
     * @param pathFile the path of the file
     * @param messages the ConcurrentLinkedQueue where the messages have to be stored
     */
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
                long sleepTime = (timeStampNs - (currentTime - timeBegin))/NS_TO_MS;
                sleep((sleepTime > 0) ?  sleepTime:0);
                timeStampNs = s.readLong();
                bytesRead = s.readNBytes(bytes, 0, bytes.length);
            }
        } catch (EOFException e) {
            // should be a log
            System.out.println("End of file reached");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetch messages from std.in and load them in the queue
     * @param messages the ConcurrentLinkedQueue where the messages have to be stored
     * @throws IOException if there are eny problems reading the stream
     */
    private void fetchMessagesSystemIn(ConcurrentLinkedQueue<RawMessage> messages) throws IOException {
        AdsbDemodulator adsbDemodulator = new AdsbDemodulator(System.in);
        while (true){
            RawMessage message = adsbDemodulator.nextMessage();
            if(Objects.nonNull(message)) messages.add(message);
        }

    }

}

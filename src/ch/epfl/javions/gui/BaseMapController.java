package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.EventType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Handles user input and transmits it to the mapParameters
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public class BaseMapController {
    private final static int SIZE_TILE = 256;
    private final static int N_TILES_PRELOADED_BORDER = 5;
    private TileManager tileManager;
    private MapParameters mapParameters;

    private Canvas canvasMap;
    private Pane paneMap;
    private  boolean redrawNeeded;
    private MouseEvent previousMouseEvent;

    public BaseMapController(TileManager tileManager, MapParameters mapParameters){
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        this.canvasMap = new Canvas();
        this.paneMap = new Pane(canvasMap);
        this.redrawNeeded = false;

        canvasMap.widthProperty().bind(paneMap.widthProperty());
        canvasMap.heightProperty().bind(paneMap.heightProperty());
        canvasMap.widthProperty().addListener(((observable, oldValue, newValue) -> redrawOnNextPulse()));
        canvasMap.heightProperty().addListener(((observable, oldValue, newValue) -> redrawOnNextPulse()));

        canvasMap.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        setupInteractionMousePane();




    }
    private void setupInteractionMousePane(){
        LongProperty minScrollTime = new SimpleLongProperty();

        //mouse on the canvas
        paneMap.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);

            mapParameters.changePosition(e.getX(), e.getY());
            mapParameters.changeZoomLevel(zoomDelta);
            mapParameters.changePosition(-e.getX(), -e.getY());
            redrawOnNextPulse();
        });

        paneMap.setOnMousePressed(e -> {
            previousMouseEvent = e;
        });
        paneMap.setOnMouseDragged(e -> {
            mapParameters.changePosition(
                    previousMouseEvent.getX() - e.getX() ,
                    previousMouseEvent.getY() - e.getY()
            );

            previousMouseEvent = e;
            redrawOnNextPulse();
        });
        paneMap.setOnMouseReleased(e -> {

        });
    }

    /**
     * gets the pane used for the map
     * @return the pane
     */
    public Pane pane(){
        return paneMap;
    }

    /**
     * centers the map onto a position
     * @param pos the position to be centered
     */
    public void centerOn(GeoPos pos){
        double x = WebMercator.x(mapParameters.getZoom(), pos.longitude()) - canvasMap.getWidth()/2;
        double y = WebMercator.y(mapParameters.getZoom(), pos.latitude()) - canvasMap.getHeight()/2;
        mapParameters.changePosition(x,y);
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        drawCanvas();
    }

    private void handleMouse(MouseEvent mouseEvent){
        System.out.println(mouseEvent);
        //mapParameters.changePosition(previousMouseEvent.getX() - mouseEvent.getX(), 0);

    }

    private void drawCanvas(){
        GraphicsContext graphicsContext = canvasMap.getGraphicsContext2D();
        graphicsContext.clearRect(0,0, canvasMap.getWidth(), canvasMap.getHeight());
        //-1 to preload borders
        for (int tileX = -1; tileX <= Math.ceil(canvasMap.getWidth()/SIZE_TILE); tileX++) {
            for (int tileY = -1; tileY <= Math.ceil(canvasMap.getHeight()/SIZE_TILE); tileY++) {
                Image image = tileManager.imageForTileAt(new TileManager.TileId(
                        mapParameters.getZoom(),
                        (int)(mapParameters.getMinX()/SIZE_TILE + tileX),
                        (int)(mapParameters.getMinY()/SIZE_TILE + tileY)));
                if (image != null){
                    graphicsContext.drawImage(image,
                            SIZE_TILE*tileX - (int)(mapParameters.getMinX()) % SIZE_TILE,
                            SIZE_TILE*tileY - (int)(mapParameters.getMinY()) % SIZE_TILE);

                }

            }
        }
    }
}

package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.EventType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;


public class BaseMapController {
    private final static int SIZE_TILE = 256;
    private TileManager tileManager;
    private MapParameters mapParameters;

    private Canvas canvasMap;
    private Pane paneMap;
    private  boolean redrawNeeded;
    private MouseEvent previousMouseEvent;

    public BaseMapController(TileManager tileManager, MapParameters mapParameters){

        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        canvasMap = new Canvas();

        paneMap = new Pane(canvasMap);
        canvasMap.widthProperty().bind(paneMap.widthProperty());
        canvasMap.heightProperty().bind(paneMap.heightProperty());

        canvasMap.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        LongProperty minScrollTime = new SimpleLongProperty();
        paneMap.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);

            double[] coordinatesTmp = {e.getX(), e.getY()};
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
    public Pane pane(){
        return paneMap;
    }
    public void centerOn(GeoPos pos){

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
        //-1 to precharge borders
        for (int tileX = -1; tileX <= 1 + Math.ceil(canvasMap.getWidth()/SIZE_TILE); tileX++) {
            for (int tileY = -1; tileY <= Math.ceil(canvasMap.getHeight()/SIZE_TILE); tileY++) {
                Image image = tileManager.imageForTileAt(new TileManager.TileId(
                        mapParameters.getZoom(),
                        (int)(mapParameters.getMinX()/SIZE_TILE + tileX),
                        (int)(mapParameters.getMinY()/SIZE_TILE + tileY)));
                if (image != null){
                    canvasMap.getGraphicsContext2D().drawImage(image,
                            SIZE_TILE*tileX - (int)(mapParameters.getMinX()) % SIZE_TILE,
                            SIZE_TILE*tileY - (int)(mapParameters.getMinY()) % SIZE_TILE);
                }

            }
        }
    }
}

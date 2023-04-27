package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

public class BaseMapController {
    private TileManager tileManager;
    private MapParameters mapParameters;

    private Canvas canvasMap;
    private Pane paneMap;

    private boolean redrawNeeded;
    public BaseMapController(TileManager tileManager, MapParameters mapParameters){
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        canvasMap = new Canvas();
        paneMap = new Pane(canvasMap);
        canvasMap.widthProperty().bind(paneMap.widthProperty());

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

            mapParameters.changeZoomLevel(e.getDeltaY());
        });

        paneMap.setOnMouseDragged(e -> {
            mapParameters.changePosition(e.getX(), e.getY());
        });
    }
    public Pane pane(){
        return null;
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

        drawTiles();
    }

    private void drawTiles(){

    }


}

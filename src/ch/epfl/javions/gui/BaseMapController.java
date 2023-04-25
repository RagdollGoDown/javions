package ch.epfl.javions.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

public class BaseMapController {
    private TileManager tileManager;
    private MapParameters mapParameters;

    private Canvas canvasMap;
    private Pane paneMap;
    public BaseMapController(TileManager tileManager, MapParameters mapParameters){
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        canvasMap = new Canvas();
        paneMap = new Pane(canvasMap);
        canvasMap.widthProperty().bind(paneMap.widthProperty());
    }
    public Pane pane(){
        return null;
    }
    public void centerOn(int x, int y){

    }
}

package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

/**
 * Is used to save and modify the zoom and position of the user in the map
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public final class MapParameters{
    private final static int MAX_ZOOM_INCLUDED = 19;
    private final static int MIN_ZOOM_INCLUDED = 6;

    private IntegerProperty zoom;
    private DoubleProperty minX;
    private DoubleProperty minY;

    /**
     *
     * @param zoom value between 6 and 19
     * @param minX (web Mercator)
     * @param minY (web Mercator)
     */
    public MapParameters(int zoom, double minX, double minY){
        Preconditions.checkArgument(MIN_ZOOM_INCLUDED<=zoom && zoom <= MAX_ZOOM_INCLUDED);
        Preconditions.checkArgument(minX>=0 && minY>=0);

        this.zoom = new SimpleIntegerProperty(zoom);
        this.minX = new SimpleDoubleProperty(minX);
        this.minY = new SimpleDoubleProperty(minY);

        this.zoom.addListener((o, oV, nV) -> {
            int z = nV.intValue()-oV.intValue();
            
            this.minX.set(
                    z < 0 ? this.minX.get() / (1<<Math.abs(z)) : this.minX.get() * (1<<z));
            this.minY.set(
                    z < 0 ? this.minY.get() / (1<<Math.abs(z)) : this.minY.get() * (1<<z));
        });
    }

    /**
     * changes the position in the map by adding the change to the new position
     * @param x the change in the abscissa
     * @param y the change in the ordinate
     */
    public void changePosition(double x, double y){
        //TODO trouver ssolution pour add qui marche pas
        minX.set(minX.get() + x);
        minY.set(minY.get() + y);

    }

    /**
     * changes the zoom level depending on the input z
     * also checks if the new zoom level is in the correct barriers
     * @param z the change in the zoom level
     */
    public void changeZoomLevel(int z){

        int newZoom = Math2.clamp(MIN_ZOOM_INCLUDED, zoom.get() + z, MAX_ZOOM_INCLUDED);

        if (newZoom == zoom.get()){return;}
        else {zoom.set(newZoom);}

    }

    //------------------------getters

    /**
     * gets the zoomProperty as readonly
     * @return the zoom property
     */
    public ReadOnlyIntegerProperty zoomProperty(){
        return zoom;
    }

    /**
     * gets the value of the zoom property
     * @return the value
     */
    public int getZoom(){return zoom.get();}

    /**
     * gets the minX as readonly
     * @return the minX property
     */
    public ReadOnlyDoubleProperty minXProperty(){
        return minX;
    }

    /**
     * gets the value of the minX property
     * @return the value
     */
    public double getMinX(){return minX.get();}

    /**
     * gets the minY as readonly
     * @return the minY property
     */
    public ReadOnlyDoubleProperty minYProperty(){
        return minY;
    }

    /**
     * gets the value of the minY property
     * @return the value
     */
    public double getMinY(){return minY.get();}
}

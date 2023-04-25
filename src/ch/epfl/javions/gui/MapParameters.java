package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

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
    }

    /**
     * changes the position in the map by adding the change to the new position
     * @param x the change in the abscissa
     * @param y the change in the ordinate
     */
    public void scroll(double x, double y){
        //TODO corriger translation
        minX.add(x);
        minY.add(y);
    }

    /**
     * changes the zoom level depending on the input z
     * also checks if the new zoom level is in the correct barriers
     * @param z the change in the zoom level
     */
    public void changeZoomLevel(int z){
        zoom.set(Math2.clamp(MIN_ZOOM_INCLUDED, zoom.get() + z, MAX_ZOOM_INCLUDED));
    }

    //------------------------getters

    public ReadOnlyIntegerProperty zoomProperty(){
        return zoom;
    }

    public int getZoom(){return zoom.get();}

    public ReadOnlyDoubleProperty minXProperty(){
        return minX;
    }

    public double getMinX(){return minX.get();}

    public ReadOnlyDoubleProperty minYProperty(){
        return minY;
    }

    public double getMinY(){return minY.get();}
}

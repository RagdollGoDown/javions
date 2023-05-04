package ch.epfl.javions.gui;

import ch.epfl.javions.WebMercator;

public final class ControllerUtils {
    private ControllerUtils() {}

    /**
     * Calculate the position of an element on the map regarding his longitude and the new origin
     * @param zoom the zoom of the origin
     * @param positionOrigin the coordinate in x of the origin
     * @param positionElement the longitude of the element
     * @return The x coordinate for the gui
     */
    public static double LongitudeToGui(int zoom, double positionOrigin, double positionElement){
        return WebMercator.x(zoom, positionElement) - positionOrigin;
    }

    /**
     * Calculate the position of an element on the map regarding his latitude and the new origin
     * @param zoom the zoom of the origin
     * @param positionOrigin the coordinate in x of the origin
     * @param positionElement the longitude of the element
     * @return The y coordinate for the gui
     */
    public static double LatitudeToGui(int zoom, double positionOrigin, double positionElement){
        return WebMercator.y(zoom, positionElement) - positionOrigin;
    }

}

package ch.epfl.javions.gui;

import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.AircraftData;

/**
 * Contains common function used by controllers
 */
public final class ControllerUtils {

    private final static int MAX_ALTITUDE_METERS = 12000;

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

    /**
     * Calculates the correct value to use for the color ramp
     * @param altitude the unchanged altitude of the aircraft in meters
     * @return the changed value
     */
    public static double correctAltitudeForColorRamp(double altitude){
        return Math.cbrt(altitude/MAX_ALTITUDE_METERS);
    }

    /**
     * finds which is available in the following order registration, type designation, Icao address
     * @param aircraft the aircraft from which we are retrieving this information
     * @return the first available
     */
    public static String findCorrectLabelTitle(ObservableAircraftState aircraft){
        AircraftData data = aircraft.aircraftData();
        if (data == null) return aircraft.address().string();

        if (data.registration() != null) return data.registration().string();
        if (data.typeDesignator() != null) return data.typeDesignator().string();
        return aircraft.address().string();
    }
}

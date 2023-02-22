package ch.epfl.javions;

public final class WebMercator {

    /**
     * uses the formula from section 2.5 to calculate
     * the horizontal position in the map
     * @param zoomLevel the level at which we have zoomed in on the map
     * @param longitude the longitude of the position
     * @return the calculated value
     */
    public static double x(int zoomLevel, double longitude){
        //TODO demander si ça va de faire comme ça et non la méthode avec les tours
        return Math.scalb(longitude / (2 * Math.PI) + 0.5,8+zoomLevel);
    }

    /**
     * uses the formula from section 2.5 to calculate
     * the vertical position in the map
     * @param zoomLevel the level at which we have zoomed in on the map
     * @param latitude the latitude of the position
     * @return the calculated value
     */
    public static double y(int zoomLevel, double latitude){
        double tanOfLatitude = Math.tan(latitude);
        return Math.scalb(Math2.asinh(tanOfLatitude) / (2 * Math.PI) + 0.5,8+zoomLevel);
    }
}

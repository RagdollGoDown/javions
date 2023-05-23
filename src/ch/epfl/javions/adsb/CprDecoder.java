package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

/**
 * Represents a CPR position decoder
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public final class CprDecoder {
    private static final int N_LATITUDE_EVEN = 60;
    private static final int N_LATITUDE_ODD = 59;

    private CprDecoder(){}

    /**
     * Find the 'A' value from the latitude
     * @param latitude  the latitude
     * @return the 'A' value
     */
    private static double aCalculation (double latitude){
        double latitudeRad = Units.convertFrom(latitude, Units.Angle.TURN);
        double A = Math.acos(1.0 - ((1.0 - (Math.cos((2.0*Math.PI)/N_LATITUDE_EVEN)))
                /(Math.cos(latitudeRad) * Math.cos(latitudeRad))));
        return A;
    }

    /**
     * Find the zLambdaEven from the latitude
     * @param latitude the latitude
     * @return the zLambdaEven
     */
    private static int zLambdaEven(double latitude){
        double A = aCalculation(latitude);
        
        if (Double.isNaN(A)){
            return 1;
        }else{
            return (int) Math.floor(2* (Math.PI) / A);
        }
    }

    /**
     * Returns the geographical position corresponding to the given normalized local positions
     * @param x0 the normalized local longitude of an even message
     * @param y0 the normalized local latitude of an even message
     * @param x1 the normalized local longitude of an odd message
     * @param y1 the normalized local latitude of an odd message
     * @param mostRecent 0 if the most recent message is the even one, 1 if it is the odd one
     * @return the decoded position in GeoPos
     *         null if the position is indeterminate or invalid
     * @throws IllegalArgumentException if the most recent parity isn't one or zero
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent){
        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);

        int zLat = (int) Math.rint((y0 * N_LATITUDE_ODD  - y1 * N_LATITUDE_EVEN));

        double latitude0 = (zLat + y0)/N_LATITUDE_EVEN;
        double latitude1 = (zLat + y1)/N_LATITUDE_ODD;

        int zonesLongitudeEven = zLambdaEven(latitude0);

        if (zonesLongitudeEven != zLambdaEven(latitude1)){
            return null;
        }
        int zonesLongitudeOdd = zonesLongitudeEven > 1 ? zonesLongitudeEven - 1 : 1;

        int zLong =  (int) Math.rint((x0 * zonesLongitudeOdd  - x1 * zonesLongitudeEven));

        double longitude0 = (zLong + x0)/zonesLongitudeEven;
        double longitude1 = (zLong + x1)/zonesLongitudeOdd;

        return mostRecent == 0 ? checkAndGetGeoPos(longitude0,latitude0) : checkAndGetGeoPos(longitude1, latitude1);
    }

    private static GeoPos checkAndGetGeoPos(double longitude, double latitude){
        int latT32 = (int) Math.rint(Units.convert(latitude, Units.Angle.TURN, Units.Angle.T32));
        if (!GeoPos.isValidLatitudeT32(latT32)) return null;

        return new GeoPos((int) Math.rint(Units.convert(longitude, Units.Angle.TURN, Units.Angle.T32)), latT32);
    }
}

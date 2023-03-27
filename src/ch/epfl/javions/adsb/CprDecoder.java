package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public final class CprDecoder {
    private static final int N_LATITUDE_EVEN = 60;
    private static final int N_LATITUDE_ODD = 59;
    private static final int N_BIT_POS = (1 << 17);

    private static double aCalculation (double latitude){
        double latitudeRad = Units.convertFrom(latitude, Units.Angle.TURN);
        double A = Math.acos(1.0 - ((1.0 - (Math.cos((2.0*Math.PI)/N_LATITUDE_EVEN)))
                /(Math.cos(latitudeRad) * Math.cos(latitudeRad))));
        return A;
    }
    private static int zLambdaEven(double latitude){
        double A = aCalculation(latitude);
        
        if (Double.isNaN(A)){
            return 1;
        }else{
            return (int) Math.floor(2* (Math.PI) / A);
        }
    }
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent){
        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);

        double zLat = Math.rint(Math.rint((y0 * N_LATITUDE_ODD  - y1 * N_LATITUDE_EVEN)));

        double latitude0 = (zLat + y0)/N_LATITUDE_EVEN;
        double latitude1 = (zLat + y1)/N_LATITUDE_ODD;

        int zonesLongitudeEven = zLambdaEven(latitude0);

        if (zonesLongitudeEven != zLambdaEven(latitude1)){
            return null;
        }
        int zonesLongitudeOdd = zonesLongitudeEven > 1 ? zonesLongitudeEven - 1 : 1;

        int zLong =  (int) Math.rint(Math.rint((x0 * zonesLongitudeOdd  - x1 * zonesLongitudeEven)));

        double longitude0 = (zLong + x0)/zonesLongitudeEven;
        double longitude1 = (zLong + x1)/zonesLongitudeOdd;

        try {
            if (mostRecent == 0){
                return new GeoPos((int) Units.convert(longitude0, Units.Angle.TURN, Units.Angle.T32), (int)Units.convert(latitude0, Units.Angle.TURN, Units.Angle.T32));
            }else{
                return new GeoPos((int) Units.convert(longitude1, Units.Angle.TURN, Units.Angle.T32), (int) Units.convert(latitude1, Units.Angle.TURN, Units.Angle.T32));
            }
        }
        catch (IllegalArgumentException e){
            return null;
        }
    }
}

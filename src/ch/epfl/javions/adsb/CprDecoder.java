package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;

public final class CprDecoder {
    private static final int N_LATITUDE_EVEN = 60;
    private static final int N_LATITUDE_ODD = 59;
    private static final int N_BIT_POS = (1 << 17);

    private static double zLambdaEven(double latitude){
        double A = Math.acos(1 - (1 - (Math.cos((2*Math.PI*1)/N_LATITUDE_EVEN))
                /(Math.cos(latitude) * Math.cos(latitude))));
        if (Double.isNaN(A)){
            return 1;
        }else{
            return Math.floor((2 * Math.PI) / (A*A));
        }
    }
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent){
        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);

        double z = Math.rint(Math.rint((y0 * N_LATITUDE_ODD  - y1 * N_LATITUDE_EVEN)));
        double latitude0 = (z + y0)/N_LATITUDE_EVEN;
        double latitude1 = (z + y1)/N_LATITUDE_ODD;

        double zonesLongitudeEven = zLambdaEven(latitude0);
        Preconditions.checkArgument(zonesLongitudeEven == zLambdaEven(latitude1));
        double zonesLongitudeOdd = zonesLongitudeEven - 1;
    }
}

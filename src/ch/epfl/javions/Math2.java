package ch.epfl.javions;

/**
 * Some mathematical operation
 */
public final class Math2 {

    private Math2(){}

    /**
     * Checks if the value v is confined in the interval [min,max]
     * and if it isn't the case then it will return the nearest border
     * @param min minimum possible value
     * @param v value to be clamped
     * @param max maximum value possible
     * @return the nearest border or v if it is in the interval
     * @throws IllegalArgumentException if the minimum is greater (strictly) than the max
     */
    public static int clamp(int min, int v, int max){
        Preconditions.checkArgument(min<=max);
        if (v <= min) return min;
        if (v >= max) return max;
        return v;
        // return Math.min(Math.max(v,min), max);
        // TODO choose the best method
    }

    /**
     * Calculate arsinh of a value
     * @param x
     * @return the arsinh
     */
    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1+x*x));
    }

}

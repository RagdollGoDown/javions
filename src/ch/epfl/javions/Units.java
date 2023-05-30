package ch.epfl.javions;

/**
 * Contains the units and their conversion rates
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public final class Units {
    /**
     * Represent 1e-2
     */
    public static final double CENTI = 1e-2;
    /**
     * Represent 1e+3
     */
    public static final double KILO = 1e+3;

    private Units(){}

    /**
     * Constants for angles
     */
    public static final class Angle {
        /**
         * Radian in terms of Radian
         */
        public static final double RADIAN = 1;
        /**
         * Turn in terms of Radian
         */
        public static final double TURN = 2 * Math.PI * RADIAN;
        /**
         * Degree in terms of Radian
         */
        public static final double DEGREE = TURN / 360;
        /**
         * T32 in terms of Radian
         */
        public static final double T32 = TURN / Math.scalb(1, 32);
    }
    /**
     * Constants for length
     */
    public static final class Length {
        /**
         * Meter in terms of meter
         */
        public static final double METER = 1;
        /**
         * Centimeter
         */
        public static final double CENTIMETER = CENTI * METER;
        /**
         *  Kilometer
         */
        public static final double KILOMETER = KILO * METER;
        /**
         * Inch in term of meter
         */
        public static final double INCH = CENTIMETER * 2.54;
        /**
         * Foot in terms of meter
         */
        public static final double FOOT = INCH * 12;
        /**
         * Nautical mile in terms of meter
         */
        public static final double NAUTICAL_MILE = METER * 1852;
    }
    /**
     * Constants for time
     */
    public static final class Time {
        /**
         * Second in terms of second
         */
        public static final double SECOND = 1;
        /**
         * Minute in terms of second
         */
        public static final double MINUTE = SECOND * 60;
        /**
         * Hour in terms of second
         */
        public static final double HOUR = MINUTE * 60;
    }

    /**
     * Constants for Speed
     */
    public static final class Speed {
        /**
         * meter per second
         */
        public static final double METER_PER_SECOND = Length.METER / Time.SECOND;
        /**
         * knot in meter per second
         */
        public static final double KNOT = Length.NAUTICAL_MILE / Time.HOUR;
        /**
         * kilometer in meter per second
         */
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER / Time.HOUR;
    }

    /**
     * Converts a value in a unit to a value in another
     * @param value the value to be converted
     * @param fromUnit the original unit of value
     * @param toUnit the final unit of value
     * @return value in the final unit
     */
    public static double convert(double value, double fromUnit, double toUnit) {
        return value * (fromUnit / toUnit);
    }

    /**
     * converts from any unit to a basic unit (Radians, Meters, Seconds)
     * @param value value to be converted
     * @param fromUnit the original unit of value
     * @return value in basic unit
     */
    public static double convertFrom(double value, double fromUnit) {
        return convert(value, fromUnit, 1);
    }

    /**
     * converts to any unit from a basic unit (Radians, Meters, Seconds)
     * @param value value to be converted
     * @param toUnit the final unit of value
     * @return value in the given unit
     */
    public static double convertTo(double value, double toUnit) {
        return convert(value, 1, toUnit);
    }
}
package ch.epfl.javions;

public final class Units {
    private Units(){}

    private static final double CENTI = 1e-2;
    private static final double KILO = 1e+3;

    public static final class Angle{
        public  static  final double RADIAN = 1;
        public static  final double TURN = 2 * Math.PI * RADIAN;
        public static final double DEGREE = TURN / 360;
        public static final double T32 = TURN / 0x1e8L; // Is it better to use the pow method or to use 0xFFFFFFFF?
    }

    public static final class Length{
        public static final double METER = 1;
        public static final double CENTIMETER = CENTI*METER;
        public static final double KILOMETER = KILO*METER;
        public static final double INCH = METER * 39.3701;
        public static final double FOOT = METER * 3.280841666667;
        public static final double NAUTICAL_MILE = METER / 1852;
    }
    
    public static final class Time{
        public static final double SECOND = 1;
        public static final double MINUTE = SECOND * 60;
        public static final double HOUR = MINUTE * 60;
    }

    public static final class Speed{
        public static final double METER_PER_SECOND = Length.METER / Time.SECOND;
        public static final double KNOT = Length.NAUTICAL_MILE / Time.HOUR;
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER / Time.HOUR;
    }

    public static double  convert(double value, double fromUnit, double toUnit){
        return value * toUnit / fromUnit;
    }
    public static double convertFrom(double value, double fromUnit){
        return convert(value, fromUnit, 1);
    }
    public static double convertTo(double value, double toUnit){
        return convert(value, 1 , toUnit);
    }

}

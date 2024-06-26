package ch.epfl.javions;

public record GeoPos(int longitudeT32, int latitudeT32)
{
    static final int POWER_2_WITH_30 = 1_073_741_824;
    public GeoPos{
        if (!isValidLatitudeT32(latitudeT32)) throw new IllegalArgumentException();
    }

    /**
     * Checks if the the latitude is between -2^30 (-90°) and 2^30 (90°)
     * @param latitudeT32 the latitude checked in T32 format
     * @return boolean
     */
    public static boolean isValidLatitudeT32(int latitudeT32){
        return -POWER_2_WITH_30 <= latitudeT32 && latitudeT32 <= POWER_2_WITH_30;
    }

    /**
     * @return longitude in radians
     */
    public double longitude(){
        return Units.convertFrom(this.longitudeT32, Units.Angle.T32);
    }

    /**
     * @return latitude in radians
     */
    public double latitude(){
            return Units.convertFrom(this.latitudeT32, Units.Angle.T32);
    }

    /**
     * @return longitude in degrees
     */
    public double longitudeDEGREE(){
        return Units.convert(this.longitudeT32, Units.Angle.T32, Units.Angle.DEGREE);
    }

    /**
     * @return latitude in degrees
     */
    public double latitudeDEGREE(){
        return Units.convert(this.latitudeT32, Units.Angle.T32, Units.Angle.DEGREE);
    }

    @Override
    public String toString() {
        return "(" + this.longitudeDEGREE() + "°, " + this.latitudeDEGREE() + "°)";
    }
}

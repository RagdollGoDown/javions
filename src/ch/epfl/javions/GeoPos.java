package ch.epfl.javions;

public record GeoPos(int longitudeT32, int latitudeT32)
{
    /**
     * Checks if the the latitude is between -2^30 (-90°) and 2^30 (90°)
     * @param latitudeT32 the latitude checked in T32 format
     * @return boolean
     */
    public static boolean isValidLatitudeT32(int latitudeT32){
        return Math.scalb(-1,30) <= latitudeT32 && latitudeT32 <= Math.scalb(1,30);
    }

    public double longitude(){
        return Units.convertFrom(this.longitudeT32, Units.Angle.T32);
    }

    public double latitude(){
            return Units.convertFrom(this.latitudeT32, Units.Angle.T32);
    }
    public double longitudeDEGREE(){
        return Units.convert(this.longitudeT32, Units.Angle.T32, Units.Angle.DEGREE);
    }
    public double latitudeDEGREE(){
        return Units.convert(this.latitudeT32, Units.Angle.T32, Units.Angle.DEGREE);
    }

    @Override
    public String toString() {
        return "(" + this.longitudeDEGREE() + "°, " + this.latitudeDEGREE() + "°)";
    }
}

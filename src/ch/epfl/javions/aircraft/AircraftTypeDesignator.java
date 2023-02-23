package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;
public class AircraftTypeDesignator extends AircraftString {
    public AircraftTypeDesignator(String string){
        super(string,"[A-Z0-9]{2,4}");
    }
}

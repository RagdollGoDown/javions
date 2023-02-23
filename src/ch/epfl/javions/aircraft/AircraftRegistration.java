package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public class AircraftRegistration extends AircraftString {
    public AircraftRegistration(String string){
        super(string,"[A-Z0-9 .?/_+-]+");
    }
}

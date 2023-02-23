package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public class AircraftDescription extends AircraftString{
    public AircraftDescription(String string){
        super(string,"[ABDGHLPRSTV-][0123468][EJPT-]");
    }
}

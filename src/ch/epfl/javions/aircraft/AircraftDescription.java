package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public class AircraftDescription extends ChaineContrainte {
    public AircraftDescription(String string){
        super(string,"[ABDGHLPRSTV-][0123468][EJPT-]", true);
    }
}

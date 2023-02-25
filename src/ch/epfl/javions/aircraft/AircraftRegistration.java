package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

import ch.epfl.javions.ChaineContrainte;

public class AircraftRegistration extends ChaineContrainte {
    public AircraftRegistration(String string){
        super(string,"[A-Z0-9 .?/_+-]+");
    }
}

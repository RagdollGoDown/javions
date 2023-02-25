package ch.epfl.javions.aircraft;

import ch.epfl.javions.ChaineContrainte;

public class AircraftTypeDesignator extends ChaineContrainte {
    public AircraftTypeDesignator(String string){
        super(string,"[A-Z0-9]{2,4}");
    }
}

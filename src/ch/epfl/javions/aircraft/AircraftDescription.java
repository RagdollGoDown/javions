package ch.epfl.javions.aircraft;

import ch.epfl.javions.ChaineContrainte;

public class AircraftDescription extends ChaineContrainte {
    public AircraftDescription(String string){
        super(string,"[ABDGHLPRSTV-][0123468][EJPT-]");
    }
}

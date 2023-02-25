package ch.epfl.javions.adsb;

import ch.epfl.javions.ChaineContrainte;

public class CallSign extends ChaineContrainte {
    public CallSign(String string){
        super(string,"[A-Z0-9 ]{0,8}");
    }
}

package ch.epfl.javions.aircraft;

import ch.epfl.javions.ChaineContrainte;

public class IcaoAddress extends ChaineContrainte {
    public IcaoAddress(String string) {
        super(string,"[0-9A-F]{6}");
    }
}


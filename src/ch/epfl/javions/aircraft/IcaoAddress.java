package ch.epfl.javions.aircraft;
import java.util.regex.Pattern;

public class IcaoAddress extends  AircraftString{
    public IcaoAddress(String string) {
        super(string,"[0-9A-F]{6}");
    }
}


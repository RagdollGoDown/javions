package ch.epfl.javions.aircraft;import java.util.regex.Pattern;

public class AircraftString {
    private String string;

    public AircraftString(String string, String regex){
        if (!Pattern.matches(regex, string)) throw new IllegalArgumentException();
        this.string = string;
    }

    public String toString() {
        return string;
    }
}

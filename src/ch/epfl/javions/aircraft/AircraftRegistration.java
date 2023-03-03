package ch.epfl.javions.aircraft;

import ch.epfl.javions.ChaineContrainte;

import java.util.regex.Pattern;

public record AircraftRegistration(String string) {
    private static final Pattern regex = Pattern.compile("[A-Z0-9 .?/_+-]+");
    public AircraftRegistration{
        Preconditions.checkArgument(regex.matcher(string).matches());
    }
}

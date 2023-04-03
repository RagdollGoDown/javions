package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 *
 * @param string
 */
public record AircraftRegistration(String string) {
    private static final Pattern regex = Pattern.compile("[A-Z0-9 .?/_+-]+");
    public AircraftRegistration{
        Preconditions.checkArgument(regex.matcher(string).matches());
    }
}

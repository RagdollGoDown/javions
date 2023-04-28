package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * records the aircraft registration of an airplane
 * @param string the aircraft registration
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public record AircraftRegistration(String string) {
    private static final Pattern REGEX = Pattern.compile("[A-Z0-9 .?/_+-]+");

    /**
     * Constructor
     * @param string the aircraft registration
     * @throws IllegalArgumentException if the aircraft registration isn't empty and doesn't follow the regex conditions
     */
    public AircraftRegistration{
        Preconditions.checkArgument(REGEX.matcher(string).matches());
    }
}

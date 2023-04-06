package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * records the aircraft description
 * @param string the description
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public record AircraftDescription(String string) {
    private static final Pattern regex = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    /**
     * Constructor
     * @param string the description of the aircraft
     * @throws IllegalArgumentException if the description isn't empty and doesn't follow the regex conditions
     */
    public AircraftDescription {
        Preconditions.checkArgument(string.equals("") || regex.matcher(string).matches());
    }
}

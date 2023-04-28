package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * records the type designator of an aircraft
 * @param string the aircraft type designator
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public record AircraftTypeDesignator(String string) {
    private static final Pattern REGEX = Pattern.compile("[A-Z0-9]{2,4}");

    /**
     * Constructor
     * @param string the aircraft type designator
     * @throws IllegalArgumentException if the aircraft type designator isn't empty and doesn't follow the regex conditions
     */
    public AircraftTypeDesignator {
        Preconditions.checkArgument(string.equals("")|| REGEX.matcher(string).matches());
    }
}

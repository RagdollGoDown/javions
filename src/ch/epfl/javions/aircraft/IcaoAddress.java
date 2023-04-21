package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Records the Icao address
 * The main identifier for an aircraft
 * @param string the Icao address
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public record IcaoAddress(String string) {
    private static final Pattern regex = Pattern.compile("[0-9A-F]{6}");

    /**
     * Constructor for icaoAddress
     * @param string the string of the icao address
     * @throws IllegalArgumentException if the string doesn't fit the icao address format
     */
    public IcaoAddress {
        Preconditions.checkArgument(regex.matcher(string).matches());
    }

    /**
     * Test if an object is equal to this instance of IcaoAdress (compare the string)
     * @param obj the object to compare
     * @return true if the object is an IcaoAddress and has the same string
     */
}


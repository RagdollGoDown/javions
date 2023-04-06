package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

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
    public IcaoAddress {
        Preconditions.checkArgument(regex.matcher(string).matches());
    }
}


package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents what is called the call sign of an aircraft
 * @param string the string is of the CallSign
 */
public record CallSign(String string){
    private static final Pattern regex = Pattern.compile("[A-Z0-9 ]{0,8}");

    /**
     * Constructor of CallSign
     * @param string the string is of the CallSign
     * @throws IllegalArgumentException if the strinf is empty or doesn't follow the format of CallSign
     */
    public CallSign{
        Preconditions.checkArgument(string.equals("") || regex.matcher(string).matches());
    }
}

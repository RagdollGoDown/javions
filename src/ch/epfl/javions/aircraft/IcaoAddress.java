package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record IcaoAddress(String string) {
    private static final Pattern regex = Pattern.compile("[0-9A-F]{6}");
    public IcaoAddress {
        Preconditions.checkArgument(regex.matcher(string).matches());
    }
}


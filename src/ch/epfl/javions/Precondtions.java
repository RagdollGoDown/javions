package ch.epfl.javions;

public final class Precondtions {
    private Precondtions(){}

    public static void checkArgument(boolean shouldBeTrue){
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }
}

package ch.epfl.javions;

/**
 * Use to test if variables respect a condition
 */
public final class Preconditions {

    private Preconditions(){}

    /**
     * Throw an error if the argument is false
     * @param shouldBeTrue a boolean which has to be true
     * @throws IllegalArgumentException if the given argument is false
     */
    public static void checkArgument(boolean shouldBeTrue){
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }
}
package ch.epfl.javions.aircraft;

/**
 * Contains the 4 different types of wake turbulence
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach Cadet (3347505)
 */
public enum WakeTurbulenceCategory {
    LIGHT,
    MEDIUM,
    HEAVY,
    UNKNOWN;

    /**
     * Gives the wake turbulence depending on the string
     * @param string either L,M or H, if it is anything else then it returns unknown
     * @return the corresponding wake turbulence
     */
    public static WakeTurbulenceCategory of(String string){
        return switch (string){
            case "L" -> LIGHT;
            case "M" -> MEDIUM;
            case "H" -> HEAVY;
            default -> UNKNOWN;
        };

    }
}

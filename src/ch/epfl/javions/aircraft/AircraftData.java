package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 * records the following parameters for a specific plane
 * @param registration the registration number
 * @param typeDesignator the aircraft type designator
 * @param model the airplane model
 * @param description a brief description of the plane
 * @param wakeTurbulenceCategory the aircraft's wake turbulence
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public record AircraftData (AircraftRegistration registration,
                            AircraftTypeDesignator typeDesignator,
                            String model,
                            AircraftDescription description,
                            WakeTurbulenceCategory wakeTurbulenceCategory){

    /**
     * Constructor
     * @param registration the registration number
     * @param typeDesignator the aircraft type designator
     * @param model the airplane model
     * @param description a brief description of the plane
     * @param wakeTurbulenceCategory the aircraft's wake turbulence
     * @throws NullPointerException if one of the above is null
     */
    public AircraftData {
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}
package ch.epfl.javions.aircraft;

import ch.epfl.javions.WakeTurbulenceCategory;

import java.util.Objects;

public class AircraftData {
    private AircraftRegistration registration;
    private AircraftTypeDesignator typeDesignator;
    private String model;
    private AircraftDescription description;
    private WakeTurbulenceCategory wakeTurbulenceCategory;

    public AircraftData(AircraftRegistration registration, AircraftTypeDesignator typeDesignator, String model, AircraftDescription description, WakeTurbulenceCategory wakeTurbulenceCategory) {
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);

        this.registration = registration;
        this.typeDesignator = typeDesignator;
        this.model = model;
        this.description = description;
        this.wakeTurbulenceCategory = wakeTurbulenceCategory;


    }
}

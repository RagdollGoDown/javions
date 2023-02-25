package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftDescriptionTest {

    @Test
    void testToString() {
        AircraftDescription AD = new AircraftDescription("L2J");
        assertEquals("L2J", AD.toString());
    }

    @Test
    void testEmptyString() {
        assertDoesNotThrow(() -> {
            new AircraftDescription("");
        });
    }

}
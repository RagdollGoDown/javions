package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftRegistrationTest {

    @Test
    void testToString() {
        AircraftRegistration obj = new AircraftRegistration("HB-JDC");
        var actual = obj.string();
        var expected = "HB-JDC";
        assertEquals(actual, expected);
    }

    @Test
    void testEmptyString() {
        assertThrows(IllegalArgumentException.class , () -> {
            new AircraftRegistration("");
        });
    }
}
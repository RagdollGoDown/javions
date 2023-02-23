package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftRegistrationTest {

    @Test
    void testToString() {
        AircraftRegistration obj = new AircraftRegistration("HB-JDC");
        var actual = obj.toString();
        var expected = "HB-JDC";
        assertEquals(actual, expected);
    }
}
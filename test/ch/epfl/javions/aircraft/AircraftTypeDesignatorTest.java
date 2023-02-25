package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftTypeDesignatorTest {

    @Test
    void testToString() {
        AircraftTypeDesignator obj = new AircraftTypeDesignator("A20N");
        var actual = obj.toString();
        var expected = "A20N";
        assertEquals(actual, expected);
    }
    @Test
    void testEmptyString() {
        assertDoesNotThrow(() -> {
            new AircraftTypeDesignator("");
        });
    }
}
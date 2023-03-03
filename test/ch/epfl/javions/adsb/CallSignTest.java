package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.AircraftRegistration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CallSignTest {
    @Test
    void testToString() {
        CallSign obj = new CallSign("AUFF9W0Z");
        var actual = obj.string();
        var expected = "AUFF9W0Z";
        assertEquals(expected, actual);
    }

    @Test
    void testEmptyString() {
        CallSign obj = new CallSign("");
        var actual = obj.string();
        var expected = "";
        assertEquals(expected, actual);
    }
}
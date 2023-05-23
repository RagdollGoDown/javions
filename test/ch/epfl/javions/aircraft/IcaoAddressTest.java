package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IcaoAddressTest {

    @Test
    void testToString() {
        IcaoAddress obj = new IcaoAddress("4B1814");
        var actual = obj.string();
        var expected = "4B1814";
        assertEquals(actual, expected);
    }


    @Test
    void constructorError() {
        assertThrows(IllegalArgumentException.class, () -> {
            new IcaoAddress("4B181");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new IcaoAddress("4B18141");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new IcaoAddress("4B18141");
        });
    }

    @Test
    void testEmptyString() {
        assertThrows(IllegalArgumentException.class , () -> {
            new IcaoAddress("");
        });
    }

}
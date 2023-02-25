package ch.epfl.javions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Crc24Test {

    @Test
    void crc() {
        var crc24 = new Crc24(0b11001);
        var actual = crc24.crc(0b11001, 0b11111100111, 4);
        var expected = 0b00110;
        assertEquals(expected, actual);
    }
}
package ch.epfl.javions;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class Crc24Test {

    @Test
    void crcTestBitwise(){
        var crc24 = new Crc24(0b11001);
        var actual11 = crc24.crc_bitwiseTest(0b1001, 0b11111100111, 4);
        var actual12 = crc24.crc_bitwiseTest(0b1001, 0b111111001110110, 4);
        var expected1 = 0b00110;
        assertEquals(expected1, actual11);
        assertEquals(0, actual12);

        var actual21 = crc24.crc_bitwiseTest(0b011, 0b11010011101100 , 3);
        var actual22 = crc24.crc_bitwiseTest(0b011, 0b11010011101100100 , 3);
        var expected2 = 0b100;
        assertEquals(expected2, actual21);
        assertEquals(0, actual22);

        var actual31 = crc24.crc_bitwiseTest(0b1, 0b0011010 , 1);
        var actual32 = crc24.crc_bitwiseTest(0b1, 0b00110101 , 1);
        var expected3 = 0b1;
        assertEquals(expected3, actual31);
        assertEquals(0, actual32);
    }
    @Test
    void crc() {
        var crc24 = new Crc24(Crc24.GENERATOR);
        String mS = "8D392AE499107FB5C00439";
        String cS = "035DB8";
        int c = Integer.parseInt(cS, 16); // == 0x035DB8

        byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }

}
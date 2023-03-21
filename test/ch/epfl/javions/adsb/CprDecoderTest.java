package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CprDecoderTest {
    @Test
    void decodePosition() {
        int x0 = 111600;
        int y0 = 94445;
        int x1 = 108865;
        int y1 = 77558;
        double N_bit = (1<<17);
        GeoPos g = CprDecoder.decodePosition(x0/N_bit,y0/N_bit ,x1/N_bit, y1/N_bit, 1 );
        System.out.println(g);
    }
}
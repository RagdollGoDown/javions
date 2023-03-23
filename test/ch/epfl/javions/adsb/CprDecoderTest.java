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


        double[] expectedForRecent0 = {7.476062, 46.323349};
        double[] expectedForRecent1 = {7.475166, 46.322363};

        double[] actualForRecent0 ={
                CprDecoder.decodePosition(x0/N_bit,y0/N_bit ,x1/N_bit, y1/N_bit, 0 )
                        .longitudeDEGREE(),
                CprDecoder.decodePosition(x0/N_bit,y0/N_bit ,x1/N_bit, y1/N_bit, 0 )
                        .latitudeDEGREE()};
        double[] actualForRecent1 ={
                CprDecoder.decodePosition(x0/N_bit,y0/N_bit ,x1/N_bit, y1/N_bit, 1 )
                        .longitudeDEGREE(),
                CprDecoder.decodePosition(x0/N_bit,y0/N_bit ,x1/N_bit, y1/N_bit, 1 )
                        .latitudeDEGREE()};

        System.out.println(actualForRecent0[0]);
        System.out.println(actualForRecent0[1]);

        //lon 0
        assertEquals(actualForRecent0[0] ,expectedForRecent0[0], Math.pow(10,-6));
        assertEquals(actualForRecent0[1] ,expectedForRecent0[1], Math.pow(10,-6));
        assertEquals(actualForRecent1[0] ,expectedForRecent1[0], Math.pow(10,-6));
        assertEquals(actualForRecent1[1] ,expectedForRecent1[1], Math.pow(10,-6));
    }

    @Test
    void checkThrowsIllegalArgument(){
        assertThrows(IllegalArgumentException.class,() -> CprDecoder.decodePosition(0,0 ,0, 0, 2 ));
        assertThrows(IllegalArgumentException.class,() -> CprDecoder.decodePosition(0,0 ,0, 0, -1 ));
        assertDoesNotThrow(() -> CprDecoder.decodePosition(0,0 ,0, 0, 1 ));
        assertDoesNotThrow(() -> CprDecoder.decodePosition(0,0 ,0, 0, 0 ));
    }
}
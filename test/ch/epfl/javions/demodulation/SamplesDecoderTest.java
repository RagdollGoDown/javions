package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.*;

class SamplesDecoderTest {

    @Test
    void checkSamplesDecoder() throws Exception {
        v02 = new FileInputStream("resources/samples.bin");
        v03 = 100;
        v01 = new ch.epfl.javions.demodulation.SamplesDecoder(v02, v03);
        v04 = new short[v03];
        v03 = v01.readBatch(v04);
    }

    ch.epfl.javions.demodulation.SamplesDecoder v01;
    java.io.InputStream v02;
    int v03;
    short[] v04;
}
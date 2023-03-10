package ch.epfl.javions.demodulation;


import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class SamplesDecoderTest {

    @Test
    void checkSamplesDecoderTrivial() throws Exception {
        InputStream stream = new FileInputStream("resources/samples.bin");
        int batchSize = 100;
        SamplesDecoder sd = new SamplesDecoder(stream, batchSize);
        short[] batch = new short[100];
        int nElementRead = sd.readBatch(batch);
        short[] expected = {-3, 8, -9, -8, -5, -8, -12, -16, -23, -9};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], batch[i]);
        }
        assertEquals(nElementRead, batchSize);
    }
    @Test
    void checkSamplesDecoderTrivialWrong() throws Exception {
        InputStream stream = new FileInputStream("resources/samples.bin");
        int batchSize = 100;
        SamplesDecoder sd = new ch.epfl.javions.demodulation.SamplesDecoder(stream, batchSize);
        short[] batch = new short[100];
        int nElementRead = sd.readBatch(batch);
        short[] expected = {10, -19, 39, 8, 5, 18, -112, 300, 203, -91};
        for (int i = 0; i < expected.length; i++) {
            assertNotEquals(expected[i], batch[i]);
        }
        assertEquals(nElementRead, batchSize);
    }
    @Test
    void checkSamplesDecoderMaxSize() throws Exception {
        InputStream stream = new FileInputStream("resources/samples.bin");
        int nNbytesSample = 4804;
        int batchSize = 3000;
        SamplesDecoder sd = new ch.epfl.javions.demodulation.SamplesDecoder(stream, batchSize);
        short[] batch = new short[batchSize];
        int nElementRead = sd.readBatch(batch);
        assertEquals(nElementRead, nNbytesSample/2);
        for (int i = 0; i < batchSize - nNbytesSample/2; i++) {
            assertEquals(0,batch[nNbytesSample/2 + i]);
        }
    }
    @Test
    void checkSamplesEmpty() throws Exception {
        byte[] bytes = {};
        InputStream stream = new ByteArrayInputStream(bytes);
        int batchSize = 21;
        SamplesDecoder sd = new ch.epfl.javions.demodulation.SamplesDecoder(stream, batchSize);
        short[] batch = new short[batchSize];
        int nElementRead = sd.readBatch(batch);
        assertEquals(0, nElementRead);
        for (int i = 0; i < batch.length; i++) {
            assertEquals(0, batch[i]);
        }
    }
    @Test
    void checkSamplesException(){
        byte[] bytes = {1,2,3,4,5};
        InputStream stream = new ByteArrayInputStream(bytes);
        int batchSize = 21;
        SamplesDecoder sd = new ch.epfl.javions.demodulation.SamplesDecoder(stream, batchSize);
        short[] batch = new short[batchSize-1];
        assertThrows(IllegalArgumentException.class, () -> {
            sd.readBatch(batch);
        });
    }
}
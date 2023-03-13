package ch.epfl.javions.demodulation;


import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
    private static final int SAMPLES_COUNT = 1 << 12;
    private static final int BIAS = 1 << 11;

    private static byte[] getSampleBytes() {
        var sampleBytes = new byte[SAMPLES_COUNT * Short.BYTES];
        var sampleBytesBuffer = ByteBuffer.wrap(sampleBytes)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer();

        for (int i = 0; i < SAMPLES_COUNT; i += 1)
            sampleBytesBuffer.put((short) i);
        return sampleBytes;
    }

    @Test
    void samplesDecoderConstructorThrowsWithInvalidBatchSize() {
        var stream = new ByteArrayInputStream(new byte[0]);
        assertThrows(
                IllegalArgumentException.class,
                () -> new SamplesDecoder(stream, -1));
        assertThrows(
                IllegalArgumentException.class,
                () -> new SamplesDecoder(stream, 0));
    }

    @Test
    void samplesDecoderConstructorThrowsWithNullStream() {
        assertThrows(
                NullPointerException.class,
                () -> new SamplesDecoder(null, 1));
    }

    @Test
    void samplesDecoderReadBatchThrowsOnInvalidBatchSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            try (var byteStream = new ByteArrayInputStream(getSampleBytes())) {
                var batchSize = 1024;
                var actualSamples = new short[batchSize - 1];
                var samplesDecoder = new SamplesDecoder(byteStream, batchSize);
                samplesDecoder.readBatch(actualSamples);
            }
        });
    }

    @Test
    void samplesDecoderReadBatchCorrectlyReadsSamples() throws IOException {
        try (var byteStream = new ByteArrayInputStream(getSampleBytes())) {
            var expectedSamples = new short[SAMPLES_COUNT];
            for (int i = 0; i < SAMPLES_COUNT; i += 1)
                expectedSamples[i] = (short) (i - BIAS);

            var actualSamples = new short[SAMPLES_COUNT];
            var samplesDecoder = new SamplesDecoder(byteStream, actualSamples.length);
            var readSamples = samplesDecoder.readBatch(actualSamples);
            assertEquals(SAMPLES_COUNT, readSamples);
            assertArrayEquals(expectedSamples, actualSamples);
        }
    }

    @Test
    void samplesDecoderWorksWithDifferentBatchSizes() throws IOException {
        var expectedSamples = new short[SAMPLES_COUNT];
        for (int i = 0; i < SAMPLES_COUNT; i += 1)
            expectedSamples[i] = (short) (i - BIAS);

        for (var batchSize = 1; batchSize < SAMPLES_COUNT; batchSize *= 2) {
            try (var byteStream = new ByteArrayInputStream(getSampleBytes())) {
                var samplesDecoder = new SamplesDecoder(byteStream, batchSize);
                var actualSamples = new short[SAMPLES_COUNT];
                var batch = new short[batchSize];
                for (var i = 0; i < SAMPLES_COUNT / batchSize; i += 1) {
                    var samplesRead = samplesDecoder.readBatch(batch);
                    assertEquals(batchSize, samplesRead);
                    System.arraycopy(batch, 0, actualSamples, i * batchSize, batchSize);
                }
                assertArrayEquals(expectedSamples, actualSamples);
            }
        }
    }
}
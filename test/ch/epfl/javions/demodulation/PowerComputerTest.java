package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class PowerComputerTest {

    @Test
    void checkPowerComputerTrivial() throws Exception{
        InputStream stream = new FileInputStream("resources/samples.bin");
        int batchSize = 100;
        PowerComputer pc = new PowerComputer(stream, batchSize);
        int[] batch = new int[100];
        int nElementRead = pc.readBatch(batch);
        int[] expected = {73, 292, 65, 745, 98, 4226, 12244, 25722, 36818, 23825};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], batch[i]);
        }
        assertEquals(batchSize, nElementRead);
    }
    @Test
    void checkPowerComputerEmpty() throws Exception{
        byte[] bytes = {};
        InputStream stream = new ByteArrayInputStream(bytes);
        int batchSize = 100;
        int[] batch = new int[batchSize];

        PowerComputer pc = new PowerComputer(stream, batchSize);

        int nElementRead = pc.readBatch(batch);
        assertEquals(0, nElementRead);
        for (int i = 0; i < batch.length; i++) {
            assertEquals(0, batch[i]);
        }
    }
    @Test
    void checkPowerComputerNElementRead() throws Exception{
        byte[] bytes = {1,2,3,4,5,6,7,8};
        InputStream stream = new ByteArrayInputStream(bytes);
        int batchSize = 100;
        PowerComputer pc = new PowerComputer(stream, batchSize);
        int[] batch = new int[batchSize];
        int nElementRead = pc.readBatch(batch);

        assertEquals(bytes.length/4, nElementRead);

        for (int i = 0; i < batchSize - bytes.length/4; i++) {
            assertEquals(0,batch[bytes.length/4 + i]);
        }
    }
    @Test
    void checkSamplesException(){
        byte[] bytes = {1,2,3,4,5};
        InputStream stream = new ByteArrayInputStream(bytes);
        int batchSize = 21;
        PowerComputer pc = new PowerComputer(stream, batchSize);
        int[] batch = new int[batchSize-1];
        assertThrows(IllegalArgumentException.class, () -> {
            pc.readBatch(batch);
        });
    }

}
package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class PowerComputerTest {

    @Test
    void checkPowerComputer() throws Exception {
        InputStream inputStream = new FileInputStream("resources/samples.bin");
        int batchSize = 100;
        int[] batch = new int[batchSize];
        PowerComputer powerComputer = new ch.epfl.javions.demodulation.PowerComputer(inputStream, batchSize);
        int readBatchSize = powerComputer.readBatch(batch);
    }
}
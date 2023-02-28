package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.*;

class PowerComputerTest {

    @Test
    void checkPowerComputer() throws Exception {
        v02 = new FileInputStream("resources/samples.bin");
        v03 = 100;
        v06 = new int[v03];
        v05 = new ch.epfl.javions.demodulation.PowerComputer(v02, v03);
        v03 = v05.readBatch(v06);
    }

    java.io.InputStream v02;
    int v03;
    ch.epfl.javions.demodulation.PowerComputer v05;
    int[] v06;
}
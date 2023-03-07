package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class PowerWindowTest {
    @Test
    void PowerWindowTrivial1() throws IOException {
        InputStream stream = new FileInputStream("resources/samples.bin");
        int windowSize = 100;
        PowerWindow pw = new PowerWindow(stream, windowSize);;
        int[] expected = {73, 292, 65, 745, 98, 4226, 12244, 25722, 36818, 23825};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], pw.get(i));
        }
    }
    void PowerWindowAdvance() throws IOException {
        InputStream stream = new FileInputStream("resources/samples.bin");
        int windowSize = 100;
        PowerWindow pw = new PowerWindow(stream, windowSize);;
        int[] expected = {73, 292, 65, 745, 98, 4226, 12244, 25722, 36818, 23825};
        for (int i = 0; i < expected.length; i++) {
            pw.advance();
            assertEquals(expected[i], pw.get(0));
        }
    }
    @Test
    void PowerWindowAdvanceN() throws IOException {
        InputStream stream = new FileInputStream("resources/samples.bin");
        int windowSize = 100;
        PowerWindow pw = new PowerWindow(stream, windowSize);;
        int[] expected = {73, 292, 65, 745, 98, 4226, 12244, 25722, 36818, 23825};
        assertEquals(73, pw.get(0));
        pw.advanceBy(2);
        assertEquals(98, pw.get(2));
    }


    @Test
    void size() {
    }

    @Test
    void advance() {
    }

    @Test
    void advanceBy() {
    }

    @Test
    void get() {
    }

    @Test
    void isFull() {
    }
}
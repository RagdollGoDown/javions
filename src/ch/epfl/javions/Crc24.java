package ch.epfl.javions;

/**
 * Represents a 24-bit CRC calculator
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public final class Crc24 {
    public static final int GENERATOR = 0xFFF409;
    private final int N = 24;
    private final int[] table;

    /**
     * The constructor of crc24
     * @param generator the generator used to calculate the crc24
     */
    public Crc24(int generator){
        this.table = buildTable(generator);
    }

    /**
     * Calculates the crc24 bit by bit
     * @param generator the generator used for the crc24 calculation
     * @param value the value for which we are looking for the crc24
     * @param N the size of the crc ( in this case 24)
     * @return the crc24 of the value given
     */
    private static int crc_bitwise(int generator, byte[] value, int N) {
        int crc = 0;
        int[] table = {0, generator};
        for (byte octet : value) {
            for (int i = Byte.SIZE - 1 ; i >= 0; i--) {
                int b = Bits.testBit(octet, i)? 1 : 0;
                crc = ((crc<<1) | b) ^ table[Bits.testBit(crc, N-1)? 1 : 0];
            }
        }
        for (int i = 0; i < N; i++) {
            crc = ((crc<<1)) ^ table[Bits.testBit(crc, N-1)? 1 : 0];
        }
        return Bits.extractUInt(crc,0,N);
    }

    /**
     * Calculates the crc24 bit by bit
     * @param generator the generator used for the crc24 calculation
     * @param value the value for which we are looking for the crc24
     * @param N the size of the crc ( in this case 24)
     * @return the crc24 of the value given
     */
    private static int crc_bitwise(int generator, int value, int N){
        assert value << N >>> N == value; //prevent overflow
        int crc = 0;
        int[] table = {0, generator};
        for (int i = Integer.SIZE - N ; i>=0; i--)
        {
            int b = Bits.testBit(value, i)? 1 : 0;
            crc = ((crc<<1) | b) ^ table[Bits.testBit(crc, N-1)? 1 : 0];
        }
        for (int i = N - 1 ; i>=0; i--)
        {
            int b = Bits.testBit(value, i)? 1 : 0;
            crc = (crc<<1) ^ table[Bits.testBit(crc, N-1)? 1 : 0];
        }
        return Bits.extractUInt(crc,0,N);
    }

    /**
     * Calculates the crc24
     * @param bytes the value for which we are looking for the crc24
     * @return the crc24 of the given bytes
     */
    public int crc(byte[] bytes) {
        int crc = 0;
        for (byte octet : bytes) {
            crc = ((crc<<8) | Byte.toUnsignedInt(octet)) ^ this.table[Bits.extractUInt(crc,N-8,8)];
        }
        for (int i = 0; i < N / 8; i++) {
            crc = (crc<<8) ^ this.table[Bits.extractUInt(crc,N-8,8)];
        }
        return Bits.extractUInt(crc,0,N);

    }

    /**
     * Build the table containing the crc24 of all the number from 0 to 255
     * @param generator the generator for the crc24
     * @return a table containing the crc24 of all the number from 0 to 255
     */
    private int[] buildTable(int generator){
        int[] table = new int[256];
        for (int i = 0; i < 256; i++) {
            byte[] b = {(byte)i};
            table[i] = crc_bitwise(generator, b, N);
        }
        return table;
    }
}

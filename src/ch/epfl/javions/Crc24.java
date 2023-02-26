package ch.epfl.javions;

public final class Crc24 {
    public static final int GENERATOR = 0xFFF409;
    private final int N = 24;
    private final int[] table;


    public Crc24(int generator){
        this.table = buildTable(generator);
    }
    //TODO remove, only keep for the tests
    public static int crc_bitwiseTest(int generator, int value, int N){
        return crc_bitwise(generator, value, N);
    }
    public static int crc_bitwise(int generator, byte[] value, int N) {
        int crc = 0;
        int[] table = {0, generator};
        for (byte octet : value) {
            for (int i = 7; i >= 0; i--) {
                int b = Bits.testBit(octet, i)? 1 : 0;
                crc = ((crc<<1) | b) ^ table[Bits.testBit(crc, N-1)? 1 : 0];
            }
        }
        for (int i = 0; i < 24; i++) {
            crc = ((crc<<1)) ^ table[Bits.testBit(crc, N-1)? 1 : 0];
        }
        return Bits.extractUInt(crc,0,N);
    }
    private static int crc_bitwise(int generator, long value, int N){
        //assert value <= 0xFF;
        value = value << N;
        int crc = 0;
        int[] table = {0, generator};
        //System.out.println("generator: " + table[1]);
        for (int i = Integer.SIZE - 1 ; i>=0; i--)
        {
            int b = Bits.testBit(value, i)? 1 : 0;
            //System.out.println("==========================");
            //System.out.println("crc: " + Integer.toString(crc, 2));
            //System.out.println("b: " + b);
            //System.out.println("crc with b: " + Integer.toString(((crc<<1) | b),2));
            //System.out.println("operation: " + (Bits.testBit(crc, N-1) ? "xor" : "pass"));
            crc = ((crc<<1) | b) ^ table[Bits.testBit(crc, N-1)? 1 : 0];
            //System.out.println("New crc: " + Integer.toString(crc,2));

        }
        return Bits.extractUInt(crc,0,N);
    }
    public int crc(byte[] bytes) {
        int crc = 0;
        for (byte octet : bytes) {
            crc = ((crc<<8) | octet) ^ this.table[Bits.extractUInt(crc,N-8,8)];
        }
        for (int i = 0; i < N / 8; i++) {
            crc = (crc<<8) ^ this.table[Bits.extractUInt(crc,N-8,8)];
        }
        return Bits.extractUInt(crc,0,N);

    }
    private int[] buildTable(int generator){
        int[] table = new int[256];
        for (int i = 0; i < 256; i++) {
            byte[] value = {(byte)i};
            table[i] = crc_bitwise(generator, value, N);
        }
        return table;
    }



}

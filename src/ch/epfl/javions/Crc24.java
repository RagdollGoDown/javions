package ch.epfl.javions;

public final class Crc24 {
    public static final int GENERATOR = 0xFFF409;

    public Crc24(int generator){
    }
    private static int crc_bitwise(int generator, int message, int N){
        //assert message <= 0x0F;
        message = message << N;
        int crc = 0;
        int[] table = {0, 0b1001};
        System.out.println("generator: " + table[1]);
        for (int i = 14 ; i>=0; i--)
        {
            int b = Bits.testBit(message, i)? 1 : 0;
            System.out.println("==========================");
            System.out.println("crc: " + Integer.toString(crc, 2));
            System.out.println("b: " + b);
            System.out.println("crc with b: " + Integer.toString(((crc<<1) | b),2));
            System.out.println("operation: " + (Bits.testBit(crc, N-1) ? "xor" : "pass"));
            crc = ((crc<<1) | b) ^ table[Bits.testBit(crc, N-1)? 1 : 0];
            System.out.println("New crc: " + Integer.toString(crc,2));

        }
        return Bits.extractUInt(crc,N,N);
    }
    public int crc(int generator , int message, int N){
        return crc_bitwise(generator, message, N);
    }



}

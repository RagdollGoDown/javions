package ch.epfl.javions;

import java.util.Objects;

public final class Bits {
    private Bits(){}

    public static int extractUInt(long value, int start, int size){
        if (size <= 0 || size >= Integer.SIZE) throw new IllegalArgumentException();
        Objects.checkFromIndexSize(start,size,Long.SIZE);
        return (int) (value << (Long.SIZE - (start + size)) >>> (start + Long.SIZE - (start + size)));
    }

    public static boolean testBit(long value, int index){
        Objects.checkIndex(index,Long.SIZE);
        long mask = (1L<<index);
        return (value & mask) >>> index  == 1;
    }
}

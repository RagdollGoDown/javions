package ch.epfl.javions;

public final class Bits {
    private Bits(){}

    public static int extractUInt(long value, int start, int size){
        if (size <= 0 || size > Integer.SIZE) throw new IllegalArgumentException();
        if (start < 0 || start + size >= Long.SIZE) throw new IndexOutOfBoundsException();

        return (int) value << (Long.SIZE - (start + size)) >>> (start + Long.SIZE - (start + size));
    }
}

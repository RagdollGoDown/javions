package ch.epfl.javions;

import java.util.Objects;

/**
 *  Help to manipulate bits
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public final class Bits {

    private Bits(){}

    /**
     * extracts an int from a long value in a certain range
     * @param value the long the int is extracted from
     * @param start the starting position at which we take the int
     * @param size  the size of the int extracted
     * @return the extracted int
     * @throws IllegalArgumentException  if the size is bigger than the size of an int or inferior to one
     * @throws IndexOutOfBoundsException if the range covered is out of the long's bounds
     */
    public static int extractUInt(long value, int start, int size) {
        Preconditions.checkArgument(size > 0 && size < Integer.SIZE);
        Objects.checkFromIndexSize(start, size, Long.SIZE);
        return (int) (value << (Long.SIZE - (start + size)) >>> (start + Long.SIZE - (start + size)));
    }

    /**
     * Checks if a certain bit in a long is equal to 1
     * ex: for 00010 1 is at position 1
     * @param value the long in question
     * @param index the position of the bit in the long
     * @return true if the bit is equal to and false if it isn't
     * @throws IndexOutOfBoundsException if the index isn't in the bounds of the long
     */
    public static boolean testBit(long value, int index) {
        Objects.checkIndex(index, Long.SIZE);
        long mask = (1L << index);
        return (value & mask) >>> index == 1;
    }



}
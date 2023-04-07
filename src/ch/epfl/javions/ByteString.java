package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 *  Represents a sequence of bytes
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public class ByteString {

    private final byte[] bytes;

    /**
     * Constructor for ByteString
     * @param bytes a byte sequence for the class
     */
    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * Creates a ByteString from a string in hexadecimal format
     * @param hexString the string used to be converted
     * @return the ByteString version of the input string
     * @throws NumberFormatException if the string comes in a format that doesn't allow the conversion
     */
    public static ByteString ofHexadecimalString(String hexString){
        if (hexString.length() % 2 == 1) throw new NumberFormatException();

        HexFormat hf = HexFormat.of().withUpperCase();

        byte[] bytes = hf.parseHex(hexString);
        return new ByteString(bytes);
    }

    /**
     * @return Gives the number of bytes held in the ByteString
     */
    public int size(){
        return bytes.length;
    }

    /**
     * Gives the byte at a certain index as unsigned
     * @param index the index in question
     * @return the unsigned byte
     * @throws IndexOutOfBoundsException when the index is inferior to zero or superior to the array size
     */
    public int byteAt(int index){
        if (index >= size() || index < 0) throw new IndexOutOfBoundsException();
        return Byte.toUnsignedInt(bytes[index]);
    }

    /**
     * Creates a long from the bytes between fromIndex(Included) to toIndex(Excluded)
     * Note that we take the bytes from left to right
     * @param fromIndex from where we start taking bytes in the bytes array
     * @param toIndex up to where we take bytes in the bytes array
     * @return the long made by the collected bytes
     * @throws IllegalArgumentException when the range size is bigger than the size of a long
     * @throws IndexOutOfBoundsException if the range covered is out of bounds of the list
     */
    public long bytesInRange(int fromIndex, int toIndex){
        Objects.checkFromIndexSize(fromIndex,toIndex-fromIndex,size());
        Preconditions.checkArgument((toIndex-fromIndex < Long.SIZE/8));

        long extractedLong = 0;
        for (int i = fromIndex; i < toIndex; i++) {
            extractedLong = (extractedLong << Byte.SIZE)  | Byte.toUnsignedInt(bytes[i]);
        }
        return extractedLong;
    }

    /**
     * Checks if an array of bytes is the same as the one in this instance
     * @param bytes the array to compare to
     * @return true if they are the same
     */
    public boolean bytesEquals(byte[] bytes){
        return Arrays.equals(this.bytes, bytes);
    }

    /**
     * Checks if another ByteString is the same as this instance
     * @param other the ByteString being compared
     * @return true if it is the same
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof  ByteString otherByteString && otherByteString.bytesEquals(this.bytes);
    }

    /**
     * @return the hashcode of the bytes array
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    /**
     * Converts the ByteString to hexadecimal format
     * @return the converted string
     */
    @Override
    public String toString() {
        return HexFormat.of().withUpperCase().formatHex(this.bytes) ;
    }
}

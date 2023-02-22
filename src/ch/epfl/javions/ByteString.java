package ch.epfl.javions;

import java.util.HexFormat;
import java.util.Objects;

public class ByteString {
    //TODO demander si on doit utiliser char pour avoir une version non-signée
    private final byte[] bytes;

    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    public static ByteString ofHexadecimalString(String hexString){
        if (hexString.length() % 2 == 0) throw new IllegalArgumentException();

        HexFormat hf = HexFormat.of().withUpperCase();

        byte[] bytes = hf.parseHex(hexString);
        return new ByteString(bytes);
    }

    public int size(){
        return bytes.length;
    }

    public int byteAt(int index){
        if (index >= size()) throw new IndexOutOfBoundsException();
        return bytes[index];
    }

    public long bytesInRange(int fromIndex, int toIndex){
        //TODO demander ce qu'ils veulent dire par poids plus faible pour toIndex
        Objects.checkFromIndexSize(fromIndex,toIndex-fromIndex,size());
        if (toIndex-fromIndex < Long.SIZE/8) throw new IllegalArgumentException();

        long extractedLong = 0;

        for (int i = fromIndex; i < toIndex; i++) {
            extractedLong = extractedLong << 8;
            extractedLong = extractedLong | bytes[i];
        }
        return extractedLong;
    }
}

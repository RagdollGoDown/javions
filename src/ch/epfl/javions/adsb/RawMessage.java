package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;

public record RawMessage(long timeStampNs, ByteString bytes) {
    public static final int LENGTH = 14;

    public RawMessage{
        Preconditions.checkArgument(timeStampNs >= 0 && bytes.size() == LENGTH);
    }

    public static RawMessage of(long timeStampNs, byte[] bytes){
        ByteString adsbMessage = new ByteString(bytes);
        int crc24 = (int)adsbMessage.bytesInRange(11,13);

        return new RawMessage(timeStampNs, crc24 == 0  ? adsbMessage : null);
    }

    public static int size(byte byte0){
        return Byte.toUnsignedInt(byte0) == 17 ? LENGTH : 0;
    }

    public static int typeCode(long payload){
        int typeCode = (int)((payload << 8) >>> 51);
        return typeCode;
    }
}

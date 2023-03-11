package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import javax.swing.*;
import java.util.Arrays;

public record RawMessage(long timeStampNs, ByteString bytes) {
    public static final int LENGTH = 14;
    private final static Crc24 crc24 = new Crc24(Crc24.GENERATOR);

    public RawMessage{
        Preconditions.checkArgument(timeStampNs >= 0 && bytes.size() == LENGTH);

    }

    /**
     * Creates a RawMessage from a long and a list of bytes
     * @param timeStampNs the horodata of the message in nanoseconds
     * @param bytes the bytes forming the message, they will be converted to a ByteString
     * @return the RawMessage
     */
    public static RawMessage of(long timeStampNs, byte[] bytes){
        ByteString adsbMessage = new ByteString(bytes);
        int crc = crc24.crc(Arrays.copyOfRange(bytes, 11, 13));
        return crc == 0  ? new RawMessage(timeStampNs, adsbMessage):null;
    }

    /**
     * Gives the size of the message if it comes in a known format
     * @param byte0 the byte containing the format, supposedly worth 17
     *              the format is contained in the 5 fist bytes (5 bits de poids forts)
     * @return returns the constant LENGTH (14) if it is 17 or 0 in any other case
     */
    public static int size(byte byte0){
        return Byte.toUnsignedInt(byte0) >>> 3 == 17 ? LENGTH : 0;
    }

    /**
     * Takes the 5 first bits (5 bits de poids forts) of the ME
     * @param payload the ME in question
     * @return returns the typeCode in the 5 bits
     */
    public static int typeCode(long payload){
        return (int)((payload << 8) >>> 51);
    }

    /**
     * @return the DF of the RawMessage contained in the first 5 bits (5 bits de poids forts)
     */
    public int downLinkFormat(){
        return Byte.toUnsignedInt((byte)(bytes.byteAt(0) >>> 3));
    }

    /**
     * @return l'IcaoAddress contained in the bytes 1 to 3
     */
    public IcaoAddress icaoAddress(){
        //TODO voir s'il faut le garder en attribut privé, parce que là c'est un nouveau à chaque fois
        return new IcaoAddress(Long.toString(bytes.bytesInRange(1,4)));
    }

    /**
     * Extracts the ME of the message in bytes 4 to 10
     * @return the payload bytes in long format
     */
    public long payload(){
        return bytes.bytesInRange(4,11);
    }

    /**
     * Extracts the type code from the ME of the message in bytes 4 to 10
     * @return the typeCode of the message using the static typeCode method
     */
    public int typeCode(){
        return RawMessage.typeCode(payload());
    }
}
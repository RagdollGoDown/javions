package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Contains the altitude and the position of a plane
 * @param timeStampNs the timeStamp of the PositionMessage in nano second (>0)
 * @param icaoAddress the icao adress
 * @param altitude the altitude
 * @param parity the parity (0 ou 1)
 * @param x position in x (between 0 and 1)
 * @param y position in y (between 0 and 1)
 */
public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x, double y) implements Message {
    private static final int ALTITUDE_SIZE = 12;
    private static final int ALTITUDE_START = 36;
    private static  final int INDEX_Q_ALTITUDE = 4;
    private static final int SIZE_LONG_LAT = 1 << 17;
    private static final int[] ORDER_ALTITUDE = {9, 3, 10, 4, 11, 5, 6, 0, 7, 1, 8, 2};

    /**
     * Constructor of AirbornePositionMessage
     * @param timeStampNs the timeStamp of the PositionMessage in nano second (>0)
     * @param icaoAddress the icao adress
     * @param altitude the altitude
     * @param parity the parity (0 ou 1)
     * @param x position in x (between 0 and 1)
     * @param y position in y (between 0 and 1)
     * @throws IllegalArgumentException if timeStampsNs is negative
     * @throws IllegalArgumentException if parity is not equal to 0 or 1
     * @throws  IllegalArgumentException if x is not between 0 and 1
     * @throws  IllegalArgumentException if y is not between 0 and 1
     */
    public AirbornePositionMessage{
        if (icaoAddress == null) throw new NullPointerException();
        Preconditions.checkArgument(0 <= timeStampNs);
        Preconditions.checkArgument(parity == 0 || parity == 1);
        Preconditions.checkArgument(0<=x && x<1);
        Preconditions.checkArgument(0<=y && y<1);
    }

    /**
     * Extract the altitude value from the payload
     * @param rawMessage the rawm message containing the altitude
     * @return (int) the altitude
     */
    private static int getAltitudePayload(RawMessage rawMessage){
        return Bits.extractUInt(rawMessage.payload(),ALTITUDE_START,ALTITUDE_SIZE);
    }

    /**
     * remove the bit at the position Q in a payload
     * @param payload the payload
     * @return payload without the index at the position 'Q'
     */
    private static int removeQBit(int payload){
        return (Bits.extractUInt(payload, INDEX_Q_ALTITUDE + 1, Integer.SIZE - (INDEX_Q_ALTITUDE + 1)) << INDEX_Q_ALTITUDE)
                | Bits.extractUInt(payload, 0 , INDEX_Q_ALTITUDE);
    }

    /**
     * Decode the altitude into the correct order
     * @param payload the payload that contains the altitude data
     * @return the sorted altitude
     */
    private static int getSortedAltitude(int payload){
        //11 10 09 08 07 06 05 04 03 02 01 00
        //C1 A1 C2 A2 C4 A4 B1 D1 B2 D2 B4 D4
        //D1 D2 D4 A1 A2 A4 B1 B2 B4 C1 C2 C4

        int sorted = 0;
        for (int i = 0; i < ORDER_ALTITUDE.length; i++) {
            sorted = sorted | ((Bits.testBit(payload, i) ? 1:0) << ORDER_ALTITUDE[i]);
        }
        return sorted;
    }

    /**
     * Decode the Gray's code
     * @param value the value that has been to be decoded
     * @param size the size of the value (number of bits)
     * @return the decoded value
     */
    private static int decodeGray(int value, int size){
        int decoded = value;
        for (int i = 1; i < size; i++) {
            decoded = decoded ^ (value >>> i);
        }
        return decoded;

    }

    /**
     * Get the altitude from a payload via the 'second' algorithm
     * @param payload the payload of the altitude
     * @return (int) the altitude int foot or null if invalid
     */
    public static Integer getValueWeirdAlgoAltitude(int payload){
        int sorted = getSortedAltitude(payload);
        int strongGroup = decodeGray(Bits.extractUInt(sorted, 3, 9), 9);
        int weakGroup = decodeGray(Bits.extractUInt(sorted, 0, 3), 3);
        // special cases
        if (weakGroup == 0 || weakGroup == 5 || weakGroup == 6){
            return null;
        }
        if (weakGroup == 7) {
            weakGroup = 5;
        }
        if (strongGroup % 2 == 1){
            weakGroup = 6 - weakGroup;
        }
        return -1300 + weakGroup * 100 + strongGroup * 500;
    }
    /**
     * Get the altitude from a payload
     * @param payload the payload that contains the altitude
     * @return the altitude int meter or null if invalid
     */
    private static Double getAltitude(int payload) throws IllegalArgumentException{
        if (Bits.testBit(payload, INDEX_Q_ALTITUDE)){
            int altitude = -1000 + removeQBit(payload) * 25;
            return Units.convert(altitude, Units.Length.FOOT, Units.Length.METER);
        }else{
            Integer altitude = getValueWeirdAlgoAltitude(payload);
            return (altitude == null) ? null : Units.convert(altitude, Units.Length.FOOT, Units.Length.METER);
        }
    }
    /**
     * The altitude payload from a raw message
     * @param rawMessage the raw message
     * @return the payload that contains altitude data
     */
    private static Double getAltitude(RawMessage rawMessage){
        int payload = getAltitudePayload(rawMessage);
        return getAltitude(payload);
    }

    /**
     * Get the x position from a raw message
     * @param rawMessage the raw message
     * @return x position from the raw message
     */
    private static double getX(RawMessage rawMessage){
        return ((double) Bits.extractUInt(rawMessage.payload(), 0, 17)) / SIZE_LONG_LAT;
    }

    /**
     * Get the y position from a raw message
     * @param rawMessage the raw message
     * @return y position from the raw message
     */
    private static double getY(RawMessage rawMessage){
        return ((double) Bits.extractUInt(rawMessage.payload(), 17, 17)) / SIZE_LONG_LAT;
    }
    /**
     * Get the parity from a raw message
     * @param rawMessage the raw message
     * @return the parity from the raw message
     */
    private static int getParity(RawMessage rawMessage){
        return Bits.extractUInt(rawMessage.payload(), 34, 1);
    }

    /**
     * Build an AirbornInstance with the information of a rawMessage
     * @param rawMessage a raw message
     * @return an Instance of AirbornePositionMessage
     *          null if the type code from rawMessage is not between 9 and 22 or if type code is equal at 19
     *          null if the altitude from the rawData is invalid
     */
    public static AirbornePositionMessage of(RawMessage rawMessage){

        long timeStampNs = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();

        Double altitude = getAltitude(rawMessage);
        if (altitude == null) return null;
        if (rawMessage.typeCode() < 9 || rawMessage.typeCode() == 19 || rawMessage.typeCode() > 22){
            return null;
        }


        int parity = getParity(rawMessage);
        double x = getX(rawMessage);
        double y = getY(rawMessage);
        return new AirbornePositionMessage(timeStampNs, icaoAddress, altitude, parity, x,y);
    }
}

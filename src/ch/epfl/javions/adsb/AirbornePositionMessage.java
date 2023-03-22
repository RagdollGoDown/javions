package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x, double y) {
    private static final int ALTITUDE_SIZE = 12;
    private static final int ALTITUDE_START = 36;
    private static  final int INDEX_Q_ALTITUDE = 4;
    private static final int SIZE_LONG_LAT = 1 << 17;

    public AirbornePositionMessage{
        if (icaoAddress == null) throw new NullPointerException();
        Preconditions.checkArgument(0 <= timeStampNs);
        Preconditions.checkArgument(parity == 0 || parity == 1);
        Preconditions.checkArgument(0<=x && x<1);
        Preconditions.checkArgument(0<=y && y<1);
    }

    private static int getAltitudePayload(RawMessage rawMessage){
        return Bits.extractUInt(rawMessage.payload(),ALTITUDE_START,ALTITUDE_SIZE);
    }
    private static int removeQBit(int payload){
        return (Bits.extractUInt(payload, INDEX_Q_ALTITUDE + 1, Integer.SIZE - (INDEX_Q_ALTITUDE + 1)) << INDEX_Q_ALTITUDE)
                | Bits.extractUInt(payload, 0 , INDEX_Q_ALTITUDE);
    }
    private static int getSortedAltitude(int payload){
        //11 10 09 08 07 06 05 04 03 02 01 00
        //C1 A1 C2 A2 C4 A4 B1 D1 B2 D2 B4 D4
        //D1 D2 D4 A1 A2 A4 B1 B2 B4 C1 C2 C4
        int[] order = {9, 3, 10, 4, 11, 5, 6, 0, 7, 1, 8, 2};
        int sorted = 0;
        for (int i = 0; i < order.length; i++) {
            sorted = sorted | ((Bits.testBit(payload, i) ? 1:0) << order[i]);
        }
        return sorted;
    }
    private static int decodeGray(int value, int size){
        int decoded = value;
        for (int i = 1; i < size; i++) {
            decoded = decoded ^ (value >>> i);
        }
        return decoded;

    }

    /**
     *
     * @param payload
     * @return -2000 if invalid
     */
    private static int getValueWeirdAlgoAltitude(int payload){
        int sorted = getSortedAltitude(payload);
        int strongGroup = decodeGray(Bits.extractUInt(sorted, 3, 9), 9);
        int weakGroup = decodeGray(Bits.extractUInt(sorted, 0, 3), 3);
        // special cases
        if (weakGroup == 0 || weakGroup == 5 || weakGroup == 6){
            return -2000;
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
     *
     * @param payload
     * @return -2000 if invalid
     */
    private static double getAltitude(int payload){
        System.out.println(payload);
        if (Bits.testBit(payload, INDEX_Q_ALTITUDE)){
            int altitude = -1000 + removeQBit(payload) * 25;
            return Units.convert(altitude, Units.Length.FOOT, Units.Length.METER);
        }else{
            int altitude = getValueWeirdAlgoAltitude(payload);
            if (altitude == -2000) return altitude;
            return Units.convert(altitude, Units.Length.FOOT, Units.Length.METER);
        }
    }
    /**
     *
     * @param rawMessage
     * @return -2000 if invalid
     */
    private static double getAltitude(RawMessage rawMessage){
        int payload = getAltitudePayload(rawMessage);
        return getAltitude(payload);
    }
    private static double getX(RawMessage rawMessage){
        return ((double) Bits.extractUInt(rawMessage.payload(), 0, 17)) / SIZE_LONG_LAT;
    }
    private static double getY(RawMessage rawMessage){
        return ((double) Bits.extractUInt(rawMessage.payload(), 17, 17)) / SIZE_LONG_LAT;
    }
    private static int getParity(RawMessage rawMessage){
        return Bits.extractUInt(rawMessage.payload(), 34, 1);
    }
    public static AirbornePositionMessage of(RawMessage rawMessage){
        long timeStampNs = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        double altitude = getAltitude(rawMessage);
        if (altitude == -2000){
            return null;
        }
        int parity = getParity(rawMessage);
        double x = getX(rawMessage);
        double y = getY(rawMessage);
        return new AirbornePositionMessage(timeStampNs, icaoAddress, altitude, parity, x,y);
    }
}

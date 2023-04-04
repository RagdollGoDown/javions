package ch.epfl.javions.adsb;

/**
 * Takes a RawMessage and transforms it into
 */
public class MessageParser {

    private final static int[] AIRBORNE_VELOCITY_TYPECODE = {19};
    private final static int[] AIRBORNE_POSITION_TYPECODE = {9,10,11,12,13,14,15,16,17,18,20,21,22};
    private final static int[] AIRCRAFT_IDENTIFIER_TYPECODE = {1,2,3,4};

    private MessageParser(){}


    public static Message parse(RawMessage rawMessage){
        int typeCode = rawMessage.typeCode();

        if (typeCode <= 4){
            return AircraftIdentificationMessage.of(rawMessage);
        }
        else if (typeCode >= 9 && typeCode <= 22){
            return typeCode == 19 ?
                    AirborneVelocityMessage.of(rawMessage) : AirbornePositionMessage.of(rawMessage);
        }
        else {
            return null;
        }
    }
}

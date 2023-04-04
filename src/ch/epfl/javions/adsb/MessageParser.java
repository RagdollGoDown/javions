package ch.epfl.javions.adsb;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Takes a RawMessage and transforms it into one of the message types that
 * can be used to update the information on the plane
 */
public class MessageParser {

    private final static HashSet<Integer> AIRBORNE_VELOCITY_TYPECODE = new HashSet<>(List.of(19));
    private final static HashSet<Integer> AIRBORNE_POSITION_TYPECODE = new HashSet<>(List.of(9,10,11,12,13,14,15,16,17,18,20,21,22));
    private final static HashSet<Integer> AIRCRAFT_IDENTIFIER_TYPECODE = new HashSet<>(List.of(1,2,3,4));

    private MessageParser(){}

    /**
     * From a RawMessage, the method will get the message type and returned a parsed version of it
     * to find the type it uses the typeCode
     * @param rawMessage the message to be parsed
     * @return the parsed message
     *          null if the RawMessage is invalid
     */
    public static Message parse(RawMessage rawMessage){
        int typeCode = rawMessage.typeCode();

        if (AIRBORNE_VELOCITY_TYPECODE.contains(typeCode)){
            return AirborneVelocityMessage.of(rawMessage);
        } else if (AIRCRAFT_IDENTIFIER_TYPECODE.contains(typeCode)){
            return AircraftIdentificationMessage.of(rawMessage);
        } else if (AIRBORNE_POSITION_TYPECODE.contains(typeCode)) {
            return AirbornePositionMessage.of(rawMessage);
        }
        else {return null;}
    }
}

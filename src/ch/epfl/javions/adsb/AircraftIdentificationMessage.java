package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * Represents an ADS-B message of identification and category
 * @param timeStampNs time Stamp in nanosecond
 * @param icaoAddress icao adress of the message
 * @param category category of the message
 * @param callSign callSign of the message
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */

public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign) implements Message {
    private final static int POSITION_START_CA = 48;
    private final static int SIZE_CA = 3;
    private final static int SIZE_CHARACTER_CODE = 6;

    /**
     * Constructor of AircraftIdentificationMessage
     * @param timeStampNs time Stamp in nanosecond
     * @param icaoAddress icao adress of the message
     * @param category category of the message
     * @param callSign callSign of the message
     * @throws NullPointerException if icaoAdress or callSign are null
     * @throws IllegalArgumentException if timeStampNS is negative
     */
    public AircraftIdentificationMessage{
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    /**
     * Build an instance of AircraftIdentificationMessage from a RawMessage
     * @param message a RawMessage
     * @return an instance of AircraftIdentificationMessage
     *          null if callSign is invalid
     */
    public static AircraftIdentificationMessage of(RawMessage message){
        long payload = message.payload();

        //if (message.typeCode() > 4 || message.typeCode() < 1) return null;

        byte bitsDePoidsFort = (byte)(14 - message.typeCode());
        byte champCA = (byte)Bits.extractUInt(payload,POSITION_START_CA, SIZE_CA);

        int category = (bitsDePoidsFort << 4) | champCA;

        CallSign callSign = decodeCallSign(payload);

        if (callSign == null) return null;
        return new AircraftIdentificationMessage(message.timeStampNs(),message.icaoAddress(),category,callSign);
    }


    /**
     * Decode the call sign from a payload
     * @param payload the payload of a RawMessage
     * @return value of the CallSign
     *         null if the Character is invalid
     */
    private static CallSign decodeCallSign(long payload){
        int currentCharacterCode;
        Character currentCharacter;
        StringBuilder callsignStringBuilder = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            currentCharacterCode = Bits.extractUInt(payload, (7 - i)*SIZE_CHARACTER_CODE, SIZE_CHARACTER_CODE);
            currentCharacter = decodeCharacter(currentCharacterCode);

            //if return null the character is invalid
            if (currentCharacter == null) return null;

            //spaces are used to mark empty emplacements
            if (currentCharacter != ' ') callsignStringBuilder.append(currentCharacter);
        }

        return new CallSign(callsignStringBuilder.toString());
    }

    /**
     * Gets the character linked to a value
     * @param characterCode a value
     * @return the character the value represents
     *         null if no character has this value
     */
    private static Character decodeCharacter(int characterCode){

        if (characterCode == 32 || (48<=characterCode && characterCode<=57)){
            // space and numbers
            return (char) characterCode;
        } else if (1<= characterCode && characterCode<=26) {
            // letters
            return (char) (characterCode+64);
        }else {
            return null;
        }
        /*
        switch (characterCode){
            case 1: return 'A';
            case 2: return 'B';
            case 3: return 'C';
            case 4: return 'D';
            case 5: return 'E';
            case 6: return 'F';
            case 7: return 'G';
            case 8: return 'H';
            case 9: return 'I';
            case 10: return 'J';
            case 11: return 'K';
            case 12: return 'L';
            case 13: return 'M';
            case 14: return 'N';
            case 15: return 'O';
            case 16: return 'P';
            case 17: return 'Q';
            case 18: return 'R';
            case 19: return 'S';
            case 20: return 'T';
            case 21: return 'U';
            case 22: return 'V';
            case 23: return 'W';
            case 24: return 'X';
            case 25: return 'Y';
            case 26: return 'Z';
            case 32: return ' ';
            case 48: return '0';
            case 49: return '1';
            case 50: return '2';
            case 51: return '3';
            case 52: return '4';
            case 53: return '5';
            case 54: return '6';
            case 55: return '7';
            case 56: return '8';
            case 57: return '9';
            default: return null;
        }

         */
    }
}

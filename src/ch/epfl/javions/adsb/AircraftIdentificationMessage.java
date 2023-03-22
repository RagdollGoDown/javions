package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign) implements Message {
    private final static int POSITION_START_CA = 48;
    private final static int SIZE_CA = 3;
    private final static int SIZE_CHARACTER_CODE = 6;

    public AircraftIdentificationMessage{
        if (icaoAddress == null || callSign == null) throw new NullPointerException();
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    public static AircraftIdentificationMessage of(RawMessage message){
        long payload = message.payload();

        if (message.typeCode() > 4 || message.typeCode() < 1) return null;

        byte bitsDePoidsFort = (byte)(14 - message.typeCode());
        byte champCA = (byte)Bits.extractUInt(payload,POSITION_START_CA, SIZE_CA);

        int category = (bitsDePoidsFort << 4) | champCA;

        int currentCharacterCode;
        Character currentCharacter;
        StringBuilder callsignStringBuilder = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            currentCharacterCode = Bits.extractUInt(payload, (7 - i)*SIZE_CHARACTER_CODE, SIZE_CHARACTER_CODE);
            currentCharacter = DecodeCharacter(currentCharacterCode);

            //s'il retourne null c'est que le character est invalide
            if (currentCharacter == null) return null;

            //les espaces servent à marquer des endroits vides
            if (currentCharacter != ' ') callsignStringBuilder.append(currentCharacter);
        }

        CallSign callSign = new CallSign(callsignStringBuilder.toString());

        return new AircraftIdentificationMessage(message.timeStampNs(),message.icaoAddress(),category,callSign);
    }

    private static Character DecodeCharacter(int characterCode){
        /*
        if (characterCode == 32 || (48<=characterCode && characterCode<=57)){
            return (char) characterCode;
        } else if (1<= characterCode && characterCode<=26) {
            return (char) (characterCode+64);
        }else {
            return null;
        }*/
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
    }
}

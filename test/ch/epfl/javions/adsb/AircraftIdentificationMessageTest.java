package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AircraftIdentificationMessageTest {
    private record RawMessageData(long timeStampNs, String bytes) {}
    private static final List<RawMessageData> EXPECTED_RAW_MESSAGE_DATA = List.of(
            new RawMessageData(1499146900L, "8D4D2228234994B7284820323B81"),
            new RawMessageData(2240535600L, "8F01024C233530F3CF6C60A19669"),
            new RawMessageData(2698727800L, "8D49529923501439CF1820419C55"),
            new RawMessageData(3215880100L, "8DA4F23925101331D73820FC8E9F"),
            new RawMessageData(4103219900L, "8D4B2964212024123E0820939C6F"));


    private static List<RawMessage> getListRawMessage(List<RawMessageData> rawMessages){
        List<RawMessage> result = new ArrayList<>();
        for (var message : EXPECTED_RAW_MESSAGE_DATA) {
            var messageBytes = HexFormat.of().parseHex(message.bytes());
            var expectedPayload = 0L;
            for (var i = 4; i < 11; i += 1)
                expectedPayload = (expectedPayload << Byte.SIZE) | Byte.toUnsignedLong(messageBytes[i]);
            var rawMessage = RawMessage.of(message.timeStampNs(), messageBytes);
            result.add(rawMessage);
        }
        return result;
    }
    private static final List<RawMessage>  LIST_RAW_MESSAGE = getListRawMessage(EXPECTED_RAW_MESSAGE_DATA);
    @Test
    void category_trivial() {
        int[] expected = {163, 163, 163, 165, 161};
        for (int i = 0; i < expected.length; i++) {
            AircraftIdentificationMessage aircraftIdentificationMessage = AircraftIdentificationMessage.of(LIST_RAW_MESSAGE.get(i));
            assertEquals(expected[i], aircraftIdentificationMessage.category());
        }
    }
    @Test
    void callSign_trivial() {
        String[] expected = {"RYR7JD", "MSC3361", "TAP931", "DAL153", "HBPRO"};
        for (int i = 0; i < expected.length; i++) {
            AircraftIdentificationMessage aircraftIdentificationMessage = AircraftIdentificationMessage.of(LIST_RAW_MESSAGE.get(i));
            assertEquals(expected[i], aircraftIdentificationMessage.callSign().string());
        }
    }
    @Test
    void icao_trivial() {
        String[] expected = {"4D2228", "01024C", "495299", "A4F239", "4B2964"};
        for (int i = 0; i < expected.length; i++) {
            AircraftIdentificationMessage aircraftIdentificationMessage = AircraftIdentificationMessage.of(LIST_RAW_MESSAGE.get(i));
            assertEquals(expected[i], aircraftIdentificationMessage.icaoAddress().string());
        }
    }


    private String[] AircraftIdentificationMessagesExpected = {
            """
            AircraftIdentificationMessage[
             timeStampNs=1499146900,
             icaoAddress=IcaoAddress[string=4D2228],
             category=163,
             callSign=CallSign[string=RYR7JD]]""",
            """
            AircraftIdentificationMessage[
             timeStampNs=2240535600,
             icaoAddress=IcaoAddress[string=01024C],
             category=163,
             callSign=CallSign[string=MSC3361]]""",
            """
            AircraftIdentificationMessage[
             timeStampNs=2698727800,
             icaoAddress=IcaoAddress[string=495299],
             category=163,
             callSign=CallSign[string=TAP931]]""",
            """
            AircraftIdentificationMessage[
             timeStampNs=3215880100,
             icaoAddress=IcaoAddress[string=A4F239],
             category=165,
             callSign=CallSign[string=DAL153]]""",
            """
            AircraftIdentificationMessage[
             timeStampNs=4103219900,
             icaoAddress=IcaoAddress[string=4B2964],
             category=161,
             callSign=CallSign[string=HBPRO]]"""
    };


    @Test
    void of() throws IOException {
        String f = "resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            AircraftIdentificationMessage a;
            int i = 0;

            while ((m = d.nextMessage()) != null && i < 5){
                    if ((a = AircraftIdentificationMessage.of(m)) != null){
                        i++;
                        System.out.println(a);
                        //alors le test ne marche pas mais jsp si c'est à cause de comment ils mettent les textes pour le test
                        assertTrue(AircraftIdentificationMessage.of(m).toString()
                                .equals(AircraftIdentificationMessagesExpected[1]));
                    }
            }
            System.out.println(i);

        }
    }
}
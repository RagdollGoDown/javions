package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;


import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AirbornePositionMessageTest {
    private record RawMessageData(long timeStampNs, String bytes) {}
    private static final List<RawMessageData> EXPECTED_RAW_MESSAGE_DATA = List.of(
            new RawMessageData(75898000L, "8D49529958B302E6E15FA352306B"),
            new RawMessageData(116538700L, "8D4241A9601B32DA4367C4C3965E"),
            new RawMessageData(138560100L, "8D4D222860B985F7F53FAB33CE76"),
            new RawMessageData(208135700L, "8D4D029F594B52EFDB7E94ACEAC8"),
            new RawMessageData(233069800L, "8D3C648158AF92F723BC275EC692"),
            new RawMessageData(10, "8D39203559B225F07550ADBE328F"),
            new RawMessageData(10, "8DAE02C85864A5F5DD4975A1A3F5"));


    private static List<RawMessage> getListRawMessage(List<RawMessageData> rawMessages){
        List<RawMessage> result = new ArrayList<>();
        for (var message : EXPECTED_RAW_MESSAGE_DATA) {
            var messageBytes = HexFormat.of().parseHex(message.bytes());
            var rawMessage = RawMessage.of(message.timeStampNs(), messageBytes);
            result.add(rawMessage);
        }
        return result;
    }
    private static final List<RawMessage>  LIST_RAW_MESSAGE = getListRawMessage(EXPECTED_RAW_MESSAGE_DATA);
    @Test
    void x_trivial() {
        double[] expected = {0.6867904663085938, 0.702667236328125, 0.6243515014648438, 0.747222900390625, 0.8674850463867188};
        for (int i = 0; i < expected.length; i++) {
            AirbornePositionMessage airbornePositionMessage = AirbornePositionMessage.of(LIST_RAW_MESSAGE.get(i));
            assertEquals(expected[i], airbornePositionMessage.x());
        }
    }
    @Test
    void y_trivial() {
        double[] expected = {0.7254638671875, 0.7131423950195312, 0.4921417236328125, 0.7342300415039062, 0.7413406372070312};
        for (int i = 0; i < expected.length; i++) {
            AirbornePositionMessage airbornePositionMessage = AirbornePositionMessage.of(LIST_RAW_MESSAGE.get(i));
            assertEquals(expected[i], airbornePositionMessage.y());
        }
    }
    @Test
    void altitude_trivial() {
        double[] expected = {10546.08, 1303.02, 10972.800000000001, 4244.34, 10370.82};
        for (int i = 0; i < expected.length; i++) {
            AirbornePositionMessage airbornePositionMessage = AirbornePositionMessage.of(LIST_RAW_MESSAGE.get(i));
            assertEquals(expected[i], airbornePositionMessage.altitude());
        }
    }
    @Test
    void altitudeQ(){
        double[] expected = {3474.72 , 7315.20};
        for (int i = 0; i < expected.length; i++) {
            AirbornePositionMessage airbornePositionMessage = AirbornePositionMessage.of(LIST_RAW_MESSAGE.get(i+5));
            assertEquals(expected[i], airbornePositionMessage.altitude(), 0.01);
        }
    }
    @Test
    void altitudeNull(){
        String value = "8D49529958" + "F6A" + "2E6E15FA352306B";
        RawMessage rawMessage = RawMessage.of(10, HexFormat.of().parseHex(value));
        AirbornePositionMessage airbornePositionMessage = AirbornePositionMessage.of(rawMessage);
        assertEquals(1, airbornePositionMessage.altitude(), 0.01);

    }
    @Test
    void parity_trivial() {
        double[] expected = {0, 0, 1, 0, 0};
        for (int i = 0; i < expected.length; i++) {
            AirbornePositionMessage airbornePositionMessage = AirbornePositionMessage.of(LIST_RAW_MESSAGE.get(i));
            assertEquals(expected[i], airbornePositionMessage.parity());
        }
    }

    @Test
    void icao_trivial() {
        String[] expectedX = {"495299", "4241A9", "4D2228", "4D029F", "3C6481"};
        for (int i = 0; i < expectedX.length; i++) {
            AirbornePositionMessage airbornePositionMessage = AirbornePositionMessage.of(LIST_RAW_MESSAGE.get(i));
            assertEquals(expectedX[i], airbornePositionMessage.icaoAddress().string());
        }
    }

}
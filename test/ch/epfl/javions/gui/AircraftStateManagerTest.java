package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class AircraftStateManagerTest {

    @Test
    void updateWithMessage() throws IOException {
        AircraftStateManager aircraftStateManager = new AircraftStateManager();

        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/messages_20230318_0915.bin")))){
            byte[] bytes = new byte[RawMessage.LENGTH];

            int i = 0;

            while (true) {
                System.out.println(i++);
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                RawMessage rawMessage = RawMessage.of(timeStampNs,bytes);
                Message message = MessageParser.parse(rawMessage);
                aircraftStateManager.updateWithMessage(MessageParser.parse(rawMessage));
            }
        } catch (EOFException e) { /* nothing to do */ }
    }

    @Test
    void knownPositionAircraftsProperty() {
    }
}
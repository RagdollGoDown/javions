package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class AirborneVelocityMessageTest {
    @Test
    void velocityMessagesInBin() throws IOException {
        String f = "resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            int i = 0;

            while ((m = d.nextMessage()) != null) {
                Message pm = MessageParser.parse(m);

                if (pm instanceof AirborneVelocityMessage){
                    System.out.println(pm.toString());
                    i++;
                }
            }

            System.out.println(i);
        }
    }
}
package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class AircraftIdentificationMessageTest {

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
            int i = 0;
            while ((m = d.nextMessage()) != null && i < 5){

                if (AircraftIdentificationMessage.of(m) != null){

                    System.out.println(AircraftIdentificationMessage.of(m).toString());
                    assertTrue(AircraftIdentificationMessage.of(m).toString().equals(AircraftIdentificationMessagesExpected[1]));
                    i++;
                }
            }
            System.out.println(i);
        }
    }
}
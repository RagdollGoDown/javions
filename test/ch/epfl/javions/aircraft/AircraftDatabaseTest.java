package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AircraftDatabaseTest {

    @Test
    void checkAircraftDatabaseGet() throws Exception {
        String icaoAddressString = "14F114";
        IcaoAddress icaoAddress = new IcaoAddress(icaoAddressString);
        AircraftDatabase aircraftDatabase = new AircraftDatabase("resources/aircraft.zip");

        var actual = aircraftDatabase.get(icaoAddress);
        var expected = new AircraftData(new AircraftRegistration("RA-61716"),
                new AircraftTypeDesignator("A148"),
                "ANTONOV An-148",
                new AircraftDescription("L2J"),
                WakeTurbulenceCategory.MEDIUM);

        assertEquals(actual.toString(),expected.toString());
    }

    @Test
    void checkAircraftDatabaseGetWhenArgsAreNull() throws Exception {
        String icaoAddressString = "31D914";
        IcaoAddress icaoAddress = new IcaoAddress(icaoAddressString);
        AircraftDatabase aircraftDatabase = new AircraftDatabase("resources/aircraft.zip");

        var actual = aircraftDatabase.get(icaoAddress);
        var expected = new AircraftData(new AircraftRegistration("I-B375"),
                new AircraftTypeDesignator("ULAC"),
                "",
                new AircraftDescription("L0-"),
                WakeTurbulenceCategory.UNKNOWN);

        assertEquals(actual.toString(),expected.toString());
    }

    @Test
    void checkAircraftDatabaseGetWhenFileIsWrong() {
        String icaoAddressString = "31D914";
        IcaoAddress icaoAddress = new IcaoAddress(icaoAddressString);
        AircraftDatabase aircraftDatabase = new AircraftDatabase("/aircraft/resources.zip");

        assertThrows(IOException.class,() -> aircraftDatabase.get(icaoAddress));
    }
}
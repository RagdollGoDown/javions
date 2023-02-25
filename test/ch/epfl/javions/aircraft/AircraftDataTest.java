package ch.epfl.javions.aircraft;

import ch.epfl.javions.WakeTurbulenceCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftDataTest {

    @Test
    void constructorTest(){
        assertDoesNotThrow(() -> new AircraftData(new AircraftRegistration("HB-JDC"),new AircraftTypeDesignator("A20N"),"4B181",new AircraftDescription("L2J"), WakeTurbulenceCategory.LIGHT));
        assertThrows(NullPointerException.class,() -> new AircraftData(null,new AircraftTypeDesignator("A20N"),"4B181",new AircraftDescription("L2J"), WakeTurbulenceCategory.LIGHT));
        assertThrows(NullPointerException.class,() -> new AircraftData(new AircraftRegistration("HB-JDC"),null,"4B181",new AircraftDescription("L2J"), WakeTurbulenceCategory.LIGHT));
        assertThrows(NullPointerException.class,() -> new AircraftData(new AircraftRegistration("HB-JDC"),new AircraftTypeDesignator("A20N"),null,new AircraftDescription("L2J"), WakeTurbulenceCategory.LIGHT));
        assertThrows(NullPointerException.class,() -> new AircraftData(new AircraftRegistration("HB-JDC"),new AircraftTypeDesignator("A20N"),"4B181",null, WakeTurbulenceCategory.LIGHT));
        assertThrows(NullPointerException.class,() -> new AircraftData(new AircraftRegistration("HB-JDC"),new AircraftTypeDesignator("A20N"),"4B181",new AircraftDescription("L2J"), null));
    }
}
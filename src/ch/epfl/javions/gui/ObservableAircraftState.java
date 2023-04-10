package ch.epfl.javions;

import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;

public final class ObservableAircraftState implements Observer, AircraftStateSetter {

    private final IcaoAddress icaoAddress;
    private final AircraftData aircraftData;

    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData){
        if (aircraftData == null){throw new NullPointerException();}

        this.icaoAddress = icaoAddress;
        this.aircraftData = aircraftData;
    }

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {

    }

    @Override
    public void setCategory(int category) {

    }

    @Override
    public void setCallSign(CallSign callSign) {

    }

    @Override
    public void setPosition(GeoPos position) {

    }

    @Override
    public void setAltitude(double altitude) {

    }

    @Override
    public void setVelocity(double velocity) {

    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {

    }
}

package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

public class AircraftStateAccumulator <T extends AircraftStateSetter>{

    private final static long TIME_BETWEEN_POSITIONS = 10000000000L;

    private final T stateSetter;

    private AirbornePositionMessage[] airbornePositions;

    public AircraftStateAccumulator(T stateSetter){
        if (stateSetter == null){throw new NullPointerException();}
        this.stateSetter = stateSetter;

        airbornePositions = new AirbornePositionMessage[2];
    }

    public T stateSetter() {
        return stateSetter;
    }

    public void update(Message message){
        stateSetter.setLastMessageTimeStampNs(message.timeStampNs());

        switch (message){
            case AircraftIdentificationMessage aim ->
                setStateOnIdentifierMessage(aim);
            case AirbornePositionMessage apb ->
                setStateOnPositionMessage(apb);
            case AirborneVelocityMessage avm ->
                setStateOnVelocityMessage(avm);
            default -> throw new Error("unrecognized message");
        }
    }

    private void setStateOnIdentifierMessage(AircraftIdentificationMessage aim){
        stateSetter.setCategory(aim.category());
        stateSetter.setCallSign(aim.callSign());
    }

    private void setStateOnPositionMessage(AirbornePositionMessage apb){
        stateSetter.setAltitude(apb.altitude());

        airbornePositions[apb.parity()] = apb;

        if (airbornePositions[(apb.parity() + 1) % 2] != null
                && apb.timeStampNs() <= airbornePositions[(apb.parity() + 1) % 2].timeStampNs() + TIME_BETWEEN_POSITIONS){

            stateSetter.setPosition(CprDecoder.decodePosition(
                    airbornePositions[0].x(), airbornePositions[0].y(),
                    airbornePositions[1].x(), airbornePositions[1].y(),
                    apb.parity()));
        }
    }

    private void setStateOnVelocityMessage(AirborneVelocityMessage avm){
        stateSetter.setVelocity(avm.speed());
        stateSetter.setTrackOrHeading(avm.trackOrHeading());
    }
}

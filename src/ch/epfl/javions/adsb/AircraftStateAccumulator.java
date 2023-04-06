package ch.epfl.javions.adsb;

/**
 * The job of this class is to pass parsed messages to a state setter for a specific plane
 * @param <T> the state setter to be set
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach Cadet (3347505)
 */
public class AircraftStateAccumulator <T extends AircraftStateSetter>{

    private final static long TIME_BETWEEN_POSITIONS = 10000000000L;

    private final T stateSetter;

    private AirbornePositionMessage[] airbornePositions;

    /**
     * Constructor for AircraftStateAccumulator
     * @param stateSetter the state setter associated to the plane
     * @throws NullPointerException if the state setter is null
     */
    public AircraftStateAccumulator(T stateSetter){
        if (stateSetter == null){throw new NullPointerException();}
        this.stateSetter = stateSetter;

        airbornePositions = new AirbornePositionMessage[2];
    }

    /**
     * @return the state setter
     */
    public T stateSetter() {
        return stateSetter;
    }

    /**
     * takes a message, checks what type of message it is and puts puts the message in the state setter
     * @param message the parsed message
     */
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

    /**
     * Gives the information from an AircraftIdentificationMessage to the state setter
     * @param aim the message sent
     */
    private void setStateOnIdentifierMessage(AircraftIdentificationMessage aim){
        stateSetter.setCategory(aim.category());
        stateSetter.setCallSign(aim.callSign());
    }

    /**
     * Gives the information from an AirbornePositionMessage to the state setter
     * if there wasn't a previous position to calculate from then it won't update the position
     * if the message arrives more than 10 seconds after the previous position message then it won't update
     * @param apm the message sent
     */
    private void setStateOnPositionMessage(AirbornePositionMessage apm){
        stateSetter.setAltitude(apm.altitude());

        airbornePositions[apm.parity()] = apm;

        if (airbornePositions[(apm.parity() + 1) % 2] != null
                && apm.timeStampNs() <= airbornePositions[(apm.parity() + 1) % 2].timeStampNs() + TIME_BETWEEN_POSITIONS){

            stateSetter.setPosition(CprDecoder.decodePosition(
                    airbornePositions[0].x(), airbornePositions[0].y(),
                    airbornePositions[1].x(), airbornePositions[1].y(),
                    apm.parity()));
        }
    }

    /**
     * Gives the information from an AirborneVelocityMessage to the state setter
     * @param avm the message sent
     */
    private void setStateOnVelocityMessage(AirborneVelocityMessage avm){
        stateSetter.setVelocity(avm.speed());
        stateSetter.setTrackOrHeading(avm.trackOrHeading());
    }
}

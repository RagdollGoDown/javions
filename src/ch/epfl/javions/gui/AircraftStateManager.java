package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.Map;

/**
 * Keep up to date the states of a set of aircraft according to the messages received from them
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public final class AircraftStateManager {
    private final static long MINUTE_IN_NS = 60000000000L;
    private final AircraftDatabase aircraftDatabase;
    private Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> mapStringIcaoToAircraft;
    private final ObservableSet<ObservableAircraftState> modifiableKnownPositionAircrafts;
    private final ObservableSet<ObservableAircraftState> unmodifiableKnownPositionAircrafts;
    private long lastMessageNs;

    public AircraftStateManager(String pathAircraftDatabase){
        aircraftDatabase = new AircraftDatabase(pathAircraftDatabase);
        modifiableKnownPositionAircrafts = FXCollections.observableSet();
        unmodifiableKnownPositionAircrafts = FXCollections.unmodifiableObservableSet(modifiableKnownPositionAircrafts);
        lastMessageNs = 0;
    }
    public AircraftStateManager() {
        this("aircraft.zip");
    }

    public ObservableSet<ObservableAircraftState> states() {
        return unmodifiableKnownPositionAircrafts;
    }


    /**
     * Update the state accumulator of the aircraft of the message given
     * @param message a Message from an aircraft
     */
    public void updateWithMessage(Message message) {
        if (message == null) throw new NullPointerException();

        IcaoAddress icaoAddress = message.icaoAddress();
        if (!mapStringIcaoToAircraft.containsKey(icaoAddress)){
            AircraftStateAccumulator<ObservableAircraftState> aircraftStateAccumulator;
            try{
                AircraftData aircraftData = aircraftDatabase.get(icaoAddress);
                aircraftStateAccumulator = new AircraftStateAccumulator<>(
                        new ObservableAircraftState(message.icaoAddress(), aircraftData));
            }
            catch (IOException e){
                aircraftStateAccumulator = new AircraftStateAccumulator<>(
                        new ObservableAircraftState(message.icaoAddress(), null));
            }

            mapStringIcaoToAircraft.put(icaoAddress, aircraftStateAccumulator);
        }
        mapStringIcaoToAircraft.get(icaoAddress).update(message);

        // check if not null add it in the set of knownPositionAircraft
        if (mapStringIcaoToAircraft.get(icaoAddress).stateSetter().getPosition() != null) {
            modifiableKnownPositionAircrafts.add(mapStringIcaoToAircraft.get(icaoAddress).stateSetter());
            if (lastMessageNs < mapStringIcaoToAircraft.get(icaoAddress).stateSetter().getLastMessageTimeStampNs());
                lastMessageNs = mapStringIcaoToAircraft.get(icaoAddress).stateSetter().getLastMessageTimeStampNs();
        }
    }

    /**
     * Remove the stateSetterAircraft of the aircraft that no messages has been sent 1 minute before the last message given in the updateWithMessage
     */
    public void purge() {
        for (ObservableAircraftState stateSetterAircraft : modifiableKnownPositionAircrafts) {
            if (stateSetterAircraft.getLastMessageTimeStampNs() <= lastMessageNs - MINUTE_IN_NS){
                modifiableKnownPositionAircrafts.remove(stateSetterAircraft);
            }
        }
    }

    //TODO get ou property?

    /**
     * @return the set of the AircraftSate where we know the position
     */
    public ObservableSet<ObservableAircraftState> knownPositionAircraftsProperty(){
        return unmodifiableKnownPositionAircrafts;
    }



}

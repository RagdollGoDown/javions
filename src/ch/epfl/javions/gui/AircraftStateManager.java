package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.*;

/**
 * Keep up to date the states of a set of aircraft according to the messages received from them
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public final class AircraftStateManager {
    private final static long MINUTE_IN_NS = 60000000000L;
    private final AircraftDatabase aircraftDatabase;
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> icaoToAircraft;
    private final ObservableSet<ObservableAircraftState> modifiableKnownPositionAircrafts;
    private final ObservableSet<ObservableAircraftState> unmodifiableKnownPositionAircrafts;
    private long lastMessageNs;

    public AircraftStateManager(AircraftDatabase aircraftDatabase){
        this.aircraftDatabase = aircraftDatabase;
        icaoToAircraft = new HashMap<>();
        modifiableKnownPositionAircrafts = FXCollections.observableSet();
        unmodifiableKnownPositionAircrafts = FXCollections.unmodifiableObservableSet(modifiableKnownPositionAircrafts);
        lastMessageNs = 0;
    }
    public AircraftStateManager() {
        this(new AircraftDatabase("resources/aircraft.zip"));
    }

    public ObservableSet<ObservableAircraftState> states() {
        return unmodifiableKnownPositionAircrafts;
    }


    /**
     * Update the state accumulator of the aircraft of the message given
     * @param message a Message from an aircraft
     */
    public void updateWithMessage(Message message) {
        if (message == null) return;

        IcaoAddress icaoAddress = message.icaoAddress();
        if (!icaoToAircraft.containsKey(icaoAddress)){
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
            icaoToAircraft.put(icaoAddress, aircraftStateAccumulator);
        }
        icaoToAircraft.get(icaoAddress).update(message);

        // check if not null add it in the set of knownPositionAircraft
        if (icaoToAircraft.get(icaoAddress).stateSetter().getPosition() != null) {
            modifiableKnownPositionAircrafts.add(icaoToAircraft.get(icaoAddress).stateSetter());
            if (lastMessageNs < icaoToAircraft.get(icaoAddress).stateSetter().getLastMessageTimeStampNs())
                lastMessageNs = icaoToAircraft.get(icaoAddress).stateSetter().getLastMessageTimeStampNs();
        }
    }

    /**
     * Remove the stateSetterAircraft of the aircraft that no messages has been sent 1 minute before the last message given in the updateWithMessage
     */
    public void purge() {
        Set<ObservableAircraftState> statesToRemove = new HashSet<>();

        for (ObservableAircraftState stateSetterAircraft : modifiableKnownPositionAircrafts) {
            if (stateSetterAircraft.getLastMessageTimeStampNs() <= lastMessageNs - MINUTE_IN_NS){
                statesToRemove.add(stateSetterAircraft);
            }
        }
        modifiableKnownPositionAircrafts.removeAll(statesToRemove);
        //remove from map
        for (ObservableAircraftState state:
             statesToRemove) {
            icaoToAircraft.remove(state.address());
        }
    }

}

package ch.epfl.javions.gui.terminal;

import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.gui.AircraftStateManager;
import ch.epfl.javions.gui.ObservableAircraftState;

import java.io.*;
import java.util.*;

public class TerminalInterface {
    private static final int SIZE_ICAO = 6;
    private static final int SIZE_INDICATIF = 8;
    private static final int SIZE_IMMAT = 6;
    private static final int SIZE_MODEL = 20;
    private static final int SIZE_LONG = 8;
    private static final int SIZE_LAT = 8;
    private static final int SIZE_ALT = 5;
    private static final int SIZE_SPEED = 4;
    private final static String HEADER = "OACI    Indicatif Immat.  Modèle             Longitude   Latitude   Alt.  Vit.\n" +
            "――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――";
    private static String pathMessageFile;
    private static AircraftStateManager aircraftStateManager;
    private static class AddressComparator
            implements Comparator<ObservableAircraftState> {
        @Override
        public int compare(ObservableAircraftState o1,
                           ObservableAircraftState o2) {
            String s1 = o1.address().string();
            String s2 = o2.address().string();
            return s1.compareTo(s2);
        }
    }
    public static void main(String[] args) throws IOException {
        init();
        updateWithMessage();
    }

    /**
     * Initialize t
     */
    public static void init(){
        aircraftStateManager = new AircraftStateManager();
        pathMessageFile = "resources/messages_20230318_0915.bin";
    }
    private static void updateWithMessage() throws IOException {
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(pathMessageFile)))){
            byte[] bytes = new byte[RawMessage.LENGTH];

            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                RawMessage rawMessage = RawMessage.of(timeStampNs,bytes);
                aircraftStateManager.updateWithMessage(MessageParser.parse(rawMessage));
                display();
            }
        } catch (EOFException e) { /* nothing to do */ }
    }
    /**
     * Display known position aircraft in terminal
     */
    private static void display(){
        System.out.println(HEADER);
        System.out.println(observableSetToString());


    }
    private static String stringBuildLine(ObservableAircraftState observableAircraftState){
        try {
            String ICAO = observableAircraftState.address().toString();
            String indicatif = observableAircraftState.getCallSign();
            String immat = observableAircraftState.aircraftData().registration().string();
            String model = observableAircraftState.aircraftData().model();
            String longitude = String.format("%f", observableAircraftState.getPosition().longitude());
            String latitude = String.format("%f", observableAircraftState.getPosition().latitude());
            String altitude = String.format("%f", observableAircraftState.getAltitude());
            String vitesse = String.format("%f", observableAircraftState.getVelocity());

            return ICAO+ "  " + indicatif+ "  " + immat+ "  " + model+ "  " + longitude + "  " + latitude + "  " + altitude + "  " + vitesse + "\n";
        }catch (NullPointerException e){

        }
        return "y a pas\n";

    }

    private static String observableSetToString(){
        List<ObservableAircraftState> knownPositionAircraft = new ArrayList<>();
        for (ObservableAircraftState aircraftState : aircraftStateManager.getKnownPositionAircrafts()) {
            knownPositionAircraft.add(aircraftState);
        }

        knownPositionAircraft.sort(new AddressComparator());
        StringBuilder sb = new StringBuilder();


        for(ObservableAircraftState aircraftState : knownPositionAircraft){
            sb.append(stringBuildLine(aircraftState));
        }

        return sb.toString();
    }


}

package ch.epfl.javions.gui.terminal;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.gui.AircraftStateManager;
import ch.epfl.javions.gui.ObservableAircraftState;

import java.io.*;
import java.util.*;

import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;

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
            long timeBegin = nanoTime();
            System.out.println( timeBegin);
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                RawMessage rawMessage = RawMessage.of(timeStampNs,bytes);
                aircraftStateManager.updateWithMessage(MessageParser.parse(rawMessage));
                aircraftStateManager.purge();
                long currentTime = nanoTime();
                long sleepTime = (rawMessage.timeStampNs() - (currentTime - timeBegin))/1000000;

                sleep( (sleepTime > 0) ?  sleepTime:0);
                display();
            }
        } catch (EOFException e) { /* nothing to do */ } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Display known position aircraft in terminal
     */
    private static void display(){
        System.out.println(HEADER);
        System.out.println(observableSetToString());

        String CSI = "\u001B[";
        String CLEAR_SCREEN = CSI + "2J";
        System.out.print(CLEAR_SCREEN);
    }
    private static String stringBuildLine(ObservableAircraftState observableAircraftState){

        String ICAO = observableAircraftState.address().string();
        String indicatif = String.format("%-8s",observableAircraftState.getCallSign() == null ? "      " : observableAircraftState.getCallSign());
        String immat = observableAircraftState.aircraftData() == null ? "      "  : observableAircraftState.aircraftData().registration().string();
        String model = observableAircraftState.aircraftData() == null ? "      "  : observableAircraftState.aircraftData().model();
        String longitude = String.format("%f", Units.convertTo(observableAircraftState.getPosition().longitude(), Units.Angle.DEGREE));
        String latitude = String.format("%f",  Units.convertTo(observableAircraftState.getPosition().latitude(), Units.Angle.DEGREE));
        String altitude = String.format("%f", observableAircraftState.getAltitude());
        String vitesse = String.format("%f", Units.convert(observableAircraftState.getVelocity(), Units.Speed.METER_PER_SECOND, Units.Speed.KILOMETER_PER_HOUR));
        String lastTimeStamp = String.format("%dl", observableAircraftState.getLastMessageTimeStampNs());

        return ICAO+ "  " + indicatif+ "  " + immat+ "  " + model+ "  " + longitude + "  " + latitude + "  " +
                altitude + "  " + vitesse + "\n";
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

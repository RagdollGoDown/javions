
package ch.epfl.javions.aircraft;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipFile;

public final class AircraftDatabase {

    private String fileName;

    public AircraftDatabase(String fileName){
        if (fileName == null) throw new NullPointerException();

        this.fileName = fileName;
    }

    /**
     * Finds a plane using the IcaoAddress and returns it's AircraftData
     * @param address the IcaoAddress of the plane
     * @return the AircraftData of the plane
     * @throws IOException if there are any problems when reading the files
     */
    public AircraftData get(IcaoAddress address) throws IOException {
        String file = getClass().getResource(fileName).getFile();
        String addressString = address.toString();

        String selectedLine = "";

        try (ZipFile zipFile = new ZipFile(file);
             InputStream stream = zipFile.getInputStream(zipFile.getEntry("14.csv"));
             Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            while ((selectedLine = bufferedReader.readLine()) != null){
                if (selectedLine.startsWith(addressString)) {
                    System.out.println(selectedLine);
                    break;
                }
            }
        }

        String[] aircraftDatabaseStrings = selectedLine.split(",",6);

        return new AircraftData(new AircraftRegistration(aircraftDatabaseStrings[1]),
                new AircraftTypeDesignator(aircraftDatabaseStrings[2]),
                aircraftDatabaseStrings[3],
                new AircraftDescription(aircraftDatabaseStrings[4]),
                WakeTurbulenceCategory.of(aircraftDatabaseStrings[5]));
    }
}
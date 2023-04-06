
package ch.epfl.javions.aircraft;

import ch.epfl.javions.ByteString;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipFile;

/**
 * Using the file name as a path, the class finds a file and gets information on a specific plane
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach Cadet (3347505)
 */
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
        String file = fileName;
        String addressString = address.string();

        int addressHex = Integer.parseInt(addressString, 16);

        String selectedLine = "";

        try (ZipFile zipFile = new ZipFile(file);
             InputStream stream = zipFile.getInputStream(zipFile.getEntry(addressString.substring(4) + ".csv"));
             Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            //iterate through lines
            while ((selectedLine = bufferedReader.readLine()) != null) {
                // if possibly pass on our searched address
                if (Integer.parseInt(selectedLine.substring(0,6),16) >= addressHex) {

                    // if it is our address, else return null
                    if (selectedLine.startsWith(addressString)) {

                        String[] aircraftDatabaseStrings = selectedLine.split(",", -1);
                        return new AircraftData(
                                new AircraftRegistration(aircraftDatabaseStrings[1]),
                                new AircraftTypeDesignator(aircraftDatabaseStrings[2]),
                                aircraftDatabaseStrings[3],
                                new AircraftDescription(aircraftDatabaseStrings[4]),
                                WakeTurbulenceCategory.of(aircraftDatabaseStrings[5])
                        );
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
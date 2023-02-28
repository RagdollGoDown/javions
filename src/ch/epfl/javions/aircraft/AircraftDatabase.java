package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipFile;

public final class AircraftDatabase {

    private String fileName;

    public AircraftDatabase(String fileName){
        if (fileName == null) throw new NullPointerException();

        this.fileName = fileName;
    }

    public AircraftData get(IcaoAddress address) throws IOException,NullPointerException {
        String file = getClass().getResource("/aircraft.zip").getFile();
        String addressString = address.toString();

        String lineRead = "";

        try (ZipFile zipFile = new ZipFile(file);
        InputStream stream = zipFile.getInputStream(zipFile.getEntry("14.csv"));
        Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader)) {

            while ((lineRead = bufferedReader.readLine()) != null){
                if (lineRead.startsWith(addressString)) {
                    System.out.println(lineRead);
                    break;
                }
            }
        }

        return null;
    }
}

package ch.epfl.javions.demodulation;

import ch.epfl.javions.Bits;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SamplesDecoder {

    private InputStream stream;
    private int batchSize;
    private byte[] readBytes;

    public SamplesDecoder(InputStream stream, int batchSize){
        if (batchSize < 0) throw new IllegalArgumentException();
        if (stream == null) throw new NullPointerException();

        this.batchSize = batchSize;
        this.readBytes = new byte[batchSize*2];
        this.stream = stream;
    }

    public int readBatch(short[] batch) throws IOException {
        if (batch.length != batchSize) throw new IllegalArgumentException();
        //TODO mettre les erreurs

        //On a besoin de savoir combien de byte qui peuvent être lu
        //pour savoir si on retourne batchsize ou le nombre lu de short
        int shortPouvantEtreLu = stream.available() / 2;

        //batch correspond au nombre de short normalement
        //donc on a besoin de deux fois ça dans le stream
        int nReadBytes = stream.readNBytes(readBytes,0,batchSize*2) ;

        assert (nReadBytes%2) == 0;

        for (int i = 0; i < nReadBytes; i+=2) {
            batch[i/2] = (short)(((Byte.toUnsignedInt(readBytes[i+1])<<8) | Byte.toUnsignedInt(readBytes[i])) - 2048);
        }

        return nReadBytes / 2;

        /*
        short tempByte = 0;

        //on utilise j comme index pour aller de deux en deux
        //TODO vérifier si c'est la bonne méthode
        for (int i = 0, j = 0; i < batchSize && i < shortPouvantEtreLu; i++, j = i * 2) {
            tempByte = (short)(tempByte | readBytes[j + 1]);
            tempByte = (short)(tempByte << 8);
            tempByte = (short)(tempByte | readBytes[j]);

            if (!Bits.testBit(readBytes[j+1],2)) tempByte = (short)(tempByte - 2048);
            /*System.out.println("byte at " + i + ", " + tempByte);
            System.out.println("j = " + j);
            System.out.println("fait de "+ Integer.toBinaryString(readBytes[j+1]) + " " + Integer.toBinaryString(readBytes[j]));*/

            batch[i] = tempByte;
            tempByte = 0;
        }

        return Math.min(shortPouvantEtreLu,batchSize);
    }
}

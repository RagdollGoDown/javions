package ch.epfl.javions.demodulation;


import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;


public class SamplesDecoder {

    private InputStream stream;
    private int batchSize;
    private byte[] readBytes;


    /**
     * Decode a little endian array of bytes into a short array
     * @param stream stream to decode
     * @param batchSize size of the batch
     */
    public SamplesDecoder(InputStream stream, int batchSize){
        if (batchSize < 0) throw new IllegalArgumentException();
        if (stream == null) throw new NullPointerException();

        this.batchSize = batchSize;
        this.readBytes = new byte[batchSize*2];
        this.stream = stream;
    }

    /**
     * Read bytes from the stream
     * @param batch array to fill
     * @return number of bytes read
     * @throws IOException
     * @throws IllegalArgumentException if batch length is not equal to batchsize
     */
    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);


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
    }
}

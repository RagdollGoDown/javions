package ch.epfl.javions.demodulation;


import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Transform the bytes from the AirSpy into signed 12-bit samples
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public final class SamplesDecoder {

    private final InputStream stream;
    private final int batchSize;
    private final byte[] readBytes;
    private static final int BIAS_SAMPLE = 2048; //1<<11


    /**
     * Decode a little endian array of bytes into a short array
     * @param stream stream to decode
     * @param batchSize size of the batch
     */
    public SamplesDecoder(InputStream stream, int batchSize){
        Preconditions.checkArgument(batchSize > 0);

        this.batchSize = batchSize;
        this.readBytes = new byte[batchSize*2];
        this.stream = Objects.requireNonNull(stream);
    }

    /**
     * Read bytes from the stream
     * @param batch array to fill
     * @return number of bytes read
     * @throws IOException in case of problems reading the stream
     * @throws IllegalArgumentException if batch length is not equal to batchsize
     */
    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int nReadBytes = stream.readNBytes(readBytes,0,batchSize*2) ;

        assert (nReadBytes%2) == 0;

        for (int i = 0; i < nReadBytes; i+=2) {
            batch[i/2] = (short)(((Byte.toUnsignedInt(readBytes[i+1])<<Byte.SIZE) | Byte.toUnsignedInt(readBytes[i])) - BIAS_SAMPLE);
        }
        return nReadBytes / 2;
    }
}

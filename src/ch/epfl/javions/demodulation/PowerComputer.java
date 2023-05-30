package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * Computing the signal power samples from the signed samples produced by a sample decoder
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public class PowerComputer {
    private final SamplesDecoder samplesDecoder;
    private final int batchSize;
    private final short[] queueOfShorts = new short[8];
    private final short[] shorts;

    /**
     * The constructor of PowerComputer
     * @param stream stream at which the powers are calculated
     * @param batchSize the size of the batch
     * @throws  IllegalArgumentException if the batch size is not divisible by 8 or not greater than 0
     */
    public PowerComputer(InputStream stream, int batchSize){
        Preconditions.checkArgument(batchSize % 8 == 0 && batchSize > 0);

        this.batchSize = batchSize;
        shorts = new short[batchSize*2];

        samplesDecoder = new SamplesDecoder(stream,batchSize*2);
    }

    /**
     * Computes the powers using the samples obtained with SamplesDecoder and the formula described in 2.4.6
     * @param batch the powers computed
     * @return the number of powers computed
     * @throws IOException if there is an issue when reading the input stream
     * @throws IllegalArgumentException if length of batch is not equal to batchSize
     */
    public int readBatch(int[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);

        int numberOfSamples = samplesDecoder.readBatch(shorts);
        int tempDoublePair;
        int tempDoubleImpair;

        //cycles through the shorts with a queue holding the last eight values
        for (int i = 2; i < numberOfSamples + 2; i += 2) {

            queueOfShorts[i%8] = shorts[i-1];
            queueOfShorts[(i-1)%8] = shorts[i-2];

            tempDoublePair = 0;
            tempDoubleImpair = 0;

            for (int j = 0; j < 4; j++) {
                if (j%2 == 0){
                     tempDoublePair -= queueOfShorts[(i-j*2+8)%8];
                     tempDoubleImpair -= queueOfShorts[(i-j*2+9)%8];
                }else{
                    tempDoublePair += queueOfShorts[(i-j*2+8)%8];
                    tempDoubleImpair += queueOfShorts[(i-j*2+9)%8];
                }
            }
            batch[i/2 - 1] = tempDoublePair*tempDoublePair + tempDoubleImpair*tempDoubleImpair;
        }
        return numberOfSamples/2;
    }
}

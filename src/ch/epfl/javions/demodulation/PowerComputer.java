package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public class PowerComputer {

    private SamplesDecoder samplesDecoder;
    private InputStream stream;
    private int batchSize;

    short[] shorts;

    public PowerComputer(InputStream stream, int batchSize){
        Preconditions.checkArgument(batchSize % 8 == 0 && batchSize > 0);

        this.stream = stream;
        this.batchSize = batchSize;
        shorts = new short[batchSize*2];

        samplesDecoder = new SamplesDecoder(stream,batchSize*2);
    }

    /**
     * Compute's the powers using the samples obtained with SamplesDecoder and the formula described in 2.4.6
     * @param batch the powers computed
     * @return the number of powers computed
     * @throws IOException if there is an issue when reading the input stream
     * @throws IllegalArgumentException if length of batch is not equal to batchSize
     */
    public int readBatch(int[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);

        int numberOfSamples = samplesDecoder.readBatch(shorts);

        short[] queueOfShorts = new short[8];
        double tempDoublePair;
        double tempDoubleImpair;

        for (int i = 2; i < numberOfSamples + 2; i += 2) {

            queueOfShorts[i%8] = shorts[i-1];
            queueOfShorts[(i-1)%8] = shorts[i-2];

            tempDoublePair = 0;
            tempDoubleImpair = 0;

            for (int j = 0; j < 4; j++) {
                 tempDoublePair += Math.pow(-1,j + 1)*queueOfShorts[(i-j*2+8)%8];
                 tempDoubleImpair += Math.pow(-1,j + 1)*queueOfShorts[(i-j*2+9)%8];
            }

            batch[i/2 - 1] = (int)(tempDoublePair*tempDoublePair + tempDoubleImpair*tempDoubleImpair);
        }
        return numberOfSamples/2;
    }
}

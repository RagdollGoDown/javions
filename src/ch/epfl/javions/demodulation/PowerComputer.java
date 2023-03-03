package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

public class PowerComputer {

    private SamplesDecoder samplesDecoder;
    private InputStream stream;
    private int batchSize;

    short[] shorts;

    public PowerComputer(InputStream stream, int batchSize){
        if (batchSize % 8 != 0 && batchSize <= 0) throw new IllegalArgumentException();

        this.stream = stream;
        this.batchSize = batchSize;
        shorts = new short[batchSize];

        samplesDecoder = new SamplesDecoder(stream,batchSize);
    }

    public int readBatch(int[] batch) throws IOException {
        if (batch.length != batchSize) throw new IllegalArgumentException();

        samplesDecoder.readBatch(shorts);
        /*
        short[] I = new short[batchSize];
        short[] Q = new short[batchSize];
        short[][] IorQ = {I,Q};

        for (int i = 0; i < batchSize; i++) {
            IorQ[i%2][i] = shorts[i];

            //pour que ce soit négatif et positif comme dans la pres en 2.4.3
            if (((i+3) % 4) < 2) IorQ[i%2][i] *= -1;
        }*/

        short[] queueOfShorts = new short[8];
        double tempdouble;

        for (int i = 2; i < batchSize; i += 2) {

            queueOfShorts[i%8] = shorts[i-1];
            queueOfShorts[(i-1)%8] = shorts[i-2];

            tempdouble = 0;

            for (int j = 0; j < 4; j++) {
                 tempdouble += Math.pow(-1,j + 1)*queueOfShorts[(i-j*2+8)%8];
            }

            batch[i/2 - 1] += tempdouble*tempdouble;

            tempdouble = 0;

            for (int j = 0; j < 4; j++) {
                tempdouble += Math.pow(-1,j + 1)*queueOfShorts[(i-j*2+9)%8];
            }

            batch[i/2 - 1] += tempdouble*tempdouble;

            //batch[i] = (shorts[2*i-6] - shorts[2*i-4] + shorts[2*i-2] - shorts[2*i])*(shorts[2*i-6] - shorts[2*i-4] + shorts[2*i-2] - shorts[2*i])
              //      +(shorts[2*i-7] - shorts[2*i-5] + shorts[2*i-3] - shorts[2*i-1])*(shorts[2*i-7] - shorts[2*i-5] + shorts[2*i-3] - shorts[2*i-1]);
        }

        return batchSize;
    }
}

package ch.epfl.javions.demodulation;

import java.io.InputStream;

public class PowerComputer {

    private InputStream stream;
    private int batchSize;

    short[] shorts;

    public PowerComputer(InputStream stream, int batchSize){
        if (batchSize % 8 != 0) throw new IllegalArgumentException();

        this.stream = stream;
        this.batchSize = batchSize;
        shorts = new short[batchSize];
    }

    public int readBatch(int[] batch){
         /*
        short[] I = new short[batchSize];
        short[] Q = new short[batchSize];
        ArrayList<short[]> IorQ = new ArrayList<>();
        IorQ.add(I);
        IorQ.add(Q);

        for (int i = 0; i < batchSize; i++) {
            IorQ.get(i%2)[i] = batch[i];

            //pour que ce soit négatif et positif comme dans la pres en 2.4.3
            if (((i+3) % 4) < 2) IorQ.get(i%2)[i] *= -1;
        }*/
        return batchSize;
    }
}

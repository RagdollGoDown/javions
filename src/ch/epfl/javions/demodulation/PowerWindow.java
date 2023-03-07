package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.sql.SQLOutput;
import java.util.Arrays;

public class PowerWindow {
    private final static int CONSTANT_BATCHSIZE = 1<<8;

    private final InputStream stream;
    private final PowerComputer powerComputer;
    private final int windowSize;
    private int position;
    private long nByteInLot1;
    private long nByteInLot2;


    private int[] lot1;
    private int[] lot2;

    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument(windowSize <= CONSTANT_BATCHSIZE);
        powerComputer = new PowerComputer(stream,CONSTANT_BATCHSIZE);
        this.stream = stream;
        this.windowSize = windowSize;

        lot1 = new int[CONSTANT_BATCHSIZE];
        lot2 = new int[CONSTANT_BATCHSIZE];
        position = 0;


        nByteInLot1 =  powerComputer.readBatch(lot1);
        System.out.println(Arrays.toString(lot1));
        nByteInLot2 = powerComputer.readBatch(lot2);
        System.out.println(Arrays.toString(lot2));
        System.out.println(lot2[0]);
    }

    private void addPosition(int p) throws IOException {
        if (position + p >= CONSTANT_BATCHSIZE){
            System.out.println("Generate new");
            int skip = 0;
            if (position + p >= 2*CONSTANT_BATCHSIZE){
                skip = (p / CONSTANT_BATCHSIZE) - 2;
            }
            generateNewLots(skip, p);
        }
        this.position = (position + p) % CONSTANT_BATCHSIZE;
    }

    public int size(){return windowSize;}

    private void checkForLotTablesUpdate(){
    }
    private void generateNewLots (int skip, int p) throws IOException {
        System.out.println("Skip: " + skip);
        long numberOfBytesToSkip = ((long) CONSTANT_BATCHSIZE) * 4 * skip;
        long n = stream.skip(numberOfBytesToSkip);
        System.out.println("n: "+ n);
        System.out.println("numb: "+ numberOfBytesToSkip);
        if (n!=numberOfBytesToSkip){
            System.out.println("Everything skipped");
            nByteInLot1 = 0;
            nByteInLot2 = 0;
            return;
        }
        if (position + p >= 2*CONSTANT_BATCHSIZE){
            System.out.println("->Skip: "+skip);
            nByteInLot1 =  powerComputer.readBatch(lot1);
            nByteInLot2 = powerComputer.readBatch(lot2);
        }
        else{
            System.out.println("classic lot generation");
            int[] tmp_list = this.lot1;
            this.lot1 =  this.lot2;
            this.lot2 = tmp_list;
            nByteInLot2 = powerComputer.readBatch(this.lot2);
        }
    }
    public void advance() throws IOException{
        addPosition(1);
    }
    public void advanceBy(int offset) throws  IOException{
        addPosition(offset);
    }
    public int get(int i){
        Preconditions.checkArgument(0<=i && i< windowSize);
        if ((position + i) < lot1.length){
            System.out.println("premier lot");
            return lot1[position+i];
        }else{
            System.out.println("2eme lot");
            return lot2[i - (lot1.length - position)];
        }
    }
    public boolean isFull(){
        return nByteInLot1 + nByteInLot2 >= position + windowSize;
    }
}

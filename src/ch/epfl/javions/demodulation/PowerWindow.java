package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;
public class PowerWindow {
    private final static int CONSTANT_BATCHSIZE = 1<<16;

    private final InputStream stream;
    private final PowerComputer powerComputer;
    private final int windowSize;

    //for position()
    private long positionInStream;
    private int position;
    private long nByteInLot1;
    private long nByteInLot2;

    private byte[] buffer;
    private int[] lot1;
    private int[] lot2;

    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument(windowSize <= CONSTANT_BATCHSIZE && windowSize > 0);

        powerComputer = new PowerComputer(stream,CONSTANT_BATCHSIZE);

        // used some time for skipping bytes instead of calculating the power for nothing
        this.stream = stream;

        this.windowSize = windowSize;

        lot1 = new int[CONSTANT_BATCHSIZE];
        lot2 = new int[CONSTANT_BATCHSIZE];

        //used to skip some bytes of the stream
        buffer = new byte[CONSTANT_BATCHSIZE * 4];
        
        position = 0;


        nByteInLot1 =  powerComputer.readBatch(lot1);
        nByteInLot2 = powerComputer.readBatch(lot2);
    }


    private void addPosition(int p) throws IOException {
        if (position + p >= CONSTANT_BATCHSIZE){
            int skip = 0;
            if (position + p >= 2*CONSTANT_BATCHSIZE){
                skip = (p / CONSTANT_BATCHSIZE) - 2;
            }
            generateNewLots(skip, p);
        }
        this.positionInStream += p;
        this.position = (position + p) % CONSTANT_BATCHSIZE;
    }
    public long position(){
        return positionInStream;
    }
    public int size(){return windowSize;}

    private void generateNewLots (int skip, int p) throws IOException {
        int numberOfBytesToSkip = CONSTANT_BATCHSIZE * 4 * skip;
        int n = 0;

        //if has to skip at least a full lot (skip function doesn't work as intended)
        for (int i = 0; i < skip; i++) {
            n += stream.readNBytes(buffer,0, CONSTANT_BATCHSIZE * 4);
        }

        if (n!=numberOfBytesToSkip){
            Arrays.fill(lot1,0);
            Arrays.fill(lot2,0);
            nByteInLot1 = 0;
            nByteInLot2 = 0;
        }
        else if (position + p >= 2*CONSTANT_BATCHSIZE){
            nByteInLot1 =  powerComputer.readBatch(lot1);
            nByteInLot2 = powerComputer.readBatch(lot2);
        }
        else{
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
             return lot1[position+i];
        }else{
            return lot2[i - (lot1.length - position)];
        }
    }
    public boolean isFull(){
        return nByteInLot1 + nByteInLot2 >= position + windowSize;
    }
}

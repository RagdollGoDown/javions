package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;

/**
 * Used to browse a big amount of the stream's data without getting it all at once
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
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

    private final byte[] buffer;
    private int[] lot1;
    private int[] lot2;

    /**
     * The constructor
     * @param stream stream at which the powers are calculated
     * @param windowSize the size of the windows
     * @throws IllegalArgumentException is the windowSize is greater than the batchSize or if winsdowsize is smaller than one
     * @throws IOException if there are any problems reading the stream
     */
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument(windowSize <= CONSTANT_BATCHSIZE && windowSize > 0);

        powerComputer = new PowerComputer(stream,CONSTANT_BATCHSIZE);

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

    /**
     * Advance by one
     * @throws IOException if there are any problems reading the stream
     */
    private void addPosition() throws IOException {
        if (position + 1 ==  CONSTANT_BATCHSIZE){
            generateNewLots();
            position = 0;
        }else{
            this.position += 1;
        }
        this.positionInStream += 1;

    }

    /**
     * Advance by p
     * @param p the amount to advance by
     * @throws IOException if there are any problems reading the stream
     */
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

    /**
     * returns the current position of the window relative to the beginning of the power value stream,
     * which is initially 0 and is incremented with each call engagement of the window position
     * @return the current position of the window relative to the beginning of the power value stream
     */
    public long position(){
        return positionInStream;
    }

    /**
     * @return the size of the window
     */
    public int size(){return windowSize;}

    /**
     * generate new lot, skip lots in the stream if the 'jump' is greater than the window
     * @param skip number of lot to skip (don't need to calculate de powercomputer
     * @param p the added to the old position
     * @throws IOException if there are any problems reading the stream
     */
    private void generateNewLots (int skip, int p) throws IOException {
        int numberOfBytesToSkip = CONSTANT_BATCHSIZE * 4 * skip;
        int n = 0;

        //if it needs to skip at least a full lot (stream.skip function doesn't work as intended)
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
            nByteInLot1 = nByteInLot2;
            nByteInLot2 = powerComputer.readBatch(this.lot2);
        }
    }

    /**
     * Generate new lots, can be used if the changement of positon is smaller than the windowSize
     * @throws IOException if there are any problems reading the stream
     */
    private void generateNewLots() throws IOException {
        int[] tmp_list = this.lot1;
        this.lot1 =  this.lot2;
        this.lot2 = tmp_list;
        nByteInLot1 = nByteInLot2;
        nByteInLot2 = powerComputer.readBatch(this.lot2);
    }

    /**
     * Advance the windows of 1 position
     * @throws IOException if there are any problems reading the stream
     */
    public void advance() throws IOException{
        addPosition();
    }

    /**
     * Advance the windows of an n position
     * @param offset the amount we advance by
     * @throws IOException if there are any problems reading the stream
     */
    public void advanceBy(int offset) throws  IOException{
        addPosition(offset);
    }

    /**
     * get element at the position i of the window
     * @param i the position of the element
     * @return the value of the element
     */
    public int get(int i){
        if (!(0<=i && i < windowSize)) throw new IndexOutOfBoundsException();
        if ((position + i) < lot1.length){
             return lot1[position+i];
        }else{
            return lot2[i - (lot1.length - position)];
        }
    }

    /**
     * @return true if the window is full of elements
     */
    public boolean isFull(){
        return nByteInLot1 + nByteInLot2 >= position + windowSize;
    }

}

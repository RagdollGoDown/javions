package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * A demodulator for ADS-B messages
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public final class AdsbDemodulator {
    private static final int WINDOW_SIZE = 1200;
    private static final int SIZE_PREAMBLE = 80;
    private static final int SIZE_STEP = 5;
    private final PowerWindow powerWindow;

    /**
     * Constructor of AsbDemodulator
     * @param samplesStream stream that as to
     * @throws IOException if there are eny problems reading the stream
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        powerWindow = new PowerWindow(samplesStream, WINDOW_SIZE);
    }

    private byte getDF(){
        byte DF = 0;
        for (int i = 0; i < Byte.SIZE; i++) {
            // formula 2.3.3
            DF = (byte) (DF << 1);
            DF += powerWindow.get(SIZE_PREAMBLE + (2*SIZE_STEP * i))
                    < powerWindow.get(SIZE_PREAMBLE + SIZE_STEP + (2*SIZE_STEP * i))
                    ? 0 : 1;
        }
        return DF;
    }

    /**
     * Find next ADS-B message of the sample stream passed to the constructor
     * @return next ADS-B message of the sample stream passed to the constructor
     * @throws IOException if there are eny problems reading the stream
     */
    public RawMessage nextMessage() throws IOException{
        byte DF = 0;
        byte[] bytes = new byte[14];
        int left = 0;
        int middle = 0;
        int right = 0;
        
        RawMessage rawMessage = null;

        //advance in the window until we find a valid result
        while (rawMessage == null && powerWindow.isFull()){
            left = sumOfPics(0);
            middle = sumOfPics(1);
            right = sumOfPics(2);
            powerWindow.advance();
            while (left >= middle ||
                    right >= middle ||
                    !preambleValleyChecker(middle,0)) {
                left = middle;
                middle = right;
                right = sumOfPics(2);
                powerWindow.advance();
            }

            DF = getDF();

            if (RawMessage.size(DF) == 0){
                continue;
            }

            //complete the rest of the message
            bytes[0] = DF;

            for (int k = 1; k < RawMessage.LENGTH; k++) {
                for (int j = 0; j < Byte.SIZE; j++) {
                    // formula 2.3.3
                    bytes[k] = (byte) (bytes[k] << 1);
                    bytes[k] += powerWindow.get(SIZE_PREAMBLE + 2*SIZE_STEP * (k * Byte.SIZE + j))
                            < powerWindow.get(SIZE_PREAMBLE + SIZE_STEP + 2*SIZE_STEP * (k * Byte.SIZE + j))
                            ? 0 : 1;
                }
            }
            rawMessage = RawMessage.of(powerWindow.position() * 100, bytes);
        }
        powerWindow.advanceBy(WINDOW_SIZE);
        return rawMessage;
    }

    /**
     * Sees if the possible preamble verifies the condition of the valleys
     * @return true if there is and false if there isn't
     */
    private boolean preambleValleyChecker(int sumOfPicsChecked, int indexInWindow){
        return  sumOfPicsChecked >= 2 * sumOfValleys(indexInWindow);
    }

    /**
     * calculates the sum of pics using the formula in 2.3.1
     * @param indexInWindow the position at which we get the pics
     * @return the sum of the pics
     */
    private int sumOfPics(int indexInWindow){
        return powerWindow.get(indexInWindow) + powerWindow.get(indexInWindow + 10)
                + powerWindow.get(indexInWindow + 35) + powerWindow.get(indexInWindow + 45);
    }

    /**
     * calculates the sum of valleys using the formula in 2.3.1
     * @param indexInWindow the position at which we get the valleys
     * @return the sum of the valleys
     */
    private int sumOfValleys(int indexInWindow){
        return powerWindow.get(indexInWindow + 5) + powerWindow.get(indexInWindow + 15)
                + powerWindow.get(indexInWindow + 20) + powerWindow.get(indexInWindow + 25)
                + powerWindow.get(indexInWindow + 30) + powerWindow.get(indexInWindow + 40);
    }
}

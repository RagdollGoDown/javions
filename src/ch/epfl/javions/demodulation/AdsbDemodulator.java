package ch.epfl.javions.demodulation;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.adsb.RawMessage;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * A demodulator for ADS-B messages
 */
public final class AdsbDemodulator {

    private final PowerWindow powerWindow;

    /**
     * Constructor of AsbDemodulator
     * @param samplesStream stream that as to
     * @throws IOException
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        powerWindow = new PowerWindow(samplesStream,1200);;
    }

    private byte getDF(){
        byte DF = 0;
        for (int i = 0; i < 8; i++) {
            // formula 2.3.3
            DF = (byte) (DF << 1);
            DF += powerWindow.get(80 + (10 * i)) < powerWindow.get(85+(10 * i)) ? 0 : 1;
        }
        return DF;
    }

    /**
     * Find next ADS-B message of the sample stream passed to the constructor
     * @return next ADS-B message of the sample stream passed to the constructor
     * @throws IOException
     */
    public RawMessage nextMessage() throws IOException{
        byte DF = 0;
        byte[] bytes = new byte[14];
        int left=0;
        int middle=0;
        int right=0;
        
        RawMessage rawMessage = null;

        //advance in the window until found a valid result
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
                for (int j = 0; j < 8; j++) {
                    // formula 2.3.3
                    bytes[k] = (byte) (bytes[k] << 1);
                    bytes[k] += powerWindow.get(80 + 10 * (k * 8 + j)) < powerWindow.get(85 + 10 * (k * 8 + j)) ? 0 : 1;
                }
            }
            rawMessage = RawMessage.of(powerWindow.position() * 100, bytes);
        }
        powerWindow.advanceBy(1200);
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

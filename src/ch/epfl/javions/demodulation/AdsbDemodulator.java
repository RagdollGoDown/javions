package ch.epfl.javions.demodulation;

import ch.epfl.javions.Crc24;
import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class AdsbDemodulator {

    private final PowerWindow powerWindow;
    private final Crc24 crc24;

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        powerWindow = new PowerWindow(samplesStream,1200);
        this.crc24 = new Crc24(Crc24.GENERATOR);
    }

    public RawMessage nextMessage() throws IOException{

        byte DF = 0;
        int resultatCrc24 = 1;
        int preambleIndex = 0;

        byte[] bytes = new byte[14];

        int left=0;
        int middle=0;
        int right=0;

        //avancer dans la fenêtre jusqu'à trouver un résultat valide
        while (RawMessage.size(DF) == 0 || resultatCrc24 != 0){
            do {
                left = middle;
                middle = right;
                right = sumOfPics(1);

                powerWindow.advance();
                ++preambleIndex;
            }while (left >= middle || right >= middle ||
                    !preambleValleyChecker(middle,0));

            for (int i = 0; i < 8; i++) {
                // formule 2.3.3
                DF += powerWindow.get(80 + 10 * i) < powerWindow.get(85+10 * i) ? 0 : 1;
                DF = (byte) (DF << 1);
            }

            //completer le reste du message
            bytes[0] = DF;

            for (int k = 1; k < 14; k++) {
                for (int j = 0; j < 8; j++) {
                    // formule 2.3.3
                    bytes[k] += powerWindow.get(80 + 10 * k * 8 + j) < powerWindow.get(85+10 * k * 8 + j) ? 0 : 1;
                    bytes[k] = (byte) (bytes[k] << 1);
                }
            }
            resultatCrc24 = crc24.crc(bytes);
            if (powerWindow.position() * 100 %10000000 == 0){
                System.out.println(powerWindow.position());
            }

        }

        return RawMessage.of(powerWindow.position() * 100, bytes);
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

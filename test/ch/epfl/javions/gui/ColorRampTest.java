package ch.epfl.javions.gui;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColorRampTest {

    private static final Color[] colors = {Color.valueOf("0x0d0887ff"), Color.valueOf("0x220690ff"),
            Color.valueOf("0x320597ff"), Color.valueOf("0x40049dff"),
            Color.valueOf("0x4e02a2ff"), Color.valueOf("0x5b01a5ff"),
            Color.valueOf("0x6800a8ff"), Color.valueOf("0x7501a8ff"),
            Color.valueOf("0x8104a7ff"), Color.valueOf("0x8d0ba5ff"),
            Color.valueOf("0x9814a0ff"), Color.valueOf("0xa31d9aff"),
            Color.valueOf("0xad2693ff"), Color.valueOf("0xb6308bff"),
            Color.valueOf("0xbf3984ff"), Color.valueOf("0xc7427cff"),
            Color.valueOf("0xcf4c74ff"), Color.valueOf("0xd6556dff"),
            Color.valueOf("0xdd5e66ff"), Color.valueOf("0xe3685fff"),
            Color.valueOf("0xe97258ff"), Color.valueOf("0xee7c51ff"),
            Color.valueOf("0xf3874aff"), Color.valueOf("0xf79243ff"),
            Color.valueOf("0xfa9d3bff"), Color.valueOf("0xfca935ff"),
            Color.valueOf("0xfdb52eff"), Color.valueOf("0xfdc229ff"),
            Color.valueOf("0xfccf25ff"), Color.valueOf("0xf9dd24ff"),
            Color.valueOf("0xf5eb27ff"), Color.valueOf("0xf0f921ff")};

    @Test
    void constructor(){
        assertThrows(IllegalArgumentException.class,() -> {
            new ColorRamp();
        });
        assertThrows(IllegalArgumentException.class,() -> {
            new ColorRamp(Color.valueOf("0x0d0887ff"));
        });
        assertDoesNotThrow(() -> {
            new ColorRamp(Color.valueOf("0x0d0887ff"), Color.valueOf("0x220690ff"));
        });
    }

    @Test
    void atOnNonInterpolatedColor() {
        ColorRamp plasma = ColorRamp.PLASMA;

        assertEquals(colors[0], plasma.at(-1));
        assertEquals(colors[colors.length - 1], plasma.at(2));

        for (int i = 0; i < colors.length; i++) {
            assertEquals(colors[i], plasma.at((double) i / (colors.length - 1)));
        }
    }

    @Test
    void atOnInterpolatedColor(){
        Color c1 = Color.valueOf("0x00000000");
        Color c2 = Color.valueOf("0xffffffff");

        ColorRamp ramp = new ColorRamp(c1,c2);

        Color actual;
        Color expected;

        for (double i = 0; i < 10; i++) {
            actual = ramp.at(i/10);
            expected = c1.interpolate(c2, i /10);
            assertEquals(actual, expected);
        }
    }
}
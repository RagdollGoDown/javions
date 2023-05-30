package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Holds methods that allow us to use a gradient
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public final class ColorRamp {

    private final List<Color> colors;
    private final int size;

    /**
     * Constant for plasma colorRamp
     */
    public static final ColorRamp PLASMA = new ColorRamp(
            Color.valueOf("0x0d0887ff"), Color.valueOf("0x220690ff"),
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
            Color.valueOf("0xf5eb27ff"), Color.valueOf("0xf0f921ff"));

    /**
     * Constructor for a color ramp that uses a list of colors to make a gradient
     * from which we can extract a color at a certain position
     * makes a defensive copy of the array given
     * @param colors the given array of colors
     */
    public ColorRamp(Color... colors){
        Preconditions.checkArgument(colors.length > 1);

        this.colors = List.of(colors);
        size = colors.length;
    }

    /**
     * Finds the color at a position in a gradient made of a list of colors
     * @param colorValue the position of the desired color in the gradient
     * @return the color desired
     */
    public Color at(double colorValue){
        if (colorValue <= 0){ return colors.get(0);}
        if (colorValue >= 1){ return colors.get(size-1);}

        int c1Index = (int)Math.floor(colorValue * (size-1));
        Color c1 = colors.get(c1Index);
        Color c2 = colors.get((int)Math.ceil(colorValue * (size-1)));

        if (c1.equals(c2)){return c1;}

        double pourcentageOfFirst = colorValue - c1Index;

        return c1.interpolate(c2, pourcentageOfFirst);
    }
}

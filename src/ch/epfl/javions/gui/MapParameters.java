package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;

public final class MapParameters{
    private int zoom;
    private  double minX;
    private double minY;

    /**
     *
     * @param zoom value between 6 and 19
     * @param minX (web Mercator)
     * @param minY (web Mercator)
     */
    public MapParameters(int zoom, double minX, double minY){
        Preconditions.checkArgument(6<=zoom && zoom <= 19);
        //TODO pas sur de ces preconditions
        Preconditions.checkArgument(minX>=0 && minY>=0);
    }


}

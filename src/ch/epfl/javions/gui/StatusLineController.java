package ch.epfl.javions.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.scene.layout.Pane;

public final class StatusLineController {
    private Pane pane;
    private  IntegerProperty aircraftCount;
    private LongProperty messageCount;
    public Pane pane(){
        return pane;
    }
    public IntegerProperty aircraftCountProperty(){
        return aircraftCount;
    }
    public LongProperty messageCountProperty(){
        return messageCount;
    }


}

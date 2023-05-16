package ch.epfl.javions.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public final class StatusLineController {
    private BorderPane borderPane;
    private IntegerProperty aircraftCount;
    private LongProperty messageCount;

    public StatusLineController(){
        borderPane = new BorderPane();

        aircraftCount = new SimpleIntegerProperty();
        messageCount = new SimpleLongProperty();

        Text aircraftCountText = new Text();
        aircraftCountText.textProperty().bind(
               aircraftCount.map(c -> "Aéronefs visibles : " + c.intValue()));

        Text messageCountText = new Text();
        messageCountText.textProperty().bind(
                messageCount.map(c -> "Messages reçus : " + c.longValue()));

        borderPane.leftProperty().set(aircraftCountText);

        borderPane.rightProperty().set(messageCountText);

    }

    public Pane pane(){
        return borderPane;
    }
    public IntegerProperty aircraftCountProperty(){
        return aircraftCount;
    }
    public LongProperty messageCountProperty(){
        return messageCount;
    }
}

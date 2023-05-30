package ch.epfl.javions.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 *  Creates and manages the line containing
 *  the number of planes and number of messages received
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public final class StatusLineController {
    private final BorderPane borderPane;
    private final IntegerProperty aircraftCount;
    private final LongProperty messageCount;

    /**
     * Status line controller constructor
     * The counters need to have the bindings done through the public
     * methods though
     */
    public StatusLineController(){
        borderPane = new BorderPane();
        borderPane.getStylesheets().add("status.css");

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

    /**
     * Gives the pane to which the nodes are connected
     * @return the pane
     */
    public Pane pane(){
        return borderPane;
    }

    /**
     * returns the property holding the number of aircraft in the table controller
     * @return the property
     */
    public IntegerProperty aircraftCountProperty(){
        return aircraftCount;
    }

    /**
     * the property holding the amount of messages received
     * @return the property
     */
    public LongProperty messageCountProperty(){
        return messageCount;
    }
}

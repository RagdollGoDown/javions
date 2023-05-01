package ch.epfl.javions.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

public final class AircraftController {

    private final Canvas canvas;
    private final Pane pane;

    private ObservableSet<ObservableAircraftState> aircraftStates;
    private ObjectProperty<ObservableAircraftState> followedAircraft;

    public AircraftController(DoubleProperty widthProperty, DoubleProperty heightProperty,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> followedAircraft){
        canvas = new Canvas();
        pane = new Pane(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        this.aircraftStates = aircraftStates;
        this.followedAircraft = followedAircraft;
    }
}

package ch.epfl.javions.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

public final class AircraftController {

    private final MapParameters mapParameters;

    private final Canvas canvas;
    private final Pane pane;

    private ObservableSet<ObservableAircraftState> aircraftStates;
    private ObjectProperty<ObservableAircraftState> followedAircraft;

    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> followedAircraft){
        this.mapParameters = mapParameters;

        canvas = new Canvas();
        pane = new Pane(canvas);
        pane.setPickOnBounds(false);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());



        aircraftStates.forEach(this::generateGroupForAircraft);

        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                   changegetElementAdded()
                });

        this.followedAircraft = followedAircraft;
    }

    public boolean hasToBeDrawn(ObservableAircraftState observableAircraftState){
        return true;
    }

    public Pane pane(){
        return pane;
    }

    private void icon(){

    }
}

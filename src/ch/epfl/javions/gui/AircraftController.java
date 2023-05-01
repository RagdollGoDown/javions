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

        this.aircraftStates = aircraftStates;
        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> { /* … corps de la lambda */ });
        this.followedAircraft = followedAircraft;
    }

    public Pane getPane(){
        //TODO demander s'il faut que ce soit une 'copie' de pane
        return pane;
    }

    private void icon(){

    }
}

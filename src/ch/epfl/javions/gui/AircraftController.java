package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.Map;

public final class AircraftController {

    private final MapParameters mapParameters;
    private final Pane pane;

    private Map<ObservableAircraftState, Group> stateToGroupMap;

    private ObjectProperty<ObservableAircraftState> followedAircraft;

    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> followedAircraft){
        this.mapParameters = mapParameters;
        pane = new Pane();
        pane.setPickOnBounds(false);
        pane.getStylesheets().add("aircraft.css");



        aircraftStates.forEach(this::generateGroupForAircraft);

        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                  generateGroupForAircraft(change.getElementAdded())));

        this.followedAircraft = followedAircraft;
    }

    public boolean hasToBeDrawn(ObservableAircraftState observableAircraftState){
        return true;
    }

    public Pane pane(){
        return pane;
    }

    private void generateGroupForAircraft(ObservableAircraftState observableAircraftState){
        Group labelGroup = new Group();
        labelGroup.getStyleClass().add("label");

        Group groupLabelIcon = new Group(labelGroup, icon(observableAircraftState));
        //groupLabelIcon.getChildren().addAll(labelGroup, icon(observableAircraftState));

        groupLabelIcon.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
        WebMercator.x(mapParameters.getZoom(),observableAircraftState.getPosition().longitude())
        - mapParameters.getMinX()));

        groupLabelIcon.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
        WebMercator.y(mapParameters.getZoom(),observableAircraftState.getPosition().latitude())
        - mapParameters.getMinY()));

        Group trajectoryGroup = new Group();
        trajectoryGroup.getStyleClass().add("trajectory");

        Group mainGroup = new Group(trajectoryGroup, groupLabelIcon);
        //mainGroup.getChildren().addAll(trajectoryGroup, groupLabelIcon);
        mainGroup.setId(observableAircraftState.address().toString());

        pane.getChildren().add(mainGroup);
    }

    private SVGPath icon(ObservableAircraftState observableAircraftState){
        SVGPath iconPath = new SVGPath();

        iconPath.contentProperty().set("src/ch/epfl/javions/gui/AircraftIcon.java");
        //TODO faire rotation
        observableAircraftState.altitudeProperty().addListener((observable, oldValue, newValue) ->{
        //TODO corriger couleur
            iconPath.fillProperty().set(ColorRamp.PLASMA.at(0));
        });

        return iconPath;
    }
}

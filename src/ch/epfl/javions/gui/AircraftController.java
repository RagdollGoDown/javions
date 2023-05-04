package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import java.util.Map;
import java.util.Objects;

public final class AircraftController {
    private final static AircraftIcon DEFAULT_SVG = AircraftIcon.BALLOON;
    private final static int MAX_ALTITUDE_METERS = 12000;
    private final static int ZOOM_FOR_VISIBLE_ETIQUETTE_LIMIT = 11;
    private final static int RECTANGLE_PADDING = 4;
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
                change -> generateGroupForAircraft(change.getElementAdded()));

        this.followedAircraft = followedAircraft;
    }



    public Pane pane(){
        return pane;
    }

    private void generateGroupForAircraft(ObservableAircraftState observableAircraftState){

        Group groupLabelIcon = new Group(label(observableAircraftState), icon(observableAircraftState));

        groupLabelIcon.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                WebMercator.x(mapParameters.getZoom(),observableAircraftState.getPosition().longitude())
                        - mapParameters.getMinX(),
                observableAircraftState.positionProperty()));

        groupLabelIcon.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
                        WebMercator.y(mapParameters.getZoom(), observableAircraftState.getPosition().latitude())
                                - mapParameters.getMinY(),
                observableAircraftState.positionProperty()));

        Group trajectoryGroup = new Group();
        trajectoryGroup.getStyleClass().add("trajectory");

        Group mainGroup = new Group(trajectoryGroup, groupLabelIcon);
        mainGroup.setId(observableAircraftState.address().toString());
        mainGroup.viewOrderProperty().bind(observableAircraftState.altitudeProperty().negate());

        pane.getChildren().add(mainGroup);
    }

    private Group label(ObservableAircraftState observableAircraftState){
        Text labelText = labelText(observableAircraftState);
        Rectangle labelBackground = labelBackground(labelText);

        Group labelGroup = new Group(labelBackground,labelText);
        labelGroup.getStyleClass().add("label");
        labelGroup.visibleProperty().bind(
                mapParameters.zoomProperty().greaterThan(ZOOM_FOR_VISIBLE_ETIQUETTE_LIMIT));

        return labelGroup;
    }

    private Text labelText(ObservableAircraftState observableAircraftState){
        Text labelText = new Text();
        labelText.textProperty().bind(
                Bindings.format("%s\n%f km/h\u2002%f m",
                        findCorrectLabelTitle(observableAircraftState),
                        observableAircraftState.velocityProperty(),
                        observableAircraftState.altitudeProperty()));
        return labelText;
    }

    private Rectangle labelBackground(Text labelText){
        Rectangle background = new Rectangle();
        background.widthProperty().bind(
                labelText.layoutBoundsProperty().map(b -> b.getWidth() + RECTANGLE_PADDING));
        background.heightProperty().bind(
                labelText.layoutBoundsProperty().map(b -> b.getHeight() + RECTANGLE_PADDING));
        return background;
    }

    private String getPathSVG(ObservableAircraftState observableAircraftState){
        if (Objects.isNull(observableAircraftState.aircraftData())) {
            return DEFAULT_SVG.svgPath();
        }

        AircraftIcon aircraftIcon = AircraftIcon.iconFor(
                observableAircraftState.aircraftData().typeDesignator(),
                observableAircraftState.aircraftData().description(),
                observableAircraftState.getCategory(),
                observableAircraftState.aircraftData().wakeTurbulenceCategory());
        return aircraftIcon.svgPath();
    }

    private SVGPath icon(ObservableAircraftState observableAircraftState){
        SVGPath iconPath = new SVGPath();
        iconPath.getStyleClass().add("aircraft");

        iconPath.setContent(getPathSVG(observableAircraftState));
        observableAircraftState.trackOrHeadingProperty().addListener((o,ov,nv) -> {
            iconPath.rotateProperty().set(
                    Units.convertTo(nv.doubleValue(),Units.Angle.DEGREE));
        });

        observableAircraftState.altitudeProperty().addListener((observable, oldValue, newValue) ->{
            iconPath.fillProperty().set(
                    ColorRamp.PLASMA.at(correctAltitudeForColorRamp(newValue.doubleValue())));
        });

        return iconPath;
    }

    private static double correctAltitudeForColorRamp(double altitude){
        return Math.cbrt(altitude/MAX_ALTITUDE_METERS);
    }

    private static String findCorrectLabelTitle(ObservableAircraftState aircraft){
        AircraftData data = aircraft.aircraftData();
        if (data == null) return aircraft.address().string();

        if (data.registration() != null) return data.registration().string();
        if (data.typeDesignator() != null) return data.typeDesignator().string();
        return aircraft.address().string();
    }
}

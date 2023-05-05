package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.*;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.*;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static javafx.scene.paint.CycleMethod.NO_CYCLE;

public final class AircraftController {
    private final static AircraftIcon DEFAULT_SVG = AircraftIcon.BALLOON;
    private final static int ZOOM_FOR_VISIBLE_ETIQUETTE_LIMIT = 11;
    private final static int RECTANGLE_PADDING = 4;
    private final MapParameters mapParameters;
    private final Pane pane;

    private final Map<IcaoAddress, Group> icaoToGroup;

    private final ObjectProperty<ObservableAircraftState> followedAircraft;


    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> followedAircraft){
        this.mapParameters = mapParameters;
        this.icaoToGroup = new HashMap<>();

        pane = new Pane();
        pane.setPickOnBounds(false);
        pane.getStylesheets().add("aircraft.css");

        aircraftStates.forEach(this::generateGroupForAircraft);

        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
            if (change.wasRemoved()) removeAircraft(change.getElementRemoved());
            if (change.wasAdded()) addAircraft(change.getElementAdded());
        });


        this.followedAircraft = followedAircraft;
        this.followedAircraft.addListener((o, ov, nv) -> {

        });
    }


    /**
     * Return the pane with all the aircraft
     * @return (Pane) the pane with all the aircraft
     */
    public Pane pane(){
        return pane;
    }

    private Group generateGroupForAircraft(ObservableAircraftState observableAircraftState ){
        //creation group with icon and label
        Group groupLabelIcon = new Group(label(observableAircraftState), icon(observableAircraftState));

        groupLabelIcon.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                ControllerUtils.LongitudeToGui(mapParameters.getZoom(),
                        mapParameters.getMinX(),
                        observableAircraftState.getPosition().longitude()
                        ),
                observableAircraftState.positionProperty(),
                mapParameters.zoomProperty(),
                mapParameters.minXProperty()));

        groupLabelIcon.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
                        ControllerUtils.LatitudeToGui(mapParameters.getZoom(),
                                mapParameters.getMinY(),
                                observableAircraftState.getPosition().latitude()
                                ),
                observableAircraftState.positionProperty(),
                mapParameters.zoomProperty(),
                mapParameters.minYProperty()));
        // group trajectory
        Group trajectoryGroup =  trajectoryGroup(observableAircraftState);
        trajectoryGroup.getStyleClass().add("trajectory");

        Group mainGroup = new Group(trajectoryGroup, groupLabelIcon);
        mainGroup.setId(observableAircraftState.address().toString());
        // order of drawing
        mainGroup.viewOrderProperty().bind(observableAircraftState.altitudeProperty().negate());


        return mainGroup;
    }
    private void addAircraft(ObservableAircraftState observableAircraftState){
        Objects.requireNonNull(observableAircraftState);
        Group group = generateGroupForAircraft(observableAircraftState);
        group.setOnMousePressed(e -> {
            followedAircraft.set(observableAircraftState);
        });
        pane.getChildren().add(group);

        icaoToGroup.put(observableAircraftState.address(), group);
    }
    private void removeAircraft(ObservableAircraftState observableAircraftState){
        Objects.requireNonNull(observableAircraftState);
        if (followedAircraft.get() == observableAircraftState) followedAircraft.set(null);
        pane.getChildren().removeIf(a -> a.getId().equals(observableAircraftState.address().string()));
        icaoToGroup.remove(observableAircraftState.address());
    }

    private Group trajectoryGroup(ObservableAircraftState observableAircraftState){
        Group groupTrajectory = new Group();
        observableAircraftState.getTrajectory().addListener(
                (ListChangeListener<? super ObservableAircraftState.AirbornePos>) change -> {
                    int size = change.getList().size();
                    if (size < 2) return;
                    ObservableAircraftState.AirbornePos previousPos =  change.getList().get(size-2);
                    ObservableAircraftState.AirbornePos newPos =  change.getList().get(size-1);
                    if (Objects.isNull(previousPos.position())){
                         return;
                    }

                    Line line = new Line();
                    setupLineEndAndStart(line, previousPos.position(), newPos.position());
                    setupLineColor(line, previousPos.altitude(), newPos.altitude());

                    groupTrajectory.getChildren().add(line);
                });

        groupTrajectory.visibleProperty().bind(Bindings.createBooleanBinding(()->
                        Objects.nonNull(followedAircraft.get()) && followedAircraft.get() == observableAircraftState
                , followedAircraft));

        return groupTrajectory;
    }

    private void setupLineEndAndStart(Line line, GeoPos start,GeoPos end){

        line.startXProperty().bind(Bindings.createDoubleBinding(() ->
                        ControllerUtils.LongitudeToGui(mapParameters.getZoom(),
                                mapParameters.getMinX(),
                                start.longitude()
                        ),
                mapParameters.zoomProperty(),
                mapParameters.minYProperty()));
        line.startYProperty().bind(Bindings.createDoubleBinding(() ->
                        ControllerUtils.LatitudeToGui(mapParameters.getZoom(),
                                mapParameters.getMinY(),
                                start.latitude()
                        ),
                mapParameters.zoomProperty(),
                mapParameters.minYProperty()));
        line.endXProperty().bind(Bindings.createDoubleBinding(() ->
                        ControllerUtils.LongitudeToGui(mapParameters.getZoom(),
                                mapParameters.getMinX(),
                                end.longitude()
                        ),
                mapParameters.zoomProperty(),
                mapParameters.minYProperty()));
        line.endYProperty().bind(Bindings.createDoubleBinding(() ->
                        ControllerUtils.LatitudeToGui(mapParameters.getZoom(),
                                mapParameters.getMinY(),
                                end.latitude()
                        ),
                mapParameters.zoomProperty(),
                mapParameters.minYProperty()));
    }

    private void setupLineColor(Line line, double altitude1, double altitude2){
        if (altitude1 == altitude2){
            line.setStroke(ColorRamp.PLASMA.at(ControllerUtils.correctAltitudeForColorRamp(altitude1)));
        }
        else {
            Stop s1 = new Stop(0, ColorRamp.PLASMA.at(ControllerUtils.correctAltitudeForColorRamp(altitude1)));
            Stop s2 = new Stop(1, ColorRamp.PLASMA.at(ControllerUtils.correctAltitudeForColorRamp(altitude2)));
            line.setStroke(new LinearGradient(0, 0, 1, 0, true, NO_CYCLE, s1, s2));
        }
    }

    private Group label(ObservableAircraftState observableAircraftState){
        Objects.requireNonNull(observableAircraftState);
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
                        ControllerUtils.findCorrectLabelTitle(observableAircraftState),
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
        iconPath.rotateProperty().bind(
                observableAircraftState.trackOrHeadingProperty().map(
                        t -> Units.convertTo(t.doubleValue(),Units.Angle.DEGREE)));

        iconPath.fillProperty().bind(
                observableAircraftState.altitudeProperty().map(
                        a-> ColorRamp.PLASMA.at(ControllerUtils.correctAltitudeForColorRamp(a.doubleValue()))));

        return iconPath;
    }
}

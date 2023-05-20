package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.collections.*;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
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
    private final static AircraftIcon DEFAULT_ICON = AircraftIcon.AIRLINER;
    private final static int ZOOM_FOR_VISIBLE_ETIQUETTE_LIMIT = 11;
    private final static int RECTANGLE_PADDING = 4;
    private final static String TRAJECTORY_GROUP_ID = "trajectory";
    private final static String CSS_STYLE_SHEET = "aircraft.css";
    private final static String CSS_STYLE_AIRCRAFT = "aircraft";
    private final static String CSS_STYLE_LABEL = "label";
    private final static String CSS_STYLE_TRAJECTORY = "trajectory";
    private final static String CSS_STYLE_LINE = "Line";
    private final MapParameters mapParameters;
    private final Pane pane;

    private final Map<IcaoAddress, Group> icaoToGroup;

    private final ObjectProperty<ObservableAircraftState> followedAircraft;
    private ListChangeListener<? super ObservableAircraftState.AirbornePos> listenerFollowedAircraft  = null;


    /**
     * Display aircraft on the map
     * @param mapParameters the mapParameter instance
     * @param aircraftStates (ObservableSet) set of known position aircraft
     * @param followedAircraft the aircraft that is currently followed
     */
    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> followedAircraft){
        this.mapParameters = mapParameters;
        this.icaoToGroup = new HashMap<>();

        pane = new Pane();
        pane.setPickOnBounds(false);
        pane.getStylesheets().add(CSS_STYLE_SHEET);

        aircraftStates.forEach(this::generateGroupForAircraft);

        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
            if (change.wasRemoved()) removeAircraft(change.getElementRemoved());
            if (change.wasAdded()) addAircraft(change.getElementAdded());
        });

        this.followedAircraft = followedAircraft;
        this.followedAircraft.addListener((observable, oldValue, newValue)->{
            if (Objects.nonNull(oldValue)) removeTrajectory(oldValue);
            if (Objects.nonNull(newValue)) setupTrajectory(newValue);
        });
    }


    /**
     * Return the pane with all the aircraft
     * @return (Pane) the pane with all the aircraft
     */
    public Pane pane(){
        return pane;
    }

    /**
     * Generate the group of the icons, tag and trajectory corresponding to an aircraft
     * @param observableAircraftState the aicraft from which the group will be crated
     * @return a group containing the icon, the tag and the trajectory
     */
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
        Group trajectoryGroup =  trajectoryGroup();
        trajectoryGroup.getStyleClass().add(CSS_STYLE_TRAJECTORY);

        Group mainGroup = new Group(trajectoryGroup, groupLabelIcon);
        mainGroup.setId(observableAircraftState.address().toString());
        // order of drawing
        mainGroup.viewOrderProperty().bind(observableAircraftState.altitudeProperty().negate());

        return mainGroup;
    }

    /**
     * Add an aircraft on the screen
     * @param observableAircraftState the aircraft to add
     */
    private void addAircraft(ObservableAircraftState observableAircraftState){
        Objects.requireNonNull(observableAircraftState);
        Group group = generateGroupForAircraft(observableAircraftState);
        pane.getChildren().add(group);

        icaoToGroup.put(observableAircraftState.address(), group);
    }

    /**
     * Remove the aircraft from screen
     * @param observableAircraftState the aircraft to remove
     */
    private void removeAircraft(ObservableAircraftState observableAircraftState){
        Objects.requireNonNull(observableAircraftState);
        if (followedAircraft.get() == observableAircraftState) followedAircraft.set(null);
        pane.getChildren().remove(icaoToGroup.get(observableAircraftState.address()));
        icaoToGroup.remove(observableAircraftState.address());
    }

    /**
     * Create an empty group for trajectory
     * @return the group for trajectory
     */
    private Group trajectoryGroup(){
        Group groupTrajectory = new Group();
        //by default lines are not created for optimization
        groupTrajectory.setId(TRAJECTORY_GROUP_ID);
        return groupTrajectory;
    }

    /**
     * Set up the trajectory of an aircraft
     * @param observableAircraftState the aircraft which needs his trajector up
     */
    private void setupTrajectory(ObservableAircraftState observableAircraftState){
        ObservableList<ObservableAircraftState.AirbornePos> trajectory = observableAircraftState.getTrajectory();

        // get group trajectory
        Group groupTrajectory = (Group) icaoToGroup.get(observableAircraftState.address())
                .getChildren().filtered((child)->TRAJECTORY_GROUP_ID.equals(child.getId())).get(0);

        //creat the previous lines
        for (int i = 2; i < trajectory.size(); i++) {
            if (Objects.isNull(trajectory.get(i-1).position()) || Objects.isNull(trajectory.get(i).position())) continue;

            Line line = new Line();
            line.getStyleClass().add(CSS_STYLE_LINE);
            setupLineEndAndStart(line, trajectory.get(i-1).position(), trajectory.get(i).position());
            setupLineColor(line, trajectory.get(i-1).altitude(), trajectory.get(i).altitude());
            groupTrajectory.getChildren().add(line);
        }

        //setup listener for new lines
        ListChangeListener<? super ObservableAircraftState.AirbornePos> listener = change -> {
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
        };
        observableAircraftState.getTrajectory().addListener(listener);
        this.listenerFollowedAircraft = listener;

        //trajectory are invisible if not the one of the followed aircraft
        groupTrajectory.visibleProperty().bind(Bindings.createBooleanBinding(()->
                        Objects.nonNull(followedAircraft.get()) && followedAircraft.get() == observableAircraftState,
                followedAircraft));
    }

    /**
     * Remove the trajectory of an aircraft
     * @param observableAircraftState the aircraft from which the trajectory has to be removed
     */
    private void removeTrajectory(ObservableAircraftState observableAircraftState){
        ObservableList<ObservableAircraftState.AirbornePos> trajectory = observableAircraftState.getTrajectory();
        Group groupTrajectory = (Group) icaoToGroup.get(observableAircraftState.address())
                .getChildren().filtered((child)->TRAJECTORY_GROUP_ID.equals(child.getId())).get(0);
        //remove lines
        groupTrajectory.getChildren().clear();
        //remove listener
        trajectory.removeListener(listenerFollowedAircraft);
    }


    private DoubleBinding bindingLongitude(double longitude){
        return Bindings.createDoubleBinding(() ->
                        ControllerUtils.LongitudeToGui(mapParameters.getZoom(),
                                mapParameters.getMinX(),
                                longitude
                        ),
                mapParameters.zoomProperty(),
                mapParameters.minXProperty());
    }
    private DoubleBinding bindingLatitude(double latitude){
        return Bindings.createDoubleBinding(() ->
                        ControllerUtils.LatitudeToGui(mapParameters.getZoom(),
                                mapParameters.getMinY(),
                                latitude
                        ),
                mapParameters.zoomProperty(),
                mapParameters.minYProperty());
    }

    /**
     * Set up a line to stay at the correct coordinates relatively to the map
     * @param line the line that has to be set up
     * @param start the start position of the line
     * @param end the end position of the line
     */
    private void setupLineEndAndStart(Line line, GeoPos start,GeoPos end){
        line.startXProperty().bind(bindingLongitude(start.longitude()));
        line.startYProperty().bind(bindingLatitude(start.latitude()));
        line.endXProperty().bind(bindingLongitude(end.longitude()));
        line.endYProperty().bind(bindingLatitude(end.latitude()));
    }

    /**
     * Set up the color of a line depending on the altitude
     * @param line the line to set up
     * @param altitude1 the altitude of the start of the line
     * @param altitude2 the altitude of the end of the line
     */
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

    /**
     * Create the group for the label
     * @param observableAircraftState the aircraft which need the label
     * @return the group of the label
     */
    private Group label(ObservableAircraftState observableAircraftState){
        Objects.requireNonNull(observableAircraftState);
        Text labelText = labelText(observableAircraftState);
        Rectangle labelBackground = labelBackground(labelText);

        Group labelGroup = new Group(labelBackground,labelText);
        labelGroup.getStyleClass().add(CSS_STYLE_LABEL);
        labelGroup.visibleProperty().bind(Bindings.createBooleanBinding(() ->
            mapParameters.zoomProperty().get() > ZOOM_FOR_VISIBLE_ETIQUETTE_LIMIT ||
            observableAircraftState.equals(followedAircraft.get()),
            mapParameters.zoomProperty(),
            followedAircraft
        ));
        return labelGroup;
    }

    /**
     * Create the text of the label
     * @param observableAircraftState the aircraft with the information for the text
     * @return (Text) the text of the information of aircraft
     */
    private Text labelText(ObservableAircraftState observableAircraftState) {

        Text labelText = new Text();
        labelText.textProperty().bind(
                Bindings.format("%s\n %s km/h\u2002 %.2f m",
                        ControllerUtils.findCorrectLabelTitle(observableAircraftState),
                        observableAircraftState.velocityProperty().map(v ->
                            v.doubleValue() != 0 && !Double.isNaN(v.doubleValue()) ?
                                    (int) Math.rint(Units.convertTo(v.doubleValue(), Units.Speed.KILOMETER_PER_HOUR)): "?"),
                        observableAircraftState.altitudeProperty()));
        return labelText;
    }

    /**
     * Generate background for the tag
     * @param labelText the text of the tag
     * @return (Rectangle) the background of the text
     */
    private Rectangle labelBackground(Text labelText){
        Rectangle background = new Rectangle();
        background.widthProperty().bind(
                labelText.layoutBoundsProperty().map(b -> b.getWidth() + RECTANGLE_PADDING));
        background.heightProperty().bind(
                labelText.layoutBoundsProperty().map(b -> b.getHeight() + RECTANGLE_PADDING));
        return background;
    }

    /**
     * Get the icon of an aircraft
     * @param observableAircraftState the aircraft of the needed icon
     * @return (SVGPath) the icon of the aircraft
     */
    private SVGPath icon(ObservableAircraftState observableAircraftState){
        AircraftIcon aircraftIcon = Objects.isNull(observableAircraftState.aircraftData()) ?
                DEFAULT_ICON :
                AircraftIcon.iconFor(
                        observableAircraftState.aircraftData().typeDesignator(),
                        observableAircraftState.aircraftData().description(),
                        observableAircraftState.getCategory(),
                        observableAircraftState.aircraftData().wakeTurbulenceCategory());

        SVGPath iconPath = new SVGPath();
        iconPath.getStyleClass().add(CSS_STYLE_AIRCRAFT);

        iconPath.setContent(aircraftIcon.svgPath());
        if (aircraftIcon.canRotate()){
            iconPath.rotateProperty().bind(
                observableAircraftState.trackOrHeadingProperty().map(
                    t -> Units.convertTo(t.doubleValue(),Units.Angle.DEGREE)));}

        iconPath.fillProperty().bind(
                observableAircraftState.altitudeProperty().map(
                        a-> ColorRamp.PLASMA.at(ControllerUtils.correctAltitudeForColorRamp(a.doubleValue()))));

        iconPath.setOnMousePressed(e -> followedAircraft.set(observableAircraftState));
        return iconPath;
    }
}

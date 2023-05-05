package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Holds the acts both as stateSetter and can give the elements if demanded
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public final class ObservableAircraftState implements AircraftStateSetter {

    public record AirbornePos(GeoPos position, double altitude){}

    private final IcaoAddress icaoAddress;

    private final AircraftData aircraftData;

    private LongProperty lastMessageTimeStampNs;
    private IntegerProperty category;
    private StringProperty callSign;
    private ObjectProperty<GeoPos> position;
    private long lastTrajectoryUpdateTimeStamp;
    private ObservableList<AirbornePos> modifiableTrajectory;
    private ObservableList<AirbornePos> unmodifiableTrajectory;
    private DoubleProperty altitude;
    private DoubleProperty velocity;
    private DoubleProperty trackOrHeading;

    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData){

        this.icaoAddress = Objects.requireNonNull(icaoAddress);
        this.aircraftData = aircraftData;

        lastMessageTimeStampNs = new SimpleLongProperty();
        category = new SimpleIntegerProperty();
        callSign = new SimpleStringProperty();
        position = new SimpleObjectProperty<>();

        lastTrajectoryUpdateTimeStamp = -1;
        modifiableTrajectory = FXCollections.observableArrayList();
        unmodifiableTrajectory = FXCollections.unmodifiableObservableList(modifiableTrajectory);

        altitude = new SimpleDoubleProperty();
        velocity = new SimpleDoubleProperty(Double.NaN);
        trackOrHeading = new SimpleDoubleProperty();
    }

    private void updateTrajectory(){
        if (lastTrajectoryUpdateTimeStamp == lastMessageTimeStampNs.get()){
            modifiableTrajectory.set(modifiableTrajectory.size() - 1, new AirbornePos(position.get(),altitude.get()));
        }
        else if(modifiableTrajectory.size() == 0
                || modifiableTrajectory.get(modifiableTrajectory.size() - 1).altitude != altitude.get()
                || modifiableTrajectory.get(modifiableTrajectory.size() - 1).position == null && position != null
                || (modifiableTrajectory.get(modifiableTrajectory.size() - 1).position != null
                && modifiableTrajectory.get(modifiableTrajectory.size() - 1).position.equals(position.get()))) {
            modifiableTrajectory.add(new AirbornePos(position.get(),altitude.get()));
            lastTrajectoryUpdateTimeStamp = lastMessageTimeStampNs.get();
        }
    }

    /**
     * @return the icaoAddress of the aircraft
     */
    public IcaoAddress address(){
        return icaoAddress;
    }

    /**
     * @return the aircraftData of the plane
     */
    public AircraftData aircraftData() {
        return aircraftData;
    }

    /**
     * @return the last timestamp when a message was received in nanoseconds
     */
    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }

    /**
     * @return a readonly version of the last time stamp property
     */
    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNs;
    }

    /**
     * change the last time stamp where a message was received
     * @param lastMessageTimeStampNs time stamps in Nanoseconds
     */
    @Override
    public void setLastMessageTimeStampNs(long lastMessageTimeStampNs) {
        this.lastMessageTimeStampNs.set(lastMessageTimeStampNs);
    }

    /**
     * @return the category of the observed aircraft
     */
    public int getCategory() {
        return category.get();
    }

    /**
     * @return a readonly version of the category property
     */
    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }

    /**
     * change the category of the airplane
     * @param category the new category
     */
    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    /**
     * @return the callSign of the aircraft
     */
    public String getCallSign() {
        return callSign.get();
    }

    /**
     * @return a readonly version of the callSign Property
     */
    public ReadOnlyStringProperty callSignProperty() {
        return callSign;
    }

    /**
     * change the aircraft's callSign
     * @param callSign the new CallSign
     */
    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign.string());
    }

    /**
     * The position is the latitude and longitude, both in radians
     * @return a readonly version of the position property
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty(){
        return position;
    }

    /**
     * The position is the latitude and longitude, both in radians
     * @return the position of the aircraft
     */
    public GeoPos getPosition() {
        return position.get();
    }

    /**
     * change both the latitude and longitude with a geopos position
     * @param position the new position
     */
    @Override
    public void setPosition(GeoPos position) {
        this.position.set(position);
        updateTrajectory();
    }

    /**
     * The trajectory is a set containing all past recorded airbornePos messages
     * @return the trajectory
     */
    public ObservableList<AirbornePos> getTrajectory() {
        return unmodifiableTrajectory;
    }

    /**
     * @return the altitude of the aircraft in meters
     */
    public double getAltitude() {
        return altitude.get();
    }

    /**
     * the altitude is kept in meters
     * @return a readonly version of the altitude property
     */
    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    /**
     * change the altitude (meters)
     * @param altitude the new altitude
     */
    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
        updateTrajectory();
    }

    /**
     * @return the velocity of the aircraft (meters per second)
     */
    public double getVelocity() {
        return velocity.get();
    }

    /**
     * @return a readonly version of the velocity property kept in meters per second
     */
    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    /**
     * change the velocity(meters per second)
     * @param velocity the new velocity
     */
    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    /**
     * @return the trackOrHeading in radians
     */
    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

    /**
     * @return a readonly version of the trackorheading property kept in radians
     */
    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    /**
     * change the trackOrHeading (radians)
     * @param trackOrHeading the value of the TrackOrHeading
     */
    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }
}

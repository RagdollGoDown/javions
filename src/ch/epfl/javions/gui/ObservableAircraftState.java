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
        if (aircraftData == null || icaoAddress == null){throw new NullPointerException();}

        this.icaoAddress = icaoAddress;
        this.aircraftData = aircraftData;

        lastMessageTimeStampNs = new SimpleLongProperty();
        category = new SimpleIntegerProperty();
        callSign = new SimpleStringProperty();
        position = new SimpleObjectProperty<>();

        lastTrajectoryUpdateTimeStamp = -1;
        modifiableTrajectory = FXCollections.observableArrayList();
        unmodifiableTrajectory = FXCollections.unmodifiableObservableList(modifiableTrajectory);

        altitude = new SimpleDoubleProperty();
        velocity = new SimpleDoubleProperty();
        trackOrHeading = new SimpleDoubleProperty();
    }

    private void updateTrajectory(){
        if (lastTrajectoryUpdateTimeStamp == lastMessageTimeStampNs.get()){
            modifiableTrajectory.set(modifiableTrajectory.size() - 1, new AirbornePos(position.get(),altitude.get()));
        }
        else if(modifiableTrajectory.size() == 0
                || modifiableTrajectory.get(modifiableTrajectory.size() - 1).altitude != altitude.get()
                || modifiableTrajectory.get(modifiableTrajectory.size() - 1).position.equals(position.get()))
                {
            modifiableTrajectory.add(new AirbornePos(position.get(),altitude.get()));
            lastTrajectoryUpdateTimeStamp = lastMessageTimeStampNs.get();
        }
    }

    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }

    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNs;
    }

    @Override
    public void setLastMessageTimeStampNs(long lastMessageTimeStampNs) {
        this.lastMessageTimeStampNs.set(lastMessageTimeStampNs);
    }

    public int getCategory() {
        return category.get();
    }

    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }

    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    public String getCallSign() {
        return callSign.get();
    }

    public ReadOnlyStringProperty callSignProperty() {
        return callSign;
    }

    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign.string());
    }

    public ReadOnlyObjectProperty<GeoPos> positionProperty(){
        return position;
    }

    public GeoPos getPosition() {
        return position.get();
    }

    @Override
    public void setPosition(GeoPos position) {
        this.position.set(position);
        updateTrajectory();
    }

    public ObservableList<AirbornePos> getTrajectory() {
        return unmodifiableTrajectory;
    }

    public double getAltitude() {
        return altitude.get();
    }

    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
        updateTrajectory();
    }

    public double getVelocity() {
        return velocity.get();
    }

    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }
}

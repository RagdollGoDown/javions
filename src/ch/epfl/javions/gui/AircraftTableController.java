package ch.epfl.javions.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;

public final class AircraftTableController {

    private final Pane pane;
    private final TableView<ObservableAircraftState> tableView;

    private final ObjectProperty<ObservableAircraftState> followedAircraft;

    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                                   ObjectProperty<ObservableAircraftState> followedAircraft){
        pane = new Pane();
        tableView = new TableView<>();

        this.followedAircraft = followedAircraft;

        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                });
    }

    /**
     * @return the pane or root node of the tableview
     */
    public Pane pane() {
        return pane;
    }

    public void setOnDoubleClick(Consumer<ObservableAircraftState> aircraftStateConsumer){
        //aircraftStateConsumer.accept();
    }
}

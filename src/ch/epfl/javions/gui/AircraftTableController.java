package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.CallSign;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;

public final class AircraftTableController {

    private final static double ICAO_PREFWIDTH = 60;
    private final static double CALLSIGN_PREFWIDTH = 70;
    private final static double REGISTRATION_PREFWIDTH = 90;
    private final static double TYPE_DESIGNATOR_PREFWIDTH = 50;
    private final static double MODEL_PREFWIDTH = 230;
    private final static double DESCRIPTION_PREFWIDTH = 70;

    private final Pane pane;
    private final TableView<ObservableAircraftState> tableView;

    private final ObjectProperty<ObservableAircraftState> followedAircraft;

    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                                   ObjectProperty<ObservableAircraftState> followedAircraft){
        pane = new Pane();
        tableView = new TableView<>();
        tableView.getStylesheets().add("table.css");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.setTableMenuButtonVisible(true);
        setupTextColumns();

        this.followedAircraft = followedAircraft;

        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
            if (change.wasAdded()){
                tableView.getItems().add(change.getElementAdded());
            } else if (change.wasRemoved()) {
                tableView.getItems().remove(change.getElementRemoved());
            }
                });
    }

    private void setupTextColumns(){
        //TODO faire fonction pour rectifier
        TableColumn<ObservableAircraftState, String> icaoColumn = new TableColumn<>();
        icaoColumn.setPrefWidth(ICAO_PREFWIDTH);
        icaoColumn.setCellValueFactory(f ->
                new ReadOnlyObjectWrapper<>(f.getValue().address().string()));
        tableView.getColumns().add(icaoColumn);

        TableColumn<ObservableAircraftState, String> callsignColumn = new TableColumn<>();
        callsignColumn.setPrefWidth(CALLSIGN_PREFWIDTH);
        callsignColumn.setCellValueFactory(f ->
                f.getValue().callSignProperty().map(c -> c));
        tableView.getColumns().add(callsignColumn);

        TableColumn<ObservableAircraftState, String> registrationColumn = new TableColumn<>();
        registrationColumn.setPrefWidth(REGISTRATION_PREFWIDTH);
        registrationColumn.setCellValueFactory(f ->
                new ReadOnlyObjectWrapper<>(f.getValue().aircraftData().registration().string()));
        tableView.getColumns().add(registrationColumn);

        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>();
        typeColumn.setPrefWidth(TYPE_DESIGNATOR_PREFWIDTH);
        typeColumn.setCellValueFactory(f ->
                new ReadOnlyObjectWrapper<>(f.getValue().aircraftData().typeDesignator().string()));
        tableView.getColumns().add(typeColumn);

        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>();
        modelColumn.setPrefWidth(MODEL_PREFWIDTH);
        modelColumn.setCellValueFactory(f ->
                new ReadOnlyObjectWrapper<>(f.getValue().aircraftData().model()));
        tableView.getColumns().add(modelColumn);

        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>();
        descriptionColumn.setPrefWidth(DESCRIPTION_PREFWIDTH);
        descriptionColumn.setCellValueFactory(f ->
                new ReadOnlyObjectWrapper<>(f.getValue().aircraftData().description().string()));
        tableView.getColumns().add(descriptionColumn);
    }

    /**
     * @return the pane or root node of the tableview
     */
    public Pane pane() {
        return pane;
    }

    //TODO finir classe
    public void setOnDoubleClick(Consumer<ObservableAircraftState> aircraftStateConsumer){
        aircraftStateConsumer.accept(followedAircraft.get());
    }
}

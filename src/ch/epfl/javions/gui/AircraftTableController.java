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
    private final static String ICAO_COLUMN_NAME = "Icao";
    private final static double CALLSIGN_PREFWIDTH = 70;
    private final static String CALLSIGN_COLUMN_NAME = "Call Sign";
    private final static double REGISTRATION_PREFWIDTH = 90;
    private final static String REGISTRATION_COLUMN_NAME = "Registration";
    private final static double TYPE_DESIGNATOR_PREFWIDTH = 50;
    private final static String TYPE_COLUMN_NAME = "Type designator";
    private final static double MODEL_PREFWIDTH = 230;
    private final static String MODEL_COLUMN_NAME = "Model";
    private final static double DESCRIPTION_PREFWIDTH = 70;
    private final static String DESCRIPTION_COLUMN_NAME = "Description";

    private final Pane pane;
    private final TableView<ObservableAircraftState> tableView;

    private final ObjectProperty<ObservableAircraftState> followedAircraft;

    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                                   ObjectProperty<ObservableAircraftState> followedAircraft){
        pane = new Pane();
        tableView = new TableView<>();
        pane.getChildren().add(tableView);
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

    private void setupTextColumns(){
        TableColumn<ObservableAircraftState, String> icaoColumn = new TableColumn<>(ICAO_COLUMN_NAME);
        icaoColumn.setPrefWidth(ICAO_PREFWIDTH);
        icaoColumn.setCellValueFactory(f ->
        {
            if (f.getValue().aircraftData() != null && f.getValue().aircraftData().model() != null){
                return new ReadOnlyObjectWrapper<>(f.getValue().address().string());
            }
            else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tableView.getColumns().add(icaoColumn);

        TableColumn<ObservableAircraftState, String> callsignColumn = new TableColumn<>(CALLSIGN_COLUMN_NAME);
        callsignColumn.setPrefWidth(CALLSIGN_PREFWIDTH);
        callsignColumn.setCellValueFactory(f ->
                f.getValue().callSignProperty().map(c -> c));
        tableView.getColumns().add(callsignColumn);

        TableColumn<ObservableAircraftState, String> registrationColumn = new TableColumn<>(REGISTRATION_COLUMN_NAME);
        registrationColumn.setPrefWidth(REGISTRATION_PREFWIDTH);
        registrationColumn.setCellValueFactory(f ->
        {
            if (f.getValue().aircraftData() != null && f.getValue().aircraftData().model() != null){
                return new ReadOnlyObjectWrapper<>(f.getValue().aircraftData().registration().string());
            }
            else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tableView.getColumns().add(registrationColumn);

        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>(TYPE_COLUMN_NAME);
        typeColumn.setPrefWidth(TYPE_DESIGNATOR_PREFWIDTH);
        typeColumn.setCellValueFactory(f ->
        {
            if (f.getValue().aircraftData() != null && f.getValue().aircraftData().model() != null){
                return new ReadOnlyObjectWrapper<>(f.getValue().aircraftData().typeDesignator().string());
            }
            else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tableView.getColumns().add(typeColumn);

        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>(MODEL_COLUMN_NAME);
        modelColumn.setPrefWidth(MODEL_PREFWIDTH);
        modelColumn.setCellValueFactory(f ->
        {
            if (f.getValue().aircraftData() != null && f.getValue().aircraftData().model() != null){
                return new ReadOnlyObjectWrapper<>(f.getValue().aircraftData().model());
            }
            else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tableView.getColumns().add(modelColumn);

        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>(DESCRIPTION_COLUMN_NAME);
        descriptionColumn.setPrefWidth(DESCRIPTION_PREFWIDTH);
        descriptionColumn.setCellValueFactory(f ->
        {
            if (f.getValue().aircraftData() != null && f.getValue().aircraftData().model() != null){
                return new ReadOnlyObjectWrapper<>(f.getValue().aircraftData().description().string());
            }
            else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tableView.getColumns().add(descriptionColumn);
    }

    private void setupNumericalColumns(){

    }
}

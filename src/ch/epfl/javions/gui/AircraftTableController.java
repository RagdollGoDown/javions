package ch.epfl.javions.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.function.Consumer;

public final class AircraftTableController {

    private final static String TABLE_STYLE_CLASS = "table.css";

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
    private final static String ON_UNKNOWN_TEXT = "";

    private final static String LONGITUDE_NAME = "Longitude";
    private final static String LATITUDE_NAME = "Latitude";
    private final static int DECIMALS_LAT_LONG = 4;
    private final static String ALTITUDE_NAME = "Altitude";
    private final static String SPEED_NAME = "Speed";
    private final static int DECIMALS_ALT_SPE = 0;
    private final static String NUMERIC_STYLE_CLASS= "numeric";
    private final static NumberFormat LONG_AND_LAT_FORMAT;
    private final static Comparator<String> LONG_AND_LAT_COMPARATOR;
    private final static NumberFormat ALT_AND_SPE_FORMAT;

    private final static Comparator<String> ALT_AND_SPE_COMPARATOR;


    static {
        LONG_AND_LAT_FORMAT = NumberFormat.getInstance();
        LONG_AND_LAT_FORMAT.setMaximumFractionDigits(DECIMALS_LAT_LONG);
        LONG_AND_LAT_FORMAT.setMinimumFractionDigits(DECIMALS_LAT_LONG);
        LONG_AND_LAT_COMPARATOR = (s1,s2) -> {
            try {
                return Double.compare(
                        LONG_AND_LAT_FORMAT.parse(s1).doubleValue(),LONG_AND_LAT_FORMAT.parse(s2).doubleValue());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        };


        ALT_AND_SPE_FORMAT = NumberFormat.getInstance();
        ALT_AND_SPE_FORMAT.setMinimumFractionDigits(DECIMALS_ALT_SPE);
        ALT_AND_SPE_FORMAT.setMaximumFractionDigits(DECIMALS_ALT_SPE);
        ALT_AND_SPE_COMPARATOR = (s1,s2) -> {
            try {
                return Double.compare(
                        ALT_AND_SPE_FORMAT.parse(s1).doubleValue(),ALT_AND_SPE_FORMAT.parse(s2).doubleValue());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private final Pane pane;
    private final TableView<ObservableAircraftState> tableView;

    private final ObjectProperty<ObservableAircraftState> followedAircraft;

    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                                   ObjectProperty<ObservableAircraftState> followedAircraft){
        pane = new Pane();
        tableView = new TableView<>();
        pane.getChildren().add(tableView);
        tableView.getStylesheets().add(TABLE_STYLE_CLASS);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.setTableMenuButtonVisible(true);
        setupTextColumns();
        setupNumericalColumns();

        this.followedAircraft = followedAircraft;

        followedAircraft.addListener((o,ov,nv) -> {
            tableView.getSelectionModel().select(nv);
            tableView.scrollTo(nv);
        });

        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
            if (change.wasAdded()){
                tableView.getItems().add(change.getElementAdded());
            } else if (change.wasRemoved()) {
                tableView.getItems().remove(change.getElementRemoved());
            }
            tableView.sort();
                });
    }

    /**
     * @return the pane or root node of the tableview
     */
    public Pane pane() {
        return pane;
    }

    /**
     * when double-clicking on an aircraft's row, the aircraft becomes the followed aircraft
     * @param aircraftStateConsumer gives us the function to apply to the selected aircraft
     */
    public void setOnDoubleClick(Consumer<ObservableAircraftState> aircraftStateConsumer){
        aircraftStateConsumer.accept(tableView.getSelectionModel().getSelectedItem());
    }

    /**
     * Sets up the columns for the icao address, the callsign, the registration,
     * the type registration, the model and the description
     */
    private void setupTextColumns(){
        TableColumn<ObservableAircraftState, String> icaoColumn = new TableColumn<>(ICAO_COLUMN_NAME);
        icaoColumn.setPrefWidth(ICAO_PREFWIDTH);
        icaoColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().address().string()));
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
            if (f.getValue().aircraftData() != null && f.getValue().aircraftData().registration() != null){
                return new ReadOnlyObjectWrapper<>(f.getValue().aircraftData().registration().string());
            }
            else {
                return new ReadOnlyObjectWrapper<>(ON_UNKNOWN_TEXT);
            }
        });
        tableView.getColumns().add(registrationColumn);

        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>(TYPE_COLUMN_NAME);
        typeColumn.setPrefWidth(TYPE_DESIGNATOR_PREFWIDTH);
        typeColumn.setCellValueFactory(f ->
        {
            if (f.getValue().aircraftData() != null && f.getValue().aircraftData().typeDesignator() != null){
                return new ReadOnlyObjectWrapper<>(f.getValue().aircraftData().typeDesignator().string());
            }
            else {
                return new ReadOnlyObjectWrapper<>(ON_UNKNOWN_TEXT);
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
                return new ReadOnlyObjectWrapper<>(ON_UNKNOWN_TEXT);
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
                return new ReadOnlyObjectWrapper<>(ON_UNKNOWN_TEXT);
            }
        });
        tableView.getColumns().add(descriptionColumn);
    }

    /**
     * Sets up the columns for the speed, altitude, latitude and longitude
     */
    private void setupNumericalColumns() {
        TableColumn<ObservableAircraftState, String> longColumn =
                createNumericalTableColumn(LONG_AND_LAT_FORMAT,LONG_AND_LAT_COMPARATOR,LONGITUDE_NAME);
        longColumn.setComparator(LONG_AND_LAT_COMPARATOR);
        tableView.getColumns().add(longColumn);

        TableColumn<ObservableAircraftState, String> latColumn =
                createNumericalTableColumn(LONG_AND_LAT_FORMAT,LONG_AND_LAT_COMPARATOR,LATITUDE_NAME);
        tableView.getColumns().add(latColumn);

        TableColumn<ObservableAircraftState, String> altColumn =
                createNumericalTableColumn(ALT_AND_SPE_FORMAT,ALT_AND_SPE_COMPARATOR,ALTITUDE_NAME);
        tableView.getColumns().add(altColumn);

        TableColumn<ObservableAircraftState, String> speedColumn =
                createNumericalTableColumn(ALT_AND_SPE_FORMAT,ALT_AND_SPE_COMPARATOR,SPEED_NAME);
        tableView.getColumns().add(speedColumn);
    }

    /**
     * creates a column for the number
     * @param nf the number's formate
     * @param comparator the comparator between the numbers
     * @param columnName the name of the column
     * @return the created column
     */
    private TableColumn<ObservableAircraftState,String> createNumericalTableColumn
            (NumberFormat nf, Comparator<String> comparator, String columnName){
        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(columnName);
        column.getStyleClass().add(NUMERIC_STYLE_CLASS);
        column.setCellValueFactory(
                f -> f.getValue().velocityProperty().map(
                        v -> nf.format(v.doubleValue())));
        column.setComparator(comparator);
    }
}

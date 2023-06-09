package edu.wpi.teamb.controllers.requests;

import edu.wpi.teamb.Bapp;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.ORMs.Alert;
import edu.wpi.teamb.DBAccess.ORMs.Move;
import edu.wpi.teamb.DBAccess.ORMs.Node;
import edu.wpi.teamb.entities.requests.EMoveRequest;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

public class MoveRequestControllerI implements IRequestController{

    @FXML private MFXButton btnSubmit;
    @FXML private SplitPane spSubmit;
    @FXML private MFXButton btnReset;
    @FXML private ImageView helpIcon;
    @FXML private VBox tableVbox;
    @FXML private MFXFilterComboBox<String> cdRoomToMove;
    @FXML private MFXFilterComboBox<Integer> cdWheretoMove;
    @FXML private DatePicker dateMove;
    @FXML private TableView<Move> tbFutureMoves;
    @FXML private MFXButton btnRemoveMove;
    @FXML private MFXButton btnEditRequest;
    @FXML private MFXRadioButton radPastMove;

    private EMoveRequest EMoveRequest;
    private int tableSize = 0;
    private boolean changeRequest = false;

    public MoveRequestControllerI() {
        this.EMoveRequest = new EMoveRequest();
    }

    @FXML
    public void initialize() throws IOException, SQLException {
        ObservableList<String> longNames = FXCollections.observableArrayList();
        longNames.addAll(Repository.getRepository().getAllLongNames());
        cdRoomToMove.setItems(longNames);

        ObservableList<Integer> NodeID = FXCollections.observableArrayList();
        ArrayList<Node> a = Repository.getRepository().getAllNodes();
        for (Node n : a) {
            NodeID.add(n.getNodeID());
        }
        cdWheretoMove.setItems(NodeID);
        moveTable();
        // start with radiobutton unchecked
        radPastMove.setSelected(false);
        btnRemoveMove.setDisable(true);
        btnEditRequest.setDisable(true);
        btnEditRequest.setVisible(false); // hide edit button for now
        initializeFields();
        initBtns();
    }

    @Override
    public void initBtns() {
        dateMove.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });

        btnSubmit.setTooltip(new Tooltip("Click to submit request"));
        btnSubmit.setOnAction(e -> handleSubmit());
        btnReset.setTooltip(new Tooltip("Click to reset fields"));
        btnReset.setOnAction(e -> handleReset());
        btnRemoveMove.setTooltip(new Tooltip("Click to remove selected move"));
        btnRemoveMove.setOnMouseClicked(e -> handleRemoveMove());
        btnEditRequest.setTooltip(new Tooltip("Click to edit selected move"));
        btnEditRequest.setOnMouseClicked(e -> handleEditRequest());
        btnReset.setDisable(true);
        ChangeListener<String> changeListener = (observable, oldValue, newValue) -> {
            btnReset.setDisable(false);
        };
        cdRoomToMove.valueProperty().addListener(changeListener);
        cdWheretoMove.valueProperty().addListener(new ChangeListener<Integer>() {
                                                      @Override
                                                      public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                                                            btnReset.setDisable(false);
                                                      }
                                                  });
                dateMove.valueProperty().addListener(new ChangeListener<LocalDate>() {
                    @Override
                    public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                        btnReset.setDisable(false);
                    }
                });
    }

    @Override
    public void initializeFields() throws SQLException {
        // initialize comboboxes
        cdRoomToMove.setTooltip(new Tooltip("Select room to move"));
        cdWheretoMove.setTooltip(new Tooltip("Select where to move selected room"));
        cdWheretoMove.setValue(-1);
        dateMove.setTooltip(new Tooltip("Select date of move"));
        dateMove.setValue(LocalDate.now());
    }

    @Override
    public void handleSubmit() {
        if (nullInputs()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Please fill out all fields: " + nullInputsLists());
        }
        else {
            String what = cdRoomToMove.getSelectedItem();
            Integer where = cdWheretoMove.getSelectedItem();
            Date when = Date.valueOf(dateMove.getValue());

            // popup error when not all fields are filled or when date is before current
            // date or when the move is already in the table
            if (what != null && where != null && when != null) {
                if (!tbFutureMoves.getItems().contains(new Move(where, what, when))) {
                    if (when.after(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24))) {
                        String[] output = {where.toString(), what, when.toString()};
                        EMoveRequest.submitRequest(output);
                        alertEmployee("unassigned", what, cdWheretoMove.getSelectedItem().toString(), when.toString());
                        handleReset();

                        updateTable();

                    } else {
                        // TODO popup error when entered date is before current date
                    }
                } else {
                    // TODO popup error when entered value is already in table
                }
                handleReset();
                submissionAlert();
            }
        }
    }

    /**
     * Grabs the current employee that is referred to in the newly made request and alerts them of this
     * @param employee
     */
    public void alertEmployee(String employee, String roomMoving, String moveTo, String when){
        edu.wpi.teamb.DBAccess.ORMs.Alert newAlert = new Alert();
        newAlert.setTitle("Move Alert");
        newAlert.setDescription("The room " + roomMoving + " will be moving to " + moveTo + " on " + when);
        newAlert.setEmployee(employee);
        newAlert.setCreated_at(new Timestamp(System.currentTimeMillis()));
        Repository.getRepository().addAlert(newAlert);
    }

    @Override
    public void handleReset() {
        cdRoomToMove.clear();
        cdRoomToMove.replaceSelection("Room to Move");
        cdWheretoMove.clear();
        cdWheretoMove.replaceSelection("Where to Move");
        dateMove.setValue(null);
        btnRemoveMove.setDisable(true);
        changeRequest = false;
        btnEditRequest.setDisable(true);
        updateTable();
    }


    @Override
    public void showPopOver() {
        final FXMLLoader popupLoader = new FXMLLoader(Bapp.class.getResource("views/components/NotAllFieldsCompleteError.fxml"));
        PopOver popOver = new PopOver();
        popOver.setDetachable(true);
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        popOver.setArrowSize(0.0);
        try {
            popOver.setContentNode(popupLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        popOver.show(btnSubmit);
    }

    @Override
    public boolean nullInputs() {
        return cdRoomToMove.getSelectedItem() == null || cdWheretoMove.getSelectedItem() == null
                || dateMove.getValue() == null;
    }

    public ArrayList<String> nullInputsLists() {
        ArrayList<String> nullInputs = new ArrayList<>();
        if (cdRoomToMove.getSelectedItem() == null) {
            nullInputs.add("Room to Move");
        }
        if (cdWheretoMove.getSelectedItem() == null) {
            nullInputs.add("Where to Move");
        }
        if (dateMove.getValue() == null) {
            nullInputs.add("Date");
        }
        return nullInputs;
    }

    private void moveTable() {
        // add moverequests attributes to table, id, longname,and date
        TableColumn<Move, Integer> ids = new TableColumn<>("ID");
        ids.setCellValueFactory(new PropertyValueFactory<Move, Integer>("nodeID"));

        TableColumn<Move, String> locs = new TableColumn<>("Location");
        locs.setCellValueFactory(new PropertyValueFactory<Move, String>("longName"));

        TableColumn<Move, Date> dates = new TableColumn<>("Dates");
        dates.setCellValueFactory(new PropertyValueFactory<Move, Date>("date"));

        tbFutureMoves.setTooltip(new Tooltip("Table of future move requests"));
        tbFutureMoves.getColumns().addAll(ids, locs, dates);

        // ArrayList<Move> moves = Repository.getRepository().getAllMoves();
        // Date now = new Date(System.currentTimeMillis());

        // for (Move move : moves) {
        //     if (move.getDate().after(now)||radPastMove.isSelected()) {
        //         tbFutureMoves.getItems().add(move);
        //         tableSize++;
        //     }
        // }
        updateTable();
    }

    // TODO: This is a temporary fix to the table not updating when a new move is
    // submitted, Should updates from database everytime. Think f a better way to do
    // this if to slow
    private void updateTable() {
        // empty the table and refil database
        tbFutureMoves.getItems().clear();
        ArrayList<Move> moves = Repository.getRepository().getAllMoves();
        Date now = new Date(System.currentTimeMillis()-1000*60*60*24);

        for (Move move : moves) {
            if (move.getDate().after(now)||radPastMove.isSelected()) {
                tbFutureMoves.getItems().add(move);
                tableSize++;
            }
        }
        tbFutureMoves.refresh();
    }

    private String mdy2ymd(String date) {
        String[] parts = date.split("/");
        return parts[2] + "-" + parts[0] + "-" + parts[1];
    }

    private String ymd2ymd2(String date) {
        String[] parts = date.split("-");
        return parts[0] + "-" + parts[1] + "-" + parts[2];
    }

    private String ymd2mdy(String date) {
        String[] parts = date.split("-");
        return parts[1] + "/" + parts[2] + "/" + parts[0];
    }

    @FXML
    public void viewMoves()
    {
        updateTable();
    }

    @FXML
    public void tbSetAlter() {
        // when clicking on a row in the table, the remove button is enabled and the
        // data is loaded into the form
        tbFutureMoves.setOnMouseClicked(event -> {
            if (tbFutureMoves.getSelectionModel().getSelectedItem() != null) {

                btnRemoveMove.setDisable(false);
                // TODO: enable edit button
                // btnEditRequest.setDisable(false);

                Move move = tbFutureMoves.getSelectionModel().getSelectedItem();
                // make roomtomove box empty
                cdRoomToMove.selectItem(move.getLongName());
                // make wheretomove box empty
                cdWheretoMove.selectItem(move.getNodeID());
                // set date value
                dateMove.setValue(LocalDate.parse(ymd2ymd2(move.getDate().toString())));
            }
        });

    }

    public void handleRemoveMove() {
        // when clicking on the remove button, the selected row is removed from the
        // table and the database
        if (tbFutureMoves.getSelectionModel().getSelectedItem() != null) {
            Move move = tbFutureMoves.getSelectionModel().getSelectedItem();
            EMoveRequest = new EMoveRequest(move);
            EMoveRequest.removeRequest();
            tbFutureMoves.getItems().remove(move);
            tableSize--;
            handleReset();

        }
        btnRemoveMove.setDisable(true);
        handleReset();
    }

    public void handleEditRequest() {
        // when clicking on the edit button, the selected row is removed from the table
        // and the database
        if (tbFutureMoves.getSelectionModel().getSelectedItem() != null) {
            Move move = tbFutureMoves.getSelectionModel().getSelectedItem();
            tbFutureMoves.getItems().remove(move);
            EMoveRequest = new EMoveRequest(move);
            // set values of move request from form info
            EMoveRequest.updateRequest();

            EMoveRequest.updateRequest();

            tableSize--;
            handleReset();
        }
    }
}
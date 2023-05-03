package edu.wpi.teamb.controllers.requests;

import edu.wpi.teamb.Bapp;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.Full.FullFlowerRequest;
import edu.wpi.teamb.DBAccess.ORMs.Alert;
import edu.wpi.teamb.controllers.components.InfoCardController;
import edu.wpi.teamb.entities.requests.EFlowerRequest;
import edu.wpi.teamb.entities.requests.IRequest;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

public class FlowerRequestControllerI implements IRequestController {

    @FXML private MFXButton btnSubmit;
    @FXML private SplitPane spSubmit;
    @FXML private MFXButton btnReset;
    @FXML private ImageView helpIcon;
    @FXML private MFXFilterComboBox<String> cbAvailableFlowers;
    @FXML private MFXFilterComboBox<String> cdAvailableColor;
    @FXML private MFXFilterComboBox<String> cdAvailableType;
    @FXML private MFXTextField txtFldNotes;
    @FXML private MFXTextField txtFldMessage;
    @FXML private MFXFilterComboBox<String> cbEmployeesToAssign;
    @FXML private MFXFilterComboBox<String> cbLongName;
    @FXML private MFXFilterComboBox<String> cbChangeStatus;

    private final EFlowerRequest EFlowerRequest;

    public FlowerRequestControllerI() {
        this.EFlowerRequest = new EFlowerRequest();
    }

    @FXML
    public void initialize() throws IOException, SQLException {
        initBtns();
        initializeFields();
        initComboBoxChangeListeners();
    }

    @Override
    public void initBtns() {
        btnSubmit.setTooltip(new Tooltip("Click to submit request"));
        btnSubmit.setOnAction(e -> handleSubmit());
        btnReset.setTooltip(new Tooltip("Click to reset the form"));
        btnReset.setOnAction(e -> handleReset());
        btnReset.setDisable(true);
        ChangeListener<String> changeListener = (observable, oldValue, newValue) -> {
            btnReset.setDisable(false);
        };
        cbAvailableFlowers.textProperty().addListener(changeListener);
        cbEmployeesToAssign.textProperty().addListener(changeListener);
        cbLongName.textProperty().addListener(changeListener);
        cdAvailableColor.textProperty().addListener(changeListener);
        cdAvailableType.textProperty().addListener(changeListener);
    }

    @Override
    public void initializeFields() throws SQLException {
        //Initialize the list of locations to direct request to via dropdown
        ObservableList<String> longNames = FXCollections.observableArrayList();
        longNames.addAll(Repository.getRepository().getPracticalLongNames());
        Collections.sort(longNames);
        cbLongName.setItems(longNames);
        cbLongName.setTooltip(new Tooltip("Select a location to direct the request to"));

        //Set types of flowers
//        /
        cbAvailableFlowers.setTooltip(new Tooltip("Select a type of flower"));

        //Set colors of flowers
        ObservableList<String> colors = FXCollections.observableArrayList("Select a flower to view the available colors");
        Collections.sort(colors);
        cdAvailableColor.setItems(colors);
        cdAvailableColor.setTooltip(new Tooltip("Select a color of flower"));


        //Set delivery types
        ObservableList<String> deliveryType = FXCollections.observableArrayList("Bouquet", "Single Flower", "Vase");
        Collections.sort(deliveryType);
        cdAvailableType.setItems(deliveryType);
        cdAvailableType.setTooltip(new Tooltip("Select a type of delivery"));
        //todo: fix locations

        //Set list of employees
        ObservableList<String> employees =
                FXCollections.observableArrayList();
        employees.addAll(EFlowerRequest.getUsernames());
        Collections.sort(employees);
        employees.add(0, "unassigned");
        cbEmployeesToAssign.setItems(employees);
        cbEmployeesToAssign.setTooltip(new Tooltip("Select an employee to assign the request to"));

        //Set status
        ObservableList<String> statuses = FXCollections.observableArrayList( "In-Progress", "Pending", "Completed");
        Collections.sort(statuses);
        cbChangeStatus.setItems(statuses);
    }

    private void initComboBoxChangeListeners() {
//        cdAvailableModels.setVisible(true);
        ObservableList<String> flower = FXCollections.observableArrayList("Rose", "Tulip", "Daisy", "Lily", "Sunflower");
        Collections.sort(flower);
        cbAvailableFlowers.getItems().clear();
        cbAvailableFlowers.getItems().addAll(flower);
        cbAvailableFlowers.valueProperty().addListener(
                ((observable, oldValue, newValue) -> {
                    if (newValue.equals("Rose")) {
                        cdAvailableColor.getItems().clear();
                        ObservableList<String> roseColors = FXCollections.observableArrayList("Red", "Orange", "Yellow", "Pink", "Purple", "White", "Green");
                        Collections.sort(roseColors);
                        cdAvailableColor.getItems().addAll(roseColors);
                        cdAvailableColor.setVisible(true);
                    } else if (newValue.equals("Tulip")) {
                        cdAvailableColor.getItems().clear();
                        ObservableList<String> tulipColors = FXCollections.observableArrayList("Yellow", "White", "Purple", "Pink", "Orange", "Red");
                        Collections.sort(tulipColors);
                        cdAvailableColor.getItems().addAll(tulipColors);
                        cdAvailableColor.setVisible(true);
                    } else if (newValue.equals("Daisy")) {
                        cdAvailableColor.getItems().clear();
                        ObservableList<String> daisyColors = FXCollections.observableArrayList("White", "Pink", "Red", "Blue");
                        Collections.sort(daisyColors);
                        cdAvailableColor.getItems().addAll(daisyColors);
                        cdAvailableColor.setVisible(true);
                    } else if (newValue.equals("Lily")) {
                        cdAvailableColor.getItems().clear();
                        ObservableList<String> lilyColors = FXCollections.observableArrayList("White", "Pink", "Red", "Orange", "Yellow");
                        Collections.sort(lilyColors);
                        cdAvailableColor.getItems().addAll(lilyColors);
                        cdAvailableColor.setVisible(true);
                    } else if (newValue.equals("Sunflower")) {
                        cdAvailableColor.getItems().clear();
                        ObservableList<String> sunflowerColors = FXCollections.observableArrayList("Yellow", "Red", "Orange", "Pink", "Purple");
                        Collections.sort(sunflowerColors);
                        cdAvailableColor.getItems().addAll(sunflowerColors);
                        cdAvailableColor.setVisible(true);
                    } else {
                        cdAvailableColor.setVisible(false);
                    }
                })
        );
    }

    @Override
    public void handleSubmit() {
        if (nullInputs()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Please fill out all required fields" + nullInputsList());
            alert.showAndWait();
        }
        else {
            // Get the standard request fields
            EFlowerRequest.setEmployee(cbEmployeesToAssign.getValue());
            EFlowerRequest.setLocationName(cbLongName.getValue());
            EFlowerRequest.setRequestStatus(IRequest.RequestStatus.Pending);
            EFlowerRequest.setNotes(txtFldNotes.getText());

            // Get the conference specific fields
            EFlowerRequest.setFlowerType(cbAvailableFlowers.getValue());
            EFlowerRequest.setColor(cdAvailableColor.getValue());
            EFlowerRequest.setSize(cdAvailableType.getValue());
            EFlowerRequest.setMessage(txtFldMessage.getText());

            //Check for required fields before allowing submittion
            if (EFlowerRequest.checkRequestFields() && EFlowerRequest.checkSpecialRequestFields()) {
                String[] output = {
                        EFlowerRequest.getEmployee(),
                        String.valueOf(EFlowerRequest.getRequestStatus()),
                        EFlowerRequest.getLocationName(),
                        EFlowerRequest.getNotes(),
                        EFlowerRequest.getFlowerType(),
                        EFlowerRequest.getColor(),
                        EFlowerRequest.getSize(),
                        EFlowerRequest.getMessage()
                };
                EFlowerRequest.submitRequest(output);
                alertEmployee(cbEmployeesToAssign.getValue());
                handleReset();
            }
            submissionAlert();
        }
    }

    /**
     * Grabs the current employee that is referred to in the newly made request and alerts them of this
     * @param employee
     */
    public void alertEmployee(String employee){
        if(!employee.equals("unassigned")) {
            Alert newAlert = new Alert();
            newAlert.setTitle("New Task Assigned");
            newAlert.setDescription("You have been assigned a new flower request to complete.");
            newAlert.setEmployee(employee);
            newAlert.setCreated_at(new Timestamp(System.currentTimeMillis()));
            Repository.getRepository().addAlert(newAlert);
        }
    }

    @Override
    public void handleReset() {
        cbAvailableFlowers.clear();
        cdAvailableColor.clear();
        cdAvailableType.clear();
        txtFldMessage.clear();
        txtFldNotes.clear();
        cbEmployeesToAssign.clear();
        cbLongName.clear();
    }


    @Override
    public boolean nullInputs() {
        return cbAvailableFlowers.getValue() == null
                || cdAvailableColor.getValue() == null
                || cdAvailableType.getValue() == null
                ||  cbEmployeesToAssign.getValue() == null
                || cbLongName.getValue() == null;
    }

    public ArrayList<String> nullInputsList() {
        ArrayList<String> nullInputs = new ArrayList<>();
        if (cbAvailableFlowers.getValue() == null) {
            nullInputs.add("Flower Type");
        }
        if (cdAvailableColor.getValue() == null) {
            nullInputs.add("Flower Color");
        }
        if (cdAvailableType.getValue() == null) {
            nullInputs.add("Flower Size");
        }
        if (cbEmployeesToAssign.getValue() == null) {
            nullInputs.add("Employee");
        }
        if (cbLongName.getValue() == null) {
            nullInputs.add("Location");
        }
        return nullInputs;
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

    //functions for editable stage in InfoCardController
    public void enterFlowerRequestEditableMode(FullFlowerRequest fullFlowerRequest, InfoCardController currentInfoCardController) {
        //set the editable fields to the values of the request
        cbAvailableFlowers.getSelectionModel().selectItem(fullFlowerRequest.getFlowerType());
        String oldEmployee = fullFlowerRequest.getEmployee();
        //loads the correct color options for the selected flower type so edit page does not crash
        cdAvailableColor.getItems().clear();
        if (cbAvailableFlowers.getSelectionModel().getSelectedItem().equals("Rose")) {
            cdAvailableColor.getItems().clear();
            cdAvailableColor.getSelectionModel().clearSelection();
            ObservableList<String> roseColors = FXCollections.observableArrayList("Red", "Orange", "Yellow", "Pink", "Purple", "White", "Green");
            Collections.sort(roseColors);
            cdAvailableColor.getItems().addAll(roseColors);
            cdAvailableColor.setVisible(true);
        } else if (cbAvailableFlowers.getSelectionModel().equals("Tulip")) {
            cdAvailableColor.getItems().clear();
            cdAvailableColor.getSelectionModel().clearSelection();
            ObservableList<String> tulipColors = FXCollections.observableArrayList("Yellow", "White", "Purple", "Pink", "Orange", "Red");
            Collections.sort(tulipColors);
            cdAvailableColor.getItems().addAll(tulipColors);
            cdAvailableColor.setVisible(true);
        } else if (cbAvailableFlowers.getSelectionModel().getSelectedItem().equals("Daisy")) {
            cdAvailableColor.getItems().clear();
            cdAvailableColor.getSelectionModel().clearSelection();
            ObservableList<String> daisyColors = FXCollections.observableArrayList("White", "Pink", "Red", "Blue");
            Collections.sort(daisyColors);
            cdAvailableColor.getItems().addAll(daisyColors);
            cdAvailableColor.setVisible(true);
        } else if (cbAvailableFlowers.getSelectionModel().getSelectedItem().equals("Lily")) {
            cdAvailableColor.getItems().clear();
            cdAvailableColor.getSelectionModel().clearSelection();
            ObservableList<String> lilyColors = FXCollections.observableArrayList("White", "Pink", "Red", "Orange", "Yellow");
            Collections.sort(lilyColors);
            cdAvailableColor.getItems().addAll(lilyColors);
            cdAvailableColor.setVisible(true);
        } else if (cbAvailableFlowers.getSelectionModel().getSelectedItem().equals("Sunflower")) {
            cdAvailableColor.getItems().clear();
            cdAvailableColor.getSelectionModel().clearSelection();
            ObservableList<String> sunflowerColors = FXCollections.observableArrayList("Yellow", "Red", "Orange", "Pink", "Purple");
            Collections.sort(sunflowerColors);
            cdAvailableColor.getItems().addAll(sunflowerColors);
            cdAvailableColor.setVisible(true);
        }
        //continue setting the editable fields to the values of the request
        cdAvailableColor.selectItem(fullFlowerRequest.getColor());
        cdAvailableColor.setText(fullFlowerRequest.getColor());
        cdAvailableColor.setValue(fullFlowerRequest.getColor());
        cdAvailableType.getSelectionModel().selectItem(fullFlowerRequest.getSize());
        txtFldMessage.setText(fullFlowerRequest.getMessage());
        txtFldNotes.setText(fullFlowerRequest.getNotes());
        cbEmployeesToAssign.getSelectionModel().selectItem(fullFlowerRequest.getEmployee());
        cbLongName.getSelectionModel().selectItem(fullFlowerRequest.getLocationName());
        cbChangeStatus.setVisible(true);
        cbChangeStatus.getSelectionModel().selectItem(fullFlowerRequest.getRequestStatus());

        //set the submit button to say update
        btnSubmit.setText("Update");
        //remove the current onAction event
        btnSubmit.setOnAction(null);
        //add a new onAction event
        btnSubmit.setOnAction(e -> {
            //set the request fields to the new values
            fullFlowerRequest.setFlowerType(cbAvailableFlowers.getValue());
            fullFlowerRequest.setColor(cdAvailableColor.getValue());
            fullFlowerRequest.setSize(cdAvailableType.getValue());
            fullFlowerRequest.setMessage(txtFldMessage.getText());
            fullFlowerRequest.setNotes(txtFldNotes.getText());
            fullFlowerRequest.setEmployee(cbEmployeesToAssign.getValue());
            fullFlowerRequest.setLocationName(cbLongName.getValue());
            fullFlowerRequest.setRequestStatus(cbChangeStatus.getValue());

            //update the request
            EFlowerRequest.updateFlowerRequest(fullFlowerRequest);
            //send the fullConferenceRequest to the info card controller
            currentInfoCardController.sendRequest(fullFlowerRequest);
            //Update new user
            if(!oldEmployee.equals(cbEmployeesToAssign.getValue())){
                alertEmployee(cbEmployeesToAssign.getValue());
            }
            //close the stage
            ((Stage) btnSubmit.getScene().getWindow()).close();
        });
        //set the reset and cancel buttons to not be visible
        btnReset.setVisible(false);
    }
}
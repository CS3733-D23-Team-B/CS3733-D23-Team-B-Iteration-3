package edu.wpi.teamb.controllers.requests;

import edu.wpi.teamb.Bapp;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.Full.FullOfficeRequest;
import edu.wpi.teamb.DBAccess.ORMs.Alert;
import edu.wpi.teamb.controllers.components.InfoCardController;
import edu.wpi.teamb.entities.requests.EOfficeRequest;
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

public class OfficeRequestControllerI implements IRequestController {

    @FXML
    private MFXButton btnSubmit;
    @FXML
    private SplitPane spSubmit;
    @FXML
    private MFXButton btnReset;
    @FXML
    private ImageView helpIcon;
    @FXML
    private MFXFilterComboBox<String> cbSupplyItems;
    @FXML
    private MFXFilterComboBox<String> cbSupplyType;
    @FXML
    private MFXTextField tbSupplyQuantities;
    @FXML
    private MFXTextField txtFldNotes;
    @FXML
    private MFXFilterComboBox<String> cbEmployeesToAssign;
    @FXML
    private MFXFilterComboBox<String> cbLongName;
    @FXML private MFXFilterComboBox<String> cbChangeStatus;

    private final EOfficeRequest EOfficeRequest;

    public OfficeRequestControllerI() {
        this.EOfficeRequest = new EOfficeRequest();
    }

    @FXML
    public void initialize() {
        initBtns();
        initializeFields();
    }

    @Override
    public void initBtns() {
        btnSubmit.setTooltip(new Tooltip("Click to submit request"));
        btnSubmit.setOnAction(e -> handleSubmit());
        btnReset.setTooltip(new Tooltip("Click to reset all fields"));
        btnReset.setOnAction(e -> handleReset());
        btnReset.setDisable(true);
        ChangeListener<String> changeListener = (observable, oldValue, newValue) -> {
            btnReset.setDisable(false);
        };
        cbEmployeesToAssign.textProperty().addListener(changeListener);
        cbLongName.textProperty().addListener(changeListener);
        cbSupplyItems.textProperty().addListener(changeListener);
        cbSupplyType.textProperty().addListener(changeListener);
        tbSupplyQuantities.textProperty().addListener(changeListener);
        txtFldNotes.textProperty().addListener(changeListener);
    }

    @Override
    public void initializeFields() {
        ObservableList<String> longNames = FXCollections.observableArrayList();
        longNames.addAll(Repository.getRepository().getPracticalLongNames());
        Collections.sort(longNames);
        cbLongName.setItems(longNames);
        cbLongName.setTooltip(new Tooltip("Select a location to direct the request to"));
        //DROPDOWN INITIALIZATION
        ObservableList<String> employees = FXCollections.observableArrayList(EOfficeRequest.getUsernames());
        Collections.sort(employees);
        employees.add(0, "unassigned");
        cbEmployeesToAssign.setItems(employees);
        cbEmployeesToAssign.setTooltip(new Tooltip("Select an employee to assign the request to"));

        //DROPDOWN INITIALIZATION
        ObservableList<String> supplies = FXCollections.observableArrayList("Please select a type of supply to view the available options.");
        Collections.sort(supplies);
        cbSupplyItems.setItems(supplies);

        //DROPDOWN INITIALIZATION
        ObservableList<String> supplyType = FXCollections.observableArrayList("Cleaning Supplies", "Electronics Supplies", "Office Supplies");
        Collections.sort(supplyType);
        //cbSupplyType.setItems(supplyType);
        initComboBoxChangeListeners();

        ObservableList<String> status = FXCollections.observableArrayList("Pending", "In-Progress", "Completed");
        Collections.sort(status);
        cbChangeStatus.setItems(status);
    }


    private void initComboBoxChangeListeners() {
//        cbSupplyItems.setVisible(false);
        cbSupplyItems.setTooltip(new Tooltip("Select a supply item"));
        cbSupplyType.setTooltip(new Tooltip("Select a supply category"));
        cbSupplyType.getItems().addAll("Cleaning Supplies", "Electronics Supplies", "Office Supplies");
        cbSupplyType.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue.equals("Office Supplies")) {
                        cbSupplyItems.getItems().clear();
                        ObservableList<String> officeSupplies = FXCollections.observableArrayList("Pencils", "Pens", "Paper", "Stapler", "Staples", "Tape", "Scissors", "Glue", "Markers", "Highlighters", "Post-It Notes", "Paper Clips", "Binder Clips", "Folders", "Envelopes", "Printer Paper");
                        Collections.sort(officeSupplies);
                        cbSupplyItems.getItems().addAll(officeSupplies);
//                        cbSupplyItems.setVisible(true);
                    } else if (newValue.equals("Cleaning Supplies")) {
                        cbSupplyItems.getItems().clear();
                        ObservableList<String> cleaningSupplies = FXCollections.observableArrayList("Bleach", "Disinfectant Wipes", "Hand Sanitizer", "Soap", "Toilet Paper", "Paper Towels", "Trash Bags", "Dish Soap", "Sponges", "Dishwasher Detergent", "Laundry Detergent", "Fabric Softener", "Dryer Sheets", "Broom", "Mop", "Vacuum", "Duster", "Dustpan", "Trash Can", "Trash Can Liners", "Air Freshener", "Glass Cleaner", "All-Purpose Cleaner", "Furniture Polish", "Squeegee", "Toilet Brush", "Plunger", "Rubber Gloves", "Bucket");
                        Collections.sort(cleaningSupplies);
                        cbSupplyItems.getItems().addAll(cleaningSupplies);
//                        cbSupplyItems.setVisible(true);
                    } else if (newValue.equals("Electronics Supplies")){
                        cbSupplyItems.getItems().clear();
                        ObservableList<String> electronicSupplies = FXCollections.observableArrayList("Batteries", "Light Bulbs", "Extension Cords", "Power Strips", "Surge Protectors", "Ethernet Cables", "HDMI Cables", "USB Cables", "Phone Chargers", "Laptop Chargers", "Headphones", "Earbuds", "Speakers", "Microphone", "Webcam", "Printer Ink", "Printer Toner", "Printer Paper");
                        Collections.sort(electronicSupplies);
                        cbSupplyItems.getItems().addAll(electronicSupplies);
                    } else {
                        cbSupplyItems.getItems().clear();
//                        cbSupplyItems.setVisible(false);
                    }
                });
    }


    @Override
    public void handleSubmit() {
        if (nullInputs()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Please fill out all fields: " + nullInputsList());
        } else if(tbSupplyQuantities.getText().replaceAll("[a-zA-Z]", "").length() != 0){
            // Get the standard request fields
            EOfficeRequest.setEmployee(cbEmployeesToAssign.getValue());
            EOfficeRequest.setLocationName(cbLongName.getValue());
            EOfficeRequest.setRequestStatus(IRequest.RequestStatus.Pending);
            EOfficeRequest.setNotes(txtFldNotes.getText());

            // Get the office specific fields
            EOfficeRequest.setType(cbSupplyType.getValue());
            EOfficeRequest.setItem(cbSupplyItems.getValue());
            EOfficeRequest.setQuantity(Integer.parseInt(tbSupplyQuantities.getText()));

            if (EOfficeRequest.checkRequestFields() && EOfficeRequest.checkSpecialRequestFields()) {
                String[] output = {
                        EOfficeRequest.getEmployee(),
                        String.valueOf(EOfficeRequest.getRequestStatus()),
                        EOfficeRequest.getLocationName(),
                        EOfficeRequest.getNotes(),
                        EOfficeRequest.getType(),
                        EOfficeRequest.getItem(),
                        Integer.toString(EOfficeRequest.getQuantity())
                };
                EOfficeRequest.submitRequest(output);
                alertEmployee(cbEmployeesToAssign.getValue());
                handleReset();
            }
            submissionAlert();
        } else {
            tbSupplyQuantities.clear();
            tbSupplyQuantities.setTooltip(new Tooltip("Please only enter integer values"));
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
            newAlert.setDescription("You have been assigned a new office request to complete.");
            newAlert.setEmployee(employee);
            newAlert.setCreated_at(new Timestamp(System.currentTimeMillis()));
            Repository.getRepository().addAlert(newAlert);
        }
    }

    @Override
    public void handleReset() {
        cbEmployeesToAssign.clear();
        cbSupplyItems.clear();
        cbSupplyType.clear();
        tbSupplyQuantities.clear();
        txtFldNotes.clear();
        cbLongName.clear();
    }


    @Override
    public boolean nullInputs(){
        return cbEmployeesToAssign.getValue() == null
                || cbSupplyItems.getValue() == null
                || cbSupplyType.getValue() == null
                || tbSupplyQuantities.getText().isEmpty()
                || cbLongName.getValue() == null;
    }

    public ArrayList<String> nullInputsList() {
        ArrayList<String> nullInputs = new ArrayList<>();
        if (cbEmployeesToAssign.getValue() == null) {
            nullInputs.add("Employee");
        }
        if (cbSupplyItems.getValue() == null) {
            nullInputs.add("Supply Item");
        }
        if (cbSupplyType.getValue() == null) {
            nullInputs.add("Supply Type");
        }
        if (tbSupplyQuantities.getText().isEmpty()) {
            nullInputs.add("Quantity");
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
    public void enterOfficeRequestsEditableMode(FullOfficeRequest fullOfficeRequest, InfoCardController currentInfoCardController) {
        //set the editable fields to the values of the request
        cbEmployeesToAssign.getSelectionModel().selectItem(fullOfficeRequest.getEmployee());
        String oldEmployee = fullOfficeRequest.getEmployee();
        System.out.println(fullOfficeRequest.getId() + " " + fullOfficeRequest.getItem());
        cbSupplyType.getSelectionModel().selectItem(fullOfficeRequest.getType());
        //loads the following supply types so edit page does not crash
        if (cbSupplyType.getSelectionModel().getSelectedItem().equals("Office Supplies")) {
            cbSupplyItems.getItems().clear();
            ObservableList<String> officeSupplies = FXCollections.observableArrayList("Pencils", "Pens", "Paper", "Stapler", "Staples", "Tape", "Scissors", "Glue", "Markers", "Highlighters", "Post-It Notes", "Paper Clips", "Binder Clips", "Folders", "Envelopes", "Printer Paper");
            Collections.sort(officeSupplies);
            cbSupplyItems.getItems().addAll(officeSupplies);
//                        cbSupplyItems.setVisible(true);
        } else if (cbSupplyType.getSelectionModel().getSelectedItem().equals("Cleaning Supplies")) {
            cbSupplyItems.getItems().clear();
            ObservableList<String> cleaningSupplies = FXCollections.observableArrayList("Bleach", "Disinfectant Wipes", "Hand Sanitizer", "Soap", "Toilet Paper", "Paper Towels", "Trash Bags", "Dish Soap", "Sponges", "Dishwasher Detergent", "Laundry Detergent", "Fabric Softener", "Dryer Sheets", "Broom", "Mop", "Vacuum", "Duster", "Dustpan", "Trash Can", "Trash Can Liners", "Air Freshener", "Glass Cleaner", "All-Purpose Cleaner", "Furniture Polish", "Squeegee", "Toilet Brush", "Plunger", "Rubber Gloves", "Bucket");
            Collections.sort(cleaningSupplies);
            cbSupplyItems.getItems().addAll(cleaningSupplies);
//                        cbSupplyItems.setVisible(true);
        } else if (cbSupplyType.getSelectionModel().getSelectedItem().equals("Electronics Supplies")){
            cbSupplyItems.getItems().clear();
            ObservableList<String> electronicSupplies = FXCollections.observableArrayList("Batteries", "Light Bulbs", "Extension Cords", "Power Strips", "Surge Protectors", "Ethernet Cables", "HDMI Cables", "USB Cables", "Phone Chargers", "Laptop Chargers", "Headphones", "Earbuds", "Speakers", "Microphone", "Webcam", "Printer Ink", "Printer Toner", "Printer Paper");
            Collections.sort(electronicSupplies);
            cbSupplyItems.getItems().addAll(electronicSupplies);
        } else {
            cbSupplyItems.getItems().clear();
//                        cbSupplyItems.setVisible(false);
        }
        //continue setting the editable fields to the values of the request
        cbSupplyItems.selectItem(fullOfficeRequest.getItem());
        cbSupplyItems.setText(fullOfficeRequest.getItem());
        cbSupplyItems.setValue(fullOfficeRequest.getItem());
        tbSupplyQuantities.setText(Integer.toString(fullOfficeRequest.getQuantity()));
        txtFldNotes.setText(fullOfficeRequest.getNotes());
        cbLongName.getSelectionModel().selectItem(fullOfficeRequest.getLocationName());
        cbChangeStatus.setVisible(true);
        cbChangeStatus.getSelectionModel().selectItem(fullOfficeRequest.getRequestStatus());

        //set the submit button to say update
        btnSubmit.setText("Update");
        //remove the current onAction event
        btnSubmit.setOnAction(null);
        //add a new onAction event that updates the request
        btnSubmit.setOnAction(e -> {
            //set the request fields to the new values
            fullOfficeRequest.setEmployee(cbEmployeesToAssign.getValue());
            fullOfficeRequest.setItem(cbSupplyItems.getValue());
            fullOfficeRequest.setType(cbSupplyType.getValue());
            fullOfficeRequest.setQuantity(Integer.parseInt(tbSupplyQuantities.getText()));
            fullOfficeRequest.setNotes(txtFldNotes.getText());
            fullOfficeRequest.setLocationName(cbLongName.getValue());
            fullOfficeRequest.setRequestStatus(cbChangeStatus.getValue());

            //update the request
            EOfficeRequest.updateOfficeReqeust(fullOfficeRequest);
            //send the fullOfficeRequest to the info card controller
            currentInfoCardController.sendRequest(fullOfficeRequest);
            //Alert new user?
            if(!oldEmployee.equals(cbEmployeesToAssign.getValue())){
                alertEmployee(cbEmployeesToAssign.getValue());
            }
            //close the stage
            ((Stage) btnSubmit.getScene().getWindow()).close();
        });

        //set the cancel and reset button to not be visible
        btnReset.setVisible(false);
    }
}

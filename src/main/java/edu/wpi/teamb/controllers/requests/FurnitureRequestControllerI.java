package edu.wpi.teamb.controllers.requests;

import edu.wpi.teamb.Bapp;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.Full.FullFurnitureRequest;
import edu.wpi.teamb.controllers.components.InfoCardController;
import edu.wpi.teamb.entities.requests.EFurnitureRequest;
import edu.wpi.teamb.entities.requests.IRequest;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class FurnitureRequestControllerI implements IRequestController{

    @FXML
    private MFXButton btnSubmit;
    @FXML
    private MFXButton btnCancel;
    @FXML
    private MFXButton btnReset;
    @FXML
    private ImageView helpIcon;
    @FXML
    private MFXComboBox<String> cbAvailableFurniture;
    @FXML
    private MFXComboBox<String> cdAvailableModels;
    @FXML
    private MFXComboBox<String> cdAssembly;
    @FXML
    private MFXTextField txtFldNotes;

    @FXML
    private MFXComboBox<String> cbEmployeesToAssign;

    @FXML private MFXFilterComboBox<String> cbLongName;

    private edu.wpi.teamb.entities.requests.EFurnitureRequest EFurnitureRequest;

    public FurnitureRequestControllerI() {
        this.EFurnitureRequest = new EFurnitureRequest();
    }

    @FXML
    public void initialize() throws IOException, SQLException {
        ObservableList<String> longNames = FXCollections.observableArrayList();
        longNames.addAll(Repository.getRepository().getPracticalLongNames());
        cbLongName.setItems(longNames);
        initializeFields();
        initBtns();
    }

    @Override
    public void initBtns() {
        btnSubmit.setOnAction(e -> handleSubmit());
        btnReset.setOnAction(e -> handleReset());
        btnCancel.setOnAction(e -> handleCancel());
        helpIcon.setOnMouseClicked(e -> handleHelp());
    }

    @Override
    public void initializeFields() throws SQLException {
        //Set list of furniture
        ObservableList<String> furniture = FXCollections.observableArrayList("Chair", "Couch", "Table", "Desk", "Bed");
        cbAvailableFurniture.setItems(furniture);

        //Set list of models
        ObservableList<String> models = FXCollections.observableArrayList("Huge", "Big", "Medium", "Small", "Tiny");
        cdAvailableModels.setItems(models);

        //Set list of assembly options
        ObservableList<String> assembly = FXCollections.observableArrayList("No", "Yes");
        cdAssembly.setItems(assembly);

        //Set list of employees
        ObservableList<String> employees =
                FXCollections.observableArrayList();
        employees.add("Unassigned");
        employees.addAll(EFurnitureRequest.getUsernames());
        cbEmployeesToAssign.setItems(employees);
    }

    @Override
    public void handleSubmit() {
        if (nullInputs())
            showPopOver();
        else {
            // Get the standard request fields
            EFurnitureRequest.setEmployee(cbEmployeesToAssign.getValue());
            EFurnitureRequest.setLocationName(cbLongName.getValue());
            EFurnitureRequest.setRequestStatus(IRequest.RequestStatus.Pending);
            EFurnitureRequest.setNotes(txtFldNotes.getText());

            // Get the conference specific fields
            EFurnitureRequest.setFurnitureType(cbAvailableFurniture.getValue());
            EFurnitureRequest.setModel(cdAvailableModels.getValue());
            EFurnitureRequest.setAssembly(cdAssembly.getValue());

            if (EFurnitureRequest.checkRequestFields() && EFurnitureRequest.checkSpecialRequestFields()) {
                String[] output = {
                        EFurnitureRequest.getEmployee(),
                        String.valueOf(EFurnitureRequest.getRequestStatus()),
                        EFurnitureRequest.getLocationName(),
                        EFurnitureRequest.getNotes(),
                        EFurnitureRequest.getFurnitureType(),
                        EFurnitureRequest.getModel(),
                        String.valueOf(stringToBoolean(EFurnitureRequest.getAssembly()))
                };
                EFurnitureRequest.submitRequest(output);
                handleReset();
                Navigation.navigate(Screen.CREATE_NEW_REQUEST);
            }
            submissionAlert();
        }
    }

    @Override
    public void handleReset() {
        cbAvailableFurniture.getSelectionModel().clearSelection();
        cdAvailableModels.getSelectionModel().clearSelection();
        cdAssembly.getSelectionModel().clearSelection();
        txtFldNotes.clear();
        cbEmployeesToAssign.getSelectionModel().clearSelection();
        cbLongName.clear();
        cbLongName.replaceSelection("All Room Names: ");
    }

    @Override
    public void handleHelp() {
        final FXMLLoader popupLoader = new FXMLLoader(Bapp.class.getResource("views/components/popovers/FurnitureRequestHelpPopOver.fxml"));
        PopOver popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_RIGHT);
        popOver.setArrowSize(0.0);
        try {
            popOver.setContentNode(popupLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        popOver.show(helpIcon);
    }

    public boolean stringToBoolean(String assembly) {
        if (assembly.equals("Yes")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean nullInputs() {
        return cbAvailableFurniture.getValue() == null
                || cdAvailableModels.getValue() == null
                || cdAssembly.getValue() == null
                || txtFldNotes.getText().isEmpty()
                || cbEmployeesToAssign.getValue() == null
                || cbLongName.getValue() == null;
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
    public void enterFurnitureRequestEditableMode(FullFurnitureRequest fullFurnitureRequest, InfoCardController currentInfoCardController) {
        //set the editable fields to the values of the request
        cbAvailableFurniture.getSelectionModel().selectItem(fullFurnitureRequest.getType());
        cdAvailableModels.getSelectionModel().selectItem(fullFurnitureRequest.getModel());
        cdAssembly.getSelectionModel().selectItem(fullFurnitureRequest.getAssembly() ? "Yes" : "No");
        txtFldNotes.setText(fullFurnitureRequest.getNotes());
        cbEmployeesToAssign.getSelectionModel().selectItem(fullFurnitureRequest.getEmployee());
        cbLongName.getSelectionModel().selectItem(fullFurnitureRequest.getLocationName());

        //set the submit button to say update
        btnSubmit.setText("Update");
        //remove the current onAction event
        btnSubmit.setOnAction(null);
        //add a new onAction event
        btnSubmit.setOnAction(e -> {
            //set the request fields to the new values
            fullFurnitureRequest.setType(cbAvailableFurniture.getValue());
            fullFurnitureRequest.setModel(cdAvailableModels.getValue());
            fullFurnitureRequest.setAssembly(stringToBoolean(cdAssembly.getValue()));
            fullFurnitureRequest.setNotes(txtFldNotes.getText());
            fullFurnitureRequest.setEmployee(cbEmployeesToAssign.getValue());
            fullFurnitureRequest.setLocationName(cbLongName.getValue());

            //update the request
            EFurnitureRequest.updateFurnitureRequest(fullFurnitureRequest);
            //send the fullFurnitureRequest to the info card controller
            currentInfoCardController.sendRequest(fullFurnitureRequest);
            //close the stage
            ((Stage) btnSubmit.getScene().getWindow()).close();
        });

        //set the reset and cancel buttons to not be visible
        btnReset.setVisible(false);
        btnCancel.setVisible(false);
    }
}

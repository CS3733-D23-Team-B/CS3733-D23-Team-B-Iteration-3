package edu.wpi.teamb.controllers.requests;

import edu.wpi.teamb.Bapp;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.Full.FullFlowerRequest;
import edu.wpi.teamb.controllers.components.InfoCardController;
import edu.wpi.teamb.entities.requests.EFlowerRequest;
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

public class FlowerRequestControllerI implements IRequestController {

    @FXML private MFXButton btnSubmit;
    @FXML private MFXButton btnCancel;
    @FXML private MFXButton btnReset;
    @FXML private ImageView helpIcon;
    @FXML private MFXComboBox<String> cbAvailableFlowers;
    @FXML private MFXComboBox<String> cdAvailableColor;
    @FXML private MFXComboBox<String> cdAvailableType;
    @FXML private MFXTextField txtFldNotes;
    @FXML private MFXTextField txtFldMessage;
    @FXML private MFXComboBox<String> cbEmployeesToAssign;
    @FXML private MFXFilterComboBox<String> cbLongName;

    private final EFlowerRequest EFlowerRequest;

    public FlowerRequestControllerI() {
        this.EFlowerRequest = new EFlowerRequest();
    }

    @FXML
    public void initialize() throws IOException, SQLException {
        initBtns();
        initializeFields();
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
        //Initialize the list of locations to direct request to via dropdown
        ObservableList<String> longNames = FXCollections.observableArrayList();
        longNames.addAll(Repository.getRepository().getPracticalLongNames());
        cbLongName.setItems(longNames);

        //Set types of flowers
        ObservableList<String> flowers = FXCollections.observableArrayList("Rose", "Tulip", "Daisy", "Lily", "Sunflower");
        cbAvailableFlowers.setItems(flowers);

        //Set colors of flowers
        ObservableList<String> colors = FXCollections.observableArrayList("Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Pink", "White", "Black", "Brown", "Other");
        cdAvailableColor.setItems(colors);


        //Set delivery types
        ObservableList<String> deliveryType = FXCollections.observableArrayList("Bouquet", "Single Flower", "Vase", "Other");
        cdAvailableType.setItems(deliveryType);
        //todo: fix locations

        //Set list of employees
        ObservableList<String> employees =
                FXCollections.observableArrayList();
        employees.add("Unassigned");
        employees.addAll(EFlowerRequest.getUsernames());
        cbEmployeesToAssign.setItems(employees);
    }

    @Override
    public void handleSubmit() {
        if (nullInputs())
            showPopOver();
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
                handleReset();
                Navigation.navigate(Screen.CREATE_NEW_REQUEST);
            }
            submissionAlert();
        }
    }

    @Override
    public void handleReset() {
        cbAvailableFlowers.getSelectionModel().clearSelection();
        cdAvailableColor.getSelectionModel().clearSelection();
        cdAvailableType.getSelectionModel().clearSelection();
        txtFldMessage.clear();
        txtFldNotes.clear();
        cbEmployeesToAssign.getSelectionModel().clearSelection();
        cbLongName.clear();
        cbLongName.replaceSelection("All Room Names: ");
    }

    @Override
    public void handleHelp() {
        final FXMLLoader popupLoader = new FXMLLoader(Bapp.class.getResource("views/components/popovers/FlowerRequestHelpPopOver.fxml"));
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

    @Override
    public boolean nullInputs() {
        return cbAvailableFlowers.getValue() == null || cdAvailableColor.getValue() == null || cdAvailableType.getValue() == null || txtFldMessage.getText().isEmpty() || txtFldNotes.getText().isEmpty() || cbEmployeesToAssign.getValue() == null || cbLongName.getValue() == null;
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
        cdAvailableColor.getSelectionModel().selectItem(fullFlowerRequest.getColor());
        cdAvailableType.getSelectionModel().selectItem(fullFlowerRequest.getSize());
        txtFldMessage.setText(fullFlowerRequest.getMessage());
        txtFldNotes.setText(fullFlowerRequest.getNotes());
        cbEmployeesToAssign.getSelectionModel().selectItem(fullFlowerRequest.getEmployee());
        cbLongName.getSelectionModel().selectItem(fullFlowerRequest.getLocationName());

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

            //update the request
            EFlowerRequest.updateFlowerRequest(fullFlowerRequest);
            //send the fullConferenceRequest to the info card controller
            currentInfoCardController.sendRequest(fullFlowerRequest);
            //close the stage
            ((Stage) btnSubmit.getScene().getWindow()).close();
        });
        //set the reset and cancel buttons to not be visible
        btnReset.setVisible(false);
        btnCancel.setVisible(false);
    }
}

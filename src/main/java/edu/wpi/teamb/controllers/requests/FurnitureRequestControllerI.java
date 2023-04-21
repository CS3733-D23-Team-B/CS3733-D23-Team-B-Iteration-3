package edu.wpi.teamb.controllers.requests;

import edu.wpi.teamb.Bapp;
import edu.wpi.teamb.DBAccess.DAO.Repository;
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
        longNames.addAll(Repository.getRepository().getAllLongNames());
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
        employees.addAll(EFurnitureRequest.getUsernames());
        cbEmployeesToAssign.setItems(employees);
    }

    @Override
    public void handleSubmit() {
        // Get the standard request fields
        EFurnitureRequest.setEmployee(cbEmployeesToAssign.getValue());
        EFurnitureRequest.setLocationName(cbLongName.getValue());
        EFurnitureRequest.setRequestStatus(IRequest.RequestStatus.Pending);
        EFurnitureRequest.setNotes(txtFldNotes.getText());

        // Get the conference specific fields
        EFurnitureRequest.setFurnitureType(cbAvailableFurniture.getValue());
        EFurnitureRequest.setModel(cdAvailableModels.getValue());
        EFurnitureRequest.setAssembly(cdAssembly.getValue());

        if(EFurnitureRequest.checkRequestFields() && EFurnitureRequest.checkSpecialRequestFields()){
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
        }else{
            final FXMLLoader popupLoader = new FXMLLoader(Bapp.class.getResource("views/components/popovers/NotAllFieldsCompleteError.fxml"));
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
        submissionAlert();
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
}
package edu.wpi.teamb.controllers.requests;

import edu.wpi.teamb.Bapp;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.Full.FullMealRequest;
import edu.wpi.teamb.controllers.components.InfoCardController;
import edu.wpi.teamb.entities.requests.EMealRequest;
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

public class MealRequestControllerI implements IRequestController{

    @FXML private MFXButton btnSubmit;
    @FXML private MFXButton btnCancel;
    @FXML private MFXButton btnReset;
    @FXML private ImageView helpIcon;
    @FXML private MFXComboBox<String> cbAvailableMeals;
    @FXML private MFXComboBox<String> cbAvailableDrinks;
    @FXML private MFXComboBox<String> cbAvailableSnacks;
    @FXML private MFXTextField txtFldNotes;
    @FXML private MFXComboBox<String> cbOrderLocation;
    @FXML private MFXComboBox<String> cbEmployeesToAssign;
    @FXML private MFXFilterComboBox<String> cbLongName;
    private EMealRequest EMealRequest;

    public MealRequestControllerI(){
        this.EMealRequest = new EMealRequest();
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
        // DROPDOWN INITIALIZATION
        ObservableList<String> longNames = FXCollections.observableArrayList();
        longNames.addAll(Repository.getRepository().getPracticalLongNames());
        cbLongName.setItems(longNames);

        ObservableList<String> locations =
                FXCollections.observableArrayList(Repository.getRepository().getLongNameByType("RETL"));
        cbOrderLocation.setItems(locations);

        // DROPDOWN INITIALIZATION
        ObservableList<String> employees =
                FXCollections.observableArrayList();
        employees.add("Unassigned");
        employees.addAll(EMealRequest.getUsernames());
        cbEmployeesToAssign.setItems(employees);


        // DROPDOWN INITIALIZATION
        ObservableList<String> meals = FXCollections.observableArrayList("Pizza", "Pasta", "Soup");
        cbAvailableMeals.setItems(meals);

        // DROPDOWN INITIALIZATION
        ObservableList<String> drinks =
                FXCollections.observableArrayList("Water", "Coca-Cola", "Ginger-Ale");
        cbAvailableDrinks.setItems(drinks);

        // DROPDOWN INITIALIZATION
        ObservableList<String> snacks = FXCollections.observableArrayList("Chips", "Apple");
        cbAvailableSnacks.setItems(snacks);
    }

    @Override
    public void handleSubmit() {
        if (nullInputs())
            showPopOver();
        else {
            // Get the standard request fields
            EMealRequest.setEmployee(cbEmployeesToAssign.getValue());
            EMealRequest.setLocationName(cbLongName.getValue());
            EMealRequest.setRequestStatus(IRequest.RequestStatus.Pending);
            EMealRequest.setNotes(txtFldNotes.getText());

            // Get the meal specific fields
            EMealRequest.setOrderFrom(cbOrderLocation.getValue());
            EMealRequest.setFood(cbAvailableMeals.getValue());
            EMealRequest.setDrink(cbAvailableDrinks.getValue());
            EMealRequest.setSnack(cbAvailableSnacks.getValue());

            //Check for required fields before allowing submittion
            if (EMealRequest.checkRequestFields() && EMealRequest.checkSpecialRequestFields()) {
                //Set the gathered fields into a string array
                String[] output = {
                        EMealRequest.getEmployee(),
                        String.valueOf(EMealRequest.getRequestStatus()),
                        EMealRequest.getLocationName(),
                        EMealRequest.getNotes(),
                        EMealRequest.getOrderFrom(),
                        EMealRequest.getFood(),
                        EMealRequest.getDrink(),
                        EMealRequest.getSnack()
                };
                EMealRequest.submitRequest(output);
                handleReset();
                Navigation.navigate(Screen.CREATE_NEW_REQUEST);
            }
            submissionAlert();
        }
    }

    @Override
    public void handleReset() {
        //Reset the combo-boxes
        cbOrderLocation.clear();
        cbOrderLocation.replaceSelection("Order Location");
        cbEmployeesToAssign.clear();
        cbEmployeesToAssign.replaceSelection("Employees Available");
        cbAvailableMeals.clear();
        cbAvailableMeals.replaceSelection("Available Meals:");
        cbAvailableDrinks.clear();
        cbAvailableDrinks.replaceSelection("Available Drinks:");
        cbAvailableSnacks.clear();
        cbAvailableSnacks.replaceSelection("Available Snacks:");
        cbLongName.clear();
        cbLongName.replaceSelection("All Room Names: ");
        //Reset text fields
        txtFldNotes.clear();
    }

    @Override
    public void handleHelp() {
        //Load the FXML component
        final FXMLLoader popupLoader = new FXMLLoader(Bapp.class.getResource("views/components/MealRequestHelpPopOver.fxml"));

        //Create a new popover with no arrow and orient it to appear above the help button
        PopOver popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_RIGHT);
        popOver.setArrowSize(0.0);

        //Load FXML into the popOver
        try {
            popOver.setContentNode(popupLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        popOver.show(helpIcon);
    }

    @Override
    public boolean nullInputs() {
        return cbOrderLocation.getValue() == null || cbEmployeesToAssign.getValue() == null || cbAvailableMeals.getValue() == null || cbAvailableDrinks.getValue() == null || cbAvailableSnacks.getValue() == null || cbLongName.getValue() == null;
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
    public void enterMealRequestEditableMode(FullMealRequest fullMealRequest, InfoCardController currentInfoCardController) {
        cbOrderLocation.getSelectionModel().selectItem(fullMealRequest.getOrderFrom());
        cbEmployeesToAssign.getSelectionModel().selectItem(fullMealRequest.getEmployee());
        cbAvailableMeals.getSelectionModel().selectItem(fullMealRequest.getFood());
        cbAvailableDrinks.getSelectionModel().selectItem(fullMealRequest.getDrink());
        cbAvailableSnacks.getSelectionModel().selectItem(fullMealRequest.getSnack());
        cbLongName.getSelectionModel().selectItem(fullMealRequest.getLocationName());
        txtFldNotes.setText(fullMealRequest.getNotes());
        btnSubmit.setText("Update");
        //remove the current onAction event
        btnSubmit.setOnAction(null);
        //add a new onAction event
        btnSubmit.setOnAction(e -> {
            //update all the fields of the fullMealRequest
            fullMealRequest.setOrderFrom(cbOrderLocation.getValue());
            fullMealRequest.setEmployee(cbEmployeesToAssign.getValue());
            fullMealRequest.setFood(cbAvailableMeals.getValue());
            fullMealRequest.setDrink(cbAvailableDrinks.getValue());
            fullMealRequest.setSnack(cbAvailableSnacks.getValue());
            fullMealRequest.setLocationName(cbLongName.getValue());
            fullMealRequest.setNotes(txtFldNotes.getText());
            //update the database
            EMealRequest.updateMealRequests(fullMealRequest);
            //close the window
            Stage stage = (Stage) btnSubmit.getScene().getWindow();
            stage.close();
            //send the fullmealrequest to the info card controller
            currentInfoCardController.sendRequest(fullMealRequest);
        });

        //make the reset and cancel buttons not visible
        btnReset.setVisible(false);
        btnCancel.setVisible(false);
    }
}

package edu.wpi.teamb.controllers.signage;

import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.entities.ESignage;
import edu.wpi.teamb.pathfinding.PathFinding;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

public class SignageFormController {
    @FXML MFXFilterComboBox<String> locationBox;
    @FXML MFXButton btnClose;
    @FXML MFXButton submit;
    @FXML MFXComboBox<String> signageGroupBox;
    @FXML MFXComboBox<String> directionBox;
    @FXML MFXDatePicker startDatePicker;
    @FXML MFXDatePicker endDatePicker;
    @FXML MFXButton btnSubmit;

    private ESignage signageE;

    public void initialize(){
        //Init
        signageE = new ESignage();
        btnClose.setOnMouseClicked(e -> handleClose());
        btnSubmit.setOnMouseClicked(e -> handleSubmit());
        initalizeComboBox();
        initialize_date_pickers();
        initialize_names();
        initialize_directions();
    }

    private void initalizeComboBox() {
        HashSet<String> signageGroups = signageE.getSignageGroups();
        //convert the hashset to an arrayList
        ArrayList<String> signageGroupsList = new ArrayList<String>();
        for (String element : signageGroups) {
            signageGroupsList.add(element);
        }
        ObservableList<String> signageGroupsObservableList = FXCollections.observableArrayList(signageGroupsList);
        signageGroupBox.setItems(signageGroupsObservableList);
        signageGroupBox.selectFirst();
    }

    private void initialize_date_pickers(){
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());

    }

    private void initialize_directions(){
        ObservableList<String> directions = FXCollections.observableArrayList();
        directions.add("up");
        directions.add("down");
        directions.add("left");
        directions.add("right");
        directions.add("stop here");
        directionBox.setItems(directions);
    }

    private void initialize_names(){
        ObservableList<String> names = FXCollections.observableArrayList();
        names.addAll(signageE.getAllLongnames()); //Alphabetical list of names
        locationBox.setItems(names);
    }

    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    private void handleSubmit(){
        
    }


}

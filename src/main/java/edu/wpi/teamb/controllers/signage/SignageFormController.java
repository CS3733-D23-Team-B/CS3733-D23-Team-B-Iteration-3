package edu.wpi.teamb.controllers.signage;

import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.entities.ESignage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.HashSet;

public class SignageFormController {
    @FXML MFXFilterComboBox sign1;
    @FXML MFXButton btnClose;
    @FXML MFXButton submit;
    @FXML MFXComboBox signageGroupBox;
    private ESignage signageE;

    public void initialize(){
        //Init
        signageE = new ESignage();
        initalizeComboBox();
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


}

package edu.wpi.teamb.controllers;

import com.sun.glass.ui.ClipboardAssistance;
import edu.wpi.teamb.Bapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class CreditsPageController {
    @FXML
    MFXButton btnClose;
    @FXML
    private Label groundFloor;
    @FXML
    private Label LL1;
    @FXML
    private Label LL2;
    @FXML
    private Label firstFloor;
    @FXML
    private Label secondFloor;
    @FXML
    private Label thirdFloor;
    @FXML
    private Label breakfast;
    @FXML
    private Label BWH_Background;
    @FXML
    private Label BWH_Logo;
    @FXML
    private Label BWH_Logo_2;
    @FXML
    private Label Cog_Wheel;
    @FXML
    private Label directions;
    @FXML
    private Label flower;
    @FXML
    private Label helpIcon;
    @FXML
    private Label Home;
    @FXML
    private Label LogOut;
    @FXML
    private Label map;
    @FXML
    private Label mealTimes;
    @FXML
    private Label phoneCall;
    @FXML
    private Label reject;
    @FXML
    private Label reply;
    @FXML
    private Label send;
    @FXML
    private Label sofa;
    @FXML
    private Label street;
    @FXML
    private Label workspace;

    @FXML
    private ImageView popup;

    @FXML
    public void initialize() throws IOException {
        initializeBtns();
        //popup.setImage(Bapp.getHospitalListOfFloors().get(2));
    }

    private void initializeBtns() {
        btnClose.setOnMouseClicked(e -> handleClose());
        //groundFloor.setOnMouseClicked(e->{popup.setImage(Bapp.getHospitalListOfFloors().get(2));});
        //LL1.setOnMouseClicked(e->{popup.setImage(Bapp.getHospitalListOfFloors().get(0));});
        handleClick();
    }

    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleClick() {
        groundFloor.setOnMouseClicked(e -> {
            popup.setImage(Bapp.getHospitalListOfFloors().get(2));
        });
        LL1.setOnMouseClicked(e -> {
            popup.setImage(Bapp.getHospitalListOfFloors().get(0));
        });
        LL2.setOnMouseClicked(e -> {
            popup.setImage(Bapp.getHospitalListOfFloors().get(1));
        });
        firstFloor.setOnMouseClicked(e -> {
            popup.setImage(Bapp.getHospitalListOfFloors().get(3));
        });
        secondFloor.setOnMouseClicked(e -> {
            popup.setImage(Bapp.getHospitalListOfFloors().get(4));
        });
        thirdFloor.setOnMouseClicked(e -> {
            popup.setImage(Bapp.getHospitalListOfFloors().get(5));
        });
        breakfast.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/breakfast.png"));
        });
        BWH_Background.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/BrighamWomens-background-img.webp"));
        });
        BWH_Logo.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/bwh-logo.jpg"));
        });
        BWH_Logo_2.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/bwh-logo (1).png"));
        });
        Cog_Wheel.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/cog-wheel-silhouette.png"));
        });
        directions.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/directions.png"));
        });
        flower.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/flower.png"));
        });
        helpIcon.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/helpicon.png"));
        });
        Home.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/home.png"));
        });
        LogOut.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/logout.png"));
        });
        map.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/map.png"));
        });
        mealTimes.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/meal-times.PNG"));
        });
        phoneCall.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/phone-call.png"));
        });
        reject.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/reject.png"));
        });
        reply.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/reply.png"));
        });
        send.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/send.png"));
        });
        sofa.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/sofa.png"));
        });
        street.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/street-sign.png"));
        });
        workspace.setOnMouseClicked(e -> {
            popup.setImage(new Image("edu/wpi/teamb/img/workspace.png"));
        });

    }
}

package edu.wpi.teamb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.event.ActionEvent;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.wpi.teamb.Bapp;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.ORMs.Node;
import edu.wpi.teamb.pathfinding.PathFinding;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import net.kurobako.gesturefx.GesturePane;
import org.controlsfx.control.PopOver;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import static javafx.scene.paint.Color.*;

public class CreditsController {

    @FXML
    private Label groundFloor;
    @FXML private Label LL1;
    @FXML private Label LL2;
    @FXML private Label firstFloor;
    @FXML private Label secondFloor;
    @FXML private Label thirdFloor;
    @FXML private Label breakfast;
    @FXML private Label BWH_Background;
    @FXML private Label BWH_Logo;
    @FXML private Label BWH_Logo_2;
    @FXML private Label Cog_Wheel;
    @FXML private Label directions;
    @FXML private Label flower;
    @FXML private Label helpIcon;
    @FXML private Label Home;
    @FXML private Label logOut;
    @FXML private Label map;
    @FXML private Label mealTimes;
    @FXML private Label phoneCall;
    @FXML private Label reject;
    @FXML private Label reply;
    @FXML private Label send;
    @FXML private Label sofa;
    @FXML private Label street;
    @FXML private Label workspace;



    @FXML private Label conferenceRoom;
    @FXML private Label flowerVase;
    @FXML private Label furnitureRequest;
    @FXML private Label mealRequest;
    @FXML private Label meeting;
    @FXML private Label officeSupplies;
    @FXML private Label edge;
    @FXML private Label move;
    @FXML private Label node;
    @FXML private Label reset;
    @FXML private Label tools;

    @FXML private ImageView popup;

//    @FXML
//    public void initialize(){
//        this.popup = new ImageView("edu/wpi/teamb/img/FloorMapsNoLocationNames/00_thegroundfloor.png");;
//    }

    @FXML
    private void handleClick() {
        groundFloor.setOnMouseClicked(e->{
            popup.setImage(Bapp.getHospitalListOfFloors().get(2));
        });
        LL1.setOnMouseClicked(e->{
            popup.setImage(Bapp.getHospitalListOfFloors().get(0));
        });
        LL2.setOnMouseClicked(e->{
            popup.setImage(Bapp.getHospitalListOfFloors().get(1));
        });
        firstFloor.setOnMouseClicked(e->{
            popup.setImage(Bapp.getHospitalListOfFloors().get(3));
        });
        secondFloor.setOnMouseClicked(e->{
            popup.setImage(Bapp.getHospitalListOfFloors().get(4));
        });
        thirdFloor.setOnMouseClicked(e->{
            popup.setImage(Bapp.getHospitalListOfFloors().get(5));
        });
        breakfast.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/breakfast.png").toExternalForm()));
        });
        BWH_Background.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/BrighamWomens-background-img.webp").toExternalForm()));
        });
        BWH_Logo.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/bwh-logo.jpg").toExternalForm()));
        });
        BWH_Logo_2.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/bwh-logo (1).png").toExternalForm()));
        });
        Cog_Wheel.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/cog-wheel-silhouette.png").toExternalForm()));
        });
        directions.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/directions.png").toExternalForm()));
        });
        flower.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/flower.png").toExternalForm()));
        });
        helpIcon.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/helpicon.png").toExternalForm()));
        });
        Home.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/home.png").toExternalForm()));
        });
        logOut.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/logout.png").toExternalForm()));
        });
        map.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/map.png").toExternalForm()));
        });
        mealTimes.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/meal-times.PNG").toExternalForm()));
        });
        phoneCall.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/meeting.png").toExternalForm()));
        });
        reject.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/reject.png").toExternalForm()));
        });
        reply.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/reply.png").toExternalForm()));
        });
        send.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/send.png").toExternalForm()));
        });
        sofa.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/sofa.png").toExternalForm()));
        });
        street.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/street-sign.png").toExternalForm()));
        });
        workspace.setOnMouseClicked(e->{
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/workspace.png").toExternalForm()));

        });
        conferenceRoom.setOnMouseClicked(e-> {
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/conference-room.jpg").toExternalForm()));
        });
        flowerVase.setOnMouseClicked(e-> {
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/flower-vase2.jpg").toExternalForm()));
        });
        furnitureRequest.setOnMouseClicked(e-> {
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/furtniture-request.jpg").toExternalForm()));
        });
        mealRequest.setOnMouseClicked(e-> {
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/meal-request.png").toExternalForm()));
        });
        meeting.setOnMouseClicked(e-> {
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/meeting.png").toExternalForm()));
        });
        officeSupplies.setOnMouseClicked(e-> {
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/Office-Supplies.jpg").toExternalForm()));
        });
        edge.setOnMouseClicked(e-> {
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/icons/edge.png").toExternalForm()));
        });
        move.setOnMouseClicked(e-> {
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/icons/move.png").toExternalForm()));
        });
        node.setOnMouseClicked(e-> {
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/icons/node.png").toExternalForm()));
        });
        reset.setOnMouseClicked(e-> {
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/icons/reset.png").toExternalForm()));
        });
        tools.setOnMouseClicked(e-> {
            popup.setImage(new Image(getClass().getResource("edu/wpi/teamb/img/icons/tools.png").toExternalForm()));
        });
    }
}

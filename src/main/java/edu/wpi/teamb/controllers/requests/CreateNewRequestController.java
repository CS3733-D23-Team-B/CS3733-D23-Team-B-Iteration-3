package edu.wpi.teamb.controllers.requests;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import edu.wpi.teamb.controllers.NavDrawerController;
import edu.wpi.teamb.entities.ELogin;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;

public class CreateNewRequestController {
    @FXML MFXButton btnHome;
    @FXML private JFXHamburger menuBurger;
    @FXML private JFXDrawer menuDrawer;
    @FXML private Pane navPane;
    @FXML private VBox requestVbox;
    @FXML private MFXButton btnMoveRequests;
    @FXML private MFXButton btnAllRequests;
    @FXML private VBox vboxActivateNav;
    @FXML private VBox vboxActivateNav1;
    @FXML private ImageView icon1;
    @FXML private ImageView icon2;
    @FXML private ImageView icon3;
    @FXML private ImageView icon4;
    @FXML private ImageView icon5;
    @FXML private VBox back1;
    @FXML private VBox back2;
    @FXML private VBox back3;
    @FXML private VBox back4;
    @FXML private VBox back5;

    ELogin.PermissionLevel adminTest;
    private NavDrawerController navDrawerController;
    private boolean navLoaded;

    @FXML
    public void initialize() throws IOException {
        initNavBar();
        requestVbox.getChildren().clear();
        requestVbox.setFillWidth(true);
        adminTest = ELogin.getLogin().getPermissionLevel();
        initializeFields();
        initIcons();
        btnAllRequests.setTooltip(new Tooltip("View all requests"));
        btnMoveRequests.setTooltip(new Tooltip("Add and view move requests"));
        if (adminTest != ELogin.PermissionLevel.ADMIN) {
            btnAllRequests.setVisible(false);
            btnMoveRequests.setVisible(false);
        }
        requestVbox.getChildren().clear();
        loadPage2();
        toggleBtns("icon2");
        navPane.setMouseTransparent(true);
        initializeNavGates();
    }

    public void initIcons() {
        icon1.setOnMouseClicked(event->{
            toggleBtns("icon1");
            loadPage1();}
        );
        Tooltip tooltip1 = new Tooltip("Meal Request");
        Tooltip.install(icon1, tooltip1);
        icon2.setOnMouseClicked(event->{
            toggleBtns("icon2");
            loadPage2();}
        );
        Tooltip tooltip2 = new Tooltip("Conference Request");
        Tooltip.install(icon2, tooltip2);
        icon3.setOnMouseClicked(event->{
            toggleBtns("icon3");
            loadPage3();}
        );
        Tooltip tooltip3 = new Tooltip("Flower Request");
        Tooltip.install(icon3, tooltip3);
        icon4.setOnMouseClicked(event->{
            toggleBtns("icon4");
            loadPage6();}
        );
        Tooltip tooltip4 = new Tooltip("Furniture Request");
        Tooltip.install(icon4, tooltip4);
        icon5.setOnMouseClicked(event->{
            toggleBtns("icon5");
            loadPage5();
        });
        Tooltip tooltip5 = new Tooltip("Office Supplies Request");
        Tooltip.install(icon5, tooltip5);
    }

    public void toggleBtns(String btn){
        switch(btn){
            case "icon1" -> {
                back1.setStyle("-fx-background-radius: 10; -fx-background-color: WHITE");
                back2.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back3.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back4.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back5.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
            }
            case "icon2" -> {
                back1.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back2.setStyle("-fx-background-radius: 10; -fx-background-color: WHITE");
                back3.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back4.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back5.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
            }
            case "icon3" -> {
                back1.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back2.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back3.setStyle("-fx-background-radius: 10; -fx-background-color: WHITE");
                back4.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back5.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
            }
            case "icon4" -> {
                back1.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back2.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back3.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back4.setStyle("-fx-background-radius: 10; -fx-background-color: WHITE");
                back5.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                }
            case "icon5" -> {
                back1.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back2.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back3.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back4.setStyle("-fx-background-radius: 10; -fx-background-color: #5f7ca4");
                back5.setStyle("-fx-background-radius: 10; -fx-background-color: WHITE");
            }
        }
    }

    @FXML
    public void clickAllRequests() {
        btnAllRequests.setOnMouseClicked(event -> Navigation.navigate(Screen.ALL_REQUEST));
    }
    @FXML public void clickMoveRequest(){
        loadPage4();
    }

    public void loadPage1() {
        requestVbox.getChildren().clear();
//        toggleBtns("icon1");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/wpi/teamb/views/requests/MealRequest.fxml"));
            Pane pane = loader.load();
            requestVbox.getChildren().addAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPage2() {
        requestVbox.getChildren().clear();
//        toggleBtns("icon2");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/wpi/teamb/views/requests/ConferenceRequest.fxml"));
            Pane pane = loader.load();
            requestVbox.getChildren().addAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPage3() {
        requestVbox.getChildren().clear();
//        toggleBtns("icon3");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/wpi/teamb/views/requests/FlowerRequests.fxml"));
            Pane pane = loader.load();
            requestVbox.getChildren().addAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadPage4() {
        requestVbox.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/wpi/teamb/views/requests/MoveRequest.fxml"));
            Pane pane = loader.load();
            requestVbox.getChildren().addAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPage5() {
        requestVbox.getChildren().clear();
//        toggleBtns("icon5");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/wpi/teamb/views/requests/OfficeRequest.fxml"));
            Pane pane = loader.load();
            requestVbox.getChildren().addAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPage6() {
        requestVbox.getChildren().clear();
//        toggleBtns("icon4");
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/edu/wpi/teamb/views/requests/FurnitureRequest.fxml"));
            Pane pane = loader.load();
            requestVbox.getChildren().addAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPage7() {
        requestVbox.getChildren().clear();
//        toggleBtns("icon4");
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/edu/wpi/teamb/views/requests/TranslationRequest.fxml"));
            Pane pane = loader.load();
            requestVbox.getChildren().addAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void initializeFields() {

        ObservableList<String> locations = FXCollections.observableArrayList("Meal Delivery", "Conference Room",
                "Flower Delivery", "Furniture Delivery", "Office Supplies", "Translation Request");
        // TODO: only add thise if the user is an admin
        if (adminTest == ELogin.PermissionLevel.ADMIN) {
            ObservableList<String> AdminOnly = FXCollections.observableArrayList("Move");
            for (String string : AdminOnly) {
                locations.add(locations.size() - 2, string);
            }
        }
        navLoaded = false;
        activateNav();
        deactivateNav();
    }

    /**
     * For some reason there are occasions when the nav-bar gates for toggling its handling does not start correctly
     * This fixes this issue
     */
    public void initializeNavGates(){
        activateNav();
        deactivateNav();
        navPane.setMouseTransparent(true);
        vboxActivateNav.setDisable(false);
        navLoaded = false;
        vboxActivateNav1.setDisable(true);
    }

    /**
     * Utilizes a gate to swap between handling the navdrawer and the rest of the page
     * Swaps ownership of the strip to the navdraw
     */

    public void activateNav(){
        vboxActivateNav.setOnMouseEntered(event -> {
            if(!navLoaded) {
                navPane.setMouseTransparent(false);
                navLoaded = true;
                vboxActivateNav.setDisable(true);
                vboxActivateNav1.setDisable(false);
            }
        });
    }

    /**
     * Utilizes a gate to swap between handling the navdrawer and the rest of the page
     * Swaps ownership of the strip to the page
     */
    public void deactivateNav(){
        vboxActivateNav1.setOnMouseEntered(event -> {
            if(navLoaded){
                navPane.setMouseTransparent(true);
                vboxActivateNav.setDisable(false);
                navLoaded = false;
                vboxActivateNav1.setDisable(true);
            }
        });
    }


    public void initNavBar() {
        // https://github.com/afsalashyana/JavaFX-Tutorial-Codes/tree/master/JavaFX%20Navigation%20Drawer/src/genuinecoder
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/wpi/teamb/views/components/NavDrawer.fxml"));
            VBox vbox = loader.load();
            NavDrawerController navDrawerController = loader.getController();
            menuDrawer.setSidePane(vbox);
            navPane.setMouseTransparent(true);
            navLoaded = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        HamburgerBackArrowBasicTransition burgerOpen = new HamburgerBackArrowBasicTransition(menuBurger);
        burgerOpen.setRate(-1);
        menuBurger.addEventHandler(
                javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    burgerOpen.setRate(burgerOpen.getRate() * -1);
                    burgerOpen.play();
                    if (menuDrawer.isOpened()) {
                        menuDrawer.close();
                        vboxActivateNav1.toFront();
                    } else {
                        menuDrawer.toFront();
                        menuBurger.toFront();
                        menuDrawer.open();
                    }
                });
    }
}
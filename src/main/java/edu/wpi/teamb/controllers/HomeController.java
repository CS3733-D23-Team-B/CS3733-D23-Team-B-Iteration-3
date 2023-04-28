package edu.wpi.teamb.controllers;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import edu.wpi.teamb.Bapp;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.ORMs.Alert;
import edu.wpi.teamb.entities.ELogin;
import edu.wpi.teamb.entities.EHome;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class HomeController {
    @FXML private JFXHamburger menuBurger;
    @FXML private JFXDrawer menuDrawer;
    @FXML private Pane navPane;
    @FXML private Pane homePane;
    @FXML private VBox vboxActivateNav;
    @FXML private VBox vboxActivateNav1;
    @FXML private MFXButton btnAbout;
    @FXML private MFXButton btnCredits;
    @FXML private  MFXButton btnSecret;
    @FXML private MFXButton btnClear;
    @FXML private MFXButton viewUserRequestButton;
    @FXML private TableView<Alert> alertsTable;
    private MFXButton pathfinderImgBtn = new MFXButton();

    private String username;

    Bounds bounds;
    private EHome homeE;

    @FXML
    public void initialize() throws IOException {
        username = ELogin.getLogin().getUsername();
        initNavBar();
        initPathfinderBtn();
        initializeBtns();
        loadAlerts();
        homeE = new EHome();
        bounds = homePane.getBoundsInLocal();

    }

    private void initPathfinderBtn() {
        pathfinderImgBtn.setOnMouseClicked(e -> onPathfinderImgClick());
        Image mapImg = Bapp.getHospitalListOfFloors().get(0);
        ImageView imageView = new ImageView(mapImg);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        pathfinderImgBtn.setPrefSize(200, 200);
        pathfinderImgBtn.setLayoutX(700);
        pathfinderImgBtn.setLayoutY(200);
        pathfinderImgBtn.setGraphic(imageView);
        homePane.getChildren().add(pathfinderImgBtn);
    }

    public void loadAlerts(){
        ArrayList<Alert> allAlerts = Repository.getRepository().getAllAlerts();
        alertsTable.setEditable(false);
        TableColumn<Alert, String> titles = new TableColumn<>("Title");
        titles.setMinWidth(100);
        titles.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<edu.wpi.teamb.DBAccess.ORMs.Alert, String> descriptions = new TableColumn<>("Description");
        descriptions.setMinWidth(220);
        descriptions.setCellValueFactory(new PropertyValueFactory<>("description"));


        TableColumn<edu.wpi.teamb.DBAccess.ORMs.Alert, Timestamp> time = new TableColumn<>("Created at");
        time.setCellValueFactory((new PropertyValueFactory<>("createdAt")));
        alertsTable.getColumns().addAll(titles, descriptions, time);
        time.setSortType(TableColumn.SortType.DESCENDING);
        alertsTable.getSortOrder().setAll(time);


        for(Alert alert : allAlerts){
            if(alert.getEmployee().equals(username) || alert.getEmployee().equals("unassigned")) {
                alertsTable.getItems().add(alert);
            }
        }

        alertsTable.getSortOrder().setAll(alertsTable.getColumns().get(2));
        time.setVisible(false);

    }



    Circle circle = new Circle();
    Image img = new Image("edu/wpi/teamb/img/secret.png");
    ImageView secretView = new ImageView(img);

    private void secret(Boolean go){
        btnSecret.setVisible(false);
        secretView.setImage(img);
        secretView.setFitHeight(150);
        secretView.setPreserveRatio(true);
        secretView.setVisible(true);
        secretView.setX(300);
        secretView.setY(300);
        btnClear.setVisible(true);
        if (!homePane.getChildren().contains(secretView)) {homePane.getChildren().add(secretView);}
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
            double deltaX = 2;
            double deltaY = 2;

            @Override
            public void handle(ActionEvent actionEvent) {

                secretView.setX(secretView.getX() + deltaX);
                secretView.setY(secretView.getY() + deltaY);

//                System.out.println(bounds);
                boolean rightBorder = secretView.getX() >= (bounds.getMaxX());
                boolean leftBorder = secretView.getX() <= (bounds.getMinX());
                boolean bottomBorder = secretView.getY() >= (bounds.getMaxY());
                boolean topBorder = secretView.getY() <= (bounds.getMinY());

                if (rightBorder || leftBorder) {
                    deltaX *= -1;
                }
                if (bottomBorder || topBorder) {
                    deltaY *= -1;
                }
                homePane.toFront();
                secretView.toFront();
            }

        }));
        if (go) {
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }
        else {
//            timeline.stop();
//            btnClear.setVisible(false);
//            btnSecret.setVisible(true);
//            homePane.getChildren().remove(secretView);
//            secretView = new ImageView();
            Navigation.navigate(Screen.HOME);
        }

    }

    public void clearSecret(){


    }

    private void initializeBtns() {
        btnCredits.setOnMouseClicked(e -> handleCredits());
        btnAbout.setOnMouseClicked(e -> handleAbout());
        btnSecret.setOnMouseClicked(e -> secret(true));
        btnClear.setOnMouseClicked(e -> secret(false));
        viewUserRequestButton.setOnMouseClicked(e -> handleUserRequests());
    }

    private void handleUserRequests() {
        //implementation using FXMLLoader
        FXMLLoader loader = null;
        //AnchorPane
        AnchorPane userRequestsPage = null;
        //controller for the corresponding InfoCard.fxml
        CurrentUserRequestsController currentUserRequestController = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/edu/wpi/teamb/views/CurrentUserRequests.fxml"));
            userRequestsPage = loader.load();
            currentUserRequestController = loader.getController();
        } catch (IOException e) {
            System.out.println("IOException in loadRequestsIntoContainer of AllRequestsController: " + e.getMessage());
        }
        //open the AnchorPane in a new window
        Stage stage = new Stage();
        stage.setTitle("My Requests");
        stage.setScene(new Scene(userRequestsPage));
        stage.initStyle(StageStyle.UTILITY);
        //set the resizable to false
        stage.setResizable(false);
        stage.show();
    }

    private void handleAbout() {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("edu/wpi/teamb/views/AboutPage.fxml")));
            Stage stage = new Stage();
            stage.setTitle("About");
            stage.setScene(new Scene(root, 1280, 720));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCredits() {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("edu/wpi/teamb/views/CreditsPage.fxml")));
            Stage stage = new Stage();
            stage.setTitle("Credits");
            stage.setScene(new Scene(root, 1280, 720));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void activateNav(){
        vboxActivateNav.setOnMouseEntered(event -> {
//            if(!navLoaded) {
            System.out.println("on");
            navPane.setMouseTransparent(false);
            navPane.setMouseTransparent(false);
//                navLoaded = true;
//            }
        });
    }

    public void deactivateNav(){
        navPane.setOnMouseClicked(event -> {
//            if(!navLoaded) {
                System.out.println("off");
                navPane.setMouseTransparent(true);
                vboxActivateNav1.setMouseTransparent(true);
//            }
        });
    }



    @FXML
    void onPathfinderImgClick() {
        Navigation.navigate(Screen.PATHFINDER);
    }

    public void initNavBar() {
        // https://github.com/afsalashyana/JavaFX-Tutorial-Codes/tree/master/JavaFX%20Navigation%20Drawer/src/genuinecoder
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/edu/wpi/teamb/views/components/NavDrawer.fxml"));
            VBox vbox = loader.load();
            NavDrawerController navDrawerController = loader.getController();
            menuDrawer.setSidePane(vbox);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HamburgerBackArrowBasicTransition burgerOpen =
                new HamburgerBackArrowBasicTransition(menuBurger);
        burgerOpen.setRate(-1);
        menuBurger.addEventHandler(
                javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    burgerOpen.setRate(burgerOpen.getRate() * -1);
                    burgerOpen.play();
                    if (menuDrawer.isOpened()) {
                        menuDrawer.close();
                    } else {
                        menuDrawer.open();
                    }
                });
    }
}

package edu.wpi.teamb.controllers.settings;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.controllers.NavDrawerController;
import edu.wpi.teamb.entities.ELogin;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SettingsController {
    @FXML
    private JFXHamburger menuBurger;
    @FXML private JFXDrawer menuDrawer;
    ELogin.PermissionLevel adminTest;
    @FXML private MFXButton btnEditUsers;
    @FXML private MFXButton btnEditAccount;
    @FXML private MFXButton btnEditAlerts;
    @FXML private MFXButton btnChangeServer;
    @FXML private MFXButton btnViewCSVs;

    @FXML
    public void initialize() throws IOException {
        initNavBar();
        initButtons();
        adminTest = ELogin.getLogin().getPermissionLevel();
        if (adminTest != ELogin.PermissionLevel.ADMIN) {
            btnEditUsers.setVisible(false);
            btnEditAlerts.setVisible(false);
            btnChangeServer.setVisible(false);
        }
    }

    public void initButtons() {
        btnEditAlerts.setOnMouseClicked(event -> Navigation.navigate(Screen.EDIT_ALERTS));
        btnEditAccount.setOnMouseClicked(event -> Navigation.navigate(Screen.EDIT_ACCOUNT));
        btnEditUsers.setOnMouseClicked(event -> Navigation.navigate(Screen.EDIT_USERS));
        btnChangeServer.setOnMouseClicked(event -> changeServer());
        btnViewCSVs.setOnMouseClicked(event -> handleCSVs());
    }

    private void handleCSVs() {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("edu/wpi/teamb/views/settings/ViewCSVs.fxml")));
            Stage stage = new Stage();
            stage.setTitle("View CSVs");
            stage.setScene(new Scene(root, 1280, 720));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeServer() {
        int server = Repository.getRepository().getDatabaseServer();
        if (server == 0) { // if server is WPI
            Repository.getRepository().switchTo(1);       // Swap to AWS
        } else if (server == 1){    // if server is AWS
            Repository.getRepository().switchTo(0);     // Else swap to WPI
        }
        server = Repository.getRepository().getDatabaseServer();
        if (server == 1) System.out.println("Server changed to AWS Postgres");
        else if (server == 0) System.out.println("Server changed to WPI Postgres");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Server changed!");
        alert.setHeaderText(null);
        if (server == 1) alert.setContentText("Successfully changed server to AWS Postgres");
        else if (server == 0) alert.setContentText("Successfully changed server to WPI Postgres");
        alert.showAndWait();
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

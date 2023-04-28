package edu.wpi.teamb.controllers.settings;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.ORMs.User;
import edu.wpi.teamb.controllers.NavDrawerController;
import edu.wpi.teamb.entities.ELogin;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;

public class EditAccountController {
    @FXML
    private JFXHamburger menuBurger;
    @FXML private JFXDrawer menuDrawer;
    @FXML private MFXTextField tfPassword;
    @FXML private MFXTextField tfUsername;
    @FXML private MFXTextField tfName;
    @FXML private MFXTextField tfEmail;
    @FXML private MFXButton btnSaveEdits;
    ELogin eLogin = ELogin.getLogin();
    private final User currentUser = Repository.getRepository().getUser(eLogin.getUsername());

    @FXML
    public void initialize() throws IOException {
        initNavBar();
        initializeFields();
        initButtons();
    }

    public void initializeFields() {
        // Initialize the user data
        tfName.setText(currentUser.getName());
        tfUsername.setText(currentUser.getUsername());
        tfUsername.setEditable(false); // cannot change username
        tfPassword.setText(currentUser.getPassword());
        tfEmail.setText(currentUser.getEmail());
    }

    public void initButtons() {
        btnSaveEdits.setOnMouseClicked(event -> handleSaveEdits());
    }

    private void handleSaveEdits() {
        User user = Repository.getRepository().getUser(eLogin.getUsername());
        user.setName(tfName.getText());
        user.setUsername(tfUsername.getText());
        user.setPassword(tfPassword.getText());
        user.setEmail(tfEmail.getText());
        Repository.getRepository().updateUser(user);

        // Create an alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Updated Account Details");
        alert.setHeaderText(null);
        alert.setContentText("Successfully Updated Account Details");
        alert.showAndWait();

        // Go home
        Navigation.navigate(Screen.HOME);
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

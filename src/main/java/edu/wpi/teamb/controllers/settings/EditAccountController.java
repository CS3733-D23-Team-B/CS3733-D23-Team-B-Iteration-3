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
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;

public class EditAccountController {
    @FXML
    private JFXHamburger menuBurger;
    @FXML private JFXDrawer menuDrawer;
    @FXML private MFXPasswordField tfPassword;
    @FXML private MFXTextField tfUsername;
    @FXML private MFXTextField tfName;
    @FXML private MFXTextField tfEmail;
    @FXML private MFXButton btnSaveEdits;
    @FXML private MFXButton btnBack;
    ELogin eLogin = ELogin.getLogin();
    private final User currentUser = Repository.getRepository().getUser(eLogin.getUsername());

    @FXML private Pane navPane;
    @FXML private VBox vboxActivateNav;
    @FXML private VBox vboxActivateNav1;
    private boolean navLoaded;

    @FXML
    public void initialize() throws IOException {
        initNavBar();
        initializeFields();
        initButtons();
        navLoaded = false;
        activateNav();
        deactivateNav();
        initializeNavGates();
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
        btnSaveEdits.setTooltip(new Tooltip("Click to save your edits"));
        btnSaveEdits.setDisable(true);
        btnBack.setTooltip(new Tooltip("Click to go back to the settings page"));
        btnSaveEdits.setOnMouseClicked(event -> handleSaveEdits());
        btnBack.setOnMouseClicked(event -> Navigation.navigate(Screen.SETTINGS));
        ChangeListener<String> changeListener = (observable, oldValue, newValue) -> {
            btnSaveEdits.setDisable(false);
        };
        tfName.textProperty().addListener(changeListener);
        tfPassword.textProperty().addListener(changeListener);
        tfEmail.textProperty().addListener(changeListener);
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
        Navigation.navigate(Screen.SETTINGS);
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

    /**
     * Utilizes a gate to swap between handling the navdrawer and the rest of the page
     * Swaps ownership of the strip to the navdraw
     */
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
                        vboxActivateNav1.toFront();
                    } else {
                        menuDrawer.toFront();
                        menuBurger.toFront();
                        menuDrawer.open();
                    }
                });
    }
}

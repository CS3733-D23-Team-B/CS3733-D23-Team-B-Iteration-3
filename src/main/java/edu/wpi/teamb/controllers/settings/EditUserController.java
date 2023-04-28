package edu.wpi.teamb.controllers.settings;

import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.ORMs.User;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class EditUserController {

    @FXML private MFXTextField tfPassword;
    @FXML private MFXTextField tfUsername;
    @FXML private MFXTextField tfName;
    @FXML private MFXTextField tfEmail;
    @FXML private MFXComboBox<String> cbPermissionLevel;
    @FXML private MFXButton btnSaveEdits;
    static User currentUser = null;
    @FXML
    public void initialize() throws IOException {
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
        cbPermissionLevel.setValue(permissionLevelToString(currentUser.getPermissionLevel()));

        // Init combo box
        ObservableList<String> permissionLevels = FXCollections.observableArrayList();
        // Initialize the permission level combo boxes
        permissionLevels.add("ADMIN");
        permissionLevels.add("EMPLOYEE");
        cbPermissionLevel.setItems(permissionLevels);
    }

    public void initButtons() {
        btnSaveEdits.setOnMouseClicked(event -> handleSaveEdits());
    }

    private void handleSaveEdits() {
        currentUser.setName(tfName.getText());
        currentUser.setUsername(tfUsername.getText());
        currentUser.setPassword(tfPassword.getText());
        currentUser.setEmail(tfEmail.getText());
        currentUser.setPermissionLevel(permissionLevelToInt(cbPermissionLevel.getValue()));
        Repository.getRepository().updateUser(currentUser);

        // Create an alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Updated Account Details");
        alert.setHeaderText(null);
        alert.setContentText("Successfully Updated Account Details");
        alert.showAndWait();
        Stage stage = (Stage) btnSaveEdits.getScene().getWindow();
        stage.close();
    }

    public static void setCurrentUser(User currentUser) {
        EditUserController.currentUser = currentUser;
    }

    private int permissionLevelToInt(String permissionLevel) {
        if (permissionLevel.equals("ADMIN")) {
            return 0;
        }
        if (permissionLevel.equals("EMPLOYEE")) {
            return 1;
        }
        else
            return 2; // Error
    }

    private String permissionLevelToString(int permissionLevel) {
        if (permissionLevel == 0) {
            return "ADMIN";
        }
        if (permissionLevel == 1) {
            return "EMPLOYEE";
        }
        else
            return "Error"; // Error
    }
}

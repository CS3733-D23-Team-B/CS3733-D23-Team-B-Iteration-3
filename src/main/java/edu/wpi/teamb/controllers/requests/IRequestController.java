package edu.wpi.teamb.controllers.requests;

import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import java.sql.SQLException;

public interface IRequestController {

    /**
     * Initializes the buttons for the request form
     */
    void initBtns();

    /**
     * Initializes the fields for the request form
     */
    void initializeFields() throws SQLException;

    /**
     * Handles the submit button
     */
    void handleSubmit();

    /**
     * Handles the reset button
     */
    void handleReset();

    /**
     * Handles the cancel button
     */
    default void handleCancel() {
        Navigation.navigate(Screen.HOME);
    };

    /**
     * Handles the help button
     */
    void handleHelp();

    default void submissionAlert() {
        // Create an alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Submission Successful");
        alert.setHeaderText(null);
        alert.setContentText("Successfully Submitted Request");
        alert.showAndWait();
        Navigation.navigate(Screen.HOME);
    }
}


package edu.wpi.teamb.controllers.signage;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.SQLException;

public class SignageComponentIndividualDirectionController {
    @FXML Text signageDirectionIconLeft;
    @FXML Text signageDirectionIconRight;
    @FXML Text signageLocationText;


    public void initialize() throws IOException, SQLException {
    }

    /**
     * sets both signageDirectionIconLeft and signageDirectionIconRight to the same text
     */
    public void setSignageDirectionIcons(String text) {
        String directionIcon = "";
        switch (text) {
            case "left":
                directionIcon = "<";
                break;
            case "right":
                directionIcon = ">";
                break;
            case "up":
                directionIcon = "/\\";
                break;
            case "down":
                directionIcon = "\\/";
                break;
            default:
                directionIcon = " ";
                break;
        }

        signageDirectionIconLeft.setText(directionIcon);
        signageDirectionIconRight.setText(directionIcon);
    }

    /**
     * sets signageLocationText to the given text
     * @param text
     */
    public void setSignageLocationText(String text) {
        signageLocationText.setText(text);
    }
}

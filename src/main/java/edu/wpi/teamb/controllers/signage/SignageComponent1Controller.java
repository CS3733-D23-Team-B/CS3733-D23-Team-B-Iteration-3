package edu.wpi.teamb.controllers.signage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SignageComponent1Controller {
    @FXML VBox signsContainerVBox;

    @FXML public void initialize() {
        signsContainerVBox.getChildren().clear();
    }

    /**
     * adds the given child to the VBox
     * @param
     */
    public void addSignToVBox(String locationName, String direction) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/edu/wpi/teamb/views/components/SignageComponentIndividualDirection.fxml"));
            AnchorPane pane = loader.load();
            //get the controller that was loaded by the loader
            SignageComponentIndividualDirectionController controller = loader.getController();
            //set the location of the sign
            controller.setSignageLocationText(locationName);
            //set the direction of the sign
            controller.setSignageDirectionIcons(direction);
            //add the sign to the VBox
            signsContainerVBox.getChildren().addAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //set the margin of the child to 3
        signsContainerVBox.setSpacing(3);
    }

}

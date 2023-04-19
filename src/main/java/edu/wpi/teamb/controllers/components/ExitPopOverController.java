package edu.wpi.teamb.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;

public class ExitPopOverController {

    @FXML MFXButton btnYes;

    public void clickExit(){
        btnYes.setOnMouseClicked(event -> System.exit(0));
    }

}

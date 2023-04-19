package edu.wpi.teamb.controllers.components;

import edu.wpi.teamb.entities.Login;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class MapEditorHelpPopOverController {


    @FXML private Text text1;
    @FXML private Text text2;
    @FXML private Text text3;
    @FXML private Text text4;
    @FXML private Text text5;
    @FXML private Text text6;

    public void initialize(){
        if(Login.getLogin().getPermissionLevel() != Login.PermissionLevel.ADMIN){
            text2.setVisible(false);
            text3.setVisible(false);
            text4.setText("Only Admin can edit the map");
            text5.setVisible(false);
            text6.setVisible(false);


        }
    }
}

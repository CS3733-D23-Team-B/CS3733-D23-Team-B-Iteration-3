package edu.wpi.teamb.controllers.signage;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import edu.wpi.teamb.controllers.NavDrawerController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class SignageController {

  @FXML private JFXHamburger menuBurger;
  @FXML private JFXDrawer menuDrawer;
  @FXML private MFXComboBox cbLocation;
  @FXML private VBox signVbox;

  @FXML
  public void initialize() throws IOException {
      signageE = new ESignage();
      initNavBar();
      initializeFields();
      signVbox.getChildren().clear();
      loadPage1();
  }

  public void clickCbLocation() {
      signVbox.getChildren().clear();
      displaySelection();
  }

    public void displaySelection() {
        String item = cbLocation.getSelectedItem().toString();
        switch (item) {
            case "Screen 1" -> loadPage1();
            case "Screen 2" -> loadPage2();
            default -> loadPage1();
        };
    }

  public void loadPage1() {
      try {
          FXMLLoader loader =
                  new FXMLLoader(getClass().getResource("/edu/wpi/teamb/views/components/SignageComponent1.fxml"));
          Pane pane = loader.load();
          signVbox.getChildren().addAll(pane);
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

    public void loadPage2() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/edu/wpi/teamb/views/components/SignageComponent2.fxml"));
            Pane pane = loader.load();
            signVbox.getChildren().addAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  public void initializeFields() {
      ObservableList<String> locations = FXCollections.observableArrayList(signageE.getSignageGroups());
      cbLocation.setItems(locations);
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

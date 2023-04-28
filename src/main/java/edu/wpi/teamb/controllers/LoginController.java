package edu.wpi.teamb.controllers;

import edu.wpi.teamb.entities.ELogin;
import edu.wpi.teamb.exceptions.EmptyLoginCredentialsException;
import edu.wpi.teamb.exceptions.IncorrectPasswordException;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController {
  @FXML private MFXButton btnLogin;
  @FXML private MFXButton btnForgotPassword;
  @FXML private MFXButton btnCreateAccount;
  @FXML private MFXRadioButton radioRememberMe;
  @FXML private MFXTextField textUsername;
  @FXML private MFXTextField textPassword;
  @FXML private Text errorMsg;
  private ELogin ELogin;

  public LoginController() {
    this.ELogin = ELogin.getLogin();
  }

  @FXML
  public void clickLogin(ActionEvent event) throws IOException {
    checkLogin();
  }

  public void clickForgotPassword(ActionEvent event) throws IOException {
    Parent root;
    try {
      root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("edu/wpi/teamb/views/settings/ForgotPassword.fxml")));
      Stage stage = new Stage();
      stage.setTitle("Forgot Password");
      stage.setScene(new Scene(root, 400, 600));
      stage.show();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Checks if the username and password entered are correct.
   *
   * @throws IOException
   */
  private void checkLogin() throws IOException {
    // first check if empty
    // otherwise send the username and password data over to Login entity to be checked with
    // database
    try {
      if (ELogin.checkLogin(textUsername.getText(), textPassword.getText())) {
        errorMsg.setText("Logged in Successful!");
        Navigation.navigate(Screen.HOME);
      } else {
        //should never run this section unless there is a catastrophic error of some sorts
        errorMsg.setText("Something has gone very wrong");
      }
    } catch (EmptyLoginCredentialsException e) {
      errorMsg.setText("Fields are empty...Enter your data.");
    } catch (NullPointerException e) {
      errorMsg.setText("Username does not exist in our system.");
    } catch (IncorrectPasswordException e) {
      errorMsg.setText("Wrong password entered for the given username.");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @FXML
  public void clickExit() {
    System.exit(0);
  }
}

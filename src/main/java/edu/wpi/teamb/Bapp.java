package edu.wpi.teamb;

import com.sun.javafx.application.LauncherImpl;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Bapp extends Application {

  @Setter @Getter private static Stage primaryStage;
  @Setter @Getter private static BorderPane rootPane;

  @Getter private static ArrayList<Image> hospitalListOfFloors;

  private static final double WIDTH = 1280;
  private static final double HEIGHT = 720;

  // Delay for preloader
  private static final int COUNT_LIMIT = 50000;

  private static int stepCount = 1;

  // Used to demonstrate step counts.
  public static String STEP() {
    return stepCount++ + ". ";
  }

  @Override
  public void init() {
    loadImages();
    log.info("Starting Up");
    for (int i = 0; i < COUNT_LIMIT; i++) {
      double progress = (100 * i) / COUNT_LIMIT;
      LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(progress));
    }
  }

  public Bapp() {
    // Constructor is called after BEFORE_LOAD.
    System.out.println(Bapp.STEP() + "Bapp constructor called, thread: " + Thread.currentThread().getName());
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    /* primaryStage is generally only used if one of your components require the stage to display */

    Image Icon = new Image("edu/wpi/teamb/img/bwh-logo.jpg");

    primaryStage.setTitle("Team B - Hospital Application");
    primaryStage.getIcons().add(Icon);
    Bapp.primaryStage = primaryStage;
    primaryStage.setResizable(false);

    final FXMLLoader loader = new FXMLLoader(Bapp.class.getResource("views/Root.fxml"));
    final BorderPane root = loader.load();

    Bapp.rootPane = root;

    final Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.show();

    Navigation.navigate(Screen.LOGIN);
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }

  public void loadImages(){
    hospitalListOfFloors = new ArrayList<>();
    hospitalListOfFloors.add(new Image(Objects.requireNonNull(Bapp.class.getResourceAsStream("img/FloorMapsNoLocationNames/00_thelowerlevel1.png"))));
    hospitalListOfFloors.add(new Image(Objects.requireNonNull(Bapp.class.getResourceAsStream("img/FloorMapsNoLocationNames/00_thelowerlevel2.png"))));
    hospitalListOfFloors.add(new Image(Objects.requireNonNull(Bapp.class.getResourceAsStream("img/FloorMapsNoLocationNames/00_thegroundfloor.png"))));
    hospitalListOfFloors.add(new Image(Objects.requireNonNull(Bapp.class.getResourceAsStream("img/FloorMapsNoLocationNames/01_thefirstfloor.png"))));
    hospitalListOfFloors.add(new Image(Objects.requireNonNull(Bapp.class.getResourceAsStream("img/FloorMapsNoLocationNames/02_thesecondfloor.png"))));
    hospitalListOfFloors.add(new Image(Objects.requireNonNull(Bapp.class.getResourceAsStream("img/FloorMapsNoLocationNames/03_thethirdfloor.png"))));
  }

}

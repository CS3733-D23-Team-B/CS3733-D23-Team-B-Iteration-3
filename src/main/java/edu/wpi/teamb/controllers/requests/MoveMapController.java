package edu.wpi.teamb.controllers.requests;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import edu.wpi.teamb.Bapp;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.Full.FullNode;
import edu.wpi.teamb.DBAccess.ORMs.Move;
import edu.wpi.teamb.controllers.NavDrawerController;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import edu.wpi.teamb.pathfinding.PathFinding;
import io.github.palexdev.materialfx.controls.*;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;
import net.kurobako.gesturefx.GesturePane;
import org.controlsfx.control.PopOver;

import static javafx.scene.paint.Color.*;

public class MoveMapController {
    @FXML private JFXHamburger menuBurger;
    @FXML private JFXDrawer menuDrawer;
    @FXML private ImageView helpIcon;
    @FXML private VBox VboxPathfinder;
    @FXML private StackPane stackPaneMapView;
    @FXML private ImageView imageViewPathfinder;
    @FXML private MFXButton btnL1;
    @FXML private MFXButton btnL2;
    @FXML private MFXButton btn1;
    @FXML private MFXButton btn2;
    @FXML private MFXButton btn3;
    private String currentFloor = "1";

    public GesturePane pane = new GesturePane();
    private ArrayList<Move> allMoves;
    private Tooltip nameToolTip;

    private HashMap<Integer,ArrayList<Move>> move_map = new HashMap<>();
    private ArrayList<Move> upcoming_moves = new ArrayList<>();
    private ArrayList<Move> moved_today = new ArrayList<>();
    private HashMap<Integer, FullNode> fullNodesByID = new HashMap<>();
    HashMap<String,FullNode> fullNodesByLongname = new HashMap<>();
    private ArrayList<FullNode> fullNodes = new ArrayList<>();
    Group pathGroup;
    Group moveInfo;
    Pane locationCanvas;
    @FXML
    public void initialize() throws IOException, SQLException {
        initNavBar();
        hoverHelp();
        initButtons();
        getMoveMap();
        PathFinding.ASTAR.force_init();
        this.stackPaneMapView = new StackPane(); // no longer @FXML
        this.pathGroup = new Group();
        this.moveInfo = new Group();
        this.locationCanvas = new Pane();
        this.allMoves = Repository.getRepository().getAllMoves();

        this.pane.setContent(stackPaneMapView);
        this.imageViewPathfinder = new ImageView(Bapp.getHospitalListOfFloors().get(3)); // no longer @FXML
        this.stackPaneMapView.getChildren().add(this.imageViewPathfinder);
        this.stackPaneMapView.getChildren().add(this.locationCanvas);

        this.locationCanvas.getChildren().add(pathGroup);
        this.locationCanvas.getChildren().add(moveInfo);
        nameToolTip = new Tooltip();

        pane.setScrollMode(GesturePane.ScrollMode.ZOOM);
        pane.setScrollBarPolicy(GesturePane.ScrollBarPolicy.NEVER);
        Platform.runLater(() -> this.pane.centreOn(new Point2D(2190, 910)));
//        getMoveMap();
//        handle_move();
        this.fullNodesByID = PathFinding.ASTAR.getFullNodesByID();
        this.fullNodes = PathFinding.ASTAR.getFullNodes();
        getFilteredLongnames();
        handle_move();
        displayMoves(currentFloor);
        System.out.println("Move Map initialized");
    }

    private void changeButtonColor(String currentFloor) {
        switch (currentFloor) {
            case "L1" -> {
                btnL1.setStyle("-fx-background-color: #f6bd38");
                btnL2.setStyle("-fx-background-color: #1C4EFE");
                btn1.setStyle("-fx-background-color: #1C4EFE");
                btn2.setStyle("-fx-background-color: #1C4EFE");
                btn3.setStyle("-fx-background-color: #1C4EFE");
            }
            case "L2" -> {
                btnL1.setStyle("-fx-background-color: #1C4EFE");
                btnL2.setStyle("-fx-background-color: #f6bd38");
                btn1.setStyle("-fx-background-color: #1C4EFE");
                btn2.setStyle("-fx-background-color: #1C4EFE");
                btn3.setStyle("-fx-background-color: #1C4EFE");
            }
            case "1" -> {
                btnL1.setStyle("-fx-background-color: #1C4EFE");
                btnL2.setStyle("-fx-background-color: #1C4EFE");
                btn1.setStyle("-fx-background-color: #f6bd38");
                btn2.setStyle("-fx-background-color: #1C4EFE");
                btn3.setStyle("-fx-background-color: #1C4EFE");
            }
            case "2" -> {
                btnL1.setStyle("-fx-background-color: #1C4EFE");
                btnL2.setStyle("-fx-background-color: #1C4EFE");
                btn1.setStyle("-fx-background-color: #1C4EFE");
                btn2.setStyle("-fx-background-color: #f6bd38");
                btn3.setStyle("-fx-background-color: #1C4EFE");
            }
            case "3" -> {
                btnL1.setStyle("-fx-background-color: #1C4EFE");
                btnL2.setStyle("-fx-background-color: #1C4EFE");
                btn1.setStyle("-fx-background-color: #1C4EFE");
                btn2.setStyle("-fx-background-color: #1C4EFE");
                btn3.setStyle("-fx-background-color: #f6bd38");
            }
        }
    }


    public void initButtons() {
        clickFloorBtn("L1");
        clickFloorBtn("L2");
        clickFloorBtn("1");
        clickFloorBtn("2");
        clickFloorBtn("3");
    }

    public void clickFloorBtn(String floor) {
        btnL1.setOnMouseClicked(event->{
            currentFloor = "L1";
            changeButtonColor(currentFloor);
            locationCanvas.getChildren().remove(pathGroup);
            pathGroup.getChildren().clear();
            imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(0));
            changeButtonColor(currentFloor);
            displayMoves(currentFloor);
            locationCanvas.getChildren().add(pathGroup);
        });
        btnL2.setOnMouseClicked(event->{
            currentFloor = "L2";
            changeButtonColor(currentFloor);
            locationCanvas.getChildren().remove(pathGroup);
            pathGroup.getChildren().clear();
            imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(1));
            changeButtonColor(currentFloor);
            displayMoves(currentFloor);
            locationCanvas.getChildren().add(pathGroup);
        });
        btn1.setOnMouseClicked(event->{
            currentFloor = "1";
            changeButtonColor(currentFloor);
            locationCanvas.getChildren().remove(pathGroup);
            pathGroup.getChildren().clear();
            imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(3));
            displayMoves(currentFloor);
            locationCanvas.getChildren().add(pathGroup);
        });
        btn2.setOnMouseClicked(event->{
            currentFloor = "2";
            changeButtonColor(currentFloor);
            locationCanvas.getChildren().remove(pathGroup);
            pathGroup.getChildren().clear();
            imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(4));
            displayMoves(currentFloor);
            locationCanvas.getChildren().add(pathGroup);
        });
        btn3.setOnMouseClicked(event->{
            currentFloor = "3";
            changeButtonColor(currentFloor);
            locationCanvas.getChildren().remove(pathGroup);
            pathGroup.getChildren().clear();
            imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(5));
            displayMoves(currentFloor);
            locationCanvas.getChildren().add(pathGroup);
        });
    }

    @FXML
    public void hoverHelp() {
        helpIcon.setOnMouseClicked(
                event -> {
                    final FXMLLoader popupLoader = new FXMLLoader(Bapp.class.getResource("views/components/popovers/PathfindingHelpPopOver.fxml"));
                    PopOver popOver = new PopOver();
                    popOver.setDetachable(true);
                    popOver.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
                    popOver.setArrowSize(0.0);
                    try {
                        popOver.setContentNode(popupLoader.load());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    popOver.show(helpIcon);
                });
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

    public void getMoveMap(){
        HashMap<Integer,ArrayList<Move>> move_map = new HashMap<>();
        ArrayList<Move> moves = Repository.getRepository().getAllMoves();
        ArrayList<Move> currentMove = new ArrayList<>();
        for (Move move : moves) {
            if (move_map.containsKey(move.getNodeID())) {currentMove = move_map.get(move.getNodeID());}
            else {currentMove = new ArrayList<>();}
            currentMove.add(move);
            move_map.put(move.getNodeID(),currentMove);
        }
        this.move_map = move_map;

    }

    public void handle_move() {
        HashMap<Integer,Move> nodes_to_update = new HashMap<>();
        upcoming_moves.clear();
        LocalDate current_date = LocalDate.now(); // possibly not getting set to the right value
        System.out.println(current_date);
        LocalDate tempDate;
        for (Integer id : move_map.keySet()){
            if (move_map.get(id).size() >= 1) {
                tempDate = move_map.get(id).get(0).getDate().toLocalDate().minusYears(1);
                for (Move move : move_map.get(id)) {
                    LocalDate move_date =  move.getDate().toLocalDate();

                    if ((move_date.isAfter(tempDate)) && move_date.isBefore(current_date)) {
                        tempDate = move_date;
                        nodes_to_update.put(move.getNodeID(),move);
                    }

                    else if (move_date.equals(current_date)) {
                        tempDate = move_date;
                        nodes_to_update.put(move.getNodeID(),move);
                    }
                    if (move_date.isAfter(current_date)) {upcoming_moves.add(move);}
                }
            }
        }
//        System.out.println(nodes_to_update);
        update_nodes_from_moves(nodes_to_update);
//      System.out.println("nodes to update");
//      System.out.println(nodes_to_update);


    }

    public void update_nodes_from_moves(HashMap<Integer,Move> nodes_to_update){
        fullNodes = new ArrayList<>();
        for (Integer id : fullNodesByID.keySet()){
            if (nodes_to_update.containsKey(id)){
                FullNode newNode = fullNodesByID.get(id);
                newNode.setLongName(nodes_to_update.get(id).getLongName());
//                newNode.setShortName(PathFinding.ASTAR.getFullNodes().get(id).getShortName());
                fullNodes.add(newNode);
            }
            else {
                fullNodes.add(fullNodesByID.get(id));
            }
        }
        getFilteredLongnames();
    }

    public ArrayList<String> getFilteredLongnames(){
        ArrayList<String> filtered_names = new ArrayList<>();
        for (FullNode node : fullNodes){
            fullNodesByID.put(node.getNodeID(),node);
            fullNodesByLongname.put(node.getLongName(),node);

            if (!node.getNodeType().equals("HALL")) {filtered_names.add(node.getLongName());}
//          if (node.getNodeType().equals("STAI") || node.getNodeType().equals("ELEV")) {
//              filtered_names.remove(node.getLongName());
//
//          }
//          else {
//              filteredFullNodes.put(node.getLongName(),node);
//          }
        }
        Collections.sort(filtered_names);
//        this.filtered_names = filtered_names;
        return filtered_names;
    }

    public void displayMoves(String currentFloor){
        moveInfo.getChildren().clear();
        for (Move move : upcoming_moves) {
            if (fullNodesByID.get(move.getNodeID()).getNodeID() != fullNodesByLongname.get(move.getLongName()).getNodeID()) {
                int originalX = fullNodesByLongname.get(move.getLongName()).getxCoord();
                int originalY = fullNodesByLongname.get(move.getLongName()).getyCoord();
                System.out.println("old "+ fullNodesByLongname.get(move.getLongName()).getNodeID());
                int newX = fullNodesByID.get(move.getNodeID()).getxCoord();
                int newY = fullNodesByID.get(move.getNodeID()).getyCoord();
                System.out.println("new "+ fullNodesByID.get(move.getNodeID()).getNodeID());
                Line line = new Line(originalX, originalY, newX, newY);
                line.setStrokeWidth(4);
                animateLine(line);
                Circle c = new Circle(originalX, originalY, 5, RED);
                Circle finalC = c;
//                if (moveInfo.contains(new Point2D(originalX,originalY))) {originalY -= 15;}
                c.setOnMouseEntered(event -> {
                                if (nameToolTip.getText().isBlank() || nameToolTip.getText().contains(nameToolTip.getText())) {nameToolTip.setText(display_move_info(move));}
                                else {nameToolTip.setText(display_move_info(move) + "\n" + nameToolTip.getText());}
                                nameToolTip.setShowDelay(Duration.millis(1));
                                nameToolTip.hideDelayProperty().set(Duration.seconds(.5));
                                Tooltip.install(finalC, nameToolTip);
                        });
                pathGroup.getChildren().add(c);
                Boolean exists = false;
                for (Node child : pathGroup.getChildren()) {
                    if (child.contains(newX,newY)) {
                        exists = true;
                    }
                }
                if (!exists) {
                    c = new Circle(newX, newY, 5, PURPLE);
                    pathGroup.getChildren().add(c);
                }
                pathGroup.getChildren().add(line);
            }
//            else if (!fullNodesByID.get(move.getNodeID()).getFloor().equals(fullNodesByLongname.get(move.getLongName()).getFloor())){
//                System.out.println("Floor changed from " + fullNodesByID.get(move.getNodeID()).getFloor() + " to " + fullNodesByLongname.get(move.getLongName()).getFloor());
//            }

        }
        pathGroup.toFront();
        moveInfo.toFront();
    }

    private void animateLine(Line line)
    {
        // https://stackoverflow.com/questions/36727777/how-to-animate-dashed-line-javafx
        line.getStrokeDashArray().setAll(25d, 15d, 10d, 20d);
        final double maxOffset =
                line.getStrokeDashArray().stream()
                        .reduce(
                                0d,
                                (a, b) -> a - b
                        );

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(
                                line.strokeDashOffsetProperty(),
                                0,
                                Interpolator.LINEAR
                        )
                ),
                new KeyFrame(
                        Duration.seconds(2),
                        new KeyValue(
                                line.strokeDashOffsetProperty(),
                                maxOffset,
                                Interpolator.LINEAR
                        )
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public String display_move_info(Move move){
        String text = move.getLongName() + " will be moving to node " + move.getNodeID() + " on " + move.getDate();

//        moveInfo.getChildren().add(text);
        return text;
    }

    public void moveAlert(ArrayList<String> input) {
        // Create an alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Upcoming Moves");
        alert.setHeaderText(null);
        String alert_message = "";
        for (String string : input) {
            alert_message += string + "\n";
        }
        alert.setContentText(alert_message);
        alert.showAndWait();
        Navigation.navigate(Screen.CREATE_NEW_REQUEST);
    }


    public void display_all_moves() {

    }

}

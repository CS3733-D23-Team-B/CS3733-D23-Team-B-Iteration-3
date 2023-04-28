package edu.wpi.teamb.controllers.mapeditor;

import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.Full.FullNode;
import edu.wpi.teamb.DBAccess.ORMs.Node;
import edu.wpi.teamb.pathfinding.PathFinding;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AddNodeMenuController {
    @FXML
    MFXTextField tfNodeId;
    @FXML
    MFXTextField tfLongName;
    @FXML
    MFXTextField tfShortName;
    @FXML
    MFXButton btnSubmitNodeDetails;
    @FXML
    MFXComboBox<String> cbNodeType;
    @FXML
    MFXTextField tfXCoord;
    @FXML
    MFXTextField tfYCoord;

    static Node currentNode = null;
    static String currentFloor = null;

    MapEditorController mapEditorController = new MapEditorController();

    public AddNodeMenuController() throws SQLException {
    }

    public static void setCurrentFloor(String currentFloor) {
        AddNodeMenuController.currentFloor = currentFloor;
    }

    @FXML
    public void initialize() throws IOException {
        initializeFields();
        initButtons();
        tfNodeId.setEditable(false);
    }

    public void initializeFields() {
        // Init combo box
        // initialize cbNodeType options
        ObservableList<String> listOfNodeTypes = FXCollections.observableArrayList(Repository.getRepository().getNodeTypesUniqueAlphabetical());
        cbNodeType.setItems(listOfNodeTypes);
        // Initialize the node data
        //FullNode newFullNode = Repository.getRepository().getFullNode(currentNode.getNodeID());
        // Initialize the user data
        tfNodeId.setText(String.valueOf(currentNode.getNodeID()));
        tfXCoord.setText(String.valueOf(currentNode.getxCoord()));
        tfYCoord.setText(String.valueOf(currentNode.getyCoord()));
    }

    public void initButtons() {
        btnSubmitNodeDetails.setOnMouseClicked(event -> handleSubmitNodeDetails());
    }

    private void handleSubmitNodeDetails() {
        submitNode();
        mapEditorController.refreshMap();
    }

    private void submitNode() {
        String shortName = tfShortName.getText();
        String longName = tfLongName.getText();
        String nodeType = cbNodeType.getValue();
        FullNode fullNode = null;

        fullNode = new FullNode(Integer.parseInt(tfNodeId.getText()), Integer.parseInt(tfXCoord.getText()), Integer.parseInt(tfYCoord.getText()), currentFloor, "Full Node Building", tfLongName.getText(), tfShortName.getText(), cbNodeType.getSelectedItem());
        Repository.getRepository().addFullNode(fullNode);

        Node newNode = new Node(fullNode.getNodeID(), fullNode.getxCoord(), fullNode.getyCoord(), fullNode.getFloor(), fullNode.getBuilding()); // Create a new node (DEFAULT IS HALL)

        MapEditorController.nodeList.add(newNode); // Add the node to the nodeList
        MapEditorController.fullNodesList.add(fullNode);

        PathFinding.ASTAR.get_node_map().put(newNode.getNodeID(), newNode); // Add the node to the nodeMap

        // Add node to the database
        //Repository.getRepository().addNode(newNode);

        System.out.println("Adding a new node with nodeID: " + newNode.getNodeID());
        // Refresh the map
        mapEditorController.refreshMap();
//        try {
//            mapEditorController.draw(currentFloor);
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }


        // Close the window
        Stage stage = (Stage) btnSubmitNodeDetails.getScene().getWindow();
        stage.close();
        mapEditorController.refreshMap();
    }



    public static void setCurrentNode(Node currentNode) {
        AddNodeMenuController.currentNode = currentNode;
    }
}

package edu.wpi.teamb.controllers.mapeditor;

import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.Full.FullNode;
import edu.wpi.teamb.DBAccess.ORMs.LocationName;
import edu.wpi.teamb.DBAccess.ORMs.Move;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EditNodeMenuController {
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

    MapEditorController mapEditorController = new MapEditorController();
    String oldLongName = "";
    String oldShortName ="";
    String oldNodeType = "";

    public EditNodeMenuController() throws SQLException {
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
        FullNode newFullNode = Repository.getRepository().getFullNode(currentNode.getNodeID());

        // Initialize the user data
        tfNodeId.setText(String.valueOf(newFullNode.getNodeID()));
        tfLongName.setText(newFullNode.getLongName());
        tfShortName.setText(newFullNode.getShortName());
        cbNodeType.selectItem(newFullNode.getNodeType());
        tfXCoord.setText(String.valueOf(currentNode.getxCoord()));
        tfYCoord.setText(String.valueOf(currentNode.getyCoord()));
        oldLongName = newFullNode.getLongName();
        oldShortName = newFullNode.getShortName();
        oldNodeType = newFullNode.getNodeType();
    }

    public void initButtons() {
        btnSubmitNodeDetails.setOnMouseClicked(event -> handleSubmitNodeDetails());
    }

    private void handleSubmitNodeDetails() {
        submitNode();
        //PathFinding.ASTAR.force_init();
    }

    private void submitNode() {
        FullNode fullNode = null;

        // Delete old moves from the database
        ArrayList<Move> moves = new ArrayList<>(Repository.getRepository().getAllMoves());
        for (Move mv : moves) {
            if (mv.getNodeID() == currentNode.getNodeID()) {
                Repository.getRepository().deleteMove(mv);
            }
        }

        // Delete old location name from the database
        LocationName ln = new LocationName(oldLongName, oldShortName, oldNodeType);
        Repository.getRepository().deleteLocationName(ln);

        // Create new full node based on the user input
        fullNode = new FullNode(Integer.parseInt(tfNodeId.getText()), (int) Integer.parseInt(tfXCoord.getText()), (int) Integer.parseInt(tfYCoord.getText()), mapEditorController.currentFloor, currentNode.getBuilding(), tfLongName.getText(), tfShortName.getText(), cbNodeType.getSelectedItem());


        Repository.getRepository().deleteNode(currentNode); // Remove old node from the database
        Repository.getRepository().addFullNode(fullNode);  // Add new node to the database

        Node newNode = new Node(fullNode.getNodeID(), fullNode.getxCoord(), fullNode.getyCoord(), fullNode.getFloor(), fullNode.getBuilding()); // Create a new node (DEFAULT IS HALL)

        // Remove full node from the MapEditor's list and add the new one
        for (FullNode fn : MapEditorController.fullNodesList) {
            if (fn.getNodeID() == newNode.getNodeID()) {
                MapEditorController.fullNodesList.remove(fn);
                MapEditorController.fullNodesList.add(fullNode);
                break;
            }
        }

        // Remove Node from the MapEditor's list and add the new one
        for (Node n : MapEditorController.nodeList) {
            if (n.getNodeID() == newNode.getNodeID()) {
                MapEditorController.nodeList.remove(n);
                MapEditorController.nodeList.add(newNode);
                break;
            }
        }


        System.out.println("Adding a new node with nodeID: " + newNode.getNodeID());
        //refreshMap();

        // Close the window
        Stage stage = (Stage) btnSubmitNodeDetails.getScene().getWindow();
        stage.close();
    }



    public static void setCurrentNode(Node currentNode) {
        EditNodeMenuController.currentNode = currentNode;
    }
}

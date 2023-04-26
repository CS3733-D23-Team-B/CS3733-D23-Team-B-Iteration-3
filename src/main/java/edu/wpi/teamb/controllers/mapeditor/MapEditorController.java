package edu.wpi.teamb.controllers.mapeditor;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import edu.wpi.teamb.Bapp;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.DBoutput;
import edu.wpi.teamb.DBAccess.Full.FullNode;
import edu.wpi.teamb.DBAccess.ORMs.Edge;
import edu.wpi.teamb.DBAccess.ORMs.LocationName;
import edu.wpi.teamb.DBAccess.ORMs.Node;
import edu.wpi.teamb.entities.EMapEditor;
import edu.wpi.teamb.pathfinding.PathFinding;
import io.github.palexdev.materialfx.controls.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.kurobako.gesturefx.GesturePane;
import org.controlsfx.control.PopOver;

import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.*;

import static javafx.scene.paint.Color.RED;

public class MapEditorController {
  public static Node currentNode;
  @FXML
  private JFXHamburger menuBurger;
  @FXML
  private JFXDrawer menuDrawer;
  @FXML
  private ImageView helpIcon;
  @FXML
  private MFXComboBox<String> NodeSelector;
  @FXML
  private MFXListView NodeInfo;
  @FXML
  private VBox VboxNodes;
  @FXML
  private ImageView imageViewPathfinder;
  @FXML
  private StackPane stackPaneMapView;

  private EMapEditor editor;
  @FXML
  private MFXButton btnL1;
  @FXML
  private MFXButton btnL2;
  @FXML
  private MFXButton btn1;
  @FXML
  private MFXButton btn2;
  @FXML
  private MFXButton btn3;

  @FXML
  private MFXButton btnNode;
  @FXML
  private MFXButton btnEdge;
  @FXML
  private MFXButton btnAlign;
  @FXML
  private MFXTextField tfState;

  @FXML
  private MFXButton uploadBtn;
  @FXML
  private MFXButton exportBtn;
  @FXML
  private MFXToggleButton toggleNodes;
  @FXML
  private MFXToggleButton toggleLocationNames;
  @FXML
  private MFXToggleButton toggleEdges;
  @FXML
  private FileChooser fileChooser;
  @FXML
  private MFXButton resetFromBackupBtn;

  //Objects that get superimposed

  public GesturePane pane = new GesturePane();
  Group nodeGroup = new Group();
  Group edgeGroup = new Group();
  Group nameGroup = new Group();
  Pane locationCanvas;
  Pane fullNodeCanvas;
  public static ArrayList<Node> nodeList = new ArrayList<>();
  public static ArrayList<FullNode> fullNodesList = new ArrayList<>();
  private ArrayList<Node> floorList = new ArrayList<>();

  //Other misc items
  public String currentFloor = "1";
  private ArrayList<LocationName> locationNameList = new ArrayList<>();
  @FXML
  private VBox vboxBtns;
  @FXML
  private VBox vboxAddNode;
  private ArrayList<Node> floorNodes = new ArrayList<>();
  public boolean boolEditingNode = false;
  public boolean boolSubmittedDetails = false;
  private Pane menuPane;
  private boolean editingNode = false; // Used for the submitting details button

  @FXML
  private MFXButton btnAdd;
  @FXML
  private MFXButton btnEdit;
  @FXML
  private MFXButton btnDelete;
  @FXML
  private MFXButton btnView;

  // Create the states
  MapEditorContext mapEditorContext = new MapEditorContext();
  MapEditorState editState = new EditState();
  MapEditorState deleteState = new DeleteState();
  MapEditorState addState = new AddState();
  MapEditorState viewState = new ViewState();
  private boolean handlingNodes = false;
  private boolean handlingEdges = false;
  private Set<Circle> selectedNodes = new HashSet<>();

  private Set<Circle> nodesToAlign = new HashSet<>();
  boolean canAlignNodes = false;

  private double m = 0;
  private double c = 0;
  int fullNodeX;
  int fullNodeY;
  Circle c1;
  Circle c2;

  public MapEditorController() throws SQLException {
    this.editor = new EMapEditor();
  }

  @FXML
  public void initialize() throws IOException, SQLException {
    initNavBar();
    hoverHelp();
    initializeFields();
    initButtons();
    initStateBtn();
    PathFinding.ASTAR.init_pathfinder();
    // Initialize the edges, nodes, and names on the map
    nodeList = Repository.getRepository().getAllNodes();
    fullNodesList = Repository.getRepository().getAllFullNodes();
//    fullNodes = Repository.getRepository().getFullNodes();

    this.stackPaneMapView = new StackPane(); // no longer @FXML
    // Used for nodes
    this.locationCanvas = new Pane();

//    this.nodeGroup = new Group();
//    this.nameGroup = new Group();
//    this.edgeGroup = new Group();

    // Used for location names
    this.fullNodeCanvas = new Pane();

    this.pane.setContent(stackPaneMapView);
    this.imageViewPathfinder = new ImageView(Bapp.getHospitalListOfFloors().get(3)); // no longer @FXML

    //Establishing everything that must occur in the stackpane
    this.stackPaneMapView.getChildren().add(this.imageViewPathfinder);
    this.stackPaneMapView.getChildren().add(this.locationCanvas);

    this.locationCanvas.getChildren().add(nodeGroup);
    this.locationCanvas.getChildren().add(edgeGroup);
    this.locationCanvas.getChildren().add(nameGroup);

    //Fitting the scrollpane
    pane.setScrollMode(GesturePane.ScrollMode.ZOOM);
    pane.setScrollBarPolicy(GesturePane.ScrollBarPolicy.NEVER);


    draw(currentFloor);

    changeButtonColor(currentFloor);
    Platform.runLater(() -> this.pane.centreOn(new Point2D(2190, 910)));

    System.out.println("MapEditorController initialized");
  }

  private void handleNodes() {
    System.out.println("Handling nodes");
    btnNode.setStyle("-fx-background-color: #f6bd38");
    btnEdge.setStyle("-fx-background-color: #1C4EFE");
    btnAlign.setStyle("-fx-background-color: #1C4EFE");
    handlingNodes = true;
    handlingEdges = false;
    determineState();
  }

  private void handleEdges() {
    System.out.println("Handling edges");
    btnEdge.setStyle("-fx-background-color: #f6bd38");
    btnNode.setStyle("-fx-background-color: #1C4EFE");
    btnAlign.setStyle("-fx-background-color: #1C4EFE");
    handlingEdges = true;
    handlingNodes = false;
    determineState();
  }

  private void alignNodes() {
    for (Circle c : nodesToAlign) {
      Node n = Repository.getRepository().getNode(Integer.parseInt(c.getId()));
      n.setyCoord(startAndEndPoints(n.getxCoord()));
      Repository.getRepository().updateNode(n);
    }
    nodesToAlign.clear();
    System.out.println("Aligning all selected nodes");
  }

  private void determineState() {
    if (mapEditorContext.getState() == addState && handlingEdges) {
      System.out.println("Adding edge");
      tfState.setText("Adding Edge");
      handleAddNode();  // Disable the ability to click and add nodes
    } else if (mapEditorContext.getState() == addState && handlingNodes) {
      System.out.println("Adding Node");
      tfState.setText("Adding Node");
      handleAddNode();
    } else if (mapEditorContext.getState() == deleteState && handlingEdges) {
      System.out.println("Deleting edge");
      tfState.setText("Delete Edge");
    } else if (mapEditorContext.getState() == deleteState && handlingNodes) {
      System.out.println("Deleting node");
      tfState.setText("Deleting Node");
    } else if (mapEditorContext.getState() == editState && handlingEdges) {
      System.out.println("Editing edge");
      tfState.setText("Editing Edge");
    } else if (mapEditorContext.getState() == editState && handlingNodes) {
      System.out.println("Editing node");
      tfState.setText("Editing Node");
    } else if (mapEditorContext.getState() == editState && !handlingNodes && !handlingEdges) {
      System.out.println("Selecting nodes");
      tfState.setText("Selecting Nodes");
    } else if (mapEditorContext.getState() == viewState) {
      System.out.println("Viewing State");
      tfState.setText("Viewing State");
    }
  }


  /**
   * Draws all the nodes on the map
   *
   * @param floor
   * @throws SQLException
   */
  public void draw(String floor) throws SQLException {
    System.out.println("Clearing the children");
    this.nodeGroup.getChildren().clear();
    this.nameGroup.getChildren().clear();
    this.edgeGroup.getChildren().clear();
    // For each node, create a circle
    for (Node n : nodeList) {
      if (n.getFloor().equals(floor)) {
        drawNode(n);
      }
    }
    System.out.println("Drawing the edges, names, and nodes for floor " + floor);
    nodeGroup.toFront();
  }

  //Draws edges for a single node, taking in a node
  //From node it grabs the neighbors from the repository and then creates the 1-3 lines required
  public void drawEdge(Node n) {
//      edgeGroup.getChildren().clear();

    //Gets the full node of the current node, as well as the neighbors of this node
    ArrayList<Integer> neighbors = PathFinding.ASTAR.get_node_map().get(n.getNodeID()).getNeighborIds(); // TODO this breaks reset from backup
    if (neighbors != null) {
      for (int i = 0; i < neighbors.size(); i++) {

        //Get the full node of the neighbor, check to see if they're both elevators or stairs
        Node neighborNode = PathFinding.ASTAR.get_node_map().get(neighbors.get(i));

        //Draws the lines of the neighbors on the current floor
        //This gets redone every floor which is slightly unoptimized but this should never span enough
        //To make this a large issue
        if (neighborNode.getFloor().equals(n.getFloor())) {
          Line line = new Line(n.getxCoord(), n.getyCoord(), neighborNode.getxCoord(), neighborNode.getyCoord());
          line.setStrokeWidth(4);
          line.setId(neighborNode.getNodeID() + "_" + n.getNodeID());
          line.setOnMouseClicked(event -> handleEdgeClick(event, line));
          edgeGroup.getChildren().add(line);

        }
      }
    }
    edgeGroup.toFront();
    nodeGroup.toFront();
  }

  //Toggles the ability to see location names
  //Takes in current ability to see them in relation to the bar
  //When on it adds the ability to generate them in the node drawing method
  //When off it replaces text with "bruh" and removed ability to see them by adding delay.
  public void handleToggleLocationNames() {
    if (toggleLocationNames.isSelected()) {
      nameGroup.setVisible(true);
      System.out.println("Location names on");
    } else {
      nameGroup.setVisible(false);
      System.out.println("Location names off");
      toggleLocationNames.setSelected(false);
    }
  }

  public void handleToggleNodes() {
    if (toggleNodes.isSelected()) {
      nodeGroup.setVisible(true);
      System.out.println("Nodes on");
    } else {
      nodeGroup.setVisible(false);
      System.out.println("Nodes off");
      toggleNodes.setSelected(false);
    }
  }

  public void handleToggleEdges() {
    if (toggleEdges.isSelected()) {
      edgeGroup.setVisible(true);
      System.out.println("Edges on");
    } else {
      edgeGroup.setVisible(false);
      System.out.println("Edges off");
      toggleEdges.setSelected(false);
    }
  }

  /**
   * Draws a node on the map
   *
   * @param n The node to be drawn
   */
  public void drawNode(Node n) {
    // Create the circle
    Circle c = new Circle(n.getxCoord(), n.getyCoord(), 5, RED);
    c.setId(String.valueOf(n.getNodeID())); // Set the circle's ID to the node's ID
    c.setOnMouseClicked(event -> {
      try {
        selectedNodes.add(c); // Used for creating edges
        nodesToAlign.add(c); // Used for aligning nodes
        checkSelectedNodes(); // Check if two circles are selected to create an edge
        checkNodesToAlign(); // Check if at least two circles are selected to align
        this.handleNodeClick(event, n);
        System.out.println("Node " + n.getNodeID() + " clicked");
      } catch (SQLException | IOException e) {
        throw new RuntimeException(e);
      }
    }); // Set the handler for clicking on a node
    drawEdge(n);
    drawName(c, n);

    // Add the circle to the nodeGroup
    floorList.add(n);
    nodeGroup.getChildren().add(c);
    nodeGroup.toFront();
  }

  private void checkNodesToAlign() {
    List<Integer> xCoords = new ArrayList<>();
    List<Integer> yCoords = new ArrayList<>();

    // Populate the list
    if (nodesToAlign.size() >= 2 && canAlignNodes) {
      for (Circle c : nodesToAlign) {
        xCoords.add((int) c.getCenterX());
        yCoords.add((int) c.getCenterY());
        System.out.println(c);
      }
      System.out.println(nodesToAlign.size());
    }

    // Assign new Y coordinates
    if (mapEditorContext.getState() == editState && !handlingNodes && !handlingEdges && canAlignNodes) {
      calcBestFit(xCoords, yCoords);  // Calculate the best fit
      alignNodes(); // Align the nodes
    }
  }

  private int startAndEndPoints(int x) {
    return (int) (m*x+c);
  }

  private void calcBestFit(List<Integer> x, List<Integer> y) {
    int n = x.size();
    double sum_x = 0, sum_y = 0,
            sum_xy = 0, sum_x2 = 0;
    for (int i = 0; i < n; i++) {
      sum_x += x.get(i);
      sum_y += y.get(i);
      sum_xy += x.get(i) * y.get(i);
      sum_x2 += Math.pow(x.get(i), 2);
    }

    m = (n * sum_xy - sum_x * sum_y) / (n * sum_x2 - Math.pow(sum_x, 2));
    c = (sum_y - m * sum_x) / n;
  }


  /**
   * Method to check if two nodes are selected and if so, add an edge between them (in the add edge state)
   */
  private void checkSelectedNodes() {
    if (selectedNodes.size() == 2)
    {
      Iterator<Circle> iterator = selectedNodes.iterator();
      c1 = iterator.next();
      c2 = iterator.next();
      System.out.println(c1.toString());
      System.out.println(c2.toString());
      selectedNodes.clear();
      if (c1 != null && c2 != null && handlingEdges && mapEditorContext.getState() == addState)
      {
        handleAddEdge(c1, c2);
      }
    }
  }

  void drawName(Circle c, Node n) {
    for (FullNode fn : fullNodesList) {
      if (!Objects.equals(fn.getNodeType(), "HALL")) {
        if (fn.getNodeID() == n.getNodeID()) {
          Text name = new Text(fn.getShortName());
          name.setX(c.getCenterX() + 5);
          name.setY(c.getCenterY() + 5);
          nameGroup.getChildren().add(name);
        }
      }
    }
  }


  /**
   * Handles the reset from backup button click
   */
  void handleResetFromBackupBtn() {
    Repository.getRepository().resetNodesFromBackup();
    nodeList = Repository.getRepository().getAllNodes();
    // Refresh the map
    refreshMap();
  }

  /**
   * Adds a node to the map
   *
   * @param x coordinate
   * @param y coordinate
   * @throws SQLException
   */
  public void addNodeToMap(double x, double y) throws SQLException {
    getMaxID();

    //tfNodeId.setText(String.valueOf(maxID + 5)); // Set the nodeID text field to the maxID + 5
  }

  public int getMaxID() {  // Get the max ID of the list of nodes
    int maxID = 0;
    for (Node n : nodeList) {
      if (n.getNodeID() > maxID) {
        maxID = n.getNodeID();
      }
    }
    return maxID;
  }

  /**
   * Refreshes the map
   */
  void refreshMap() {
    // Clear the map
    nodeGroup.getChildren().clear();
    edgeGroup.getChildren().clear();
    // Redraw the map
    try {
      if (mapEditorContext.getState() != editState) {
        PathFinding.ASTAR.force_init();
      }
      draw(currentFloor);
      System.out.println("Refreshing map for floor " + currentFloor + "...");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void handleEdgeClick(MouseEvent e, Line line) {
    if (mapEditorContext.getState() == deleteState) {
      handleDeleteEdge(e, line);
    }
    PathFinding.ASTAR.force_init();
  }

  /**
   * Handles the node click event (creates a context menu)
   *
   * @param e
   * @param n
   */
  private void handleNodeClick(MouseEvent e, Node n) throws SQLException, IOException {
    if (mapEditorContext.getState() == deleteState) {
      handleDeleteNode(e, n);
    } else if (mapEditorContext.getState() == editState && handlingNodes) {
      handleEditNode(n);
    } else if (mapEditorContext.getState() == editState && handlingEdges) {
      handleAddEdge(c1, c2);
    }
    else if (mapEditorContext.getState() == editState && canAlignNodes) {
      alignNodes();
    }
  }


  private void handleAddNode() {
    if (mapEditorContext.getState() == addState && handlingNodes) {
      stackPaneMapView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::tapToAddNode);
      System.out.println("Added tapToAddNode event handler");
    } else {
      stackPaneMapView.setDisable(false);
      System.out.println("Removed tapToAddNode event handler");
    }
  }

  private void tapToAddNode(MouseEvent e) {
    // Method to allow for double click to add a new node
    try {
      Node n = new Node();
      n.setxCoord((int) e.getX());
      n.setyCoord((int) e.getY());
      setFullNodeX((int) e.getX());
      setFullNodeY((int) e.getY());
      n.setFloor(currentFloor);
      AddNodeMenuController.setCurrentFloor(currentFloor);
      n.setNodeID(getMaxID() + 5);
      showAddNodeMenu(n);
      editingNode = false;

      addNodeToMap(e.getX(), e.getY());   // get the X and Y of the cursor
      System.out.println("Added a node at " + e.getX() + ", " + e.getY());
      refreshMap();
    } catch (SQLException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private void setFullNodeX(int x) {
    fullNodeX = x;
  }

  private void setFullNodeY(int y) {
    fullNodeY = y;
  }


  private void showAddNodeMenu(Node n) throws IOException {
    Parent root;
    AddNodeMenuController.setCurrentNode(n);
    root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("edu/wpi/teamb/views/mapeditor/AddNodeMenu.fxml")));
    Stage stage = new Stage();
    stage.setTitle("Add Node");
    stage.setScene(new Scene(root, 400, 600));
    stage.show();
  }

  private void showEditNodeMenu(Node n) throws IOException {
    Parent root;
    EditNodeMenuController.setCurrentNode(n);
    root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("edu/wpi/teamb/views/mapeditor/EditNodeMenu.fxml")));
    Stage stage = new Stage();
    stage.setTitle("Edit Node");
    stage.setScene(new Scene(root, 400, 600));
    stage.show();
  }

  /**
   * Handles the delete node event
   *
   * @param e
   * @throws SQLException
   */
  private void handleDeleteNode(MouseEvent e, Node n) throws SQLException {
    // Get the node ID from the circle's ID
    int nodeID = n.getNodeID();
    // delete from move table
    Repository.getRepository().deleteMove(Repository.getRepository().getMove(nodeID));
    // Delete the node from the database
    Repository.getRepository().deleteNode(Repository.getRepository().getNode(nodeID));
    PathFinding.ASTAR.get_node_map().remove(n.getNodeID());

    // Remove the node from the map
    nodeGroup.getChildren().remove(n);

    // remove node from list
    for (Node node : nodeList) {
      if (node.getNodeID() == nodeID) {
        nodeList.remove(node);
        break;
      }
    }

    refreshMap();
    System.out.println("Node: " + nodeID + " deleted");
  }

  private void handleDeleteEdge(MouseEvent e, Line l) {
    // Delete the edge from the database
    Repository.getRepository().deleteEdge(Repository.getRepository().getEdge(l.getId()));

    // Remove the edge from the map
    edgeGroup.getChildren().remove(l);

    System.out.println("Edge: " + l.getId() + " deleted");
    refreshMap();
  }

  private void handleEditEdge() {
    if (mapEditorContext.getState() == editState && handlingEdges) {
      //stackPaneMapView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::tapToEditEdge);
      System.out.println("Added tapToEditEdge event handler");
    } else {
      //stackPaneMapView.removeEventHandler(MouseEvent.MOUSE_CLICKED, this::tapToEditEdge);
      System.out.println("Removed tapToEditEdge event handler");
    }
  }


  /**
   * Handles the add edge event
   * @param c1
   * @param c2
   */
  private void handleAddEdge(Circle c1, Circle c2) {

    // Generate the line between the two nodes
    Line line = new Line(c1.getCenterX(), c1.getCenterY(), c2.getCenterX(), c2.getCenterY());
    line.setStrokeWidth(4);
    line.setId(c1.getId() + "_" + c2.getId());
    edgeGroup.getChildren().add(line);
    System.out.println("Added Edge with ID = " + line.getId());

    // Add edge to the database
    Edge edge = new Edge();
    edge.setStartNode(Repository.getRepository().getNode(Integer.parseInt(c1.getId())));
    edge.setEndNode(Repository.getRepository().getNode(Integer.parseInt(c2.getId())));
    Repository.getRepository().addEdge(edge);
  }

  /**
   * Handles the edit node event
   * @param n
   * @throws SQLException
   */
  private void handleEditNode(Node n) throws SQLException, IOException {
    editingNode = true;
    // Get the node ID from the circle's ID
    int nodeID = n.getNodeID();

    // Allow click and drag of the Circle
    for (javafx.scene.Node c: nodeGroup.getChildren()) {
      if (c.getId().equals(String.valueOf(nodeID))) {
        //makeDraggable((Circle) nodeGroup.getChildren().get(i));
        makeDraggable((Circle) c);
        boolEditingNode = true;
        System.out.println("Node: " + nodeID + " is draggable");
      }
      if (boolEditingNode) {
        stackPaneMapView.setOnMouseClicked(event -> {
          if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            try {
              // Update the node's location
              System.out.println("Node: " + nodeID + " edited");
              //newNode.setxCoord((int) (event.getX()));
              //newNode.setyCoord((int) (event.getY()));
              //System.out.println("Location: " + newNode.getxCoord() + ", " + newNode.getyCoord());
              //showEditNodeMenu(newNode);
              n.setxCoord((int) (event.getX()));
              n.setyCoord((int) (event.getY()));
              System.out.println("Location: " + n.getxCoord() + ", " + n.getyCoord());
              showEditNodeMenu(n);
            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
            pane.gestureEnabledProperty().set(true);
            boolEditingNode = false;
          }
        });
      }
    }
    //refreshMap();
  }


  /**
   * https://stackoverflow.com/questions/17312734/how-to-make-a-draggable-node-in-javafx-2-0
   */
  private class Delta {
    public double x;
    public double y;
  }

  /**
   *  Allow the user to drag the node
   *  //https://stackoverflow.com/questions/17312734/how-to-make-a-draggable-node-in-javafx-2-0
   * @param node
   */
  private void makeDraggable(Circle node) {
    boolEditingNode = true;
    pane.setGestureEnabled(false);    // Disable gestures while dragging
    final Delta dragDelta = new Delta();
    node.setOnMouseEntered(me -> {
      if (!me.isPrimaryButtonDown()) {
        node.getScene().setCursor(Cursor.HAND);
      }
    });
    node.setOnMouseExited(me -> {
      if (!me.isPrimaryButtonDown()) {
        node.getScene().setCursor(Cursor.DEFAULT);
      }
    });
    node.setOnMousePressed(me -> {
      if (me.isPrimaryButtonDown()) {
        node.getScene().setCursor(Cursor.DEFAULT);
      }
      dragDelta.x = me.getX();
      dragDelta.y = me.getY();
      node.getScene().setCursor(Cursor.MOVE);
    });
    node.setOnMouseReleased(me -> {
      if (!me.isPrimaryButtonDown()) {
        node.getScene().setCursor(Cursor.DEFAULT);
      }
    });
    node.setOnMouseDragged(me -> {
      node.setLayoutX(node.getLayoutX() + me.getX() - dragDelta.x);
      node.setLayoutY(node.getLayoutY() + me.getY() - dragDelta.y);
    });
  }


  public void initButtons() {
    clickFloorBtn();
    uploadBtn.setOnMouseClicked(event->{handleUploadBtn();});
    exportBtn.setOnMouseClicked(event->{handleExportBtn();});
    resetFromBackupBtn.setOnMouseClicked(event->{handleResetFromBackupBtn();});

    // initialize the toggles
    toggleEdges.setSelected(true);
    toggleNodes.setSelected(true);
    toggleLocationNames.setSelected(true);
    toggleLocationNames.setOnMouseClicked(event->{handleToggleLocationNames();});
    toggleEdges.setOnMouseClicked(event->{handleToggleEdges();});
    toggleNodes.setOnMouseClicked(event->{handleToggleNodes();});

    btnNode.setOnMouseClicked(event -> handleNodes());
    btnEdge.setOnMouseClicked(event -> handleEdges());
    btnAlign.setOnMouseClicked(event -> canAlignNodes = true);
  }

  public void clickFloorBtn() {
    btnL1.setOnMouseClicked(event->{
      imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(0));
      currentFloor = "L1";
      changeButtonColor(currentFloor);
      floorList = Repository.getRepository().getNodesByFloor("L1");
      try {
        edgeGroup.getChildren().clear();
        nodeGroup.getChildren().clear();
        draw( "L1");
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
    btnL2.setOnMouseClicked(event->{
      imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(1));
      currentFloor = "L2";
      changeButtonColor(currentFloor);
      floorList = Repository.getRepository().getNodesByFloor("L2");

      try {
        edgeGroup.getChildren().clear();
        nodeGroup.getChildren().clear();
        draw( "L2");
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
    btn1.setOnMouseClicked(event->{
      currentFloor = "1";
      imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(3));
      changeButtonColor(currentFloor);
      floorList = Repository.getRepository().getNodesByFloor("1");
      try {
        edgeGroup.getChildren().clear();
        nodeGroup.getChildren().clear();
        draw( "1");
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
    btn2.setOnMouseClicked(event->{
      currentFloor = "2";
      imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(4));
      changeButtonColor(currentFloor);
      floorList = Repository.getRepository().getNodesByFloor("2");
      try {
        edgeGroup.getChildren().clear();
        nodeGroup.getChildren().clear();
        draw( "2");
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
    btn3.setOnMouseClicked(event->{
      currentFloor = "3";
      imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(5));
      changeButtonColor(currentFloor);
      floorList = Repository.getRepository().getNodesByFloor("3");
      try {
        edgeGroup.getChildren().clear();
        nodeGroup.getChildren().clear();
        draw( "3");
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public void initStateBtn() {
    btnView.setOnMouseClicked(event->{
      mapEditorContext.setState(viewState);
      mapEditorContext.getState().printStatus();
      changeStateButtonColor("View");
    } );
    btnEdit.setOnMouseClicked(event->{
      mapEditorContext.setState(editState);
      mapEditorContext.getState().printStatus();
      changeStateButtonColor("Edit");
    } );
    btnAdd.setOnMouseClicked(event->{
      mapEditorContext.setState(addState);
      mapEditorContext.getState().printStatus();
      changeStateButtonColor("Add");
    } );
    btnDelete.setOnMouseClicked(event->{
      mapEditorContext.setState(deleteState);
      mapEditorContext.getState().printStatus();
      changeStateButtonColor("Delete");
    } );
  }

  /**
   * Handles the click event for the upload button
   */
  public void handleUploadBtn() {
    fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
    File file = fileChooser.showOpenDialog(null);
    String selectedItem = getSelectedItem();
    String absolutePath = null;
    if (file != null) {
      switch (selectedItem) {
        case "Moves" -> Repository.getRepository().importMovesFromCSV(file.getAbsolutePath(), 2);
        case "Nodes" -> Repository.getRepository().importNodesFromCSV(file.getAbsolutePath(), 2);
        case "Edges" -> Repository.getRepository().importEdgesFromCSV(file.getAbsolutePath(), 2);
        case "Location Names" -> Repository.getRepository().importLocationNamesFromCSV(file.getAbsolutePath(), 2);
        case "Users" -> Repository.getRepository().importUsersFromCSV(file.getAbsolutePath(), 2);
        case "Requests" -> Repository.getRepository().importRequestsFromCSV(file.getAbsolutePath(), 2);
        case "Conference Requests" -> Repository.getRepository().importConferenceRequestsFromCSV(file.getAbsolutePath(), 2);
        case "Flower Requests" -> Repository.getRepository().importFlowerRequestsFromCSV(file.getAbsolutePath(), 2);
        case "Furniture Requests" -> Repository.getRepository().importFurnitureRequestsFromCSV(file.getAbsolutePath(), 2);
        case "Meal Requests" -> Repository.getRepository().importMealRequestsFromCSV(file.getAbsolutePath(), 2);
        case "Office Requests" -> Repository.getRepository().importOfficeRequestsFromCSV(file.getAbsolutePath(), 2);
        case "Alerts" -> Repository.getRepository().importAlertsFromCSV(file.getAbsolutePath(), 2);
        case "Signs" -> Repository.getRepository().importSignsFromCSV(file.getAbsolutePath(), 2);
      }
    }
  }

  /**
   * Handles the click event for the export button
   */
  public void handleExportBtn() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    File selectedDirectory = directoryChooser.showDialog(null);
    String absolutePath = null;
    String item = getSelectedItem();
    if (selectedDirectory != null) {
      switch (item) {
        case "Moves" -> absolutePath = selectedDirectory.getAbsolutePath() + "/moves";
        case "Nodes" -> absolutePath = selectedDirectory.getAbsolutePath() + "/nodes";
        case "Edges" -> absolutePath = selectedDirectory.getAbsolutePath() + "/edges";
        case "Location Names" -> absolutePath = selectedDirectory.getAbsolutePath() + "/locationNames";
        case "Users" -> absolutePath = selectedDirectory.getAbsolutePath() + "/users";
        case "Requests" -> absolutePath = selectedDirectory.getAbsolutePath() + "/requests";
        case "Conference Requests" -> absolutePath = selectedDirectory.getAbsolutePath() + "/conferenceRequests";
        case "Flower Requests" -> absolutePath = selectedDirectory.getAbsolutePath() + "/flowerRequests";
        case "Furniture Requests" -> absolutePath = selectedDirectory.getAbsolutePath() + "/furnitureRequests";
        case "Meal Requests" -> absolutePath = selectedDirectory.getAbsolutePath() + "/mealRequests";
        case "Office Requests" -> absolutePath = selectedDirectory.getAbsolutePath() + "/officeRequests";
        case "Alerts" -> absolutePath = selectedDirectory.getAbsolutePath() + "/alerts";
        case "Signs" -> absolutePath = selectedDirectory.getAbsolutePath() + "/signs";

        default -> absolutePath = selectedDirectory.getAbsolutePath() + "/defaulttest";
      };
      switch (item) {
        case "Nodes" -> Repository.getRepository().exportNodesToCSV(absolutePath, 2);
        case "Location Names" -> DBoutput.exportLocationNamesToCSV(absolutePath, 2);
        case "Moves" -> Repository.getRepository().exportMovesToCSV(absolutePath, 2);
        case "Edges" -> Repository.getRepository().exportEdgesToCSV(absolutePath, 2);
        case "Users" -> Repository.getRepository().exportUsersToCSV(absolutePath, 2);
        case "Requests" -> Repository.getRepository().exportRequestsToCSV(absolutePath, 2);
        case "Conference Requests" -> Repository.getRepository().exportConferenceRequestsToCSV(absolutePath, 2);
        case "Flower Requests" -> Repository.getRepository().exportFlowerRequestsToCSV(absolutePath, 2);
        case "Furniture Requests" -> Repository.getRepository().exportFurnitureRequestsToCSV(absolutePath, 2);
        case "Meal Requests" -> Repository.getRepository().exportMealRequestsToCSV(absolutePath, 2);
        case "Office Requests" -> Repository.getRepository().exportOfficeRequestsToCSV(absolutePath, 2);
        case "Alerts" -> Repository.getRepository().exportAlertsToCSV(absolutePath, 2);
        case "Signs" -> Repository.getRepository().exportSignsToCSV(absolutePath, 2);
        default -> new ArrayList<>();
      };
    }
    System.out.println("here"); // 1 means success, 0 means failure
  }

  /**
   * Changes the color of the buttons to indicate which floor is currently being viewed
   * @param currentFloor
   */
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

  private void changeStateButtonColor(String state) {
    switch (state) {
      case "View"-> {
        btnView.setStyle("-fx-background-color: #f6bd38");
        btnEdit.setStyle("-fx-background-color: #1C4EFE");
        btnDelete.setStyle("-fx-background-color: #1C4EFE");
        btnAdd.setStyle("-fx-background-color: #1C4EFE");
      }
      case "Add" -> {
        btnAdd.setStyle("-fx-background-color: #f6bd38");
        btnEdit.setStyle("-fx-background-color: #1C4EFE");
        btnDelete.setStyle("-fx-background-color: #1C4EFE");
        btnView.setStyle("-fx-background-color: #1C4EFE");
      }
      case "Edit" -> {
        btnEdit.setStyle("-fx-background-color: #f6bd38");
        btnAdd.setStyle("-fx-background-color: #1C4EFE");
        btnDelete.setStyle("-fx-background-color: #1C4EFE");
        btnView.setStyle("-fx-background-color: #1C4EFE");
      }
      case "Delete" -> {
        btnDelete.setStyle("-fx-background-color: #f6bd38");
        btnEdit.setStyle("-fx-background-color: #1C4EFE");
        btnAdd.setStyle("-fx-background-color: #1C4EFE");
        btnView.setStyle("-fx-background-color: #1C4EFE");
      }
    }
  }


  @FXML
  public void hoverHelp() {
    helpIcon.setOnMouseClicked(
            event -> {
              final FXMLLoader popupLoader = new FXMLLoader(Bapp.class.getResource("views/components/popovers/MapEditorHelpPopOver.fxml"));
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
    helpIcon.setOnMouseExited(event -> {});
  }

  private void initializeFields() {
    ArrayList<String> tables = new ArrayList<String>();
    tables.add("Nodes");
    tables.add("Edges");
    tables.add("Moves");
    tables.add("Location Names");
    tables.add("Users");
    tables.add("Requests");
    tables.add("Conference Requests");
    tables.add("Flower Requests");
    tables.add("Furniture Requests");
    tables.add("Meal Requests");
    tables.add("Office Requests");
    tables.add("Alerts");
    tables.add("Signs");
    NodeSelector.setItems(FXCollections.observableList(tables));
  }

  public void clickNodeSelector() {
    displaySelection();
  }

  public void displaySelection() {
    String item = getSelectedItem();
    ArrayList<?> itemsList = switch (item) {
      case "Moves" -> editor.getMoveList();
      case "Nodes" -> editor.getNodeList();
      case "Edges" -> editor.getEdgeList();
      case "Location Names" -> editor.getLocationNameList();
      default -> new ArrayList<>();
    };

    ObservableList<?> items = FXCollections.observableList(itemsList);
    NodeInfo.setItems(items);
    VboxNodes.getChildren().clear();
    VboxNodes.getChildren().addAll(NodeInfo);
  }

  public String getSelectedItem() {
    return NodeSelector.getSelectedItem();
  }

  public void initNavBar() {
    // https://github.com/afsalashyana/JavaFX-Tutorial-Codes/tree/master/JavaFX%20Navigation%20Drawer/src/genuinecoder
    try {
      FXMLLoader loader =
              new FXMLLoader(getClass().getResource("/edu/wpi/teamb/views/components/NavDrawer.fxml"));
      VBox vbox = loader.load();
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

  public ArrayList<Node> getNodeList() {
    return editor.getNodeList();
  }

  public void addToNodeList(Node n) {
    nodeList.add(n);
  }

  public void addToFullNodeList(FullNode n) {
    fullNodesList.add(n);
  }
}
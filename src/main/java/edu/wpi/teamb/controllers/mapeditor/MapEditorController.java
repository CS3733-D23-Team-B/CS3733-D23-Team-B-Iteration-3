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
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
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
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.kurobako.gesturefx.GesturePane;
import org.controlsfx.control.PopOver;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ResponseCache;
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
  private MFXToggleButton toggleNodes;
  @FXML
  private MFXToggleButton toggleLocationNames;
  @FXML
  private MFXToggleButton toggleEdges;
  @FXML private MFXButton btnViewMoveMap;

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
  Text tfState;
  @FXML
  private MFXButton btnAlignNodes;

  // New States
  MapEditorState addNodeState = new AddNodeState();
  MapEditorState editNodeState = new EditNodeState();
  MapEditorState deleteNodeState = new DeleteNodeState();
  MapEditorState addEdgeState = new AddEdgeState();
  MapEditorState deleteEdgeState = new DeleteEdgeState();
  MapEditorState addMoveState = new AddMoveState();
  MapEditorState alignNodesState = new AlignNodesState();

  // Create the states
  MapEditorContext mapEditorContext = new MapEditorContext();
  private Set<Circle> selectedNodes = new HashSet<>();

  private Set<Circle> nodesToAlign = new HashSet<>();

  private double m = 0;
  private double c = 0;
  Circle c1;
  Circle c2;

  // New menu buttons
  @FXML
  private MenuButton btnMenuNode;
  @FXML private MenuItem itemAddNode;
    @FXML private MenuItem itemEditNode;
    @FXML private MenuItem itemDeleteNode;
  @FXML
  private MenuButton btnMenuEdge;
    @FXML private MenuItem itemAddEdge;
        @FXML private MenuItem itemDeleteEdge;
  @FXML
  private MenuButton btnMenuMove;
    @FXML private MenuItem itemAddMove;
    @FXML private MenuItem itemDeleteMove;
  @FXML private MenuItem itemViewMoves;
  @FXML
  private MenuButton btnMenuTools;
    @FXML private MenuItem itemAlign;
    @FXML private MenuItem itemSetDefault;

  @FXML private MenuButton btnMenuBackup;
    @FXML private MenuItem itemResetFromBackup;
  @FXML private MenuItem itemSaveToBackup;

  public MapEditorController() throws SQLException {
    this.editor = new EMapEditor();
  }

  @FXML
  public void initialize() throws IOException, SQLException {
    initNavBar();
    hoverHelp();
//    initializeFields();
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



    FileInputStream input = new FileInputStream("src/main/resources/edu/wpi/teamb/img/breakfast.png");
    Image image = new Image(input);
    ImageView imageView1 = new ImageView(image);
    ImageView imageView2 = new ImageView(image);
    ImageView imageView3 = new ImageView(image);
    ImageView imageView4 = new ImageView(image);
    ImageView imageView5 = new ImageView(image);
    btnMenuNode.setGraphic(imageView1);
    btnMenuEdge.setGraphic(imageView2);
    btnMenuBackup.setGraphic(imageView3);
    btnMenuMove.setGraphic(imageView4);
    btnMenuTools.setGraphic(imageView5);
    btnAlignNodes.setVisible(false);

    System.out.println("MapEditorController initialized");
  }

  private void handleNodes() {
    System.out.println("Handling nodes");
    determineState();
  }

  private void handleEdges() {
    System.out.println("Handling edges");
    determineState();
  }

  /**
   * Handles the alignment of nodes
   */
  private void alignNodes() {
    if (mapEditorContext.getState() == alignNodesState) {
      // Iterate through the selected nodes
      edgeGroup.getChildren().clear();
      for (Circle c : nodesToAlign) {
        Node n = Repository.getRepository().getNode(Integer.parseInt(c.getId())); // get the node
        n.setyCoord(startAndEndPoints(n.getxCoord()));  // Set the y coordinate
        Repository.getRepository().updateNode(n); // Update the node
      }
      nodesToAlign.clear(); // Clear the selected nodes list
      System.out.println("Aligning all selected nodes");
      refreshMap();
      btnAlignNodes.setVisible(false);
    }
  }


  /**
   * Determines the state we are in and changes the text field accordingly
   */
  private void determineState() {
    if (mapEditorContext.getState() == addEdgeState) {
      System.out.println("Adding edge");
      tfState.setText("Adding Edge");
    } else if (mapEditorContext.getState() == addNodeState) {
      System.out.println("Adding Node");
      tfState.setText("Adding Node");
    } else if (mapEditorContext.getState() == deleteEdgeState) {
      System.out.println("Deleting edge");
      tfState.setText("Delete Edge");
    } else if (mapEditorContext.getState() == deleteNodeState) {
      System.out.println("Deleting node");
      tfState.setText("Deleting Node");
    } else if (mapEditorContext.getState() == editNodeState) {
      System.out.println("Editing node");
      tfState.setText("Editing Node");
    }else if (mapEditorContext.getState() == addMoveState) {
        System.out.println("Adding Move");
        tfState.setText("Adding Move");
    } else if (mapEditorContext.getState() == alignNodesState) {
      System.out.println("Selecting nodes");
      tfState.setText("Selecting Nodes");
    }
  }


  /**
   * Draws all the nodes on the map
   *
   * @param floor
   * @throws SQLException
   */
  public void draw(String floor) {
    System.out.println("Clearing the children");
    this.nodeGroup.getChildren().clear();
    this.nameGroup.getChildren().clear();
    this.edgeGroup.getChildren().clear();
    // For each node, create a circle
    for (Node n : nodeList) {
      if (n.getFloor().equals(floor)) {
        drawNode(n);
        drawEdge(n);
        drawName(n);
      }
    }
    System.out.println("Drawing the edges, names, and nodes for floor " + floor);
    nodeGroup.toFront();
  }

  /**
   * Draws the edges on the map
   *
   * @param n Node to draw edges for
   */
  public void drawEdge(Node n) {
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
          line.setOnMouseClicked(event -> handleDeleteEdge(event, line));
          edgeGroup.getChildren().add(line);

        }
      }
    }
    edgeGroup.toFront();
    nodeGroup.toFront();
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
    // change the color of a circle when hovered
    c.setOnMouseEntered(event -> {
      c.setFill(Color.GREEN);
      c.setRadius(10);
    });
    c.setOnMouseExited(event -> {
      c.setFill(Color.RED);
      c.setRadius(5);
    });
    c.setOnMouseClicked(event -> {
      try {
        selectedNodes.add(c); // Used for creating edges
        nodesToAlign.add(c); // Used for aligning nodes
        checkSelectedNodes(); // Check if two circles are selected to create an edge
        checkNodesToAlign(); // Check if at least two circles are selected to align

        // Set node click event handlers
        if (mapEditorContext.getState() == editNodeState)
          handleEditNode(n);
        if (mapEditorContext.getState() == deleteNodeState)
          handleDeleteNode(event, n);
        System.out.println("Node " + n.getNodeID() + " clicked");
      } catch (SQLException | IOException e) {
        throw new RuntimeException(e);
      }
    }); // Set the handler for clicking on a node


    // Add the circle to the nodeGroup
    floorList.add(n);
    nodeGroup.getChildren().add(c);
    nodeGroup.toFront();
  }


  /**
   * Checks if at least 2 nodes are selected to align
   */
  private void checkNodesToAlign() {
    List<Integer> xCoords = new ArrayList<>();
    List<Integer> yCoords = new ArrayList<>();

    // Populate the list
    if (nodesToAlign.size() > 2) {
      for (Circle c : nodesToAlign) {
        xCoords.add((int) c.getCenterX());
        yCoords.add((int) c.getCenterY());
        System.out.println(c);
      }
      System.out.println(nodesToAlign.size());
      btnAlignNodes.setVisible(true);
    }

    // Assign new Y coordinates
    if (mapEditorContext.getState() == alignNodesState) {
      calcBestFit(xCoords, yCoords);  // Calculate the best fit
    }
  }

  private int startAndEndPoints(int x) {
    return (int) (m * x + c);
  }


  /**
   * Function to calculate the best fit line
   *
   * @param x
   * @param y
   */
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
    if (selectedNodes.size() == 2) {
      Iterator<Circle> iterator = selectedNodes.iterator();
      c1 = iterator.next();
      c2 = iterator.next();
      System.out.println(c1.toString());
      System.out.println(c2.toString());
      selectedNodes.clear();
      handleAddEdge(c1, c2);
    }
  }

  /**
   * Draws the location names on the map
   */
  void drawName(Node n) {
    for (FullNode fn : fullNodesList) {
      if (!Objects.equals(fn.getNodeType(), "HALL")) {
        if (fn.getNodeID() == n.getNodeID()) {
          Text name = new Text(fn.getShortName());
          name.setX(n.getxCoord() + 5);
          name.setY(n.getyCoord() + 5);
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

  /**
   * Gets the maximum ID of the list of nodes
   */
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
    nameGroup.getChildren().clear();
    // Redraw the map
//    try {
//      if (mapEditorContext.getState() != editState) {
        PathFinding.ASTAR.force_init();
//      }
      nodeList = Repository.getRepository().getAllNodes();
      fullNodesList = Repository.getRepository().getAllFullNodes();
      draw(currentFloor);
      System.out.println("Refreshing map for floor " + currentFloor + "...");
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
  }

  private void handleAddNode() {
    stackPaneMapView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::tapToAddNode);
    System.out.println("Added tapToAddNode event handler");
  }

  private void tapToAddNode(MouseEvent e) {
      // Method to allow for double click to add a new node
      try {
        Node n = new Node();
        n.setxCoord((int) e.getX());
        n.setyCoord((int) e.getY());
        n.setFloor(currentFloor);
        AddNodeMenuController.setCurrentFloor(currentFloor);
        n.setNodeID(getMaxID() + 5);
        showAddNodeMenu(n);
        editingNode = false;
        addNodeToMap(e.getX(), e.getY());   // get the X and Y of the cursor
        System.out.println("Added a node at " + e.getX() + ", " + e.getY());
      } catch (SQLException | IOException ex) {
        throw new RuntimeException(ex);
      }
    stackPaneMapView.setDisable(true);
      refreshMap();
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
    if (mapEditorContext.getState() == deleteEdgeState) {
      // Delete the edge from the database
      Edge edge = Repository.getRepository().getEdge(l.getId());
      Repository.getRepository().deleteEdge(edge);

      ArrayList<Edge> edges = Repository.getRepository().getAllEdges();

      // Remove the edge from the map
      edgeGroup.getChildren().remove(l);

      refreshMap();
      System.out.println("Edge: " + l.getId() + " deleted");
    }
  }


  /**
   * Handles the add edge event
   * @param c1
   * @param c2
   */
  private void handleAddEdge(Circle c1, Circle c2) {
    if (c1 != null && c2 != null && mapEditorContext.getState() == addEdgeState) {
      // Generate the line between the two nodes
      Line line = new Line(c1.getCenterX(), c1.getCenterY(), c2.getCenterX(), c2.getCenterY());
      line.setStrokeWidth(4);
      line.setId(c1.getId() + "_" + c2.getId());
      edgeGroup.getChildren().add(line);
      System.out.println("Added Edge with ID = " + line.getId());

      // Add edge to the database
      Edge edge = new Edge();
      edge.setStartNodeID(Integer.parseInt(c1.getId()));
      edge.setEndNodeID(Integer.parseInt(c2.getId()));
      Repository.getRepository().addEdge(edge);
//    ArrayList<Edge> edges = Repository.getRepository().getAllEdges();
//    edges.size();
      refreshMap();
    }
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
      for (javafx.scene.Node c : nodeGroup.getChildren()) {
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
   * Allow the user to drag the node
   * //https://stackoverflow.com/questions/17312734/how-to-make-a-draggable-node-in-javafx-2-0
   *
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
    itemResetFromBackup.setOnAction(event -> {
      handleResetFromBackupBtn();
    });

    btnViewMoveMap.setOnMouseClicked(e -> Navigation.navigate(Screen.MOVE_MAP));

    // initialize the toggles
    toggleEdges.setSelected(true);
    toggleNodes.setSelected(true);
    toggleLocationNames.setSelected(true);
    toggleLocationNames.setOnMouseClicked(event -> {
      handleToggleLocationNames();
    });
    toggleEdges.setOnMouseClicked(event -> {
      handleToggleEdges();
    });
    toggleNodes.setOnMouseClicked(event -> {
      handleToggleNodes();
    });

    // Init new buttons
    btnAlignNodes.setOnMouseClicked(event -> alignNodes());
  }

  private void handleAddMove() {
  }

  /**
   * Toggles the ability to see location names on the maps
   */
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

  /**
   * Toggles the ability to see nodes on the maps
   */
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

  /**
   * Toggles the ability to see edges on the maps
   */
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

  public void clickFloorBtn() {
    btnL1.setOnMouseClicked(event -> {
      imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(0));
      currentFloor = "L1";
      changeButtonColor(currentFloor);
      floorList = Repository.getRepository().getNodesByFloor("L1");
        edgeGroup.getChildren().clear();
        nodeGroup.getChildren().clear();
        draw("L1");
    });
    btnL2.setOnMouseClicked(event -> {
      imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(1));
      currentFloor = "L2";
      changeButtonColor(currentFloor);
      floorList = Repository.getRepository().getNodesByFloor("L2");

        edgeGroup.getChildren().clear();
        nodeGroup.getChildren().clear();
        draw("L2");
    });
    btn1.setOnMouseClicked(event -> {
      currentFloor = "1";
      imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(3));
      changeButtonColor(currentFloor);
      floorList = Repository.getRepository().getNodesByFloor("1");
        edgeGroup.getChildren().clear();
        nodeGroup.getChildren().clear();
        draw("1");
    });
    btn2.setOnMouseClicked(event -> {
      currentFloor = "2";
      imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(4));
      changeButtonColor(currentFloor);
      floorList = Repository.getRepository().getNodesByFloor("2");
        edgeGroup.getChildren().clear();
        nodeGroup.getChildren().clear();
        draw("2");
    });
    btn3.setOnMouseClicked(event -> {
      currentFloor = "3";
      imageViewPathfinder.setImage(Bapp.getHospitalListOfFloors().get(5));
      changeButtonColor(currentFloor);
      floorList = Repository.getRepository().getNodesByFloor("3");
        edgeGroup.getChildren().clear();
        nodeGroup.getChildren().clear();
        draw("3");
    });
  }

  public void initStateBtn() {
    // Init New State Buttons
    itemAddEdge.setOnAction(event -> {
      mapEditorContext.setState(addEdgeState);
      mapEditorContext.getState().printStatus();
    });
    itemAddNode.setOnAction(event -> {
      mapEditorContext.setState(addNodeState);
      mapEditorContext.getState().printStatus();
      handleAddNode();
    });
    itemDeleteEdge.setOnAction(event -> {
      mapEditorContext.setState(deleteEdgeState);
      mapEditorContext.getState().printStatus();
    });
    itemDeleteNode.setOnAction(event -> {
      mapEditorContext.setState(deleteNodeState);
      mapEditorContext.getState().printStatus();
    });
    itemEditNode.setOnAction(event -> {
      mapEditorContext.setState(editNodeState);
      mapEditorContext.getState().printStatus();
    });
    itemAddMove.setOnAction(event -> {
      mapEditorContext.setState(addMoveState);
      mapEditorContext.getState().printStatus();
    });
    itemAlign.setOnAction(event -> {
      mapEditorContext.setState(alignNodesState);
      mapEditorContext.getState().printStatus();
    });
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
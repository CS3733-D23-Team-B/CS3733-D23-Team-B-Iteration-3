package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.*;
import edu.wpi.teamb.DBAccess.DBinput;
import edu.wpi.teamb.DBAccess.Full.*;
import edu.wpi.teamb.DBAccess.ORMs.*;
import edu.wpi.teamb.pathfinding.PathFinding;

import java.sql.*;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class Repository {
    private Repository() {
        try {
            dbConnection = DBconnection.getDBconnection();
            nodeDAO = new NodeDAOImpl();
            edgeDAO = new EdgeDAOImpl();
            locationNameDAO = new LocationNameDAOImpl();
            moveDAO = new MoveDAOImpl();
            userDAO = new UserDAOImpl();
            requestDAO = RequestDAOImpl.getRequestDaoImpl();
            conferenceRequestDAO = new ConferenceRequestDAOImpl();
            flowerRequestDAO = new FlowerRequestDAOImpl();
            mealRequestDAO = new MealRequestDAOImpl();
            furnitureRequestDAO = new FurnitureRequestDAOImpl();
            officeRequestDAO = new OfficeRequestDAOImpl();
            alertDAO = new AlertDAOImpl();
            signDAO = new SignDAOImpl();
        } catch (SQLException e) {
            System.out.println("ERROR: Repository failed to initialize");
            throw new RuntimeException(e);
        }
    }

    private static class SingletonHelper {
        //Nested class is referenced after getRepository() is called
        private static final Repository repository = new Repository();
    }

    public static Repository getRepository() {
        return SingletonHelper.repository;
    }

    private final NodeDAOImpl nodeDAO;
    private final EdgeDAOImpl edgeDAO;
    private final LocationNameDAOImpl locationNameDAO;
    private final MoveDAOImpl moveDAO;
    private final UserDAOImpl userDAO;
    private final RequestDAOImpl requestDAO;
    private final ConferenceRequestDAOImpl conferenceRequestDAO;
    private final FlowerRequestDAOImpl flowerRequestDAO;
    private final MealRequestDAOImpl mealRequestDAO;
    private final FurnitureRequestDAOImpl furnitureRequestDAO;
    private final OfficeRequestDAOImpl officeRequestDAO;
    private final AlertDAOImpl alertDAO;
    private final SignDAOImpl signDAO;
    private final DBconnection dbConnection;

    //TODO Node methods

    /**
     * Gets a node by its ID
     *
     * @param id The ID of the node
     * @return The node with the given ID
     */
    public Node getNode(Object id) {
        Node n = nodeDAO.get(id);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return n;
    }

    /**
     * Gets all local Node objects
     *
     * @return an ArrayList of all local Node objects
     */
    public ArrayList<Node> getAllNodes() {
        ArrayList<Node> nodes = nodeDAO.getAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return nodes;
    }

    /**
     * Gets all local FullNode objects
     *
     * @return an ArrayList of all local FullNode objects
     */
    public ArrayList<FullNode> getAllFullNodes() {
        ArrayList<FullNode> nodes = nodeDAO.getAllFullNodes();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return nodes;
    }

    /**
     * Sets all Node objects using the database
     */
    public void setAllNodes () {
        nodeDAO.setAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Adds a Node object to the both the database and local list
     *
     * @param n the Node object to be added
     */
    public void addNode(Object n) {
        nodeDAO.add(n);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Removes a Node from the both the database and the local list
     *
     * @param n the Node object to be removed
     */
    public void deleteNode(Object n) {
        Node node = (Node) n;
        nodeDAO.delete(n);
        ArrayList<Integer> nodeIDs = node.getNeighborIds();
        for(int id : nodeIDs) {
            Edge delete = getEdge(node.getNodeID() + "_" + id);
            deleteEdge(delete);
        }
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Updates a Node object in both the database and local list
     *
     * @param n the Node object to be updated
     */
    public void updateNode(Object n) {
        nodeDAO.update(n);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Gets all nodes from the database
     *
     * @return a list of all nodes
     */
    public ArrayList<Node> getAllNodesFromDB() {
        ArrayList<Node> nodes = nodeDAO.getAllHelper();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return nodes;
    }

    /**
     * Searches through the database for the row(s) that matches the col and value in the Nodes table
     *
     * @param col   the column to search for
     * @param value the value to search for
     * @return A ResultSet of the row(s) that match the col and value
     */
    private ResultSet getDBRowFromCol(String col, String value) {
        ResultSet rs = nodeDAO.getDBRowFromCol(col, value);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return rs;
    }

    /**
     * Gets a ResultSet of all rows from the Nodes table
     *
     * @return a ResultSet of all rows from the Nodes table
     */
    public ResultSet getDBRowAllNodes() {
        ResultSet rs = nodeDAO.getDBRowAllNodes();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return rs;
    }

    /**
     * Gets a ResultSet of rows from the Nodes table that match the given nodeID
     *
     * @param nodeID the nodeID to look for to get Node data
     * @return a ResultSet of the row(s) that match the nodeID
     */
    public ResultSet getDBRowNodeIDFromNodes(int nodeID) {
        ResultSet rs = nodeDAO.getDBRowNodeID(nodeID);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return rs;
    }

    /**
     * Gets a ResultSet of rows from the Nodes table that match the given xCoord
     *
     * @param xCoord the longName to look for to get Node data
     * @return a ResultSet of the row(s) that match the xCoord
     */
    public ResultSet getDBRowXCoordFromNodes(int xCoord) {
        ResultSet rs = nodeDAO.getDBRowXCoord(xCoord);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return rs;
    }

    /**
     * Gets a ResultSet of rows from the Nodes table that match the given yCoord
     *
     * @param yCoord the longName to look for to get Node data
     * @return a ResultSet of the row(s) that match the yCoord
     */
    public ResultSet getDBRowYCoordFromNodes(int yCoord) {
        ResultSet rs = nodeDAO.getDBRowYCoord(yCoord);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return rs;
    }

    /**
     * Gets a ResultSet of rows from the Nodes table that match the given floor
     *
     * @param floor the floor to look for to get Node data
     * @return a ResultSet of the row(s) that match the floor
     */
    public ResultSet getDBRowFloorFromNodes(String floor) {
        ResultSet rs = nodeDAO.getDBRowFloor(floor);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return rs;
    }

    /**
     * Gets an ArrayList of Nodes from the Nodes table that are on the given floor
     *
     * @param floor the floor to look for to get Node data
     * @return an ArrayList of the Nodes that are on the given floor
     */
    public ArrayList<Node> getNodesFromFloor(String floor) {
        ArrayList<Node> nodes = nodeDAO.getNodesFromFloor(floor);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return nodes;
    }

    /**
     * Gets a ResultSet of rows from the Nodes table that match the given building
     *
     * @param building the building to look for to get Node data
     * @return a ResultSet of the row(s) that match the building
     */
    public ResultSet getDBRowBuilding(String building) {
        ResultSet rs = nodeDAO.getDBRowBuilding(building);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return rs;
    }

    /**
     * Updates the database with the information in this Node object
     *
     * @param value a String array containing values to update (nodeID, xCoord, yCoord, floor, building)
     */
    private void updateRowNode(String[] value) {
        nodeDAO.updateRow(value);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Deletes the row in the database that matches the nodeID of this Node object
     *
     * @param confirm 0 to confirm, anything else to cancel
     */
    public void deleteDBNode(int confirm, Node n) {
        nodeDAO.deleteDBNode(confirm, n);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Returns a String of all the information about the node
     *
     * @return a String of all the information about the node
     */
    public String nodeToString(Node node) {
        String nodeString = nodeDAO.toString(node);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return nodeString;
    }

    /**
     * Inserts a Node object into the database
     *
     * @param n the Node to insert
     */
    public void insertDBNode(Node n) {
        nodeDAO.insertDBNode(n);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Resets the node, location names, and moves tables using the backup tables
     */
    public void resetNodesFromBackup() {
        nodeDAO.resetNodesFromBackup();
        nodeDAO.setAll();
        locationNameDAO.setAll();
        moveDAO.setAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Gets all the neighbors of a given Node using its nodeID
     *
     * @return an ArrayList of all the neighbors of the given node using its nodeID
     */
    public ArrayList<Node> getNodeNeighbors(int nodeID) {
        ArrayList<Node> nodes = nodeDAO.getNeighbors(nodeID);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return nodes;
    }

    /**
     * Gets all the neighbors of a given Node using its nodeID
     *
     * @return an ArrayList of all the nodeIDs of the neighbors of the given node using its nodeID
     */
    public ArrayList<Integer> getNodeNeighborsAsNodeIDs(int nodeID) {
        ArrayList<Integer> nodes = nodeDAO.getNeighborsAsNodeIDs(nodeID);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return nodes;
    }

    /**
     * Returns a list of nodeIDs given a list of nodes
     *
     * @return an integer ArrayList of nodeIDs
     */
    public ArrayList<Integer> nodeToIDs(ArrayList<Node> nodes) {
        ArrayList<Integer> nodeIDs = nodeDAO.nodeToIDs(nodes);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return nodeIDs;
    }

    /**
     * Instantiates all local nodes using the database
     */
    public ArrayList<Node> instantiateNodes() {
        ArrayList<Node> nodes = nodeDAO.instantiateNodes();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return nodes;
    }

    /**
     * Updates a certain node in the database
     *
     * @param node the node to be updated
     * @param newNodeID the new nodeID
     * @param newXCoord the new xCoord
     * @param newYCoord the new yCoord
     * @param newFloor the new floor
     * @param newBuilding the new building
     */
    public void updateEditedNode(Node node, int newNodeID, int newXCoord, int newYCoord, String newFloor, String newBuilding) {
        nodeDAO.updateEditedNode(node, newNodeID, newXCoord, newYCoord, newFloor, newBuilding);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Gets the short name of a node given its nodeID
     *
     * @param nodeID the nodeID of the node
     * @return the short name of the node
     */
    public String getShortName(int nodeID) {
        String shortName = nodeDAO.getShortName(nodeID);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return shortName;
    }

    /**
     * A helper function for getting a list of full nodes
     *
     * @return the long name of the node
     */
    public ArrayList<FullNode> getFullNodesHelper() {
        ArrayList<FullNode> fullNodes = nodeDAO.getFullNodesHelper();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return fullNodes;
    }

    /**
     * A helper function for getting a FullNode object given its nodeID
     *
     * @param nodeID the nodeID of the node
     * @return a FullNode object
     */
    public FullNode getFullNode(int nodeID) {
        FullNode fullNode = nodeDAO.getFullNode(nodeID);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return fullNode;
    }

    /**
     * A helper function for getting a list of node neighbors given a list of nodes
     *
     * @param floorNodes the list of nodes on a certain floor
     * @return the long name of the node
     */
    public ArrayList<ArrayList<Integer>> nodeNeighborIDs(ArrayList<Node> floorNodes, ArrayList<Edge> edges) {
        ArrayList<ArrayList<Integer>> nodeNeighborIDs = nodeDAO.nodeNeighborIDs(floorNodes, edges);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return nodeNeighborIDs;
    }

    /**
     * Gets the node type of a node given its nodeID
     *
     * @param n the Node object
     * @return the node type of the node
     */
    public String getNodeType (Node n) {
        String nodeType = nodeDAO.getNodeType(n);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return nodeType;
    }

    /**
     * Gets the value of the column from the table that matches the condition
     *
     * @return a ResultSet containing the full nodes table joined with the moves table and the locationnames table
     */
    public ResultSet joinFullNodes() {
        ResultSet rs = nodeDAO.joinFullNodes();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return rs;
    }

    /**
     * Gets the value of the column from the table that matches the condition
     *
     * @param nodeID the nodeID to get the longName from
     * @return the longName of the node
     */
    public String getLongNameFromNodeID(int nodeID) {
        String longName = nodeDAO.getLongNameFromNodeID(nodeID);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return longName;
    }

    /**
     * Gets the value of the column from the table that matches the condition
     *
     * @param nodeID NodeID to get shortname from
     * @return a String with the shortname associated with the given NodeID
     */
    public String getShortNameFromNodeID(int nodeID) {
        String shortName = nodeDAO.getShortNameFromNodeID(nodeID);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return shortName;
    }

    /**
     * Gets a Node object given its nodeID
     *
     * @param nodeID the nodeID to get the node from
     * @return a Node object
     */
    public Node getNode(int nodeID) {
        Node node = nodeDAO.getNode(nodeID);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return node;
    }

    /**
     * 'Fills' a node with its neighbors
     *
     * @param nodeID the nodeID to get the neighbors from
     * @return a 'filled' Node object with the given nodeID
     */
    public Node nodeFill(int nodeID) {
        Node node = nodeDAO.nodeFill(nodeID);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return node;
    }


    //TODO Edge methods

    /**
     * Gets an edge by its endpoints
     *
     * @param id The endpoints of the edge
     * @return The edge with the given endpoints
     */
    public Edge getEdge(Object id) {
        Edge edge = edgeDAO.get(id);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return edge;
    }

    /**
     * Gets all local Edge objects
     *
     * @return an ArrayList of all local Edge objects
     */

    public ArrayList<Edge> getAllEdges() {
        ArrayList<Edge> edges = edgeDAO.getAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return edges;
    }

    /**
     * Sets all Edge objects using the database
     */
    public void setAllEdges() {
        edgeDAO.setAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Gets all edges from the database
     *
     * @return a list of all edges
     */
    public ArrayList<Edge> getAllEdgesHelper() {
        ArrayList<Edge> edges = edgeDAO.getAllHelper();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return edges;
    }

    /**
     * Adds an Edge object to the both the database and local list
     *
     * @param edge the Edge object to be added
     */

    public void addEdge(Object edge) {
        edgeDAO.add(edge);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Removes an Edge from the both the database and the local list
     *
     * @param edge the Edge object to be removed
     */

    public void deleteEdge(Object edge) {
        edgeDAO.delete(edge);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Updates an Edge object in both the database and local list
     *
     * @param edge the Edge object to be updated
     */

    public void updateEdge(Object edge) {
        edgeDAO.update(edge);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Adds this edge to its start and end nodes. If the list of nodes does not
     * contain the start or end node, it will make them
     *
     * @param nodes the list of nodes to look through add the edge to the start and
     *              end nodes
     */
    public void addToNode(Map<Integer, Node> nodes, Edge e) {
        edgeDAO.addToNode(nodes, e);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Gets the row(s) from the database that matches the startNode
     *
     * @param startNode the startNode to search for, make sure there are NO single
     *                  quotes surrounding the startNode
     * @return the row(s) that have a matching the startNode
     */
    public ResultSet getDBRowStartNodeFromEdges(String startNode) {
        ResultSet rs = EdgeDAOImpl.getDBRowStartNode(startNode);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return rs;
    }

    /**
     * Gets the row(s) from the database that matches the endNode
     *
     * @param endNode the endNode to search for, make sure there are NO single
     *                quotes surrounding the endNode
     * @return the row(s) that have a matching the endNode
     */
    public ResultSet getDBRowEndNodeFromEdges(String endNode) {
        ResultSet rs = EdgeDAOImpl.getDBRowEndNode(endNode);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return rs;
    }

    /**
     * Searches through the database for the row(s) that matches the col and value in the Edges table
     *
     * @param col   the column to search for
     * @param value the value to search for
     * @return A ResultSet of the row(s) that match the col and value
     */
    private ResultSet getRowFromCol(String col, String value) {
        ResultSet rs = EdgeDAOImpl.getRowFromCol(col, value);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return rs;
    }

    /**
     * Gets all the rows from the Edge table
     */
    public ResultSet getDBRowAllEdges() {
        ResultSet rs = edgeDAO.getDBRowAllEdges();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return rs;
    }

    /**
     * Inserts an edge into the database
     *
     * @param startNode the startNode of the edge
     * @param endNode the endNode of the edge
     */
    public void insertEdge(int startNode, int endNode) {
        edgeDAO.insertEdge(startNode, endNode);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Updates the row in the database that matches the edgeID
     *
     * @param cols   the columns to update
     * @param values the values to update the columns to
     */
    private void updateRow(String[] cols, String[] values, Edge e) {
        edgeDAO.updateRow(cols, values, e);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Updates the startNode in the database
     *
     * @param startNode the new startNode
     */
    public void updateDBStartNodeInEdges(String startNode, Edge e) {
        edgeDAO.updateDBStartNode(startNode, e);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Updates the endNode in the database
     *
     * @param endNode the new endNode
     * @param e the Edge to update
     */
    public void updateDBEndNodeInEdges(String endNode, Edge e) {
        edgeDAO.updateDBEndNode(endNode, e);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Updates all attributes in the database to match the edge
     */
    public void updateDBEdge(Edge e) {
        edgeDAO.updateDBEdge(e);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Deletes the row in the database that matches the edgeID
     * only use this if you are sure you want to delete the row
     *
     * @param confirm 0 to confirm delete, anything else to cancel
     * @param e the edge to delete
     */
    public void deleteDBEdge(int confirm, Edge e) {
        edgeDAO.deleteDBEdge(confirm, e);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Gets the edge from the database that matches the edgeID
     *
     * @param endpoints a String of endpoints in the format "startNode_endNode"
     * @return the edge that matches the endpoints
     */
    public Edge getEdge(String endpoints) {
        Edge e = edgeDAO.getEdge(endpoints);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return e;
    }

    /**
     * Returns the edge in a string
     *
     * @return the edge in a string with the format "[edgeID] [startNode] [endNode]"
     */
    public String edgeToString(Edge e) {
        String s = edgeDAO.toString(e);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return s;
    }

    /**
     * Resets the edge table using the backup table
     */
    public void resetEdgesFromBackup() {
        edgeDAO.resetEdgesFromBackup();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    //TODO LocationName methods

    public int getNodeIDfromLongName(String longName) {
        int id = locationNameDAO.getNodeIDfromLongName(longName);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return id;
    }


    //TODO Move methods



    //TODO User methods



    //TODO Request methods



    //TODO ConferenceRequest methods



    //TODO FlowerRequest methods



    //TODO MealRequest methods



    //TODO FurnitureRequest methods



    //TODO OfficeRequest methods

    /**
     * Updates an OfficeRequest in the database
     * @param fullOfficeRequest
     */
    public void updateOfficeRequest(FullOfficeRequest fullOfficeRequest) {
        officeRequestDAO.update(fullOfficeRequest);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    //TODO Alert methods



    //TODO Sign methods

    public Sign get(Object id) {
        Sign s = signDAO.get(id);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return s;
    }

    public ArrayList<Sign> getAllSigns() {
        ArrayList<Sign> signs = signDAO.getAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return signs;
    }

    public ArrayList<Sign> getAllSignsHelper() {
        ArrayList<Sign> signs = signDAO.getAllHelper();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return signs;
    }

    public void setAllSigns() {
        signDAO.setAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void addSign(Object object) {
        signDAO.add(object);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteSign(Object object) {
        signDAO.delete(object);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void updateSign(Object object) {
        signDAO.update(object);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void updateBlock(Object object) {
        signDAO.updateBlock(object);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void insertSign(Sign s) {
        signDAO.insertSign(s);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteSignGroup(Sign s) {
        signDAO.deleteSignGroup(s);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteSpecificSign(Sign s) {
        signDAO.deleteSpecificSign(s);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void updateSign(Sign s) {
        signDAO.updateSign(s);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void updateSignageGroup(Sign s) {
        signDAO.updateSignageGroup(s);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public HashSet<String> getSignageGroupsFromDB() {
        HashSet<String> hset = signDAO.getSignageGroupsFromDB();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return hset;
    }

    public ArrayList<String> getLocationNamesFromDB(String signBlock) {
        ArrayList<String> names = signDAO.getLocationNamesFromDB(signBlock);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return names;
    }

    public HashSet<String> getSignageGroups() {
        HashSet<String> hset = signDAO.getSignageGroups();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return hset;
    }

    public ArrayList<String> getLocationNames(String signBlock) {
        ArrayList<String> names = signDAO.getLocationNames(signBlock);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return names;
    }

    //TODO DBinput methods

    /**
     * This method imports the Nodes table from a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void importNodesFromCSV(String filename, int location) {
        DBinput.importNodesFromCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method imports the Edges table from a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void importEdgesFromCSV(String filename, int location) {
        DBinput.importEdgesFromCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method imports the LocationNames table from a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void importLocationNamesFromCSV(String filename, int location) {
        DBinput.importLocationNamesFromCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method imports the Moves table from a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void importMovesFromCSV(String filename, int location) {
        DBinput.importMovesFromCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method imports the Users table from a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void importUsersFromCSV(String filename, int location) {
        DBinput.importUsersFromCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method imports the Requests table from a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void importRequestsFromCSV(String filename, int location) {
        DBinput.importRequestsFromCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Imports the conference requests from a CSV file into the database
     *
     * @param filename The name of the CSV file to be imported as a String
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void importConferenceRequestsFromCSV(String filename, int location) {
        DBinput.importConferenceRequestsFromCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Imports the flower requests from a CSV file into the database
     *
     * @param filename The name of the CSV file to be imported as a String
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void importFlowerRequestsFromCSV(String filename, int location) {
        DBinput.importFlowerRequestsFromCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Imports the furniture requests from a CSV file into the database
     *
     * @param filename The name of the CSV file to be imported as a String
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void importFurnitureRequestsFromCSV(String filename, int location) {
        DBinput.importFurnitureRequestsFromCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Imports the meal requests from a CSV file into the database
     *
     * @param filename The name of the CSV file to be imported as a String
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void importMealRequestsFromCSV(String filename, int location) {
        DBinput.importMealRequestsFromCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Imports the office requests from a CSV file into the database
     *
     * @param filename The name of the CSV file to be imported as a String
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void importOfficeRequestsFromCSV(String filename, int location) {
        DBinput.importOfficeRequestsFromCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Imports the alerts from a CSV file into the database
     *
     * @param filename The name of the CSV file to be imported as a String
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public void importAlertsFromCSV(String filename, int location) {
        DBinput.importAlertsFromCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * Imports the signage from a CSV file into the database
     *
     * @param filename The name of the CSV file to be imported as a String
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public void importSignsFromCSV(String filename, int location) {
        DBinput.importSignsFromCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    //TODO DBoutput methods

    /**
     * This method exports the Nodes table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void exportNodesToCSV(String filename, int location) {
        DBoutput.exportNodesToCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method exports the Edges table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void exportEdgesToCSV(String filename, int location) {
        DBoutput.exportEdgesToCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method exports the LocationNames table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void exportLocationNamesToCSV(String filename, int location) {
        DBoutput.exportLocationNamesToCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method exports the Moves table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void exportMovesToCSV(String filename, int location) {
        DBoutput.exportMovesToCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method exports the Users table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void exportUsersToCSV(String filename, int location) {
        DBoutput.exportUsersToCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method exports the Requests table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void exportRequestsToCSV(String filename, int location) {
        DBoutput.exportRequestsToCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method exports the ConferenceRequests table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void exportConferenceRequestsToCSV(String filename, int location) {
        DBoutput.exportConferenceRequestsToCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method exports the FlowerRequests table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void exportFlowerRequestsToCSV(String filename, int location) {
        DBoutput.exportFlowerRequestsToCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method exports the FurnitureRequests table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void exportFurnitureRequestsToCSV(String filename, int location) {
        DBoutput.exportFurnitureRequestsToCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method exports the MealRequests table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void exportMealRequestsToCSV(String filename, int location) {
        DBoutput.exportMealRequestsToCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method exports the OfficeRequests table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void exportOfficeRequestsToCSV(String filename, int location) {
        DBoutput.exportOfficeRequestsToCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method exports the SanitationRequests table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program), 2 (custom location), or 3
     *                 (developer: CSV Files in package)
     */
    public void exportAlertsToCSV(String filename, int location) {
        DBoutput.exportAlertsToCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    /**
     * This method exports the Signage table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public void exportSignsToCSV(String filename, int location) {
        DBoutput.exportSignsToCSV(filename, location);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    //TODO Unorganized stuff below

    public void addEdge(Edge e) {
        edgeDAO.add(e);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteEdge(Edge e) {
        edgeDAO.delete(e);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void updateEdge(Edge e) {
        edgeDAO.update(e);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void addConferenceRequest(String[] cr) {
        conferenceRequestDAO.add(cr);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteConferenceRequest(ConferenceRequest cr) {
        conferenceRequestDAO.delete(cr);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteConferenceRequest(FullConferenceRequest cr) {
        conferenceRequestDAO.delete(cr);
    }

    public FullConferenceRequest getConferenceRequest(int id) {
        FullConferenceRequest fcr = (FullConferenceRequest) conferenceRequestDAO.get(id);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return fcr;
    }

    public ArrayList<FullConferenceRequest> getAllConferenceRequests() {
        ArrayList<FullConferenceRequest> fcr = conferenceRequestDAO.getAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return fcr;
    }

    public void updateConferenceRequest(ConferenceRequest cr) {
        conferenceRequestDAO.update(cr);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void updateConferenceRequest(FullConferenceRequest cr) {
        conferenceRequestDAO.update(cr);
    }

    public void addMealRequest(String[] mr) {
        mealRequestDAO.add(mr);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteMealRequest(MealRequest mr) {
        mealRequestDAO.delete(mr);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteMealRequest(FullMealRequest mr) {
        mealRequestDAO.delete(mr);
    }

    public FullMealRequest getMealRequest(int id) {
        FullMealRequest fmr = (FullMealRequest) mealRequestDAO.get(id);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return fmr;
    }

    public ArrayList<FullMealRequest> getAllMealRequests() {
        ArrayList<FullMealRequest> fmr = mealRequestDAO.getAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return fmr;
    }

    public void updateMealRequest(MealRequest mr) {
        mealRequestDAO.update(mr);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void updateMealRequest(FullMealRequest mr) {
        mealRequestDAO.update(mr);
    }

    public Object getRequest(int id) {
        Object o = requestDAO.get(id);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return o;
    }

    public ArrayList<Request> getAllRequests() {
        ArrayList<Request> r = requestDAO.getAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return r;
    }

    public void addLocationName(LocationName ln) {
        locationNameDAO.add(ln);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteLocationName(LocationName ln) {
        locationNameDAO.delete(ln);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public LocationName getLocationName(int id) {
        LocationName ln = (LocationName) locationNameDAO.get(id);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return ln;
    }

    public ArrayList<LocationName> getAllLocationNames() {
        ArrayList<LocationName> ln = locationNameDAO.getAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return ln;
    }

    public void updateLocationName(LocationName ln) {
        locationNameDAO.update(ln);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void updateExistingLocationName(LocationName ln, String longName) {
        locationNameDAO.updateExisting(ln, longName);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void addUser(User u) {
        userDAO.add(u);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteUser(User u) {
        userDAO.delete(u);
        requestDAO.updateRequestDeleteUser(u.getUsername());
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public User getUser(String id) {
        User u = (User) userDAO.get(id);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return u;
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> u = userDAO.getAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return u;
    }

    public void updateUser(User u) {
        userDAO.update(u);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void addNode(Node n) {
        nodeDAO.add(n);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteNode(Node n) {
        nodeDAO.delete(n);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void updateNode(Node n) {
        nodeDAO.update(n);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void addMove(Move m) {
        moveDAO.add(m);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteMove(Move m) {
        moveDAO.delete(m);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public Move getMove(int id) {
        Move m = (Move) moveDAO.get(id);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return m;
    }

    public ArrayList<Move> getAllMoves() {
        ArrayList<Move> m = moveDAO.getAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return m;
    }

    public void updateMove(Move m) {
        moveDAO.update(m);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void addFlowerRequest(String[] fr) {
        flowerRequestDAO.add(fr);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteFlowerRequest(FlowerRequest fr) {
        flowerRequestDAO.delete(fr);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteFlowerRequest(FullFlowerRequest fr) {
        flowerRequestDAO.delete(fr);
    }

    public FullFlowerRequest getFlowerRequest(int id) {
        FullFlowerRequest fcr = (FullFlowerRequest) flowerRequestDAO.get(id);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return fcr;
    }

    public ArrayList<FullFlowerRequest> getAllFlowerRequests() {
        ArrayList<FullFlowerRequest> fcr = flowerRequestDAO.getAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return fcr;
    }

    public void updateFlowerRequest(FlowerRequest fr) {
        flowerRequestDAO.update(fr);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public ArrayList<Integer> getNeighborsAsNodeIDs(int nodeID) {
        ArrayList<Integer> neighbors = nodeDAO.getNeighborsAsNodeIDs(nodeID);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return neighbors;
    }

    public void updateFlowerRequest(FullFlowerRequest fr) {
        flowerRequestDAO.update(fr);
    }

    public ArrayList<String> getAllLongNames() {
        ArrayList<String> longNames = locationNameDAO.getLongNamesAlphebeticalOrder();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return longNames;
    }

    public ArrayList<String> getAllShortNames() {
        ArrayList<String> shortNames = locationNameDAO.getShortNamesAlphebeticalOrder();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return shortNames;
    }

    public ArrayList<Node> getNodesByFloor(String floor) {
        ArrayList<Node> nodes = nodeDAO.getNodesFromFloor(floor);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return nodes;
    }

    public void addFurnitureRequest(String[] fr) {
        furnitureRequestDAO.add(fr);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteFurnitureRequest(FurnitureRequest fr) {
        furnitureRequestDAO.delete(fr);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteFurnitureRequest(FullFurnitureRequest fr) {
        furnitureRequestDAO.delete(fr);
    }

    public FullFurnitureRequest getFurnitureRequest(int id) {
        FullFurnitureRequest fcr = (FullFurnitureRequest) furnitureRequestDAO.get(id);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return fcr;
    }

    public ArrayList<FullFurnitureRequest> getAllFurnitureRequests() {
        ArrayList<FullFurnitureRequest> fcr = furnitureRequestDAO.getAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return fcr;
    }

    public void updateFurnitureRequest(FurnitureRequest fr) {
        furnitureRequestDAO.update(fr);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void updateFurnitureRequest(FullFurnitureRequest fr) {
        furnitureRequestDAO.update(fr);
    }

    public void addOfficeRequest(String[] or) {
        officeRequestDAO.add(or);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteOfficeRequest(OfficeRequest or) {
        officeRequestDAO.delete(or);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteOfficeRequest(FullOfficeRequest or) {
        officeRequestDAO.delete(or);
    }

    public FullOfficeRequest getOfficeRequest(int id) {
        FullOfficeRequest fcr = (FullOfficeRequest) officeRequestDAO.get(id);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return fcr;
    }

    public ArrayList<FullOfficeRequest> getAllOfficeRequests() {
        ArrayList<FullOfficeRequest> fcr = officeRequestDAO.getAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return fcr;
    }

    public Connection getConnection() {
        return dbConnection.getConnection();
    }

    public int getDatabaseServer() {
        return dbConnection.getDatabaseServer();
    }

    public void switchTo(int databaseServer) {
        dbConnection.switchTo(databaseServer);
        nodeDAO.setAll();
        edgeDAO.setAll();
        locationNameDAO.setAll();
        moveDAO.setAll();
        userDAO.setAll();
        requestDAO.setAll();
        conferenceRequestDAO.setAll();
        flowerRequestDAO.setAll();
        mealRequestDAO.setAll();
        furnitureRequestDAO.setAll();
        officeRequestDAO.setAll();
        alertDAO.setAll();
        signDAO.setAll();
        PathFinding.ASTAR.force_init();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public ArrayList<String> getNodeTypesUniqueAlphabetical () {
        ArrayList<String> nodeTypes = locationNameDAO.getNodeTypesUniqueAlphabetical();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return nodeTypes;
    }

    public void addFullNode (Object n) {
        FullNode.addFullNode(n);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteFullNode(Object n) {
        FullNode.deleteFullNode(n);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void updateFullNode(Object n) {
        FullNode.updateFullNode(n);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public ArrayList<IFull> getAllFullRequests() {
        ArrayList<IFull> requests = new ArrayList<>();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return requests;
    }

    public ArrayList<IFull> getAllFullRequestsByUser(String username) {
        ArrayList<IFull> requests = requestDAO.getFullRequestsbyEmployee(username);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return requests;
    }

    public ArrayList<IFull> getAllFullRequestsByStatus(String status) {
        ArrayList<IFull> requests = requestDAO.getFullRequestsbyStatus(status);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return requests;
    }

    public ArrayList<String> getLongNameByType(String type) {
        ArrayList<String> longNames = locationNameDAO.getLongNameByType(type);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return longNames;
    }

    public ArrayList<String> getPracticalLongNames() {
        ArrayList<String> longNames = locationNameDAO.getLongNamePractical();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return longNames;
    }

    public Alert getAlert(int id) {
        Alert a = alertDAO.get(id);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return a;
    }

    public ArrayList<Alert> getAllAlerts() {
        ArrayList<Alert> alerts = alertDAO.getAll();
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return alerts;
    }

    public void addAlert(Alert a) {
        alertDAO.add(a);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void deleteAlert(Alert a) {
        alertDAO.delete(a);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public void updateAlert(Alert a) {
        alertDAO.update(a);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
    }

    public ArrayList<Edge> getEdgesByNode(Node n) {
        ArrayList<Edge> edges = edgeDAO.getEdgesByNode(n);
        dbConnection.closeDBconnection();
        dbConnection.forceClose();
        return edges;
    }
}

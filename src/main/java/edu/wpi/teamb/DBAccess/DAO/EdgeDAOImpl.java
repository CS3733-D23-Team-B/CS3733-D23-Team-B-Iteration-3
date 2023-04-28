package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.DBconnection;
import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.ORMs.Edge;
import edu.wpi.teamb.DBAccess.ORMs.Node;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class EdgeDAOImpl implements IDAO {
    private static ArrayList<Edge> edges;

    static ArrayList<Edge> getEdges() {
        return edges;
    }

    public EdgeDAOImpl() {
        edges = getAllHelper();
    }

    /**
     * Gets an edge by its endpoints
     *
     * @param id The endpoints of the edge
     * @return The edge with the given endpoints
     */
    @Override
    public Edge get(Object id) {
        String endpoints = (String) id;
        String[] endpointsArray = endpoints.split("_");
        ResultSet rs = DButils.getRowCond("Edges", "*", "startnode = " + endpointsArray[0] + " AND endnode = " + endpointsArray[1]);
        try {
            if (rs.isBeforeFirst()) { // if there is something it found
                rs.next();
                for (Edge e : edges) {
                    if (e.endpoints == endpoints) {
                        return e;
                    }
                }
            } else
                try {
                    ResultSet rs1 = DButils.getRowCond("Edges", "*", "startnode = " + endpointsArray[1] + " AND endnode = " + endpointsArray[0]);
                    if (rs1.isBeforeFirst()) { // if there is something it found
                        rs1.next();
                        endpoints = endpointsArray[1] + "_" + endpointsArray[0];
                        for (Edge e : edges) {
                            if (e.endpoints == endpoints) {
                                return e;
                            }
                        }// make the edge
                    } else {
                        System.err.println("ERROR Query Failed in method 'EdgeDAOImpl.get': No edge found with endpoints " + endpoints);
                        return null;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'EdgeDAOImpl.get': " + e.getMessage());
            return null;
        }
        return null;
    }

    /**
     * Gets all local Edge objects
     *
     * @return an ArrayList of all local Edge objects
     */
    @Override
    public ArrayList<Edge> getAll() {
        return edges;
    }

    /**
     * Sets all Edge objects using the database
     */
    @Override
    public void setAll() { edges = getAllHelper(); }

    /**
     * Gets all edges from the database
     *
     * @return a list of all edges
     */
    public static ArrayList<Edge> getAllHelper() {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        try {
            ResultSet rs = DButils.getCol("Edges", "*");
            while (rs.next()) {
                edges.add(new Edge(rs));
            }
            return edges;
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'EdgeDAOImpl.getAllHelper': " + e.getMessage());
        }
        DBconnection.getDBconnection().closeDBconnection();
        DBconnection.getDBconnection().forceClose();
        return edges;
    }

    /**
     * Adds an Edge object to the both the database and local list
     *
     * @param edge the Edge object to be added
     */
    @Override
    public void add(Object edge) {
        Edge e = (Edge) edge;
        insertEdge(e.getStartNodeID(), e.getEndNodeID());
        edges.add(e);
    }

    /**
     * Removes an Edge from the both the database and the local list
     *
     * @param edge the Edge object to be removed
     */
    @Override
    public void delete(Object edge) {
        Edge e = (Edge) edge;
        deleteDBEdge(0, e);
        for (int i = 0; i < edges.size(); i++) {
            if (Objects.equals(edges.get(i).endpoints, e.endpoints)) {
                edges.remove(i);
            }
        }
//        edges.remove(edge);
    }

    /**
     * Updates an Edge object in both the database and local list
     *
     * @param edge the Edge object to be updated
     */
    @Override
    public void update(Object edge) {
        Edge e = (Edge) edge;
        updateDBEdge(e);
    }

    /**
     * Adds this edge to its start and end nodes. If the list of nodes does not
     * contain the start or end node, it will make them
     *
     * @param nodes the list of nodes to look through add the edge to the start and
     *              end nodes
     */
    public void addToNode(Map<Integer, Node> nodes, Edge e) {
        if (nodes.containsKey(e.getStartNodeID())) {
            Node node = nodes.get(e.getStartNodeID());
            //node.addEdge(this);
        } else {
            Node node = NodeDAOImpl.getNode(e.getStartNodeID());
            //node.addEdge(this);
            nodes.put(e.getStartNodeID(), node);
        }
        if (nodes.containsKey(e.getEndNodeID())) {
            Node node = nodes.get(e.getEndNodeID());
            //node.addEdge(this);
        } else {
            Node node = NodeDAOImpl.getNode(e.getEndNodeID());
            //node.addEdge(this);
            nodes.put(e.getEndNodeID(), node);
        }
    }

    /**
     * Gets the row(s) from the database that matches the startNode
     *
     * @param startNode the startNode to search for, make sure there are NO single
     *                  quotes surrounding the startNode
     * @return the row(s) that have a matching the startNode
     */
    public static ResultSet getDBRowStartNode(String startNode) {
        return getRowFromCol("startnode", "" + startNode + "");
    }

    /**
     * Gets the row(s) from the database that matches the endNode
     *
     * @param endNode the endNode to search for, make sure there are NO single
     *                quotes surrounding the endNode
     * @return the row(s) that have a matching the endNode
     */
    public static ResultSet getDBRowEndNode(String endNode) {
        return getRowFromCol("endNode", "" + endNode + "");
    }

    /**
     * Searches through the database for the row(s) that matches the col and value in the Edges table
     *
     * @param col   the column to search for
     * @param value the value to search for
     * @return A ResultSet of the row(s) that match the col and value
     */
    static ResultSet getRowFromCol(String col, String value) {
        if (col == null || value == null || col.equals("") || value.equals("")) {
            throw new IllegalArgumentException("col or value is null");
        }
        return DButils.getRowCond("edges", "*", col + " = " + value + "");
    }

    /**
     * Gets all the rows from the database
     */
    public ResultSet getDBRowAllEdges() {
        return DButils.getRowCond("edges", "*", "TRUE");
    }

    /**
     * Inserts an edge into the database
     *
     * @param startNode the startNode of the edge
     * @param endNode the endNode of the edge
     */
    public void insertEdge(int startNode, int endNode) {
        String[] cols = { "startnode", "endnode" };
        String[] values = {String.valueOf(startNode), String.valueOf(endNode)};
        DButils.insertRow("edges", cols, values);
    }

    /**
     * Updates the row in the database that matches the edgeID
     *
     * @param cols   the columns to update
     * @param values the values to update the columns to
     */
    void updateRow(String[] cols, String[] values, Edge e) {
        String[] endpointsA= e.endpoints.split("_");
        if (cols == null || values == null) {
            throw new IllegalArgumentException("col or value is null");
        }
        DButils.updateRow("edges", cols, values, "startnode = " + endpointsA[0] + "AND endnode = " + endpointsA[1]);
    }

    /**
     * Updates the startNode in the database
     *
     * @param startNode the new startNode
     */
    public void updateDBStartNode(String startNode, Edge e) {
        if (startNode == null || startNode.equals("")) {
            throw new IllegalArgumentException("startNode is null");
        }
        String[] cols = { "startnode" };
        String[] values = { "'" + startNode + "'" };
        updateRow(cols, values, e);
        e.endpoints = startNode + "_" + e.getEndNodeID();
        e.setEndNode(NodeDAOImpl.getNode(parseInt(startNode)));
    }

    /**
     * Updates the endNode in the database
     *
     * @param endNode the new endNode
     * @param e the Edge to update
     */
    public void updateDBEndNode(String endNode, Edge e) {
        if (endNode == null || endNode.equals("")) {
            throw new IllegalArgumentException("endNode is null");
        }
        String[] cols = { "endnode" };
        String[] values = { "'" + endNode + "'" };
        updateRow(cols, values, e);
        e.endpoints = e.getStartNodeID() + "_" + endNode;
        e.setEndNode(NodeDAOImpl.getNode(parseInt(endNode)));
    }

    /**
     * Updates all attributes in the database to match the edge
     */
    public void updateDBEdge(Edge e) {
        updateDBStartNode(""+ e.getStartNode(), e);
        updateDBEndNode(""+ e.getEndNode(), e);
    }

    /**
     * Deletes the row in the database that matches the edgeID
     * only use this if you are sure you want to delete the row
     *
     * @param confirm 0 to confirm delete, anything else to cancel
     * @param e the edge to delete
     */
    public void deleteDBEdge(int confirm, Edge e) {
        String[] endpointsA = e.endpoints.split("_");
        if (confirm == 0) {
            DButils.deleteRow("edges", "startnode = " + endpointsA[0] + " AND endnode = " + endpointsA[1]);
        } else {
            System.out.println("Delete not confirmed");
        }
    }

    /**
     * Gets the edge from the database that matches the edgeID
     *
     * @param endpoints a String of endpoints in the format "startNode_endNode"
     * @return the edge that matches the endpoints
     */
    public Edge getEdge(String endpoints) {
        String[] endpointsArray = endpoints.split("_");
        ResultSet rs = DButils.getRowCond("Edges", "*", "startnode = " + endpointsArray[0] + " AND endnode = " + endpointsArray[1]);
        try {
            if (rs.isBeforeFirst()) { // if there is something it found
                rs.next();
                return new Edge(Integer.parseInt(endpointsArray[0]), Integer.parseInt(endpointsArray[1])); // make the edge
            } else
                try {
                    ResultSet rs1 = DButils.getRowCond("Edges", "*", "startnode = " + endpointsArray[1] + " AND endnode = " + endpointsArray[0]);
                    if (rs1.isBeforeFirst()) { // if there is something it found
                        rs1.next();
                        return new Edge(Integer.parseInt(endpointsArray[1]), Integer.parseInt(endpointsArray[0])); // make the edge
                    } else {
                        System.err.println("ERROR: Edge not found in method 'EdgeDAOImpl.getEdge'");
                        return null;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'EdgeDAOImpl.getEdge': " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns the edge in a string
     *
     * @return the edge in a string with the format "[edgeID] [startNode] [endNode]"
     */
    public String toString(Edge e) {
        return e.getStartNodeID() + " " + e.getEndNodeID();
    }

    /**
     * Resets the edge table using the backup table
     */
    public void resetEdgesFromBackup() {

        String dropEdges = "DROP TABLE IF EXISTS Edges";

        Statement dropStatement = null;

        try {

            dropStatement = DBconnection.getDBconnection().getConnection().createStatement();
            dropStatement.executeUpdate(dropEdges);
            dropStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String recreateEdges =
                """
                CREATE TABLE edges(startNode INT, endNode INT, primary key (startNode, endNode));
                
                INSERT INTO edges SELECT * FROM edgeBackup;
                """;

        Statement recreateStatement = null;

        try {

            recreateStatement = DBconnection.getDBconnection().getConnection().createStatement();
            recreateStatement.executeUpdate(recreateEdges);
            recreateStatement.close();

        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'EdgeDAOImpl.resetEdgesFromBackup': " + e.getMessage());
        }
    }

    public ArrayList<Edge> getEdgesByNode(Node n) {
        ArrayList<Edge> edges = new ArrayList<>();
        for (Edge e : edges) {
            if (e.getStartNodeID() == n.getNodeID() || e.getEndNodeID() == n.getNodeID()) {
                edges.add(e);
            }
        } return edges;
    }

}

package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.ORMs.Edge;
import edu.wpi.teamb.DBAccess.ORMs.Node;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class EdgeDAOImpl implements IDAO {
    private static ArrayList<Edge> edges;

    static ArrayList<Edge> getEdges() {
        return edges;
    }

    public EdgeDAOImpl() throws SQLException {
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
        ResultSet rs = DButils.getRowCond("edges", "*", "startnode = " + endpointsArray[0] + " AND endnode = " + endpointsArray[1]);
        try {
            if (rs.isBeforeFirst()) { // if there is something it found
                rs.next();
                return new Edge(rs); // make the edge
            } else
                throw new SQLException("No rows found");
        } catch (SQLException e) {
            // handle error

            System.err.println("ERROR Query Failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets all edges
     *
     * @return A list of all edges
     */
    @Override
    public ArrayList<Edge> getAll() {
        return edges;
    }

    @Override
    public void setAll() { edges = getAllHelper(); }

    /**
     * Gets all edges from the database
     *
     * @return A list of all edges
     */
    public ArrayList<Edge> getAllHelper() {
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
        return edges;
    }

    /**
     * Adds an edge
     *
     * @param edge The edge to add
     */
    @Override
    public void add(Object edge) {
        Edge e = (Edge) edge;
        insertEdge(e.getStartNodeID(), e.getEndNodeID());
        edges.add(e);
    }

    /**
     * Deletes an edge
     *
     * @param edge The edge to delete
     */
    @Override
    public void delete(Object edge) {
        Edge e = (Edge) edge;
        deleteDBEdge(0, e);
        edges.remove(e);
    }

    /**
     * Updates an edge
     *
     * @param edge The edge to update
     */
    //TODO: figure out how to update an edge
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
     * Gets the row(s) from the database that matches the startnode
     *
     * @param startNode the startnode to search for, make sure there are NO single
     *                  quotes surrounding the startnode
     * @return the row(s) that have a matching the startnode
     */
    public static ResultSet getDBRowStartNode(String startNode) {

        return getRowFromCol("startnode", "" + startNode + "");
    }

    /**
     * Gets the row(s) from the database that matches the endnode
     *
     * @param endNode the endnode to search for, make sure there are NO single
     *                quotes surrounding the endnode
     * @return the row(s) that have a matching the endnode
     */
    public static ResultSet getDBRowEndNode(String endNode) {
        return getRowFromCol("endnode", "" + endNode + "");
    }

    /**
     * Searches through the database for the row(s) that matches the col and value
     *
     * @param col   the column to search for
     * @param value the value to search for
     * @return A ResultSet of the row(s) that match the col and value
     */
    private static ResultSet getRowFromCol(String col, String value) {
        if (col == null || value == null || col.equals("") || value.equals("")) {
            throw new IllegalArgumentException("col or value is null");
        }
        return DButils.getRowCond("edges", "*", col + " = " + value + "");
    }

    /**
     * gets all the rows from the database
     */
    public ResultSet getDBRowAllEdges() {
        return DButils.getRowCond("edges", "*", "TRUE");
    }

    /**
     * inserts an edge into the database
     * @param startNode the startnode of the edge
     * @param endNode the endnode of the edge
     */
    public void insertEdge(int startNode, int endNode) {
        String[] cols = { "startnode", "endnode" };
        String[] values = { "'" + startNode + "'", "'" + endNode + "'" };
        DButils.insertRow("edges", cols, values);
    }

    /**
     * Updates the row in the database that matches the edgeid
     *
     * @param cols   the columns to update
     * @param values the values to update the columns to
     */
    private void updateRow(String[] cols, String[] values, Edge e) {
        String[] endpointsA= e.endpoints.split("_");
        if (cols == null || values == null) {
            throw new IllegalArgumentException("col or value is null");
        }
        DButils.updateRow("edges", cols, values, "startnode = " + endpointsA[0] + "AND endnode = " + endpointsA[1]);
    }

    // /**
    // * Updates the edgeid in the database
    // *
    // * @param edgeID the new edgeid
    // */
    // public void updateDBEdgeID(String edgeID) {
    // if (edgeID == null || edgeID.equals("")) {
    // throw new IllegalArgumentException("edgeID is null or empty");
    // }
    // String[] cols = { "edgeid" };
    // String[] values = { "'" + edgeID + "'" };
    // updateRow(cols, values);
    // }

    /**
     * Updates the startnode in the database
     *
     * @param startNode the new startnode
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
     * Updates the endnode in the database
     *
     * @param endNode the new endnode
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
     * Deletes the row in the database that matches the edgeid
     * only use this if you are sure you want to delete the row
     *
     * @param confirm 0 to confirm delete, anything else to cancel
     */
    public void deleteDBEdge(int confirm, Edge e) {
        String[] endpointsA = e.endpoints.split("_");
        if (confirm == 0) {
            DButils.deleteRow("edges", "startnode = " + endpointsA[0] + "AND endnode = " + endpointsA[1]);
        } else {
            System.out.println("Delete not confirmed");
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

}

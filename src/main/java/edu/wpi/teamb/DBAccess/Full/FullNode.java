package edu.wpi.teamb.DBAccess.Full;

import edu.wpi.teamb.DBAccess.ORMs.LocationName;
import edu.wpi.teamb.DBAccess.ORMs.Move;
import edu.wpi.teamb.DBAccess.ORMs.Node;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FullNode {
    int nodeID;
    int xCoord;
    int yCoord;
    String floor;
    String building;
    String longName;
    String shortName;
    String nodeType;

    public FullNode(int nodeID, int xCoord, int yCoord, String floor, String building, String longName, String shortName, String nodeType) {
        this.nodeID = nodeID;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.floor = floor;
        this.building = building;
        this.longName = longName;
        this.shortName = shortName;
        this.nodeType = nodeType;
    }

    public FullNode(Node n, Move m, LocationName l) {
        this.nodeID = n.getNodeID();
        this.xCoord = n.getxCoord();
        this.yCoord = n.getyCoord();
        this.floor = n.getFloor();
        this.building = n.getBuilding();
        this.longName = m.getLongName();
        this.shortName = l.getShortName();
        this.nodeType = l.getNodeType();
    }

    public FullNode(ResultSet rs) throws SQLException {
        this(
                rs.getInt("nodeid"),
                rs.getInt("xcoord"),
                rs.getInt("ycoord"),
                rs.getString("floor"),
                rs.getString("building"),
                rs.getString("longname"),
                rs.getString("shortname"),
                rs.getString("nodetype")
        );
    }

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public int getxCoord() {
        return xCoord;
    }

    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}


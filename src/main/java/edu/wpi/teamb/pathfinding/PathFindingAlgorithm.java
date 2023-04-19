package edu.wpi.teamb.pathfinding;

import edu.wpi.teamb.DBAccess.ORMs.Node;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public interface PathFindingAlgorithm {
    public ArrayList<Integer> findPath(int start, int goal) throws SQLException;
    public void init_pathfinder() throws SQLException;
    public HashMap<Integer, Node> get_node_map();

    public String[] getPathAsStrings(ArrayList<Integer> shortestPath);
    public void force_init() throws SQLException;
}


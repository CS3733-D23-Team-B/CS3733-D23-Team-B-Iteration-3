package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.DBconnection;
import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.ORMs.Move;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MoveDAOImpl implements IDAO {

    static ArrayList<Move> moves;

    public MoveDAOImpl() {
        moves = getAllHelper();
    }

    /**
     * Gets a Move by its nodeID
     *
     * @param id the nodeID of the Move
     * @return the Move with the given nodeID
     */
    @Override
    public Move get(Object id) {
        ResultSet rs = DButils.getRowCond("Moves", "*", "nodeID = " + id + "");
        try {
            if (rs.isBeforeFirst()) {
                rs.next();
                return new Move(rs);
            } else
                throw new SQLException("No rows found");
        } catch (SQLException e) {
            // handle error

            System.err.println("ERROR Query Failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets all local Move objects
     *
     * @return an ArrayList of all local Move objects
     */
    @Override
    public ArrayList<Move> getAll() {
        return moves;
    }

    /**
     * Sets all Move objects using the database
     */
    @Override
    public void setAll() { moves = getAllHelper(); }

    /**
     * Gets all moves from the database
     *
     * @return a list of all moves
     */
    public ArrayList<Move> getAllHelper() {
        ArrayList<Move> mvs = new ArrayList<Move>();
        try {
            ResultSet rs = DButils.getCol("moves", "*");
            while (rs.next()) {
                mvs.add(new Move(rs));
            }
            return mvs;
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'MoveDAOImpl.getAllHelper': " + e.getMessage());
        }
        DBconnection.getDBconnection().closeDBconnection();
        DBconnection.getDBconnection().forceClose();
        return mvs;
    }

    /**
     * Adds a Move object to the both the database and local list
     *
     * @param m the LocationName object to be added
     */
    @Override
    public void add(Object m) {
        Move move = (Move) m;
        insertDBMove(move);
        moves.add(move);
    }

    /**
     * Removes a Move from the both the database and the local list
     *
     * @param m the LocationName object to be removed
     */
    @Override
    public void delete(Object m) {
        Move move = (Move) m;
        DButils.deleteRow("Moves", "nodeID = " + move.getNodeID()+" AND longName = '"+move.getLongName()+"'"+ " AND date = '" + move.getDate() + "'");
        moves.remove(move);
    }

    /**
     * Updates a Move object in both the database and local list
     *
     * @param m the LocationName object to be updated
     */
    @Override
    public void update(Object m) {
        Move move = (Move) m;
        String[] cols = {"nodeID", "longName", "date"};
        String[] vals = {Integer.toString(move.getNodeID()), move.getLongName(), move.getDate().toString()};
        DButils.updateRow("Moves", cols, vals, "nodeID = " + move.getNodeID());
        for (int i = 0; i < moves.size(); i++) {
            if (moves.get(i).getNodeID() == (move.getNodeID())) {
                moves.set(i, move);
            }
        }
    }

    /**
     * Gets a Move by its longName
     *
     * @param longName the longName of the Move
     * @return the Move with the given longName
     */
    public static Move getMoveFromLongName(String longName) {
        ResultSet rs = DButils.getRowCond("Moves", "*", "longName = '" + longName + "'");
        try {
            if (rs.isBeforeFirst()) {
                rs.next();
                return new Move(rs);
            } else
                throw new SQLException("No rows found");
        } catch (SQLException e) {
            // handle error
            System.err.println("ERROR Query Failed in method 'MoveDAOImpl.getMoveFromLongName': " + e.getMessage());
            return null;
        }
    }

    /**
     * Searches through the database for the row(s) that matches the col and value in the Moves table
     *
     * @param col   the column to search for
     * @param value the value to search for
     * @return A ResultSet of the row(s) that match the col and value
     */
    private ResultSet getDBRowFromCol(String col, String value) {
        return DButils.getRowCond("Moves", "*", col + " = " + value);
    }

    /**
     * Gets a ResultSet of all rows from the Moves table
     *
     * @return a ResultSet of all rows from the Moves table
     */
    public ResultSet getDBRowAllMoves() {
        return DButils.getRowCond("moves", "*", "TRUE");
    }

    /**
     * Gets a ResultSet of rows from the Moves table that match the given nodeID
     *
     * @param nodeID the node to look for to get Move data
     */
    public ResultSet getDBRowNodeID(int nodeID) {
        return getDBRowFromCol("nodeID", "" + nodeID + "");
    }

    /**
     * Gets a ResultSet of rows from the Moves table that match the given longName
     *
     * @param longName the longName to look for to get Move data
     * @return a ResultSet of rows from the Moves table that match the given longName
     */
    public ResultSet getDBRowLongName(String longName) {
        return getDBRowFromCol("longName", "'" + longName + "'");
    }

    /**
     * Gets a ResultSet of rows from the Moves table that match the given date
     *
     * @param date the date to look for to get Move data
     * @return a ResultSet of rows from the Moves table that match the given date
     */
    public ResultSet getDBRowDate(Date date) {
        return getDBRowFromCol("date", "" + date + "");
    }

    /**
     * Inserts a Move into the Moves table
     *
     * @param m the Move to be inserted
     */
    public void insertDBMove(Move m) {
        String[] cols = {"nodeID", "longName", "date"};
        String[] vals = {Integer.toString(m.getNodeID()), m.getLongName(), m.getDate().toString()};
        DButils.insertRow("Moves", cols, vals);
    }

    /**
     * Gets a Move from the database
     *
     * @param id the nodeID of the Move
     * @return the Move with the given nodeID
     */
    public Move getMove(int id) {
        ResultSet rs = DButils.getRowCond("Moves", "*", "nodeID = " + id + "");
        try {
            if (rs.isBeforeFirst()) {
                rs.next();
                return new Move(rs);
            } else
                throw new SQLException("No rows found");
        } catch (SQLException e) {
            // handle error

            System.err.println("ERROR Query Failed in method 'MoveDAOImpl.getMove': " + e.getMessage());
            return null;
        }
    }
}

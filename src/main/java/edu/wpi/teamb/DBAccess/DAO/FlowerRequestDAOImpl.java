package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.DBconnection;
import edu.wpi.teamb.DBAccess.Full.FullFactory;
import edu.wpi.teamb.DBAccess.Full.FullFlowerRequest;
import edu.wpi.teamb.DBAccess.Full.IFull;
import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.ORMs.FlowerRequest;
import edu.wpi.teamb.DBAccess.ORMs.Request;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class FlowerRequestDAOImpl implements IDAO {

    ArrayList<FullFlowerRequest> flowerRequests;

    public FlowerRequestDAOImpl() {
        flowerRequests = getAllHelper();
    }

    /**
     * Gets all local FlowerRequest objects
     *
     * @return an ArrayList of all local FlowerRequest objects
     */
    @Override
    public ArrayList<FullFlowerRequest> getAll() {
        return flowerRequests;
    }

    /**
     * Sets all FlowerRequest objects using the database
     */
    @Override
    public void setAll() {
        flowerRequests = getAllHelper();
    }

    /**
     * Gets a FullFlowerRequest object from the database
     *
     * @param id of the FlowerRequest object
     * @return a FullFlowerRequest object with information from request and flower request tables
     */
    @Override
    public FullFlowerRequest get(Object id) {
        Integer idInt = (Integer) id;
        FlowerRequest fr = null;
        Request r = null;
        try {
            ResultSet rs = DButils.getRowCond("flowerrequests", "*", "id = " + idInt);
            rs.next();
            fr = new FlowerRequest(rs);
            ResultSet rs1 = RequestDAOImpl.getDBRowID(idInt);
            rs1.next();
            r = new Request(rs1);
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'FlowerRequestDAOImpl.get': " + e.getMessage());
            return null;
        }
        return new FullFlowerRequest(r, fr);
    }

    /**
     * Gets all FlowerRequest objects from the database
     *
     * @return an ArrayList of all FlowerRequest objects
     */
    public ArrayList<FullFlowerRequest> getAllHelper() {
        FullFactory ff = new FullFactory();
        IFull flower = ff.getFullRequest("Flower");
        ArrayList<FlowerRequest> frs = new ArrayList<FlowerRequest>();
        try {
            ResultSet rs = getDBRowAllRequests();
            while (rs.next()) {
                frs.add(new FlowerRequest(rs));
            }
            return (ArrayList<FullFlowerRequest>) flower.listFullRequests(frs);
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'FlowerRequestDAOImpl.getAllHelper': " + e.getMessage());
        }
        DBconnection.getDBconnection().closeDBconnection();
        DBconnection.getDBconnection().forceClose();
        return (ArrayList<FullFlowerRequest>) flower.listFullRequests(frs);
    }

    /**
     * Adds a FlowerRequest object to the both the database and local list
     *
     * @param request the FlowerRequest object to be added
     */
    @Override
    public void add(Object request) {
        String[] flowerReq = (String[]) request;

        String[] values = {flowerReq[0], flowerReq[1], "Flower", flowerReq[2], flowerReq[3], flowerReq[4], flowerReq[5], flowerReq[6], flowerReq[7]};
        int id = insertDBRowNewFlowerRequest(values);
        ResultSet rs = DButils.getRowCond("requests", "dateSubmitted", "id = " + id);
        Timestamp dateSubmitted = null;
        try {
            rs.next();
            dateSubmitted = rs.getTimestamp("dateSubmitted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        flowerRequests.add(new FullFlowerRequest(id, flowerReq[0], dateSubmitted, flowerReq[1], flowerReq[2], flowerReq[3], flowerReq[4], flowerReq[5], flowerReq[6], flowerReq[7]));
        RequestDAOImpl.getRequestDaoImpl().getAll().add(new Request(id, flowerReq[0], dateSubmitted, flowerReq[1], "Flower", flowerReq[2], flowerReq[3]));
    }

    /**
     * Removes a FlowerRequest from both the database and local list
     *
     * @param request the FlowerRequest object to be removed
     */
    @Override
    public void delete(Object request) {
        FullFlowerRequest ffr = (FullFlowerRequest) request;
        DButils.deleteRow("flowerrequests", "id =" + ffr.getId() + "");
        DButils.deleteRow("requests", "id =" + ffr.getId() + "");
        flowerRequests.remove(ffr);
        Request req = new Request(ffr.getId(), ffr.getEmployee(), ffr.getDateSubmitted(), ffr.getRequestStatus(), ffr.getRequestType(), ffr.getLocationName(), ffr.getNotes());
        RequestDAOImpl.getRequestDaoImpl().getAll().remove(req);
    }

    /**
     * Updates a FlowerRequest object in both the database and the local list
     *
     * @param request the FlowerRequest object to be updated
     */
    @Override
    public void update(Object request) {
        FullFlowerRequest ffr = (FullFlowerRequest) request;
        String[] colsFlower = {"flowertype", "color", "size", "message"};
        String[] valuesFlower = {ffr.getFlowerType(), ffr.getColor(), ffr.getSize(), ffr.getMessage()};
        String[] colsReq = {"employee", "datesubmitted", "requeststatus", "requesttype", "locationname", "notes"};
        String[] valuesReq = {ffr.getEmployee(), String.valueOf(ffr.getDateSubmitted()), ffr.getRequestStatus(), ffr.getRequestType(), ffr.getLocationName(), ffr.getNotes()};
        DButils.updateRow("flowerrequests", colsFlower, valuesFlower, "id = " + ffr.getId());
        DButils.updateRow("requests", colsReq, valuesReq, "id = " + ffr.getId());
        for (int i = 0; i < flowerRequests.size(); i++) {
            if (flowerRequests.get(i).getId() == ffr.getId()) {
                flowerRequests.set(i, ffr);
            }
        }
        Request req = new Request(ffr.getId(), ffr.getEmployee(), ffr.getDateSubmitted(), ffr.getRequestStatus(), ffr.getRequestType(), ffr.getLocationName(), ffr.getNotes());
        RequestDAOImpl.getRequestDaoImpl().update(req);
    }

    /**
     * Gets all rows from the database of flower requests
     *
     * @return the result set of the row(s) that matches the given column and value
     */
    private ResultSet getDBRowAllRequests() {
        return DButils.getCol("FlowerRequests", "*");
    }

    /**
     * Updates the special instructions of a row in the FlowerRequests table
     *
     * @param specialInstructions the specialInstructions to update
     * @param col                 the column to search for the value
     * @param val                 the value to search for in the column
     */
    public void updateRowSpecialInstructions(String specialInstructions, String[] col, String[] val) {
        updateRows(col, val, "specialInstructions = " + specialInstructions);
    }

    /**
     * Returns a String representation of a FlowerRequest
     *
     * @param request the FlowerRequest to turn into a String
     * @return the String representation of the FlowerRequest
     */
    public String toString(FlowerRequest request) {
        return request.getId() + " " + request.getFlowerType() + " " + request.getColor() + " " + request.getSize() + " " + request.getMessage();
    }

    /**
     * Inserts a new row into the FlowerRequests table
     *
     * @param values the values of the FlowerRequest you want to add
     * @return an int representing the id of the new row
     */
    public static int insertDBRowNewFlowerRequest(String[] values) {
        String[] colsFlower = {"id", "flowerType", "color", "size", "message"};
        String[] colsReq = {"employee", "requeststatus", "requesttype", "locationname", "notes"};
        String[] valuesReq = {values[0], values[1], values[2], values[3], values[4]};
        int id = DButils.insertRowRequests("requests", colsReq, valuesReq);
        String[] valuesFlower = {Integer.toString(id), values[5], values[6], values[7], values[8]};
        DButils.insertRow("flowerrequests", colsFlower, valuesFlower);
        return id;
    }

    /**
     * Returns a ResultSet of the row(s) that matches the given column and value in the FlowerRequests table
     *
     * @param col   the column to search for the value
     * @param value the value to search for in the column
     * @return a ResultSet of the row(s) that matches the given column and value
     */
    private ResultSet getDBRowFromCol(String col, String value) {
        return DButils.getRowCond("FlowerRequests", "*", col + " = " + value);
    }

    /**
     * Returns a ResultSet of the row(s) that matches the given ID in the FlowerRequests table
     *
     * @param id the id of the row to get
     * @return a ResultSet of the row(s) that matches the given ID
     */
    public ResultSet getDBRowID(int id) {
        return getDBRowFromCol("id", Integer.toString(id));
    }

    /**
     * Returns a ResultSet of the row(s) that matches the given flowerType
     *
     * @param flowerType the flowerType of the row to get
     * @return a ResultSet of the row(s) that matches the given flowerType
     */
    public ResultSet getDBRowFlowerType(String flowerType) {
        return getDBRowFromCol("flowerType", flowerType);
    }

    /**
     * Returns a ResultSet of the row(s) that matches the given color
     *
     * @param color the color of the row to get
     * @return a ResultSet of the row(s) that matches the given color
     */
    public ResultSet getDBRowColor(String color) {
        return getDBRowFromCol("color", color);
    }

    public ResultSet getDBRowSize(String size) {
        return getDBRowFromCol("size", size);
    }

        /**
         * Returns a ResultSet of the row(s) that matches the given type
         *
         * @param type the type of the row to get
         * @return a ResultSet of the row(s) that matches the given type
         */
        public ResultSet getDBRowType (String type){
            return getDBRowFromCol("type", type);
        }

        /**
         * Returns a ResultSet of the row(s) that matches the given message
         *
         * @param message the message of the row to get
         * @return a ResultSet of the row(s) that matches the given message
         */
        public ResultSet getDBRowMessage (String message){
            return getDBRowFromCol("message", message);
        }

        /**
         * Returns a ResultSet of the row(s) that matches the given specialInstructions
         *
         * @param specialInstructions the specialInstructions of the row to get
         * @return a ResultSet of the row(s) that matches the given specialInstructions
         */
        public ResultSet getDBRowSpecialInstructions (String specialInstructions){
            return getDBRowFromCol("specialInstructions", specialInstructions);
        }

        /**
         * Updates the rows in the FlowerRequests table that match the given condition
         *
         * @param col the columns to update
         * @param val the values to update the columns to
         * @param cond the condition to search for
         */
        private void updateRows (String[]col, String[]val, String cond){
            if (col.length != val.length)
                throw new IllegalArgumentException("Column and value arrays must be the same length");
            DButils.updateRow("FlowerRequests", col, val, cond);
        }

        /**
         * Updates the rows in the FlowerRequests table that match the given ID
         *
         * @param id the id of the row to update
         * @param col the columns to update
         * @param val the values to update the columns to
         */
        public void updateRowID ( int id, String[] col, String[]val){
            updateRows(col, val, "id = " + id);
        }

        /**
         * Updates the rows in the FlowerRequests table that match the given flowerType
         *
         * @param flowerType the flowerType of the row to update
         * @param col the columns to update
         * @param val the values to update the columns to
         */
        public void updateRowFlowerType (String flowerType, String[]col, String[]val){
            updateRows(col, val, "flowerType = " + flowerType);
        }

        /**
         * Updates the rows in the FlowerRequests table that match the given color
         *
         * @param color the color of the row to update
         * @param col the columns to update
         * @param val the values to update the columns to
         */
        public void updateRowColor (String color, String[]col, String[]val){
            updateRows(col, val, "color = " + color);
        }

        /**
         * Updates the rows in the FlowerRequests table that match the given size
         *
         * @param size the type of the row to update
         * @param col the columns to update
         * @param val the values to update the columns to
         */
        public void updateRowSize (String size, String[]col, String[]val){
            updateRows(col, val, "size = " + size);
        }

        /**
         * Updates the rows in the FlowerRequests table that match the given message
         *
         * @param message the message of the row to update
         * @param col the columns to update
         * @param val the values to update the columns to
         */
        public void updateRowMessage (String message, String[]col, String[]val){
            updateRows(col, val, "message = " + message);
        }
    }

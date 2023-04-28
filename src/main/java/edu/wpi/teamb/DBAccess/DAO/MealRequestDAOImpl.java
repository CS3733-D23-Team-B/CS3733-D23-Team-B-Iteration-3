package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.DBconnection;
import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.Full.FullFactory;
import edu.wpi.teamb.DBAccess.Full.FullMealRequest;
import edu.wpi.teamb.DBAccess.Full.FullOfficeRequest;
import edu.wpi.teamb.DBAccess.Full.IFull;
import edu.wpi.teamb.DBAccess.ORMs.MealRequest;
import edu.wpi.teamb.DBAccess.ORMs.Request;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MealRequestDAOImpl implements IDAO {

    ArrayList<FullMealRequest> mealRequests;

    public MealRequestDAOImpl() {
        mealRequests = getAllHelper();
    }

    /**
     * Gets a FullMealRequest object from the database
     *
     * @param id of the MealRequest object
     * @return a FullMealRequest object with information from request and meal request tables
     */
    @Override
    public FullMealRequest get(Object id) {
        int idInt = (Integer) id;
        MealRequest mr = null;
        Request r = null;
        try {
            ResultSet rs = DButils.getRowCond("mealrequests", "*", "id = " + idInt);
            rs.next();
            mr = new MealRequest(rs);
            ResultSet rs1 = RequestDAOImpl.getDBRowID(idInt);
            rs1.next();
            r = new Request(rs1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new FullMealRequest(r, mr);
    }

    /**
     * Gets all local MealRequest objects
     *
     * @return an ArrayList of all local MealRequest objects
     */
    @Override
    public ArrayList<FullMealRequest> getAll() {
        return mealRequests;
    }

    /**
     * Sets all MealRequest objects using the database
     */
    @Override
    public void setAll() { mealRequests = getAllHelper(); }

    /**
     * Gets all MealRequest objects from the database
     *
     * @return an ArrayList of all MealRequest objects
     */
    public ArrayList<FullMealRequest> getAllHelper() {
        FullFactory ff = new FullFactory();
        IFull mealRequest = ff.getFullRequest("Meal");
        ArrayList<MealRequest> mrs = new ArrayList<MealRequest>();
        try {
            ResultSet rs = getDBRowAllRequests();
            while (rs.next()) {
                mrs.add(new MealRequest(rs));
            }
            return (ArrayList<FullMealRequest>) mealRequest.listFullRequests(mrs);
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'MealRequestDAOImpl.getAllHelper': " + e.getMessage());
        }
        DBconnection.getDBconnection().closeDBconnection();
        DBconnection.getDBconnection().forceClose();
        return (ArrayList<FullMealRequest>) mealRequest.listFullRequests(mrs);
    }

    /**
     * Adds a MealRequest object to the both the database and local list
     *
     * @param request the MealRequest object to be added
     */
    @Override
    public void add(Object request) {
        String[] mealReq = (String[]) request;
        String[] values = {mealReq[0], mealReq[1], "Meal", mealReq[2], mealReq[3], mealReq[4], mealReq[5], mealReq[6], mealReq[7]};
        int id = insertDBRowNewMealRequest(values);
        ResultSet rs = DButils.getRowCond("requests", "datesubmitted", "id = " + id);
        Timestamp dateSubmitted = null;
        try {
            rs.next();
            dateSubmitted = rs.getTimestamp("datesubmitted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mealRequests.add(new FullMealRequest(id, mealReq[0], dateSubmitted, mealReq[1], mealReq[2], mealReq[3], mealReq[4], mealReq[5], mealReq[6], mealReq[7]));
        RequestDAOImpl.getRequestDaoImpl().getAll().add(new Request(id, mealReq[0], dateSubmitted, mealReq[1], "Meal", mealReq[2], mealReq[3]));
    }

    /**
     * Removes a MealRequest from the both the database and the local list
     *
     * @param request the MealRequest object to be removed
     */
    @Override
    public void delete(Object request) {
        FullMealRequest fmr = (FullMealRequest) request;
        DButils.deleteRow("mealrequests", "id =" + fmr.getId() + "");
        DButils.deleteRow("requests", "id =" + fmr.getId() + "");
        mealRequests.remove(fmr);
        Request r = new Request(fmr.getId(), fmr.getEmployee(), fmr.getDateSubmitted(), fmr.getRequestStatus(), fmr.getRequestType(), fmr.getLocationName(), fmr.getNotes());
        RequestDAOImpl.getRequestDaoImpl().getAll().remove(r);
    }

    /**
     * Updates a MealRequest object in both the database and the local list
     *
     * @param request the MealRequest object to be updated
     */
    @Override
    public void update(Object request) {
        FullMealRequest fmr = (FullMealRequest) request;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String[] values = {
//                Integer.toString(fmr.getId()), fmr.getEmployee(), fmr.getDateSubmitted(), fmr.getRequestStatus(), "Meal", fmr.getOrderFrom(), fmr.getFood(), fmr.getDrink(), fmr.getSnack(), fmr.getMealModification()};
        String[] colsMeal = {"orderfrom", "food", "drink", "snack"};
        String[] valuesMeal = {fmr.getOrderFrom(), fmr.getFood(), fmr.getDrink(), fmr.getSnack()};
        String[] colsReq = {"employee", "datesubmitted", "requeststatus", "requesttype", "locationname", "notes"};
        String[] valuesReq = {fmr.getEmployee(), dateFormat.format(fmr.getDateSubmitted()), fmr.getRequestStatus(), fmr.getRequestType(), fmr.getLocationName(), fmr.getNotes()};
        DButils.updateRow("mealrequests", colsMeal, valuesMeal, "id = " + fmr.getId());
        DButils.updateRow("requests", colsReq, valuesReq, "id = " + fmr.getId());
        for (int i = 0; i < mealRequests.size(); i++) {
            if (mealRequests.get(i).getId() == fmr.getId()) {
                mealRequests.set(i, fmr);
            }
        }
        Request r = new Request(fmr.getId(), fmr.getEmployee(), fmr.getDateSubmitted(), fmr.getRequestStatus(), fmr.getRequestType(), fmr.getLocationName(), fmr.getNotes());
        RequestDAOImpl.getRequestDaoImpl().update(r);
    }
    //Insert into Database Methods

    /**
     * Inserts a new row into the MealRequests table
     *
     * @param values the values of the MealRequest you want to add
     * @return an int representing the id of the new row
     */
    public static int insertDBRowNewMealRequest(String[] values) {
        String[] colMeal = {"id","orderfrom", "food", "drink", "snack"};
        String[] colRequest = {"employee", "requeststatus", "requesttype", "location_name", "notes"};
        String[] valuesReq = {values[0], values[1], values[2], values[3], values[4]};
        int id = DButils.insertRowRequests("requests", colRequest, valuesReq);
        String[] valuesMeal = {Integer.toString(id), values[5], values[6], values[7], values[8]};
        DButils.insertRow("mealrequests", colMeal, valuesMeal);
        return id;
    }


    // Access from Database Methods

    // Methods to get information about the meal request from the database

    /**
     * Returns a ResultSet of the row(s) that matches the given column and value in the MealRequests table
     *
     * @param col the column to search for the value
     * @param value the value to search for in the column
     * @return a ResultSet of the row(s) that matches the given column and value
     */
    private ResultSet getDBRowFromCol(String col, String value) {
        return DButils.getRowCond("MealRequests", "*", col + " = " + value);
    }

    /**
     * Gets all rows from the database of meal requests
     *
     * @return the result set of the row(s) that matches the given column and value
     */
    private ResultSet getDBRowAllRequests() {
        return DButils.getCol("MealRequests", "*");
    }

    /**
     * Gets the row from the database that matches the given id
     *
     * @param id the id of the mealRequest to search for
     * @return the result set of the row that matches the given id
     */
    public ResultSet getDBRowID(int id) {
        return getDBRowFromCol("id", Integer.toString(id));
    }

    /**
     * Gets the row(s) from the database that matches the given order from
     *
     * @param orderFrom the location that the order was made from
     * @return the result set of the row that matches the given orderfrom
     */
    public ResultSet getDBRowOrderFrom(String orderFrom) {
        return getDBRowFromCol("orderfrom",  "'" + orderFrom + "'");
    }

    /**
     * Gets the row(s) from the database that matches the given food
     *
     * @param food the food that is being ordered
     * @return the result set of the row that matches the given food
     */
    public ResultSet getDBRowFood(String food) {
        return getDBRowFromCol("food",  "'" + food  + "'");
    }

    /**
     * Gets the row(s) from the database that matches the given drink
     *
     * @param drink the drink that is being ordered
     * @return the result set of the row that matches the given drink
     */
    public ResultSet getDBRowDrink(String drink) {
        return getDBRowFromCol("drink",  "'" + drink  + "'");
    }

    /**
     * Gets the row(s) from the database that matches the given snack
     *
     * @param snack the snack that is being ordered
     * @return the result set of the row that matches the given snack
     */
    public ResultSet getDBRowSnack(String snack) {
        return getDBRowFromCol("snack",  "'" + snack  + "'");
    }

    // Method to Update the Database

    /**
     * Updates the database with the information in this MealRequest object
     *
     * @param col   the columns to update
     * @param value the values to update
     */
    private void updateRows(String[] col, String[] value, String condition) {
        if (col.length == value.length) {
            throw new IllegalArgumentException("The column and value arrays must be the same length");
        }
        DButils.updateRow("MealRequests", col, value, condition);
    }

    /**
     * Update the order from  in the database
     *
     * @param oldOrderFrom the old employee
     * @param newOrderFrom the new employee
     *
     */
    public void updateEmployee(String oldOrderFrom, String newOrderFrom) {
        String[] col = { "employee" };
        String[] value = { "'" + newOrderFrom + "'" };
        updateRows(col, value, "employee = '" + oldOrderFrom + "'");
    }

    // list information about this Request object
    /**
     * Returns a string of all the information about the MealRequest
     *
     * @return String of all the information about the MealRequest
     */
    public String toString(MealRequest mr) {
        return "ID: " + mr.getId() + "\tOrder From: " + mr.getOrderFrom() + "\tFood: " + mr.getFood() + "\tDrink: " + mr.getDrink() + "\tSnack: " + mr.getSnack();
    }

    /**
     * Gets a MealRequest object from the database
     *
     * @param id of the MealRequest object
     * @return a MealRequest object with information from the MealRequests table
     */
    public static MealRequest getMealRequest(int id) {
        ResultSet rs = DButils.getRowCond("MealRequests", "*", "id = '" + id + "'");
        try {
            if (rs.isBeforeFirst()) {
                rs.next();
                return new MealRequest(rs);
            } else
                throw new SQLException("Error in method 'MealRequestDAOImpl.getMealRequest': No rows found");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.FullMealRequest;
import edu.wpi.teamb.DBAccess.FullOfficeRequest;
import edu.wpi.teamb.DBAccess.ORMs.MealRequest;
import edu.wpi.teamb.DBAccess.ORMs.Request;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MealRequestDAOImpl implements IDAO {

    ArrayList<FullMealRequest> mealRequests;

    public MealRequestDAOImpl() throws SQLException {
        mealRequests = getAllHelper();
    }
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

    @Override
    public ArrayList<FullMealRequest> getAll() {
        return mealRequests;
    }

    @Override
    public void setAll() { mealRequests = getAllHelper(); }

    /**
     * gets all Meal requests
     *
     * @return list of Meal requests
     */
    public ArrayList<FullMealRequest> getAllHelper() {
        ArrayList<MealRequest> mrs = new ArrayList<MealRequest>();
        try {
            ResultSet rs = getDBRowAllRequests();
            while (rs.next()) {
                mrs.add(new MealRequest(rs));
            }
            return FullMealRequest.listFullMealRequests(mrs);
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'MealRequestDAOImpl.getAllHelper': " + e.getMessage());
        }
        return FullMealRequest.listFullMealRequests(mrs);
    }

    /**
     * adds given request
     *
     * @param request to add
     */
    @Override
    public void add(Object request) {
        String[] mealReq = (String[]) request;
       // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Date dateSubmitted;
        String[] values = {mealReq[0], mealReq[1], mealReq[2], mealReq[3], mealReq[4], mealReq[5], mealReq[6], mealReq[7], mealReq[8], mealReq[9], mealReq[10]};
        int id = insertDBRowNewMealRequest(values);
        ResultSet rs = DButils.getRowCond("requests", "dateSubmitted", "id = " + id);
        Date dateSubmitted = null;
        try {
            rs.next();
            dateSubmitted = rs.getDate("dateSubmitted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mealRequests.add(new FullMealRequest(id, mealReq[0], mealReq[1], mealReq[2], dateSubmitted, mealReq[4], mealReq[5], mealReq[6], mealReq[7], mealReq[8], mealReq[9], mealReq[10]));
        RequestDAOImpl.getRequestDaoImpl().getAll().add(new Request(id, mealReq[0], mealReq[1], mealReq[2], dateSubmitted, mealReq[4], (mealReq[5]), mealReq[10]));
    }

    /**
     * deletes given request
     *
     * @param request to delete
     */
    @Override
    public void delete(Object request) {
        FullOfficeRequest fmr = (FullOfficeRequest) request;
        DButils.deleteRow("mealrequests", "id" + fmr.getId() + "");
        DButils.deleteRow("requests", "id =" + fmr.getId() + "");
        mealRequests.remove(fmr);
        Request r = new Request(fmr.getId(), fmr.getEmployee(), fmr.getFloor(), fmr.getRoomNumber(), fmr.getDateSubmitted(), fmr.getRequestStatus(), fmr.getRequestType(), fmr.getLocationName());
        RequestDAOImpl.getRequestDaoImpl().getAll().remove(r);
    }

    /**
     * updates given request with the given values
     *
     * @param request to update with new values
     */
    @Override
    public void update(Object request) {
        FullMealRequest fmr = (FullMealRequest) request;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] values = {
                Integer.toString(fmr.getId()), fmr.getEmployee(), fmr.getFloor(), fmr.getRoomNumber(), dateFormat.format(fmr.getDateSubmitted()), fmr.getRequestStatus(), "Meal", fmr.getOrderFrom(), fmr.getFood(), fmr.getDrink(), fmr.getSnack(), fmr.getMealModification()};
        String[] colsMeal = {"orderfrom", "food", "drink", "snack", "mealmodification"};
        String[] valuesMeal = {values[7], values[8], values[9], values[10], values[11]};
        String[] colsReq = {"employee", "floor", "roomnumber", "datesubmitted", "requeststatus", "requesttype"};
        String[] valuesReq = {values[1], values[2], values[3], values[4], values[5], values[6]};
        DButils.updateRow("mealrequests", colsMeal, valuesMeal, "id = " + values[0]);
        DButils.updateRow("requests", colsReq, valuesReq, "id = " + values[0]);
        for (int i = 0; i < mealRequests.size(); i++) {
            if (mealRequests.get(i).getId() == fmr.getId()) {
                mealRequests.set(i, fmr);
            }
        }
        Request r = new Request(fmr.getId(), fmr.getEmployee(), fmr.getFloor(), fmr.getRoomNumber(), fmr.getDateSubmitted(), fmr.getRequestStatus(), fmr.getRequestType(), fmr.getLocationName());
        RequestDAOImpl.getRequestDaoImpl().update(r);
    }
    //Insert into Database Methods

    /**
     * Inserts a new row into the meal request table
     *
     * @param value the values to insert into the corresponding columns
     */
    public static int insertDBRowNewMealRequest(String[] value) {
        String[] colMeal = {"id","orderfrom", "food", "drink", "snack", "mealmodification"};
        String[] colRequest = {"employee", "floor", "roomnumber", "requeststatus", "requesttype", "location_name"};
        String[] valuesReq = {value[0], value[1], value[2], value[3], value[4], value[10]};
        int id = DButils.insertRowRequests("requests", colRequest, valuesReq);
        String[] valuesMeal = {Integer.toString(id), value [5], value[6], value[7], value[8], value[9]};
        DButils.insertRow("mealrequests", colMeal, valuesMeal);
        return id;
    }


    // Access from Database Methods

    // Methods to get information about the meal request from the database

    /**
     * Searches through the database for the row(s) that matches the given column
     * and value
     *
     * @param col   the column to search for
     * @param value the value to search for
     * @return the result set of the row(s) that matches the given column and value
     */
    private ResultSet getDBRowFromCol(String col, String value) {
        return DButils.getRowCond("MealRequests", "*", col + " = " + value);
    }

    /**
     * Gets all rows from the database of meal requests
     *
     * @param col   the column to search for
     * @param value the value to search for
     * @return the result set of the row(s) that matches the given column and value
     */
    private ResultSet getDBRowAllRequests(String col, String value) {
        return DButils.getRowCond("MealRequests", "*", col + " = " + value);
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

    /**
     * Gets the row(s) from the database that matches the given meal modification
     *
     * @param mealmodiification the meal modification with the order
     * @return the result set of the row that matches the meal modification
     */
    public ResultSet getDBRowMealModification(String mealmodiification) {
        return getDBRowFromCol("mealmodification",  "'" + mealmodiification  + "'");
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
        return "ID: " + mr.getId() + "\tOrder From: " + mr.getOrderFrom() + "\tFood: " + mr.getFood() + "\tDrink: " + mr.getDrink() + "\tSnack: " + mr.getSnack() + "\tMeal Modification: " + mr.getMealModification();
    }

    /**
     * Gets all rows from the database of meal requests
     *
     * @return the result set of the row(s) that matches the given column and value
     */
    private ResultSet getDBRowAllRequests() throws SQLException {
        return DButils.getCol("mealrequests", "*");
    }
}

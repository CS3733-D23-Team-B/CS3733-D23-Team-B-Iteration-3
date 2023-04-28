package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.DBconnection;
import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.Full.IFull;
import edu.wpi.teamb.DBAccess.ORMs.Request;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RequestDAOImpl implements IDAO {
    private ArrayList<Request> requests;

    public void setRequests(ArrayList<Request> requests) {
        this.requests = requests;
    }

    private RequestDAOImpl() {
        requests = getAllHelper();
    }

    private static class SingletonHelper {
        private static final RequestDAOImpl requestDaoImp = new RequestDAOImpl();
    }

    public static RequestDAOImpl getRequestDaoImpl() {
        return SingletonHelper.requestDaoImp;
    }

    /**
     * Gets a Request object from the database
     *
     * @param id of the Request object
     * @return a Request object with information from request table
     */
    @Override
    public IFull get(Object id) {
        int whichRequest = 0;
        ResultSet rs = DButils.getRowCond("requests", "requesttype", "id = " + id);
        String requestType = null;
        while (true) {
            try {
                if (!rs.next()) break;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                requestType = rs.getString("requesttype");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        switch (requestType) {
            case "Meal":
                MealRequestDAOImpl mr = null;
                mr = new MealRequestDAOImpl();
                return mr.get(id);
            case "Conference":
                ConferenceRequestDAOImpl cr = null;
                cr = new ConferenceRequestDAOImpl();
                return cr.get(id);
            case "Flower":
                FlowerRequestDAOImpl fr = null;
                fr = new FlowerRequestDAOImpl();
                return fr.get(id);
            case "Furniture":
                FurnitureRequestDAOImpl ffr = null;
                ffr = new FurnitureRequestDAOImpl();
                return ffr.get(id);
            case "Office":
                OfficeRequestDAOImpl or = null;
                or = new OfficeRequestDAOImpl();
        }
        return null;
    }

    /**
     * Gets all local Request objects
     *
     * @return an ArrayList of all local Request objects
     */
    @Override
    public ArrayList<Request> getAll() {
        return requests;
    }

    /**
     * Sets all Requests using the database
     */
    @Override
    public void setAll() { requests = getAllHelper(); }

    /**
     * Gets all Request objects from the database
     *
     * @return an ArrayList of all Request objects
     */
    public ArrayList<Request> getAllHelper() {
        ArrayList<Request> rqs = new ArrayList<Request>();
        try {
            ResultSet rs = DButils.getCol("requests", "*");
            while (rs.next()) {
                Request r = new Request(rs);
                rqs.add(r);
            }
            return rqs;
        } catch (SQLException e) {
            System.out.println("ERROR Query Failed in method 'RequestDAOImpl.getAllHelper': " + e.getMessage());
        }
        DBconnection.getDBconnection().closeDBconnection();
        DBconnection.getDBconnection().forceClose();
        return rqs;
    }

    public ArrayList<IFull> getAllHelper1() {
        ResultSet rs = null;
        ArrayList<IFull> rqs = new ArrayList<IFull>();
        try {
            rs = DButils.getCol("requests", "*");
            while (rs.next()) {
                Request r = new Request(rs);
                rqs.add((IFull) get(r.getId()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rqs;
    }

    /**
     * Adds a Request object to the both the database and local list
     *
     * @param request the Request object to be added
     */
    @Override
    public void add(Object request) {
        Request r = (Request) request;
        Request req = new Request(r.getId(), r.getEmployee(), r.getDateSubmitted(), r.getRequestStatus(), r.getRequestType(), r.getLocationName(), r.getNotes());
        addHelper(req);
    }

    public void addHelper(Object request) {
        Request r = (Request) request;
        Request req = new Request(r.getId(), r.getEmployee(), r.getDateSubmitted(), r.getRequestStatus(), r.getRequestType(), r.getLocationName(), r.getNotes());
        requests.add(req);
    }

    /**
     * Removes a Request object from the both the database and local list
     *
     * @param request the Request object to be removed
     */
    @Override
    public void delete(Object request) {

    }

    /**
     * Updates a Request object in the both the database and local list
     *
     * @param request the Request object to be updated
     */
    @Override
    public void update(Object request) {
        Request r = (Request) request;
        getRequestIndex(r.getId());
        requests.set(getRequestIndex(r.getId()), r);
        String[] cols = {"employee", "datesubmitted", "requeststatus", "requesttype", "locationname", "notes"};
        String[] vals = {r.getEmployee(), String.valueOf(r.getDateSubmitted()), r.getRequestStatus(), r.getRequestType(), r.getLocationName(), r.getNotes()};
        DButils.updateRow("requests", cols, vals, "id = " + r.getId());
    }

    /**
     * Gets the index of a Request object in the local list
     *
     * @param id the id of the Request object
     * @return the index of the Request object in the local list, or
     *         -1 if the Request object is not in the local list
     */
    public int getRequestIndex(int id) {
        for (Request r : requests) {
            if (r.getId() == id) {
                return requests.indexOf(r);
            }
        }
        return -1;
    }

    //Insert into Database Methods

    /**
     * Inserts a new row into the requests table in the database
     *
     * @param values the values to be inserted into the row
     * @return the id of the new row
     */
    public static int insertDBRowNewRequest(String[] values) {
        String[] col = {"employee", "floor", "roomNumber", "dateSubmitted", "requestStatus", "requestType", "locationname"};
        int id = DButils.insertRowRequests("requests", col, values);
        return id;
    }

    // Access from Database Methods

    // Methods to get information about the request from the database

    /**
     * Returns a ResultSet of the row(s) that matches the given column and value in the Requests table
     *
     * @param col the column to search for the value
     * @param value the value to search for in the column
     * @return a ResultSet of the row(s) that matches the given column and value
     */
    private static ResultSet getDBRowFromCol(String col, String value) {
        return DButils.getRowCond("requests", "*", col + " = " + value);
    }

    /**
     * Returns a ResultSet of the row(s) that matches the given employee in the Requests table
     *
     * @param employee the employee to search for
     * @return the result set of the row that matches the given employee
     */
    public static ResultSet getDBRowEmployee(String employee) {
        return getDBRowFromCol("employee", "'" + employee + "'");
    }

    /**
     * Returns a ResultSet of the row(s) that matches the given ID in the Requests table
     *
     * @param id the employee to search for
     * @return the result set of the row that matches the given ID
     */
    public static ResultSet getDBRowID(int id) {
        return getDBRowFromCol("id", Integer.toString(id));
    }

    /**
     * Returns a ResultSet of the row(s) that matches the given floor in the Requests table
     *
     * @param floor the floor to search for
     * @return the result set of the row that matches the given floor
     */
    public ResultSet getDBRowFloor(int floor) {
        return getDBRowFromCol("floor", Integer.toString(floor));
    }

    /**
     * Returns a ResultSet of the row(s) that matches the given room number in the Requests table
     *
     * @param roomNumber the room number to search for
     * @return the result set of the row that matches the given room number
     */
    public ResultSet getDBRowPosition(String roomNumber) {
        return getDBRowFromCol("roomNumber", roomNumber);
    }

    /**
     * Gets all rows from the database of requests
     *
     * @return the result set of the row(s) that matches the given column and value
     */
    public ResultSet getDBRowAllRequests() {
        return DButils.getRowCond("Requests", "*", "TRUE");
    }

    // Method to Update the Database

    /**
     * Updates the rows in the Requests table that match the given condition
     *
     * @param col the columns to update
     * @param val the values to update the columns to
     * @param cond the condition to search for
     */
    private void updateRows(String[] col, String[] val, String cond) {
        if (col.length != val.length) {
            throw new IllegalArgumentException("The column and value arrays must be the same length");
        }
        DButils.updateRow("Requests", col, val, cond);
    }

    /**
     * Update the employee  in the database
     *
     * @param oldEmployee the old employee
     * @param newEmployee the new employee
     */
    public void updateEmployee(String oldEmployee, String newEmployee, Request r) {
        String[] col = {"employee"};
        String[] value = {"'" + newEmployee + "'"};
        updateRows(col, value, "employee = '" + oldEmployee + "'");
        r.setEmployee(newEmployee);
    }

    //Get rid of oldrequeststatus param and just use the requeststatus field?

    /**
     * Update the request status in the database
     *
     * @param oldRequestStatus the old request status
     * @param newRequestStatus the new request status
     * @param r the Request to update
     */
    public void updateRequestStatus(String oldRequestStatus, String newRequestStatus, Request r) {
        String[] col = {"requeststatus"};
        String[] value = {"'" + newRequestStatus + "'"};
        updateRows(col, value, "requeststatus = '" + oldRequestStatus + "'");
        r.setRequestStatus(newRequestStatus);
    }

    /**
     * Update the request type  in the database
     *
     * @param oldRequestType the old request type
     * @param newRequestType the new request type
     * @param r the Request to update
     */
    public void updateRequestType(String oldRequestType, String newRequestType, Request r) {
        String[] col = {"requesttype"};
        String[] value = {"'" + newRequestType + "'"};
        updateRows(col, value, "requesttype = '" + oldRequestType + "'");
        r.setRequestType(newRequestType);
    }

    // Method to Delete the Database

    /**
     * Deletes the row in the database that matches the employee of this Request object
     *
     * @param confirm 0 to confirm, anything else to cancel
     */
    public void deleteDBRequest(int confirm, Request r) {
        if (confirm == 0)
            DButils.deleteRow("Requests", "employee = '" + r.getEmployee() + "'");
        else {
            System.out.println("Delete cancelled");
        }
    }

    // list information about this Request object


    /**
     * Returns a Request object given an ID
     *
     * @return a Request object given an ID
     */
    public static Request getRequest(int id) {
        ResultSet rs = DButils.getRowCond("requests", "*", "id = " + id);
        try {
            if (rs.isBeforeFirst()) {
                rs.next();
                return new Request(rs);
            } else throw new SQLException("No rows found");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * when deleting a user, all requests assigned to that user will be unassigned
     * @param username username of user to be deleted
     */
    public void updateRequestDeleteUser(String username) {
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getEmployee().equals(username)) {
                Request newRequest = requests.get(i);
                newRequest.setEmployee("Unassigned");
                update(newRequest);
            }
        }
    }

    /**
     * Returns a list of all requests that is assigned to given user
     * @param employee user logged in
     * @return list of requests that is assigned to given user
     */
    public ArrayList<IFull> getFullRequestsbyEmployee(String employee) {
        ArrayList<IFull> fullRequests = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getEmployee().equals(employee)) {
                fullRequests.add(get(requests.get(i).getId()));
            }
        }
        return fullRequests;
    }

    /**
     * returns a list of requests that are a given status
     * @param status given status
     * @return list of requests that are a given status
     */
    public ArrayList<IFull> getFullRequestsbyStatus(String status) {
        ArrayList<IFull> fullRequests = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getRequestStatus().equals(status)) {
                fullRequests.add(get(requests.get(i).getId()));
            }
        }
        return fullRequests;
    }
}

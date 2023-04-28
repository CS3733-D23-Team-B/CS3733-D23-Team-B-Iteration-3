package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.DBconnection;
import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.Full.FullFactory;
import edu.wpi.teamb.DBAccess.Full.FullOfficeRequest;
import edu.wpi.teamb.DBAccess.Full.IFull;
import edu.wpi.teamb.DBAccess.ORMs.OfficeRequest;
import edu.wpi.teamb.DBAccess.ORMs.Request;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class OfficeRequestDAOImpl implements IDAO {
    ArrayList<FullOfficeRequest> officeRequests;

    public OfficeRequestDAOImpl() {
        officeRequests = getAllHelper();
    }

    /**
     * Gets a FullOfficeRequest object from the database
     *
     * @param id of the OfficeRequest object
     * @return an OfficeRequest object with information from request and office request tables
     */
    @Override
    public FullOfficeRequest get(Object id) {
        Integer idInt = (Integer) id;
        OfficeRequest or = null;
        Request r = null;
        try {
            ResultSet rs = DButils.getRowCond("officerequests", "*", "id = " + idInt);
            rs.next();
            or = new OfficeRequest(rs);
            ResultSet rs1 = RequestDAOImpl.getDBRowID(idInt);
            rs1.next();
            r = new Request(rs1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new FullOfficeRequest(r, or);
    }

    /**
     * Gets all local OfficeRequests objects
     *
     * @return list of OfficeRequests objects
     */
    @Override
    public ArrayList<FullOfficeRequest> getAll() {
        return officeRequests;
    }

    /**
     * Sets all OfficeRequests using the database
     */
    @Override
    public void setAll() { officeRequests = getAllHelper(); }

    /**
     * Gets all OfficeRequests from the database
     *
     * @return list of OfficeRequests objects
     */
    public ArrayList<FullOfficeRequest> getAllHelper() {
        FullFactory ff = new FullFactory();
        IFull or = ff.getFullRequest("Office");
        ArrayList<OfficeRequest> ors = new ArrayList<OfficeRequest>();
        try {
            ResultSet rs = getDBRowAllRequests();
            while (rs.next()) {
                ors.add(new OfficeRequest(rs));
            }
            return (ArrayList<FullOfficeRequest>) or.listFullRequests(ors);
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'OfficeRequestDAOImpl.getAllHelper': " + e.getMessage());
        }
        DBconnection.getDBconnection().closeDBconnection();
        DBconnection.getDBconnection().forceClose();
        return (ArrayList<FullOfficeRequest>) or.listFullRequests(ors);
    }

    /**
     * Adds an OfficeRequest to the both the database and the local list
     *
     * @param request the OfficeRequest to add
     */
    @Override
    public void add(Object request) {
        String[] officeReq = (String[]) request;
        String[] values = {officeReq[0], officeReq[1], "Office", officeReq[2], officeReq[3], officeReq[4], officeReq[5], officeReq[6]};
        int id = insertDBRowNewOfficeRequest(values);
        ResultSet rs = DButils.getRowCond("requests", "dateSubmitted", "id = " + id);
        Timestamp dateSubmitted = null;
        try {
            rs.next();
            dateSubmitted = rs.getTimestamp("dateSubmitted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        officeRequests.add(new FullOfficeRequest(id, officeReq[0], dateSubmitted, officeReq[1], officeReq[2], officeReq[3], officeReq[4], officeReq[5], Integer.valueOf(officeReq[6])));
        RequestDAOImpl.getRequestDaoImpl().getAll().add(new Request(id, officeReq[0], dateSubmitted, officeReq[1], "Office", officeReq[2], officeReq[3]));
    }

    /**
     * Removes an OfficeRequest from the both the database and the local list
     *
     * @param request the OfficeRequest object to be removed
     */
    @Override
    public void delete(Object request) {
        FullOfficeRequest ffr = (FullOfficeRequest) request;
        DButils.deleteRow("officerequests", "id =" + ffr.getId() + "");
        DButils.deleteRow("requests", "id =" + ffr.getId() + "");
        officeRequests.remove(ffr);
        Request req = new Request(ffr.getId(), ffr.getEmployee(), ffr.getDateSubmitted(), ffr.getRequestStatus(), ffr.getRequestType(), ffr.getLocationName(), ffr.getNotes());
        RequestDAOImpl.getRequestDaoImpl().getAll().remove(req);
    }

    /**
     * Updates an OfficeRequest in the both the database and the local list
     *
     * @param request the OfficeRequest object to be updated
     */
    @Override
    public void update(Object request) {
        FullOfficeRequest ofr = (FullOfficeRequest) request;
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //String[] values = {Integer.toString(ofr.getId()), ofr.getEmployee(), ofr.getDateSubmitted().toString(), ofr.getRequestStatus(), ofr.getRequestType(), ofr.getItem(), Integer.toString(ofr.getQuantity()), ofr.getSpecialInstructions(), ofr.getType()};
        String[] colsOffice = {"type", "item", "quantity"};
        String[] valuesOffice = {ofr.getType(), ofr.getItem(), String.valueOf(ofr.getQuantity())};
        String[] colsReq = {"employee", "datesubmitted", "requeststatus", "requesttype", "locationname", "notes"};
        String[] valuesReq = {ofr.getEmployee(), String.valueOf(ofr.getDateSubmitted()), ofr.getRequestStatus(), ofr.getRequestType(), ofr.getLocationName(), ofr.getNotes()};
        DButils.updateRow("officerequests", colsOffice, valuesOffice, "id = " + ofr.getId());
        DButils.updateRow("requests", colsReq, valuesReq, "id = " + ofr.getId());
        for (int i = 0; i < officeRequests.size(); i++) {
            if (officeRequests.get(i).getId() == ofr.getId()) {
                officeRequests.set(i, ofr);
            }
        }
        Request req = new Request(ofr.getId(), ofr.getEmployee(), ofr.getDateSubmitted(), ofr.getRequestStatus(), ofr.getRequestType(), ofr.getLocationName(), ofr.getNotes());
        RequestDAOImpl.getRequestDaoImpl().update(req);
    }

    /**
     * Gets all rows from the database of conference requests
     *
     * @return the result set of the row(s) that matches the given column and value
     */
    private ResultSet getDBRowAllRequests() throws SQLException {
        return DButils.getCol("officerequests", "*");
    }

    /**
     * Updates the special instructions of a row in the OfficeRequests table
     *
     * @param specialInstructions the specialInstructions to update
     * @param col the column to search for the value
     * @param val the value to search for in the column
     */
    public void updateRowSpecialInstructions(String specialInstructions, String[] col, String[] val) {
        updateRows(col, val, "specialInstructions = " + specialInstructions);
    }

    /**
     * Inserts a new row into the OfficeRequests table
     *
     * @param values the values of the OfficeRequest you want to add
     * @return an int representing the id of the new row
     */
    public static int insertDBRowNewOfficeRequest(String[] values) {
        String[] colsOffice = {"id", "type", "item", "quantity"};
        String[] colsReq = {"employee", "requeststatus","requesttype", "locationname", "notes"};
        String[] valuesReq = {values[0], values[1], values[2], values[3], values[4]};
        int id = DButils.insertRowRequests("requests", colsReq, valuesReq);
        String[] valuesOffice = {Integer.toString(id), values[5], values[6], values[7]};
        DButils.insertRow("officerequests", colsOffice, valuesOffice);
        return id;
    }

    /**
     * Returns a ResultSet of the row(s) that matches the given column and value in the OfficeRequests table
     *
     * @param col the column to search for the value
     * @param value the value to search for in the column
     * @return a ResultSet of the row(s) that matches the given column and value
     */
    private ResultSet getDBRowFromCol(String col, String value) {
        return DButils.getRowCond("OfficeRequests", "*", col + " = " + value);
    }

    /**
     * Returns a ResultSet of the row(s) that matches the given id in the OfficeRequests table
     *
     * @param id the id to search for in the column
     * @return a ResultSet of the row(s) that matches the given id
     */
    public ResultSet getDBRowID(int id) {
        return getDBRowFromCol("id", Integer.toString(id));
    }
    private void updateRows(String[] col, String[] val, String cond) {
        if (col.length != val.length)
            throw new IllegalArgumentException("Column and value arrays must be the same length");
        DButils.updateRow("OfficeRequests", col, val, cond);
    }
}

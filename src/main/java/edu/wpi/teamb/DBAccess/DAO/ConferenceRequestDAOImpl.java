package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.DBconnection;
import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.Full.FullConferenceRequest;
import edu.wpi.teamb.DBAccess.Full.FullFactory;
import edu.wpi.teamb.DBAccess.Full.FullFurnitureRequest;
import edu.wpi.teamb.DBAccess.Full.IFull;
import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.ORMs.ConferenceRequest;
import edu.wpi.teamb.DBAccess.ORMs.Request;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
;

public class ConferenceRequestDAOImpl implements IDAO {
    ArrayList<FullConferenceRequest> conferenceRequests;

    public ConferenceRequestDAOImpl() {
        conferenceRequests = getAllHelper();
    }

    /**
     * Gets a FullConferenceRequest including the information from request table given an ID
     *
     * @param id of the request
     * @return a FullConferenceRequest with information from request and conference request tables
     */
    @Override
    public FullConferenceRequest get(Object id) {
        int idInt = (Integer) id;
        ConferenceRequest cr = null;
        Request r = null;
        try {
            ResultSet rs = DButils.getRowCond("conferencerequests", "*", "id = " + idInt);
            rs.next();
            cr = new ConferenceRequest(rs);
            ResultSet rs1 = RequestDAOImpl.getDBRowID(idInt);
            rs1.next();
            r = new Request(rs1);
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'ConferenceRequestDAOImpl.get': " + e.getMessage());
            return null;
        }
        return new FullConferenceRequest(r, cr);
    }

    /**
     * Gets all local ConferenceRequests objects
     *
     * @return list of ConferenceRequests objects
     */
    @Override
    public ArrayList<FullConferenceRequest> getAll() {
        return conferenceRequests;
    }

    /**
     * Sets all ConferenceRequests using the database
     */
    @Override
    public void setAll() { conferenceRequests = getAllHelper(); }


    /**
     * Gets all conference requests including the information from request table from the database
     *
     * @return an ArrayList of conference requests
     */
    public ArrayList<FullConferenceRequest> getAllHelper() {
        FullFactory ff = new FullFactory();
        IFull conf = ff.getFullRequest("Conference");
        ArrayList<ConferenceRequest> crs = new ArrayList<ConferenceRequest>();

        try {
            ResultSet rs = getDBRowAllRequests();
            while (rs.next()) {
                crs.add(new ConferenceRequest(rs));
            }
            return (ArrayList<FullConferenceRequest>) conf.listFullRequests(crs);
        } catch (SQLException e) {
        System.err.println("ERROR Query Failed in method 'ConferenceRequestDAOImpl.getAllHelper': " + e.getMessage());
        }
        DBconnection.getDBconnection().closeDBconnection();
        DBconnection.getDBconnection().forceClose();
        return (ArrayList<FullConferenceRequest>) conf.listFullRequests(crs);
    }

    /**
     * Adds a ConferenceRequest to the both the database and the local list
     *
     * @param request the ConferenceRequest to add
     */
    @Override
    public void add(Object request) {
        String[] confReq = (String[]) request;
        String[] values = {confReq[0], confReq[1], "Conference", confReq[2], confReq[3], confReq[4], confReq[5], confReq[6], confReq[7]};
        int id = insertDBRowNewConferenceRequest(values);
        ResultSet rs = DButils.getRowCond("requests", "dateSubmitted", "id = " + id);
        Timestamp dateSubmitted = null;
        ResultSet rs1 = DButils.getRowCond("conferencerequests", "dateRequested", "id = " + id);
        Timestamp dateRequested = null;
        try {
            rs.next();
            dateSubmitted = rs.getTimestamp("dateSubmitted");
            rs1.next();
            dateRequested = rs1.getTimestamp("dateRequested");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conferenceRequests.add(new FullConferenceRequest(id, confReq[0], dateSubmitted, confReq[1], confReq[2], confReq[3], dateRequested, confReq[5], confReq[6], Integer.parseInt(confReq[7])));
        RequestDAOImpl.getRequestDaoImpl().getAll().add(new Request(id, confReq[0], dateSubmitted, confReq[2], "Conference", confReq[3], confReq[4]));
    }

    /**
     * Removes a ConferenceRequest from the both the database and the local list
     *
     * @param request the ConferenceRequest object to be removed
     */
    @Override
    public void delete(Object request) {
        FullConferenceRequest fcr = (FullConferenceRequest) request;
        DButils.deleteRow("conferencerequests", "id =" + fcr.getId() + "");
        DButils.deleteRow("requests", "id =" + fcr.getId() + "");
        conferenceRequests.remove(fcr);
        Request req = new Request(fcr.getId(), fcr.getEmployee(), fcr.getDateSubmitted(), fcr.getRequestStatus(), fcr.getRequestType(), fcr.getLocationName(), fcr.getNotes());
        RequestDAOImpl.getRequestDaoImpl().getAll().remove(req);
    }

    /**
     * Updates a ConferenceRequest object in both the database and the local list
     *
     * @param request the ConferenceRequest object to be updated
     */
    @Override
    public void update(Object request) {
        FullConferenceRequest fcr = (FullConferenceRequest) request;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] colsConf = {"daterequested", "eventname", "bookingreason", "duration"};
        String[] valuesConf = {String.valueOf(fcr.getDateRequested()), fcr.getEventName(), fcr.getBookingReason(), String.valueOf(fcr.getDuration())};
        String[] colsReq = {"employee", "datesubmitted", "requeststatus", "requesttype", "locationname", "notes"};
        String[] valuesReq = {fcr.getEmployee(), String.valueOf(fcr.getDateSubmitted()), fcr.getRequestStatus(), fcr.getRequestType(), fcr.getLocationName(), fcr.getNotes()};
        DButils.updateRow("conferencerequests", colsConf, valuesConf, "id = " + fcr.getId());
        DButils.updateRow("requests", colsReq, valuesReq, "id = " + fcr.getId());
        for (int i = 0; i < conferenceRequests.size(); i++) {
            if (conferenceRequests.get(i).getId() == fcr.getId()) {
                conferenceRequests.set(i, fcr);
            }
        }
        Request req = new Request(fcr.getId(), fcr.getEmployee(), fcr.getDateSubmitted(), fcr.getRequestStatus(), fcr.getRequestType(), fcr.getLocationName(), fcr.getNotes());
        RequestDAOImpl.getRequestDaoImpl().update(req);
    }

    /**
     * inserts a new conference request into the database
     *
     * @param values to insert
     * @return the id of the new conference request
     */
    public static int insertDBRowNewConferenceRequest(String[] values) {
        String[] colsConf = {"id", "daterequested", "eventname", "bookingreason", "duration"};
        String[] colsReq = {"employee", "requeststatus", "requesttype", "locationname", "notes"};
        String[] valuesReq = {values[0], values[1], values[2], values[3], values[4]};
        int id = DButils.insertRowRequests("requests", colsReq, valuesReq);
        String[] valuesConf = {Integer.toString(id),values[5], values[6], values[7], values[8]};
        DButils.insertRow("conferencerequests", colsConf, valuesConf);
        return id;
    }

    /**
     * Searches through the ConferenceRequests table for the row(s) that matches the given ID
     *
     * @param id   the id of the conferenceRequest to search for
     * @return the result set of the row(s) that matches the given column and value
     */
    public ResultSet getDBRowID(int id) {
        return getDBRowFromCol("id", Integer.toString(id));
    }

    /**
     * Returns a ResultSet of the row(s) that matches the given column and value in the ConferenceRequests table
     *
     * @param col the column to search for the value
     * @param value the value to search for in the column
     * @return a ResultSet of the row(s) that matches the given column and value
     */
    private ResultSet getDBRowFromCol(String col, String value) {
        return DButils.getRowCond("ConferenceRequests", "*", col + " = " + value);
    }

    /**
     * Gets all rows from the ConferenceRequests table
     *
     * @return the result set of the row(s) that matches the given column and value
     */
    private ResultSet getDBRowAllRequests() {
        return DButils.getCol("conferencerequests", "*");
    }

    /**
     * Searches through the database for the row(s) that matches the given request ID in the ConferenceRequests table
     *
     * @param id the request ID to search for
     * @return the result set of the row(s) that matches the given column and value
     */
    public ConferenceRequest getConfRequest(int id) {
        ResultSet rs = DButils.getRowCond("ConferenceRequests", "*", "id = '" + id + "'");
        try {
            assert rs != null;
            if (rs.isBeforeFirst()) {
                rs.next();
                return new ConferenceRequest(rs);
            } else
                throw new SQLException("Error in method 'ConferenceRequestDAOImpl.getConfRequest': No rows found");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

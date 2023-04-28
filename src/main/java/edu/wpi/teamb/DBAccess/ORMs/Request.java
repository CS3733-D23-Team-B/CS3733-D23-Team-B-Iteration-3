package edu.wpi.teamb.DBAccess.ORMs;
import edu.wpi.teamb.DBAccess.DButils;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Request {
  private int id;
  private String employee;
  private Timestamp dateSubmitted;
  private String requestStatus;
  private String requestType;
  private String locationName;
  private String notes;

  public Request() {
    this.id = 0;
    this.employee = "";
    this.dateSubmitted = null;
    this.requestStatus = "";
    this.requestType = "";
    this.locationName = "";
    this.notes = "";
  }

  public Request(int id, String employee, Timestamp dateSubmitted, String requestStatus, String requestType, String locationName, String notes) {
    this.id = id;
    this.employee = employee;
    this.dateSubmitted = dateSubmitted;
    this.requestStatus = requestStatus;
    this.requestType = requestType;
    this.locationName = locationName;
    this.notes = notes;
  }

  public Request(ResultSet rs) throws java.sql.SQLException {
    this(
            rs.getInt("id"),
            rs.getString("employee"),
            rs.getTimestamp("datesubmitted"),
            rs.getString("requeststatus"),
            rs.getString("requesttype"),
            rs.getString("locationname"),
            rs.getString("notes")
    );
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getEmployee() {
    return employee;
  }

  public void setEmployee(String employee) {
    this.employee = employee;
  }

  public Timestamp getDateSubmitted() {
    return dateSubmitted;
  }

  public void setDateSubmitted(Timestamp dateSubmitted) {
    this.dateSubmitted = dateSubmitted;
  }

  public String getRequestStatus() {
    return requestStatus;
  }

  public void setRequestStatus(String requestStatus) {
    this.requestStatus = requestStatus;
  }

  public String getRequestType() {
    return requestType;
  }

  public void setRequestType(String requestType) {
    this.requestType = requestType;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  @Override
  public String toString() {
    return "Request ID: " + id + ",\t" +
            "employee: " + employee + ",\t" +
            "Date Submitted: " + dateSubmitted + ",\t" +
            "Request Status: " + requestStatus + ",\t" +
            "Request Type: " + requestType + ".";
  }
}

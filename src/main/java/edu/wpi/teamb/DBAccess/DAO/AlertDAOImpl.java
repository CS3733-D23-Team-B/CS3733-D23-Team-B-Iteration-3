package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.DBconnection;
import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.ORMs.Alert;
import edu.wpi.teamb.DBAccess.ORMs.Edge;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AlertDAOImpl implements IDAO {
    private static ArrayList<Alert> alerts;


    public AlertDAOImpl() {
        alerts = getAllHelper();
    }
    @Override
    public Alert get(Object id) {
        int intID = (int) id;
        ResultSet rs = DButils.getRowCond("alerts", "*", "id = " + intID);
        try {
            if (rs.isBeforeFirst()) { // if there is something it found
                rs.next();
                return new Alert(rs); // make the edge
            } else
                throw new SQLException("No rows found");
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'AlertDAOImpl.get': " + e.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<Alert> getAll() {
        return alerts;
    }

    public ArrayList<Alert> getAllHelper() {
        ArrayList<Alert> alerts = new ArrayList<Alert>();
        try {
            ResultSet rs = DButils.getCol("alerts", "*");
            while (rs.next()) {
                alerts.add(new Alert(rs));
            }
            return alerts;
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'AlertDAOImpl.getAllHelper': " + e.getMessage());
        }
        DBconnection.getDBconnection().closeDBconnection();
        DBconnection.getDBconnection().forceClose();
        return alerts;
    }

    @Override
    public void setAll() {
        alerts = getAllHelper();
    }

    @Override
    public void add(Object object) {
        Alert a = (Alert) object;
        insertAlert(a);
        alerts.add(a);
    }

    @Override
    public void delete(Object object) {
        Alert a = (Alert) object;
        deleteAlert(a);
        alerts.remove(a);
    }

    @Override
    public void update(Object object) {
        Alert a = (Alert) object;
        delete(a);
        add(a);
    }

    public void insertAlert(Alert a) {
        String[] cols = { "title", "description", "created_at", "employee" };
        String[] values = {a.getTitle(), a.getDescription(), String.valueOf(a.getCreatedAt()), a.getEmployee()};
        DButils.insertRow("alerts", cols, values);
    }

    /**
     * Deletes an alert from the database
     * @param a the alert to delete
     */
    public void deleteAlert(Alert a) {
        DButils.deleteRow("alerts", "id = " + a.getId() + "");
    }

    public void updateAlert(Alert a) {
        String[] cols = { "title", "description", "created_at", "employee" };
        String[] values = {a.getTitle(), a.getDescription(), String.valueOf(a.getCreatedAt()), a.getEmployee()};
        DButils.updateRow("alerts", cols, values, "id = " + a.getId() + "");
    }
}

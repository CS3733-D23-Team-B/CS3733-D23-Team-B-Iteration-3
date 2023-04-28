package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.DBconnection;
import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.ORMs.Alert;
import edu.wpi.teamb.DBAccess.ORMs.Sign;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

public class SignDAOImpl implements IDAO {

    private static ArrayList<Sign> signs;


    public SignDAOImpl() {
        signs = getAllHelper();
    }
    @Override
    public Sign get(Object id) {
        String signageGroup = id.toString();
        ResultSet rs = DButils.getRowCond("signs", "*", "signageGroup like " + signageGroup);
        try {
            if (rs.isBeforeFirst()) { // if there is something it found
                rs.next();
                return new Sign(rs); // make the edge
            } else
                throw new SQLException("No rows found");
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'SignDAOImpl.get': " + e.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<Sign> getAll() {
        return signs;
    }

    public ArrayList<Sign> getAllHelper() {
        ArrayList<Sign> signs = new ArrayList<Sign>();
        try {
            ResultSet rs = DButils.getCol("signs", "*");
            while (rs.next()) {
                signs.add(new Sign(rs));
            }
            return signs;
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'SignDAOImpl.getAllHelper': " + e.getMessage());
        }
        DBconnection.getDBconnection().closeDBconnection();
        DBconnection.getDBconnection().forceClose();
        return signs;
    }

    @Override
    public void setAll() {
        signs = getAllHelper();
    }

    @Override
    public void add(Object object) {
        Sign s = (Sign) object;
        insertSign(s);
        signs.add(s);
    }

    @Override
    public void delete(Object object) {
        Sign s = (Sign) object;
        deleteSpecificSign(s);
        signs.remove(s);
    }

    @Override
    public void update(Object object) {
        Sign s = (Sign) object;
        updateSign(s);
        signs.set(signs.indexOf(s), s);
    }

    public void updateBlock(Object object) {
        Sign s = (Sign) object;
        updateSignageGroup(s);
        signs.set(signs.indexOf(s), s);
    }

    public void insertSign(Sign s) {
        String[] cols = { "signageGroup", "locationName", "direction", "startDate", "endDate", "singleBlock" };
        String[] values = { "'" + s.getSignageGroup() + "'", "'" + s.getLocationName() + "'", "'" + s.getDirection() + "'", "'" + s.getStartDate() + "'", "'" + s.getEndDate() + "'", "'" + s.isSingleBlock() + "'" };
        DButils.insertRow("signs", cols, values);
    }

    public void deleteSignGroup(Sign s) {
        DButils.deleteRow("signs", "signageGroup like " + s.getSignageGroup() + "");
    }

    public void deleteSpecificSign(Sign s) {
        DButils.deleteRow("signs", "signageGroup like " + s.getSignageGroup() + "" + " and locationName like " + s.getLocationName() + "");
    }

    public void updateSign(Sign s) {
        String[] cols = { "locationName", "direction", "startDate", "endDate" };
        String[] values = { "'" + s.getLocationName() + "'", "'" + s.getDirection() + "'", "'" + s.getStartDate() + "'", "'" + s.getEndDate() + "'" };
        DButils.updateRow("signs", cols, values, "signageGroup like " + s.getSignageGroup() + "" + " and locationName like " + s.getLocationName() + "");
    }

    public void updateSignageGroup(Sign s) {
        String[] cols = { "singleBlock" };
        String[] values = { "'" + s.isSingleBlock() + "'" };
        DButils.updateRow("signs", cols, values, "signageGroup like " + s.getSignageGroup() + "");
    }

    public HashSet<String> getSignageGroupsFromDB() {
        ArrayList<String> blocks = new ArrayList<String>();
        try {
            ResultSet rs = DButils.getCol("signs", "signageGroup");
            while (rs.next()) {
                blocks.add(rs.getString("signageGroup"));
            }
            return new HashSet<String>(blocks);
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'SignDAOImpl.getSignBlocks': " + e.getMessage());
        }
        return new HashSet<String>(blocks);
    }

    public ArrayList<String> getLocationNamesFromDB(String signBlock) {
        ArrayList<String> blocks = new ArrayList<String>();
        try {
            ResultSet rs = DButils.getRowCond("signs", "locationName", "signageGroup like " + signBlock + "");
            while (rs.next()) {
                blocks.add(rs.getString("locationName"));
            }
            return blocks;
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'SignDAOImpl.getLocationNames': " + e.getMessage());
        }
        return blocks;
    }

    public HashSet<String> getSignageGroups() {
        ArrayList<String> blocks = new ArrayList<String>();
        for (Sign s : signs) {
            blocks.add(s.getSignageGroup());
        }
        return new HashSet<String>(blocks);
    }

    public ArrayList<String> getLocationNames(String signBlock) {
        ArrayList<String> names = new ArrayList<String>();
        for (Sign s : signs) {
            if (s.getSignageGroup().equals(signBlock)) {
                names.add(s.getLocationName());
            }
        }
        return names;
    }

}

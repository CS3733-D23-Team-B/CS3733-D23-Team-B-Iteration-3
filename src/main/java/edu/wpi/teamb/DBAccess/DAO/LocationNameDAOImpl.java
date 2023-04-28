package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.DBconnection;
import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.ORMs.LocationName;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class LocationNameDAOImpl implements IDAO {

    static ArrayList<LocationName> locationNames;

    public LocationNameDAOImpl() {
        locationNames = getAllHelper();
    }

    /**
     * Gets a LocationName by its ID
     *
     * @param id the ID of the LocationName
     * @return the LocationName with the given ID
     */
    public LocationName get(Object id) {
        String name = (String) id;
        ResultSet rs = DButils.getRowCond("LocationNames", "*", "longname = " + name);
        try {
        if (rs.isBeforeFirst()) { // if there is something it found
            rs.next();
            return new LocationName(rs); // make the locationName
        } else
            throw new SQLException("No rows found");
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'LocationNameDAOImpl.get': " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets all local LocationName objects
     *
     * @return an ArrayList of all local LocationName objects
     */
    @Override
    public ArrayList<LocationName> getAll() {
        return locationNames;
    }

    /**
     * Sets all LocationName objects using the database
     */
    @Override
    public void setAll() { locationNames = getAllHelper(); }

    /**
     * Gets all LocationNames from the database
     *
     * @return a list of all locations
     */
    public ArrayList<LocationName> getAllHelper() {
        ArrayList<LocationName> lns = new ArrayList<LocationName>();
        try {
            ResultSet rs = DButils.getCol("locationnames", "*");
            while (rs.next()) {
                lns.add(new LocationName(rs));
            }
            return lns;
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'LocationNameDAOImpl.getAllHelper': " + e.getMessage());
        }
        DBconnection.getDBconnection().closeDBconnection();
        DBconnection.getDBconnection().forceClose();
        return lns;
    }

    /**
     * Adds a LocationName object to the both the database and local list
     *
     * @param l the LocationName object to be added
     */
    @Override
    public void add(Object l) {
        LocationName location = (LocationName) l;
        String[] cols = {"longName", "shortName", "nodeType"};
        String[] vals = {location.getLongName(), location.getShortName(), location.getNodeType()};
        DButils.insertRow("locationnames", cols, vals);
        locationNames.add(location);
    }

    /**
     * Removes a LocationName from the both the database and the local list
     *
     * @param l the LocationName object to be removed
     */
    @Override
    public void delete(Object l) {
        LocationName location = (LocationName) l;
        DButils.deleteRow("LocationNames", "longName = " + "'" + location.getLongName() + "'");
        locationNames.remove(location);
    }

    /**
     * Updates a LocationName object in both the database and local list
     *
     * @param l the LocationName object to be updated
     */
    @Override
    public void update(Object l) {
        LocationName location = (LocationName) l;
        String[] cols = {"longName", "shortName", "nodeType"};
        String[] vals = {location.getLongName(), location.getShortName(), location.getNodeType()};
        DButils.updateRow("LocationNames", cols, vals, "longName = '" + location.getLongName() + "'");
        for (int i = 0; i < locationNames.size(); i++) {
            if (locationNames.get(i).getLongName().equals(location.getLongName())) {
                locationNames.set(i, location);
            }
        }
    }

    public void updateExisting(Object l, String oldName) {
        LocationName location = (LocationName) l;
        String[] cols = {"longName", "shortName", "nodeType"};
        String[] vals = {location.getLongName(), location.getShortName(), location.getNodeType()};
        DButils.updateRow("LocationNames", cols, vals, "longName like '" + oldName + "'");
        for (int i = 0; i < locationNames.size(); i++) {
            if (locationNames.get(i).getLongName().equals(oldName)) {
                locationNames.set(i, location);
            }
        }
    }

    /**
     * Searches through the database for the row(s) that matches the col and value in the LocationNames table
     *
     * @param col   the column to search for
     * @param value the value to search for
     * @return A ResultSet of the row(s) that match the col and value
     */
    private ResultSet getDBRowFromCol(String col, String value) {
        return DButils.getRowCond("LocationNames", "*", col + " = " + value);
    }

    /**
     * Gets a ResultSet of all rows from the LocationNames table
     *
     * @return a ResultSet of all rows from the LocationNames table
     */
    public ResultSet getDBRowAllLocationNames() {
        return DButils.getRowCond("LocationNames", "*", "TRUE");
    }

    /**
     * Gets a ResultSet of rows from the LocationNames table that match the given longName
     *
     * @param longName the longName to look for to get LocationName data
     * @return a ResultSet of the row(s) that match the longName
     */
    public ResultSet getDBRowLongName(String longName) {
        return getDBRowFromCol("longName", "" + longName + "");
    }

    /**
     * Gets a ResultSet of rows from the LocationNames table that match the given shortName
     *
     * @param shortName the shortName to look for to get LocationName data
     * @return a ResultSet of the row(s) that match the shortName
     */
    public ResultSet getDBRowShortName(String shortName) {
        return getDBRowFromCol("shortName", "" + shortName + "");
    }

    /**
     * Gets a ResultSet of rows from the LocationNames table that match the given nodeType
     *
     * @param nodeType the date to look for to get Move data
     * @return A ResultSet of the row(s) that match the nodeType
     */
    public ResultSet getDBRowNodeType(String nodeType) {
        return getDBRowFromCol("nodeType", "" + nodeType + "");
    }

    /**
     * Returns a string of all the information about the LocationName
     *
     * @return a String of all the information about the LocationName
     */
    public String toString(LocationName ln) {
        return ln.getLongName() + ", " + ln.getShortName() + ", " + ln.getNodeType();
    }

    /**
     * Returns an ArrayList of Strings of all the longNames in alphabetical order
     *
     * @return an ArrayList of Strings of all the longNames in alphabetical order
     */
    public ArrayList<String> getLongNamesAlphebeticalOrder() {
        ArrayList<String> longNames = new ArrayList<>();
        for (LocationName ln : locationNames) {
            longNames.add(ln.getLongName());
        }
        longNames.sort(String::compareToIgnoreCase);
        return longNames;
    }

    /**
     * Returns an ArrayList of Strings of all the shortNames in alphabetical order
     *
     * @return an ArrayList of Strings of all the shortNames in alphabetical order
     */
    public ArrayList<String> getShortNamesAlphebeticalOrder() {
        ArrayList<String> shortNames = new ArrayList<>();
        for (LocationName ln : locationNames) {
            shortNames.add(ln.getLongName());
        }
        shortNames.sort(String::compareToIgnoreCase);
        return shortNames;
    }

    /**
     * Returns an ArrayList of Strings of all the unique nodeTypes in alphabetical order
     *
     * @return an ArrayList of Strings of all the unique nodeTypes in alphabetical order
     */
    public ArrayList<String> getNodeTypesUniqueAlphabetical() {
        ArrayList<String> uniqueAlphabetical = new ArrayList<>();
        for (LocationName ln : locationNames) {
            if (!uniqueAlphabetical.contains(ln.getNodeType())) {
                uniqueAlphabetical.add(ln.getNodeType());
            }
        }
        uniqueAlphabetical.sort(String::compareToIgnoreCase);
        return uniqueAlphabetical;
    }

    /**
     * Returns a LocationName object of the given longName
     *
     * @return a LocationName object of the given longName
     */
    public LocationName getLocationName(String name) {
        ResultSet rs = DButils.getRowCond("LocationNames", "*", "longname = " + name);
        try {
            if (rs.isBeforeFirst()) { // if there is something it found
                rs.next();
                return new LocationName(rs); // make the locationName
            } else
                throw new SQLException("No rows found");
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'LocationNameDAOImpl.getLocationName': " + e.getMessage());
            return null;
        }
    }
    public ArrayList<String> getLongNameByType(String type) {
        ArrayList<String> longNames = new ArrayList<>();
        ResultSet rs = DButils.getRowCond("locationnames", "*", "nodeType = '" + type + "'");
        try {
            while (rs.next()) {
                longNames.add(rs.getString("longname"));
            }
            rs.close();
            return longNames;
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'NodeDAOImpl.getNodesByType': " + e.getMessage());
            return null;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                System.err.println("ERROR Query Failed in method 'NodeDAOImpl.getNodesByType': " + e.getMessage());
            }
        }
    }

    public ArrayList<String> getLongNamePractical() {
        String[] nodeTypes = {"BATH", "REST", "ELEV", "STAI", "HALL", "EXIT"};
        ArrayList<String> longNames = new ArrayList<>();
        ResultSet rs = DButils.getRowCond("locationnames", "*", "nodeType != '" + nodeTypes[0] + "' AND nodeType != '" + nodeTypes[1] + "' AND nodeType != '" + nodeTypes[2] + "' AND nodeType != '" + nodeTypes[3] + "' AND nodeType != '" + nodeTypes[4] + "' AND nodeType != '" + nodeTypes[5] + "'");
        try {
            while (rs.next()) {
                longNames.add(rs.getString("longname"));
            }
            rs.close();
            return longNames;
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'NodeDAOImpl.getNodesByType': " + e.getMessage());
            return null;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                System.err.println("ERROR Query Failed in method 'NodeDAOImpl.getNodesByType': " + e.getMessage());
            }
        }
    }

    public int getNodeIDfromLongName(String longName) {
        try {
            Statement stmt = DBconnection.getDBconnection().getConnection().createStatement();
            String query = "SELECT * from locationnames join moves m on locationnames.longname = m.longname where locationnames.longname like " + longName;
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            int set = rs.getInt("nodeID");
            rs.close();
            return set;
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'NodeDAOImpl.getLongNameFromNodeID': " + e.getMessage());
            return 0;
        }
    }
}

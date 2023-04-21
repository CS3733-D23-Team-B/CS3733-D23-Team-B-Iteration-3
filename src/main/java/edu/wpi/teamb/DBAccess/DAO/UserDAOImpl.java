package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.ORMs.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDAOImpl implements IDAO {
    ArrayList<User> users;

    public UserDAOImpl() throws SQLException {
        users = getAllHelper();
    }

    /**
     * Gets a User by its username
     *
     * @param id the username of the User
     * @return the User with the given username
     */
    @Override
    public User get(Object id) {
        String username = (String) id;
        ResultSet rs = DButils.getRowCond("Users", "*", "username = '" + username + "'");
        try {
            if (rs != null) {
                if (rs.isBeforeFirst()) {
                    rs.next();
                    return new User(rs);
                } else throw new SQLException("No rows found"); }
        } catch (SQLException e) {
            // handle error
            System.err.println("ERROR Query Failed in method 'UserDAOImpl.get': " + e.getMessage());
            return null;
        }
        return null;
    }

    /**
     * Gets all local User objects
     *
     * @return an ArrayList of all local User objects
     */
    @Override
    public ArrayList<User> getAll() {
        return users;
    }

    /**
     * Sets all User objects using the database
     */
    @Override
    public void setAll() { users = getAllHelper(); }

   /**
    * Gets all users from the database
    *
    * @return an ArrayList of all users in the database
    */
    public ArrayList<User> getAllHelper() {
        ArrayList<User> users = new ArrayList<User>();
        try {
            ResultSet rs = DButils.getCol("users", "*");
            while (rs.next()) {
                users.add(new User(rs));
            }
            return users;
        } catch (SQLException e) {
            System.err.println("ERROR Query Failed in method 'UserDAOImpl.getAllHelper': " + e.getMessage());
        }
        return users;
    }

    /**
     * Updates a User object in both the database and local list
     *
     * @param u the User object to be updated
     */
    @Override
    public void update(Object u) {
        User user = (User) u;
        String[] values = {user.getUsername(), user.getPassword(), user.getPosition(), String.valueOf(user.getPermissionLevel())};
        String[] colsUser = {"password", "position", "permissionlevel"};
        String[] valuesUser = {values[1], values[2], values[3]};
        DButils.updateRow("users", colsUser, valuesUser, "username = '" + values[0] +"'");
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername() == user.getUsername()) {
                users.set(i, user);
            }
        }
    }

    // Method to Delete the Database

    /**
     * Removes a User from the both the database and the local list
     *
     * @param u the User object to be removed
     */
    @Override
    public void delete(Object u) {
        User user = (User) u;
        DButils.deleteRow("Users", "username = '" + user.getUsername() + "'");
        users.remove(user);
    }

    /**
     * Adds a User object to the both the database and local list
     *
     * @param u the User object to be added
     */
    @Override
    public void add(Object u) {
        User user = (User) u;
        String[] cols = {"username", "password", "permissionLevel", "position"};
        String[] vals = {user.getUsername(), user.getPassword(), Integer.toString(user.getPermissionLevel()), user.getPosition()};
        DButils.insertRow("Users", cols, vals);
        users.add(user);
    }

    /**
     * Searches the Users table for the row(s) that matches the given column and value
     *
     * @param col   the column to search for
     * @param value the value to search for
     * @return the result set of the row(s) that matches the given column and value
     */
    public ResultSet getDBRowFromCol(String col, String value) {
        return DButils.getRowCond("Users", "*", col + " = " + value);
    }

    /**
     * Returns a ResultSet of the row(s) that matches the given username
     *
     * @param username the username of the row to get
     * @return a ResultSet of the row(s) that matches the given username
     */
    public ResultSet getDBRowUsername(String username) {
        return getDBRowFromCol("username", "'" + username + "'");
    }

    /**
     * Returns a ResultSet of the row(s) that matches the given permission level
     *
     * @param permissionLevel the permission level of the row to get
     * @return a ResultSet of the row(s) that matches the given permission level
     */
    public ResultSet getDBRowPermissionLevel(int permissionLevel) {
        return getDBRowFromCol("permissionLevel",  Integer.toString(permissionLevel));
    }

    /**
     * Returns a ResultSet of the row(s) that matches the given position
     *
     * @param position the position of the row to get
     * @return a ResultSet of the row(s) that matches the given position
     */
    public ResultSet getDBRowPosition(String position) {
        return getDBRowFromCol("position", position);
    }


    /**
     * Updates the rows in the Users table that matches the given column and value
     *
     * @param user the user to update
     * @param col the columns to update
     * @param val the values to update the columns to
     */
    public void updateRow(User user, String[] col, String[] val) {
        if (col == null || val == null)
            throw new IllegalArgumentException("The column and value arrays must be the same length");
        DButils.updateRow("Users", col, val, "username = '" + user.getUsername() + "'");
    }

    /**
     * Update a specific user's username in the database
     *
     * @param user the user to update
     * @param username the new username
     *
     */
    public void updateUsername(User user, String username) {
        String[] col = { "username" };
        String[] value = { username };
        updateRow(user, col, value);
        user.setUsername(username);
    }

    /**
     * Update a specific user's password in the database
     *
     * @param user the user to update
     * @param password the new password
     *
     */
    public void updatePassword(User user, String password) {
        if (password.length() < 8)
            throw new IllegalArgumentException("The password is too short -- it must be at least 8 characters long.");
        String[] col = { "password" };
        String[] value = { password };
        updateRow(user, col, value);
        user.setPassword(password);
    }

    /**
     * Update a specific user's permission level in the database
     *
     * @param user the user to update
     * @param permissionLevel the new permission level
     *
     */
    public void updatePermissionLevel(User user, int permissionLevel) {
        if (permissionLevel < 0)
            throw new IllegalArgumentException("The permission level must be greater than 0.");
        String[] col = { "permissionLevel" };
        String[] value = { Integer.toString(permissionLevel) };
        updateRow(user, col, value);
        user.setPermissionLevel(permissionLevel);
    }

    /**
     * Update a specific user's position in the database
     *
     * @param user the user to update
     * @param position the new position
     *
     */
    public void updatePosition(User user, String position) {
        if (position == null || position.equals(""))
            throw new IllegalArgumentException("The floor is null or empty");
        String[] col = { "position" };
        String[] value = { position };
        updateRow(user, col, value);
        user.setPosition(position);
    }

    /**
     * Returns a User object from the given username
     *
     * @param username the username of the user to get
     * @return a User object from the given username
     */
    public User getUser(String username) {
        ResultSet rs = DButils.getRowCond("Users", "*", "username like '" + username + "'");
        try {
            if (rs != null) {
                if (rs.isBeforeFirst()) {
                    rs.next();
                    return new User(rs);
                } else throw new SQLException("No rows found"); }
        } catch (SQLException e) {
            // handle error
            System.err.println("ERROR Query Failed: " + e.getMessage());
            return null;
        }
        return null;
    }
}

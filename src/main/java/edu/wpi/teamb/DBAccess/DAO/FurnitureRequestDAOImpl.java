package edu.wpi.teamb.DBAccess.DAO;

import edu.wpi.teamb.DBAccess.DBconnection;
import edu.wpi.teamb.DBAccess.DButils;
import edu.wpi.teamb.DBAccess.Full.FullFactory;
import edu.wpi.teamb.DBAccess.Full.FullFurnitureRequest;
import edu.wpi.teamb.DBAccess.Full.IFull;
import edu.wpi.teamb.DBAccess.ORMs.FurnitureRequest;
import edu.wpi.teamb.DBAccess.ORMs.Request;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FurnitureRequestDAOImpl implements IDAO {
    ArrayList<FullFurnitureRequest> furnitureRequests;

        public FurnitureRequestDAOImpl() {
            furnitureRequests = getAllHelper();
        }


        /**
         * Gets all local FurnitureRequest objects
         *
         * @return an ArrayList of all local FurnitureRequest objects
         */
        @Override
        public ArrayList<FullFurnitureRequest> getAll() {
            return furnitureRequests;
        }

        /**
         * Sets all FurnitureRequest objects using the database
         */
        @Override
        public void setAll() { furnitureRequests = getAllHelper(); }

        /**
         * Gets a FurnitureRequest including the information from request table
         *
         * @param id of the request
         * @return a FullFurnitureRequest with information from request and furniture request tables
         */
        @Override
        public FullFurnitureRequest get(Object id) {
            Integer idInt = (Integer) id;
            FurnitureRequest fr = null;
            Request r = null;
            try {
                ResultSet rs = DButils.getRowCond("furniturerequests", "*", "id = " + idInt);
                rs.next();
                fr = new FurnitureRequest(rs);
                ResultSet rs1 = RequestDAOImpl.getDBRowID(idInt);
                rs1.next();
                r = new Request(rs1);
            } catch (SQLException e) {
                System.err.println("ERROR Query Failed in method 'FurnitureRequestDAOImpl.get': " + e.getMessage());
                return null;
            }
            return new FullFurnitureRequest(r, fr);
        }

        /**
         * Gets all FurnitureRequest objects from the database
         *
         * @return an ArrayList of all FurnitureRequest objects from the database
         */
        public ArrayList<FullFurnitureRequest> getAllHelper() {
            FullFactory ff = new FullFactory();
            IFull furn = ff.getFullRequest("Furniture");
            ArrayList<FurnitureRequest> frs = new ArrayList<FurnitureRequest>();
            try {
                ResultSet rs = getDBRowAllRequests();
                while (rs.next()) {
                    frs.add(new FurnitureRequest(rs));
                }
                return (ArrayList<FullFurnitureRequest>) furn.listFullRequests(frs);
            } catch (SQLException e) {
                System.err.println("ERROR Query Failed in method 'FurnitureRequestDAOImpl.getAllHelper': " + e.getMessage());
            }
            DBconnection.getDBconnection().closeDBconnection();
            DBconnection.getDBconnection().forceClose();
            return (ArrayList<FullFurnitureRequest>) furn.listFullRequests(frs);
        }

        /**
         * Adds a FurnitureRequest object to the both the database and local list
         *
         * @param request the FurnitureRequest object to be added
         */
        @Override
        public void add(Object request) {
            String[] furnitureReq = (String[]) request;
            String[] values = {furnitureReq[0], furnitureReq[1], "Furniture", furnitureReq[2], furnitureReq[3], furnitureReq[4], furnitureReq[5], furnitureReq[6]};
            int id = insertDBRowNewFurnitureRequest(values);
            ResultSet rs = DButils.getRowCond("requests", "dateSubmitted", "id = " + id);
            Timestamp dateSubmitted = null;
            try {
                rs.next();
                dateSubmitted = rs.getTimestamp("dateSubmitted");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            furnitureRequests.add(new FullFurnitureRequest(id, furnitureReq[0], dateSubmitted, furnitureReq[1], furnitureReq[2], furnitureReq[3], furnitureReq[4], furnitureReq[5], Boolean.getBoolean(furnitureReq[6])));
            RequestDAOImpl.getRequestDaoImpl().getAll().add(new Request(id, furnitureReq[0], dateSubmitted, furnitureReq[1], "Furniture", furnitureReq[2], furnitureReq[3]));
        }

        /**
         * Removes a FurnitureRequest from the both the database and the local list
         *
         * @param request the FurnitureRequest object to be removed
         */
        @Override
        public void delete(Object request) {
            FullFurnitureRequest ffr = (FullFurnitureRequest) request;
            DButils.deleteRow("furniturerequests", "id =" + ffr.getId() + "");
            DButils.deleteRow("requests", "id =" + ffr.getId() + "");
            furnitureRequests.remove(ffr);
            Request req = new Request(ffr.getId(), ffr.getEmployee(), ffr.getDateSubmitted(), ffr.getRequestStatus(), ffr.getRequestType(), ffr.getLocationName(), ffr.getNotes());
            RequestDAOImpl.getRequestDaoImpl().getAll().remove(req);
        }

        /**
         * Updates a FurnitureRequest object in both the database and local list
         *
         * @param request the FurnitureRequest object to be updated
         */
        @Override
        public void update(Object request) {
            FullFurnitureRequest ffr = (FullFurnitureRequest) request;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //String[] values = {Integer.toString(ffr.getId()), ffr.getEmployee(), ffr.getFloor(), ffr.getRoomNumber(), ffr.getDateSubmitted().toString(), ffr.getRequestStatus(), ffr.getRequestType(), ffr.getType(), ffr.getModel(), String.valueOf(ffr.isAssembly())};
            String[] colsFurniture = {"type", "model", "assembly"};
            String[] valuesFurniture = {ffr.getType(), ffr.getModel(), String.valueOf(ffr.isAssembly())};
            String[] colsReq = {"employee", "requeststatus", "requesttype", "locationname", "notes"};
            String[] valuesReq = {ffr.getEmployee(), ffr.getRequestStatus(), ffr.getRequestType(), ffr.getLocationName(), ffr.getNotes()};;
            DButils.updateRow("furniturerequests", colsFurniture, valuesFurniture, "id = " + ffr.getId());
            DButils.updateRow("requests", colsReq, valuesReq, "id = " + ffr.getId());
            for (int i = 0; i < furnitureRequests.size(); i++) {
                if (furnitureRequests.get(i).getId() == ffr.getId()) {
                    furnitureRequests.set(i, ffr);
                }
            }
            Request req = new Request(ffr.getId(), ffr.getEmployee(), ffr.getDateSubmitted(), ffr.getRequestStatus(), ffr.getRequestType(), ffr.getLocationName(), ffr.getNotes());
            RequestDAOImpl.getRequestDaoImpl().update(req);
        }

        /**
         * Gets all rows from the database of conference requests
         *
         * @return the result set of the row(s) that matches the given column and value
         */
        private ResultSet getDBRowAllRequests() throws SQLException {
            return DButils.getCol("furniturerequests", "*");
        }

        /**
         * Updates the special instructions of a row in the FurnitureRequests table
         *
         * @param specialInstructions the specialInstructions to update
         * @param col the column to search for the value
         * @param val the value to search for in the column
         */
        public void updateRowSpecialInstructions(String specialInstructions, String[] col, String[] val) {
            updateRows(col, val, "specialInstructions = " + specialInstructions);
        }

        /**
         * Returns a String representation of a FlowerRequest
         *
         * @param request the FlowerRequest to turn into a String
         * @return the String representation of the FlowerRequest
         */
        public String toString(FurnitureRequest request) {
            return request.getId() + " " + request.getType() + " " + request.getModel() + " " + request.isAssembly();
        }

        /**
         * Inserts a new row into the FurnitureRequests table
         *
         * @param values the values to insert into the row
         * @return the id of the row that was inserted
         */
        public static int insertDBRowNewFurnitureRequest(String[] values) {
            String[] colsFurniture = {"id", "type", "model", "assembly",};
            String[] colsReq = {"employee", "requeststatus","requesttype", "locationname", "notes"};
            String[] valuesReq = {values[0], values[1], values[2], values[3], values[4]};
            int id = DButils.insertRowRequests("requests", colsReq, valuesReq);
            String[] valuesFurniture = {Integer.toString(id), values[5], values[6], values[7]};
            DButils.insertRow("furniturerequests", colsFurniture, valuesFurniture);
            return id;
        }

        /**
         * Returns a ResultSet of the row(s) that matches the given column and value in the FurnitureRequests table
         *
         * @param col the column to search for the value
         * @param value the value to search for in the column
         * @return a ResultSet of the row(s) that matches the given column and value
         */
        private ResultSet getDBRowFromCol(String col, String value) {
            return DButils.getRowCond("furniturerequests", "*", col + " = " + value);
        }

        /**
         * Returns a ResultSet of the row(s) that matches the given ID in the FurnitureRequests table
         *
         * @param id the id of the row to get
         * @return a ResultSet of the row(s) that matches the given ID
         */
        public ResultSet getDBRowID(int id) {
            return getDBRowFromCol("id", Integer.toString(id));
        }

        /**
         * Returns a ResultSet of the row(s) that matches the given furnitureType in the FurnitureRequests table
         *
         * @param furnitureType the furnitureType of the row to get
         * @return a ResultSet of the row(s) that matches the given furnitureType
         */
        public ResultSet getDBRowFurnitureType(String furnitureType) {
            return getDBRowFromCol("type", furnitureType);
        }

        /**
         * Returns a ResultSet of the row(s) that matches the given model in the FurnitureRequests table
         *
         * @param model the model of the row to get
         * @return a ResultSet of the row(s) that matches the given model
         */
        public ResultSet getDBRowModel(String model) {
            return getDBRowFromCol("model", model);
        }

        /**
         * Returns a ResultSet of the row(s) that matches the given assembly in the FurnitureRequests table
         *
         * @param assembly the employee of the row to get
         * @return a ResultSet of the row(s) that matches the given assembly
         */
        public ResultSet getDBRowAssembly(String assembly) {
            return getDBRowFromCol("assembly", assembly);
        }

        /**
         * Updates the rows in the FurnitureRequests table that match the given condition
         *
         * @param col the columns to update
         * @param val the values to update the columns to
         * @param cond the condition to search for
         */
        private void updateRows(String[] col, String[] val, String cond) {
            if (col.length != val.length)
                throw new IllegalArgumentException("Column and value arrays must be the same length");
            DButils.updateRow("FurnitureRequests", col, val, cond);
        }

        /**
         * Updates the rows in the FurnitureRequests table that match the given ID
         *
         * @param id the id of the row to update
         * @param col the columns to update
         * @param val the values to update the columns to
         */
        public void updateRowID(int id, String[] col, String[] val) {
            updateRows(col, val, "id = " + id);
        }

        /**
         * Updates the rows in the FurnitureRequests table that match the given furnitureType
         *
         * @param furnitureType the furnitureType of the row to update
         * @param col the columns to update
         * @param val the values to update the columns to
         */
        public void updateRowFurnitureType(String furnitureType, String[] col, String[] val) {
            updateRows(col, val, "furnitureType = " + furnitureType);
        }

        /**
         * Updates the rows in the FurnitureRequests table that match the given model
         *
         * @param model the model of the row to update
         * @param col the columns to update
         * @param val the values to update the columns to
         */
        public void updateRowModel(String model, String[] col, String[] val) {
            updateRows(col, val, "model = " + model);
        }

        /**
         * Updates the rows in the FurnitureRequests table that match the given assembly
         *
         * @param assembly the assembly of the row to update
         * @param col the columns to update
         * @param val the values to update the columns to
         */
        public void updateRowAssembly(boolean assembly, String[] col, String[] val) {
            updateRows(col, val, "assembly = " + assembly);
        }

}

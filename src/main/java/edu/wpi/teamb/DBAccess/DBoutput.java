package edu.wpi.teamb.DBAccess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBoutput {

    /**
     * This method exports the Nodes table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void exportNodesToCSV(String filename, int location) {

        try {
            String allQuery = "SELECT * FROM Nodes";
            Statement allStmt = DBconnection.getDBconnection().getConnection().createStatement();
            ResultSet allRS = allStmt.executeQuery(allQuery);

            BufferedWriter bw = switch (location) {
                case 0 -> new BufferedWriter(new FileWriter(filename));
                case 1 -> new BufferedWriter(new FileWriter("./" + filename + ".csv"));
                case 2 -> new BufferedWriter(new FileWriter(filename + ".csv"));
                case 3 -> new BufferedWriter(new FileWriter("./src/main/resources/CSV Files/" + filename + ".csv"));
                case 4 -> new BufferedWriter(new FileWriter("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                default -> throw new IllegalStateException("Unexpected value: " + location);
            };

            bw.write("nodeID,xCoord,yCoord,floor,building");

            while (allRS.next()) {
                int nodeID = allRS.getInt("nodeID");
                int xCoord = allRS.getInt("xCoord");
                int yCoord = allRS.getInt("yCoord");
                String floor = allRS.getString("floor");
                String building = allRS.getString("building");

                String line = String.format(
                        "%d,%d,%d,%s,%s",
                        nodeID, xCoord, yCoord, floor, building);

                bw.newLine();
                bw.write(line);
            }
            allStmt.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to file " + filename + " in method 'exportNodesToCSV'");
        } catch (SQLException e) {
            System.err.println("ERROR: SQL query failed in method 'exportNodesToCSV'");
        }
    }

    /**
     * This method exports the Edges table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void exportEdgesToCSV(String filename, int location) {

        try {
            String allQuery = "SELECT * FROM Edges";
            Statement allStmt = DBconnection.getDBconnection().getConnection().createStatement();
            ResultSet allRS = allStmt.executeQuery(allQuery);

            BufferedWriter bw = switch (location) {
                case 0 -> new BufferedWriter(new FileWriter(filename));
                case 1 -> new BufferedWriter(new FileWriter("./" + filename + ".csv"));
                case 2 -> new BufferedWriter(new FileWriter(filename + ".csv"));
                case 3 -> new BufferedWriter(new FileWriter("./src/main/resources/CSV Files/" + filename + ".csv"));
                case 4 -> new BufferedWriter(new FileWriter("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                default -> throw new IllegalStateException("Unexpected value: " + location);
            };

            bw.write("startNode,endNode");

            while (allRS.next()) {
                String startNode = allRS.getString("startNode");
                String endNode = allRS.getString("endNode");

                String line = String.format("%s,%s", startNode, endNode);

                bw.newLine();
                bw.write(line);
            }
            allStmt.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to file " + filename + " in method 'exportEdgesToCSV'");
        } catch (SQLException e) {
            System.err.println("ERROR: SQL query failed in method 'exportEdgesToCSV'");
        }
    }

    /**
     * This method exports the LocationNames table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void exportLocationNamesToCSV(String filename, int location) {

        try {
            String allQuery = "SELECT * FROM locationNames";
            Statement allStmt = DBconnection.getDBconnection().getConnection().createStatement();
            ResultSet allRS = allStmt.executeQuery(allQuery);

            BufferedWriter bw = switch (location) {
                case 0 -> new BufferedWriter(new FileWriter(filename));
                case 1 -> new BufferedWriter(new FileWriter("./" + filename + ".csv"));
                case 2 -> new BufferedWriter(new FileWriter(filename + ".csv"));
                case 3 -> new BufferedWriter(new FileWriter("./src/main/resources/CSV Files/" + filename + ".csv"));
                case 4 -> new BufferedWriter(new FileWriter("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                default -> throw new IllegalStateException("Unexpected value: " + location);
            };

            bw.write("longName,shortName,nodeType");

            while (allRS.next()) {
                String longName = allRS.getString("longName");
                String shortName = allRS.getString("shortName");
                String nodeType = allRS.getString("nodeType");

                String line = String.format("%s,%s,%s", longName, shortName, nodeType);

                bw.newLine();
                bw.write(line);
            }
            allStmt.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to file " + filename + " in method 'exportLocationNamesToCSV'");
        } catch (SQLException e) {
            System.err.println("ERROR: SQL query failed in method 'exportLocationNamesToCSV'");
        }
    }

    /**
     * This method exports the Moves table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */

    public static void exportMovesToCSV(String filename, int location) {

        try {
            String allQuery = "SELECT * FROM moves";
            Statement allStmt = DBconnection.getDBconnection().getConnection().createStatement();
            ResultSet allRS = allStmt.executeQuery(allQuery);

            BufferedWriter bw = switch (location) {
                case 0 -> new BufferedWriter(new FileWriter(filename));
                case 1 -> new BufferedWriter(new FileWriter("./" + filename + ".csv"));
                case 2 -> new BufferedWriter(new FileWriter(filename + ".csv"));
                case 3 -> new BufferedWriter(new FileWriter("./src/main/resources/CSV Files/" + filename + ".csv"));
                case 4 -> new BufferedWriter(new FileWriter("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                default -> throw new IllegalStateException("Unexpected value: " + location);
            };

            bw.write("nodeID,longName,date");

            while (allRS.next()) {
                int nodeID = allRS.getInt("nodeID");
                String longName = allRS.getString("longName");
                String date = allRS.getString("date");

                String line = String.format("%d,%s,%s", nodeID, longName, date);

                bw.newLine();
                bw.write(line);
            }
            allStmt.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to file " + filename + " in method 'exportMovesToCSV'");
        } catch (SQLException e) {
            System.err.println("ERROR: SQL query failed in method 'exportMovesToCSV'");
        }
    }

    /**
     * This method exports the Users table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void exportUsersToCSV(String filename, int location) {
        try {
            String allQuery = "SELECT * FROM users";
            Statement allStmt = DBconnection.getDBconnection().getConnection().createStatement();
            ResultSet allRS = allStmt.executeQuery(allQuery);

            BufferedWriter bw = switch (location) {
                case 0 -> new BufferedWriter(new FileWriter(filename));
                case 1 -> new BufferedWriter(new FileWriter("./" + filename + ".csv"));
                case 2 -> new BufferedWriter(new FileWriter(filename + ".csv"));
                case 3 -> new BufferedWriter(new FileWriter("./src/main/resources/CSV Files/" + filename + ".csv"));
                case 4 -> new BufferedWriter(new FileWriter("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                default -> throw new IllegalStateException("Unexpected value: " + location);
            };

            bw.write("name,username,password,email,permissionLevel");

            while (allRS.next()) {
                String name = allRS.getString("name");
                String username = allRS.getString("username");
                String password = allRS.getString("password");
                String email = allRS.getString("email");
                int permissionLevel = allRS.getInt("permissionLevel");

                String line = String.format("%s,%s,%s,%s,%d", name, username, password, email, permissionLevel);

                bw.newLine();
                bw.write(line);
            }
            allStmt.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to file " + filename + " in method 'exportUsersToCSV'");
        } catch (SQLException e) {
            System.err.println("ERROR: SQL query failed in method 'exportUsersToCSV'");
        }
    }

    /**
     * This method exports the Requests table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void exportRequestsToCSV(String filename, int location) {
        try {
            String allQuery = "SELECT * FROM requests";
            Statement allStmt = DBconnection.getDBconnection().getConnection().createStatement();
            ResultSet allRS = allStmt.executeQuery(allQuery);

            BufferedWriter bw = switch (location) {
                case 0 -> new BufferedWriter(new FileWriter(filename));
                case 1 -> new BufferedWriter(new FileWriter("./" + filename + ".csv"));
                case 2 -> new BufferedWriter(new FileWriter(filename + ".csv"));
                case 3 -> new BufferedWriter(new FileWriter("./src/main/resources/CSV Files/" + filename + ".csv"));
                case 4 -> new BufferedWriter(new FileWriter("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                default -> throw new IllegalStateException("Unexpected value: " + location);
            };

            bw.write("id,employee,dateSubmitted,requestStatus,requestType,locationName,notes");

            while (allRS.next()) {
                int id = allRS.getInt("id");
                String employee = allRS.getString("employee");
                String dateSubmitted = allRS.getString("dateSubmitted");
                String requestStatus = allRS.getString("requestStatus");
                String requestType = allRS.getString("requestType");
                String locationName = allRS.getString("locationName");
                String notes = allRS.getString("notes");

                String line = String.format("%d,%s,%s,%s,%s,%s,%s", id, employee, dateSubmitted, requestStatus, requestType, locationName, notes);

                bw.newLine();
                bw.write(line);
            }
            allStmt.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to file " + filename + " in method 'exportRequestsToCSV'");
        } catch (SQLException e) {
            System.err.println("ERROR: SQL query failed in method 'exportRequestsToCSV'");
        }
    }

    /**
     * This method exports the ConferenceRequests table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void exportConferenceRequestsToCSV(String filename, int location) {
        try {
            String allQuery = "SELECT * FROM conferenceRequests";
            Statement allStmt = DBconnection.getDBconnection().getConnection().createStatement();
            ResultSet allRS = allStmt.executeQuery(allQuery);

            BufferedWriter bw = switch (location) {
                case 0 -> new BufferedWriter(new FileWriter(filename));
                case 1 -> new BufferedWriter(new FileWriter("./" + filename + ".csv"));
                case 2 -> new BufferedWriter(new FileWriter(filename + ".csv"));
                case 3 -> new BufferedWriter(new FileWriter("./src/main/resources/CSV Files/" + filename + ".csv"));
                case 4 -> new BufferedWriter(new FileWriter("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                default -> throw new IllegalStateException("Unexpected value: " + location);
            };

            bw.write("id,dateRequested,eventName,bookingReason,duration");

            while (allRS.next()) {
                int id = allRS.getInt("id");
                String dateRequested = allRS.getString("dateRequested");
                String eventName = allRS.getString("eventName");
                String bookingReason = allRS.getString("bookingReason");
                int duration = allRS.getInt("duration");

                String line = String.format("%d,%s,%s,%s,%s", id, dateRequested, eventName, bookingReason, duration);

                bw.newLine();
                bw.write(line);
            }
            allStmt.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to file " + filename + " in method 'exportConferenceRequestsToCSV'");
        } catch (SQLException e) {
            System.err.println("ERROR: SQL query failed in method 'exportConferenceRequestsToCSV'");
        }
    }

    /**
     * This method exports the FlowerRequests table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void exportFlowerRequestsToCSV(String filename, int location) {
        try {
            String allQuery = "SELECT * FROM flowerRequests";
            Statement allStmt = DBconnection.getDBconnection().getConnection().createStatement();
            ResultSet allRS = allStmt.executeQuery(allQuery);

            BufferedWriter bw = switch (location) {
                case 0 -> new BufferedWriter(new FileWriter(filename));
                case 1 -> new BufferedWriter(new FileWriter("./" + filename + ".csv"));
                case 2 -> new BufferedWriter(new FileWriter(filename + ".csv"));
                case 3 -> new BufferedWriter(new FileWriter("./src/main/resources/CSV Files/" + filename + ".csv"));
                case 4 -> new BufferedWriter(new FileWriter("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                default -> throw new IllegalStateException("Unexpected value: " + location);
            };

            bw.write("id,flowerType,color,size,message");

            while (allRS.next()) {
                int id = allRS.getInt("id");
                String flowerType = allRS.getString("flowerType");
                String color = allRS.getString("color");
                String size = allRS.getString("size");
                String message = allRS.getString("message");

                String line = String.format("%d,%s,%s,%s,%s", id, flowerType, color, size, message);

                bw.newLine();
                bw.write(line);
            }
            allStmt.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to file " + filename + " in method 'exportFlowerRequestsToCSV'");
        } catch (SQLException e) {
            System.err.println("ERROR: SQL query failed in method 'exportFlowerRequestsToCSV'");
        }
    }

    /**
     * This method exports the FurnitureRequests table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void exportFurnitureRequestsToCSV(String filename, int location) {
        try {
            String allQuery = "SELECT * FROM furnitureRequests";
            Statement allStmt = DBconnection.getDBconnection().getConnection().createStatement();
            ResultSet allRS = allStmt.executeQuery(allQuery);

            BufferedWriter bw = switch (location) {
                case 0 -> new BufferedWriter(new FileWriter(filename));
                case 1 -> new BufferedWriter(new FileWriter("./" + filename + ".csv"));
                case 2 -> new BufferedWriter(new FileWriter(filename + ".csv"));
                case 3 -> new BufferedWriter(new FileWriter("./src/main/resources/CSV Files/" + filename + ".csv"));
                case 4 -> new BufferedWriter(new FileWriter("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                default -> throw new IllegalStateException("Unexpected value: " + location);
            };

            bw.write("id,type,model,assembly");

            while (allRS.next()) {
                int id = allRS.getInt("id");
                String type = allRS.getString("type");
                String model = allRS.getString("model");
                String assembly = allRS.getString("assembly");

                String line = String.format("%d,%s,%s,%s", id, type, model, assembly);

                bw.newLine();
                bw.write(line);
            }
            allStmt.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to file " + filename + " in method 'exportFurnitureRequestsToCSV'");
        } catch (SQLException e) {
            System.err.println("ERROR: SQL query failed in method 'exportFurnitureRequestsToCSV'");
        }
    }

    /**
     * This method exports the MealRequests table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void exportMealRequestsToCSV(String filename, int location) {
        try {
            String allQuery = "SELECT * FROM mealRequests";
            Statement allStmt = DBconnection.getDBconnection().getConnection().createStatement();
            ResultSet allRS = allStmt.executeQuery(allQuery);

            BufferedWriter bw = switch (location) {
                case 0 -> new BufferedWriter(new FileWriter(filename));
                case 1 -> new BufferedWriter(new FileWriter("./" + filename + ".csv"));
                case 2 -> new BufferedWriter(new FileWriter(filename + ".csv"));
                case 3 -> new BufferedWriter(new FileWriter("./src/main/resources/CSV Files/" + filename + ".csv"));
                case 4 -> new BufferedWriter(new FileWriter("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                default -> throw new IllegalStateException("Unexpected value: " + location);
            };

            bw.write("id,orderFrom,food,drink,snack");

            while (allRS.next()) {
                int id = allRS.getInt("id");
                String orderFrom = allRS.getString("orderFrom");
                String food = allRS.getString("food");
                String drink = allRS.getString("drink");
                String snack = allRS.getString("snack");

                String line = String.format("%d,%s,%s,%s,%s", id, orderFrom, food, drink, snack);

                bw.newLine();
                bw.write(line);
            }
            allStmt.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to file " + filename + " in method 'exportMealRequestsToCSV'");
        } catch (SQLException e) {
            System.err.println("ERROR: SQL query failed in method 'exportMealRequestsToCSV'");
        }
    }

    /**
     * This method exports the OfficeRequests table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void exportOfficeRequestsToCSV(String filename, int location) {
        try {
            String allQuery = "SELECT * FROM officeRequests";
            Statement allStmt = DBconnection.getDBconnection().getConnection().createStatement();
            ResultSet allRS = allStmt.executeQuery(allQuery);

            BufferedWriter bw = switch (location) {
                case 0 -> new BufferedWriter(new FileWriter(filename));
                case 1 -> new BufferedWriter(new FileWriter("./" + filename + ".csv"));
                case 2 -> new BufferedWriter(new FileWriter(filename + ".csv"));
                case 3 -> new BufferedWriter(new FileWriter("./src/main/resources/CSV Files/" + filename + ".csv"));
                case 4 -> new BufferedWriter(new FileWriter("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                default -> throw new IllegalStateException("Unexpected value: " + location);
            };

            bw.write("id,item,quantity,type");

            while (allRS.next()) {
                int id = allRS.getInt("id");
                String item = allRS.getString("item");
                int quantity = allRS.getInt("quantity");
                String type = allRS.getString("type");

                String line = String.format("%d,%s,%s,%s", id, item, quantity, type);

                bw.newLine();
                bw.write(line);
            }
            allStmt.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to file " + filename + " in method 'exportOfficeRequestsToCSV'");
        } catch (SQLException e) {
            System.err.println("ERROR: SQL query failed in method 'exportOfficeRequestsToCSV'");
        }
    }

    /**
     * This method exports the Alerts table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void exportAlertsToCSV(String filename, int location) {
        try {
            String allQuery = "SELECT * FROM alerts";
            Statement allStmt = DBconnection.getDBconnection().getConnection().createStatement();
            ResultSet allRS = allStmt.executeQuery(allQuery);

            BufferedWriter bw = switch (location) {
                case 0 -> new BufferedWriter(new FileWriter(filename));
                case 1 -> new BufferedWriter(new FileWriter("./" + filename + ".csv"));
                case 2 -> new BufferedWriter(new FileWriter(filename + ".csv"));
                case 3 -> new BufferedWriter(new FileWriter("./src/main/resources/CSV Files/" + filename + ".csv"));
                case 4 -> new BufferedWriter(new FileWriter("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                default -> throw new IllegalStateException("Unexpected value: " + location);
            };

            bw.write("id,item,quantity,type,employee");

            while (allRS.next()) {
                int id = allRS.getInt("id");
                String title = allRS.getString("title");
                String description = allRS.getString("description");
                Date created_at = allRS.getDate("created_at");
                String employee = allRS.getString("employee");

                String line = String.format("%d,%s,%s,%s,%s", id, title, description, created_at.toString(), employee);

                bw.newLine();
                bw.write(line);
            }
            allStmt.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to file " + filename + " in method 'exportAlertsToCSV'");
        } catch (SQLException e) {
            System.err.println("ERROR: SQL query failed in method 'exportAlertsToCSV'");
        }
    }

    /**
     * This method exports the Signage table into a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void exportSignsToCSV(String filename, int location) {
        try {
            String allQuery = "SELECT * FROM signs";
            Statement allStmt = DBconnection.getDBconnection().getConnection().createStatement();
            ResultSet allRS = allStmt.executeQuery(allQuery);

            BufferedWriter bw = switch (location) {
                case 0 -> new BufferedWriter(new FileWriter(filename));
                case 1 -> new BufferedWriter(new FileWriter("./" + filename + ".csv"));
                case 2 -> new BufferedWriter(new FileWriter(filename + ".csv"));
                case 3 -> new BufferedWriter(new FileWriter("./src/main/resources/CSV Files/" + filename + ".csv"));
                case 4 -> new BufferedWriter(new FileWriter("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                default -> throw new IllegalStateException("Unexpected value: " + location);
            };

            bw.write("signageGroup,locationName,direction,startDate,endDate,singleBlock,signLocation");

            while (allRS.next()) {
                String signageGroup = allRS.getString("signageGroup");
                String locationName = allRS.getString("locationName");
                String direction = allRS.getString("direction");
                Date startDate = allRS.getDate("startDate");
                Date endDate = allRS.getDate("endDate");
                String singleBlock = allRS.getString("singleBlock");
                String signLocation = allRS.getString("signLocation");


                String line = String.format("%s,%s,%s,%s,%s,%s,%s", signageGroup, locationName, direction, startDate.toString(), endDate.toString(), singleBlock, signLocation);

                bw.newLine();
                bw.write(line);
            }
            allStmt.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to file " + filename + " in method 'exportSignsToCSV'");
        } catch (SQLException e) {
            System.err.println("ERROR: SQL query failed in method 'exportSignsToCSV'");
        }
    }
}

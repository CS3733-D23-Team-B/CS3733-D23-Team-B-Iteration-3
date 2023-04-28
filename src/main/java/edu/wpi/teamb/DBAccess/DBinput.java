package edu.wpi.teamb.DBAccess;

import edu.wpi.teamb.DBAccess.DAO.Repository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

public class DBinput {

    /**
     * This method imports the Nodes table from a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void importNodesFromCSV(String filename, int location) {

        String line = "";
        String splitBy = ",";

        BufferedReader br = null;

        try {
            try {
                br = switch (location) {
                    case 0 -> new BufferedReader(new FileReader(filename));
                    case 1 -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                    case 2 -> new BufferedReader(new FileReader(filename + ".csv"));
                    case 3 -> new BufferedReader(new FileReader("./src/main/resources/CSV Files/" + filename + ".csv"));
                    case 4 -> new BufferedReader(new FileReader("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                    default -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                };
            } catch (FileNotFoundException e) {
                System.out.println("Error reading the file in the method 'DBinput.importNodesFromCSV'");
                e.printStackTrace();
            }

            Statement tableStmt = DBconnection.getDBconnection().getConnection().createStatement();

            String dropMovesTable = "DROP TABLE IF EXISTS Moves";
            int dropUpdateMoves = tableStmt.executeUpdate(dropMovesTable);

            String dropNodesTable = "DROP TABLE IF EXISTS Nodes";
            int dropUpdateNodes = tableStmt.executeUpdate(dropNodesTable);

            String createTableNodes = "CREATE TABLE Nodes (nodeID INT PRIMARY KEY, xCoord INT, yCoord INT, floor VARCHAR(255), building VARCHAR(255));";
            int tableUpdateNodes = tableStmt.executeUpdate(createTableNodes);

            String createTableMoves = "CREATE TABLE moves " +
                    "(nodeID INT, longName VARCHAR(255), date VARCHAR(20), primary key (nodeID, longName, date)," +
                    "constraint fk_nodeID foreign key(nodeID) references nodes(nodeID)," +
                    "constraint fk_longName foreign key(longName) references locationNames(longName));";
            int tableUpdateMoves = tableStmt.executeUpdate(createTableMoves);
            importMovesFromCSV("Moves", 3);

            try {
                while (((line = br.readLine()) != null)) {
                    String[] nodeValues = line.split(splitBy);
                    if (!(nodeValues[0].equals("nodeID"))) {
                        // System.out.println(nodeValues[0]);
                        String rowQuery = "INSERT INTO Nodes (nodeID, xCoord, yCoord, floor, building) VALUES ("
                                + nodeValues[0]
                                + ","
                                + nodeValues[1]
                                + ","
                                + nodeValues[2]
                                + ","
                                + "'"
                                + nodeValues[3]
                                + "'"
                                + ","
                                + "'"
                                + nodeValues[4]
                                + "'"
                                + ");";
                        Statement rowStmt = DBconnection.getDBconnection().getConnection().createStatement();
                        int rowUpdate = rowStmt.executeUpdate(rowQuery);
                        rowStmt.close();
                    }
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error reading the file in the method 'DBinput.importNodesFromCSV'");
                e.printStackTrace();
            }
            tableStmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: Query could not be executed in the method 'DBinput.importNodesFromCSV'");
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method imports the Edges table from a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void importEdgesFromCSV(String filename, int location) {

        String line = "";
        String splitBy = ",";

        BufferedReader br = null;

        try {
            try {
                br = switch (location) {
                    case 0 -> new BufferedReader(new FileReader(filename));
                    case 1 -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                    case 2 -> new BufferedReader(new FileReader(filename + ".csv"));
                    case 3 -> new BufferedReader(new FileReader("./src/main/resources/CSV Files/" + filename + ".csv"));
                    case 4 -> new BufferedReader(new FileReader("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                    default -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                };
            } catch (FileNotFoundException e) {
                System.out.println("Error reading the file in the method 'DBinput.importEdgesFromCSV'");
                e.printStackTrace();
            }

            Statement tableStmt = DBconnection.getDBconnection().getConnection().createStatement();
            String dropEdgesTable = "DROP TABLE IF EXISTS Edges";
            int dropUpdate = tableStmt.executeUpdate(dropEdgesTable);
            String createTable = "CREATE TABLE edges (startNode INT, endNode INT, primary key (startNode, endNode));";
            int tableUpdate = tableStmt.executeUpdate(createTable);

            try {
                while (((line = br.readLine()) != null)) {
                    String[] edgeValues = line.split(splitBy);
                    if (!(edgeValues[0].equals("startNode"))) {
                        // System.out.println(edgeValues[0]);
                        String rowQuery = "INSERT INTO Edges (startNode, endNode) VALUES ("
                                + edgeValues[0]
                                + ","
                                + edgeValues[1]
                                + ");";
                        // System.out.println(rowQuery);
                        Statement rowStmt = DBconnection.getDBconnection().getConnection().createStatement();
                        int rowUpdate = rowStmt.executeUpdate(rowQuery);
                        rowStmt.close();
                    }
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error reading the file in the method 'DBinput.importEdgesFromCSV'");
                e.printStackTrace();
            }
            tableStmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: Query could not be executed in the method 'DBinput.importNodesFromCSV'");
            e.printStackTrace();
        }
    }

    /**
     * This method imports the LocationNames table from a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void importLocationNamesFromCSV(String filename, int location) {

        String line = "";
        String splitBy = ",";

        BufferedReader br = null;

        try {
            try {
                br = switch (location) {
                    case 0 -> new BufferedReader(new FileReader(filename));
                    case 1 -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                    case 2 -> new BufferedReader(new FileReader(filename + ".csv"));
                    case 3 -> new BufferedReader(new FileReader("./src/main/resources/CSV Files/" + filename + ".csv"));
                    case 4 -> new BufferedReader(new FileReader("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                    default -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                };
            } catch (FileNotFoundException e) {
                System.out.println("Error reading the file in the method 'DBinput.importLocationNamesFromCSV'");
                e.printStackTrace();
            }

            Statement tableStmt = DBconnection.getDBconnection().getConnection().createStatement();
            String dropMovesTable = "DROP TABLE IF EXISTS moves";
            int dropUpdateMoves = tableStmt.executeUpdate(dropMovesTable);
            String dropLocationNamesTable = "DROP TABLE IF EXISTS locationNames";
            int dropUpdateLocationNames = tableStmt.executeUpdate(dropLocationNamesTable);

            String createTableLocationNames = "CREATE TABLE locationNames " +
                    "(longName VARCHAR(255), shortName VARCHAR(255), nodeType VARCHAR(20), primary key (longName));";
            int tableUpdateLocationNames = tableStmt.executeUpdate(createTableLocationNames);

            String createTableMoves = "CREATE TABLE moves " +
                    "(nodeID INT, longName VARCHAR(255), date VARCHAR(20), primary key (nodeID, longName, date)," +
                    "constraint fk_nodeID foreign key(nodeID) references nodes(nodeID)," +
                    "constraint fk_longName foreign key(longName) references locationNames(longName));";
            int tableUpdateMoves = tableStmt.executeUpdate(createTableMoves);
            importMovesFromCSV("Moves", 3);

            try {
                while (((line = br.readLine()) != null)) {
                    String[] locationNameValues = line.split(splitBy);
                    if (!(locationNameValues[0].equals("longName"))) {
                        // System.out.println(edgeValues[0]);
                        String rowQuery = "INSERT INTO LocationNames (longName, shortName, nodeType) VALUES ("
                                + "'"
                                + locationNameValues[0]
                                + "'"
                                + ","
                                + "'"
                                + locationNameValues[1]
                                + "'"
                                + ","
                                + "'"
                                + locationNameValues[2]
                                + "'"
                                + ");";
                        Statement rowStmt = DBconnection.getDBconnection().getConnection().createStatement();
                        int rowUpdate = rowStmt.executeUpdate(rowQuery);
                        rowStmt.close();
                    }
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error reading the file in the method 'DBinput.importLocationNamesFromCSV'");
                e.printStackTrace();
            }
            tableStmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: Query could not be executed in the method 'DBinput.importLocationNamesFromCSV'");
            e.printStackTrace();
        }
    }

    /**
     * This method imports the Moves table from a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void importMovesFromCSV(String filename, int location) {

        String line = "";
        String splitBy = ",";

        BufferedReader br = null;

        try {
            try {
                br = switch (location) {
                    case 0 -> new BufferedReader(new FileReader(filename));
                    case 1 -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                    case 2 -> new BufferedReader(new FileReader(filename + ".csv"));
                    case 3 -> new BufferedReader(new FileReader("./src/main/resources/CSV Files/" + filename + ".csv"));
                    case 4 -> new BufferedReader(new FileReader("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                    default -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                };
            } catch (FileNotFoundException e) {
                System.out.println("Error reading the file in the method 'DBinput.importMovesFromCSV'");
                e.printStackTrace();
            }

            Statement tableStmt = DBconnection.getDBconnection().getConnection().createStatement();
            String dropMovesTable = "DROP TABLE IF EXISTS moves";
            int dropUpdate = tableStmt.executeUpdate(dropMovesTable);

            String createTable = "CREATE TABLE moves " +
                    "(nodeID INT, longName VARCHAR(255), date VARCHAR(20), primary key (nodeID, longName, date)," +
                    "constraint fk_nodeID foreign key(nodeID) references nodes(nodeID)," +
                    "constraint fk_longName foreign key(longName) references locationNames(longName));";
            int tableUpdate = tableStmt.executeUpdate(createTable);

            try {
                while (((line = br.readLine()) != null)) {
                    String[] moveValues = line.split(splitBy);
                    if (!(moveValues[0].equals("nodeID"))) {
                        // System.out.println(edgeValues[0]);
                        String rowQuery = "INSERT INTO Moves (nodeID, longName, date) VALUES ("
                                + moveValues[0]
                                + ","
                                + "'"
                                + moveValues[1]
                                + "'"
                                + ","
                                + "'"
                                + moveValues[2]
                                + "'"
                                + ");";
                        // System.out.println(rowQuery);
                        Statement rowStmt = DBconnection.getDBconnection().getConnection().createStatement();
                        int rowUpdate = rowStmt.executeUpdate(rowQuery);
                        rowStmt.close();
                    }
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error reading the file in the method 'DBinput.importMovesFromCSV'");
                e.printStackTrace();
            }
            tableStmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: Query could not be executed in the method 'DBinput.importMovesFromCSV'");
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method imports the Users table from a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void importUsersFromCSV(String filename, int location) {

            String line = "";
            String splitBy = ",";

            BufferedReader br = null;

            try {
                try {
                    br = switch (location) {
                        case 0 -> new BufferedReader(new FileReader(filename));
                        case 1 -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                        case 2 -> new BufferedReader(new FileReader(filename + ".csv"));
                        case 3 -> new BufferedReader(new FileReader("./src/main/resources/CSV Files/" + filename + ".csv"));
                        case 4 -> new BufferedReader(new FileReader("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                        default -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                    };
                } catch (FileNotFoundException e) {
                    System.out.println("Error reading the file in the method 'DBinput.importUsersFromCSV'");
                    e.printStackTrace();
                }

                Statement tableStmt = DBconnection.getDBconnection().getConnection().createStatement();
                String dropUsersTable = "DROP TABLE IF EXISTS users";
                int dropUpdate = tableStmt.executeUpdate(dropUsersTable);

                String createTable = "CREATE TABLE users " +
                        "(name VARCHAR(255), username VARCHAR(20) primary key, password VARCHAR(20), email VARCHAR(255) primary key," +
                        "permissionLevel INT);";
                int tableUpdate = tableStmt.executeUpdate(createTable);

                try {
                    while (((line = br.readLine()) != null)) {
                        String[] userValues = line.split(splitBy);
                        if (!(userValues[0].equals("userid"))) {
                            // System.out.println(edgeValues[0]);
                            String rowQuery = "INSERT INTO Users (name, username, password, email, permissionlevel) VALUES ("
                                    + "'"
                                    + userValues[0]
                                    + "'"
                                    + ","
                                    + "'"
                                    + userValues[1]
                                    + "'"
                                    + ","
                                    + "'"
                                    + userValues[2]
                                    + "'"
                                    + ","
                                    + "'"
                                    + userValues[3]
                                    + "'"
                                    + ","
                                    + "'"
                                    + userValues[4]
                                    + "'"
                                    + ");";
                            Statement rowStmt = DBconnection.getDBconnection().getConnection().createStatement();
                            int rowUpdate = rowStmt.executeUpdate(rowQuery);
                            rowStmt.close();
                        }
                    }
                    br.close();
                } catch (IOException e) {
                    System.out.println("Error reading the file in the method 'DBinput.importUsersFromCSV'");
                    e.printStackTrace();
                }
                tableStmt.close();
            } catch (SQLException e) {
                System.out.println("ERROR: Query could not be executed in the method 'DBinput.importUsersFromCSV'");
            }
    }

    /**
     * This method imports the Requests table from a CSV file
     *
     * @param filename The name of the CSV file to be exported (excludes '.csv' extension unless
     *                 location is 2)
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void importRequestsFromCSV(String filename, int location) {

            String line = "";
            String splitBy = ",";

            BufferedReader br = null;

            try {
                try {
                    br = switch (location) {
                        case 0 -> new BufferedReader(new FileReader(filename));
                        case 1 -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                        case 2 -> new BufferedReader(new FileReader(filename + ".csv"));
                        case 3 -> new BufferedReader(new FileReader("./src/main/resources/CSV Files/" + filename + ".csv"));
                        case 4 -> new BufferedReader(new FileReader("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                        default -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                    };
                } catch (FileNotFoundException e) {
                    System.out.println("Error reading the file in the method 'DBinput.importRequestsFromCSV'");
                    e.printStackTrace();
                }

                Statement tableStmt = DBconnection.getDBconnection().getConnection().createStatement();
                String dropConferenceRequestsTable = "DROP TABLE IF EXISTS conferenceRequests";
                int dropUpdateConferenceRequests = tableStmt.executeUpdate(dropConferenceRequestsTable);
                String dropFlowerRequestsTable = "DROP TABLE IF EXISTS flowerRequests";
                int dropUpdateFlowerRequests = tableStmt.executeUpdate(dropFlowerRequestsTable);
                String dropFurnitureRequests = "DROP TABLE IF EXISTS furnitureRequests";
                int dropUpdateFurnitureRequests = tableStmt.executeUpdate(dropFurnitureRequests);
                String dropMealRequestsTable = "DROP TABLE IF EXISTS mealRequests";
                int dropUpdateMealRequests = tableStmt.executeUpdate(dropMealRequestsTable);
                String dropOfficeRequests = "DROP TABLE IF EXISTS officeRequests";
                int dropUpdateOfficeRequests = tableStmt.executeUpdate(dropOfficeRequests);
                String dropRequestsTable = "DROP TABLE IF EXISTS requests";
                int dropUpdateRequests = tableStmt.executeUpdate(dropRequestsTable);

                String createTableRequests = "CREATE TABLE requests " +
                        "(id INT not null primary key, employee VARCHAR(255), dateSubmitted TIMESTAMP, requestStatus VARCHAR(255), requestType VARCHAR(255)," +
                        "locationName VARCHAR(255))";
                int tableUpdateRequests = tableStmt.executeUpdate(createTableRequests);

                String createTableOfficeRequests = "CREATE TABLE officeRequests " +
                        "(id INT not null primary key references requests, item VARCHAR(255), quantity INT, type VARCHAR(255)," +
                        "locationName VARCHAR(255))";
                int tableUpdateOfficeRequests = tableStmt.executeUpdate(createTableOfficeRequests);
                importOfficeRequestsFromCSV("OfficeRequests_sync", 4);

                String createTableMealRequests = "CREATE TABLE mealRequests " +
                        "(id INT not null primary key references requests, orderFrom VARCHAR(255), food VARCHAR(255), drink VARCHAR(255)," +
                        "snack VARCHAR(255))";
                int tableUpdateMealRequests = tableStmt.executeUpdate(createTableMealRequests);
                importMealRequestsFromCSV("MealRequests_sync", 4);

                String createTableFurnitureRequests = "CREATE TABLE furnitureRequests " +
                        "(id INT not null primary key references requests, type VARCHAR(255), model VARCHAR(255)," +
                        "assembly BOOLEAN)";
                int tableUpdateFurnitureRequests = tableStmt.executeUpdate(createTableFurnitureRequests);
                importFurnitureRequestsFromCSV("FurnitureRequests_sync", 4);

                String createTableFlowerRequests = "CREATE TABLE flowerRequests " +
                        "(id INT not null primary key references requests, flowerType VARCHAR(255), color VARCHAR(255)," +
                        "size VARCHAR(255), message TEXT)";
                int tableUpdateFlowerRequests = tableStmt.executeUpdate(createTableFlowerRequests);
                importFlowerRequestsFromCSV("FlowerRequests_sync", 4);

                String createTableConferenceRequests = "CREATE TABLE conferenceRequests " +
                        "(id INT not null primary key references requests, dateRequested TIMESTAMP, eventName VARCHAR(255), bookingReason VARCHAR(255)," +
                        "duration INT)";
                int tableUpdateConferenceRequests = tableStmt.executeUpdate(createTableConferenceRequests);
                importConferenceRequestsFromCSV("ConferenceRequests_sync", 4);

                try {
                    while (((line = br.readLine()) != null)) {
                        String[] requestValues = line.split(splitBy);
                        if (!(requestValues[0].equals("id"))) {
                            // System.out.println(edgeValues[0]);
                            String rowQuery = "INSERT INTO Requests (id, employee, datesubmitted, requeststatus, requesttype, locationname, notes) VALUES ("
                                    + requestValues[0]
                                    + ","
                                    + "'"
                                    + requestValues[1]
                                    + "'"
                                    + ","
                                    + "'"
                                    + requestValues[2]
                                    + "'"
                                    + ","
                                    + "'"
                                    + requestValues[3]
                                    + "'"
                                    + ","
                                    + "'"
                                    + requestValues[4]
                                    + "'"
                                    + ","
                                    + "'"
                                    + requestValues[5]
                                    + "'"
                                    + ","
                                    + "'"
                                    + requestValues[6]
                                    + "'"
                                    + ");";
                            // System.out.println(rowQuery);
                            Statement rowStmt = DBconnection.getDBconnection().getConnection().createStatement();
                            int rowUpdate = rowStmt.executeUpdate(rowQuery);
                            rowStmt.close();
                        }
                    }
                    br.close();
                } catch (IOException e) {
                    System.out.println("Error reading the file in the method 'DBinput.importRequestsFromCSV'");
                    e.printStackTrace();
                }
                tableStmt.close();
            } catch (SQLException e) {
                System.out.println("ERROR: Query could not be executed in the method 'DBinput.importRequestsFromCSV'");
            }
    }

    /**
     * Imports the conference requests from a CSV file into the database
     *
     * @param filename The name of the CSV file to be imported as a String
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void importConferenceRequestsFromCSV(String filename, int location) {

        String line = "";
        String splitBy = ",";

        BufferedReader br = null;

        try {
            try {
                br = switch (location) {
                    case 0 -> new BufferedReader(new FileReader(filename));
                    case 1 -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                    case 2 -> new BufferedReader(new FileReader(filename + ".csv"));
                    case 3 -> new BufferedReader(new FileReader("./src/main/resources/CSV Files/" + filename + ".csv"));
                    case 4 -> new BufferedReader(new FileReader("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                    default -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                };
            } catch (FileNotFoundException e) {
                System.out.println("Error reading the file in the method 'DBinput.importConferenceRequestsFromCSV'");
                e.printStackTrace();
            }

            Statement tableStmt = DBconnection.getDBconnection().getConnection().createStatement();
            String dropConferenceRequestsTable = "DROP TABLE IF EXISTS conferenceRequests";
            int dropUpdateConferenceRequests = tableStmt.executeUpdate(dropConferenceRequestsTable);

            String createTableConferenceRequests = "CREATE TABLE conferenceRequests " +
                    "(id INT not null primary key references requests, dateRequested TIMESTAMP, eventName VARCHAR(255), bookingReason VARCHAR(255)," +
                    "duration INT)";
            int tableUpdateConferenceRequests = tableStmt.executeUpdate(createTableConferenceRequests);

            try {
                while (((line = br.readLine()) != null)) {
                    String[] requestValues = line.split(splitBy);
                    if (!(requestValues[0].equals("id"))) {
                        // System.out.println(edgeValues[0]);
                        String rowQuery = "INSERT INTO conferenceRequests (id, dateRequested, eventName, bookingReason, duration) VALUES ("
                                + requestValues[0]
                                + ","
                                + "'"
                                + requestValues[1]
                                + "'"
                                + ","
                                + "'"
                                + requestValues[2]
                                + "'"
                                + ","
                                + "'"
                                + requestValues[3]
                                + "'"
                                + ","
                                + requestValues[4]
                                + ");";
                        Statement rowStmt = DBconnection.getDBconnection().getConnection().createStatement();
                        int rowUpdate = rowStmt.executeUpdate(rowQuery);
                        rowStmt.close();
                    }
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error reading the file in the method 'DBinput.importConferenceRequestsFromCSV'");
                e.printStackTrace();
            }
            tableStmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: Query could not be executed in the method 'DBinput.importConferenceRequestsFromCSV'");
        }
    }

    /**
     * Imports the flower requests from a CSV file into the database
     *
     * @param filename The name of the CSV file to be imported as a String
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void importFlowerRequestsFromCSV(String filename, int location) {

        String line = "";
        String splitBy = ",";

        BufferedReader br = null;

        try {
            try {
                br = switch (location) {
                    case 0 -> new BufferedReader(new FileReader(filename));
                    case 1 -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                    case 2 -> new BufferedReader(new FileReader(filename + ".csv"));
                    case 3 -> new BufferedReader(new FileReader("./src/main/resources/CSV Files/" + filename + ".csv"));
                    case 4 -> new BufferedReader(new FileReader("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                    default -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                };
            } catch (FileNotFoundException e) {
                System.out.println("Error reading the file in the method 'DBinput.importFlowerRequestsFromCSV'");
                e.printStackTrace();
            }

            Statement tableStmt = DBconnection.getDBconnection().getConnection().createStatement();
            String dropFlowerRequestsTable = "DROP TABLE IF EXISTS flowerRequests";
            int dropUpdateFlowerRequests = tableStmt.executeUpdate(dropFlowerRequestsTable);

            String createTableFlowerRequests = "CREATE TABLE flowerRequests " +
                    "(id INT not null primary key references requests, flowerType VARCHAR(255), color VARCHAR(255)," +
                    "size VARCHAR(255), message TEXT)";
            int tableUpdateFlowerRequests = tableStmt.executeUpdate(createTableFlowerRequests);

            try {
                while (((line = br.readLine()) != null)) {
                    String[] requestValues = line.split(splitBy);
                    if (!(requestValues[0].equals("id"))) {
                        // System.out.println(edgeValues[0]);
                        String rowQuery = "INSERT INTO flowerRequests (id, flowerType, color, size, message) VALUES ("
                                + requestValues[0]
                                + ","
                                + "'"
                                + requestValues[1]
                                + "'"
                                + ","
                                + "'"
                                + requestValues[2]
                                + "'"
                                + ","
                                + "'"
                                + requestValues[3]
                                + "'"
                                + ","
                                + "'"
                                + requestValues[4]
                                + "'"
                                + ");";
                        Statement rowStmt = DBconnection.getDBconnection().getConnection().createStatement();
                        int rowUpdate = rowStmt.executeUpdate(rowQuery);
                        rowStmt.close();
                    }
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error reading the file in the method 'DBinput.importFlowerRequestsFromCSV'");
                e.printStackTrace();
            }
            tableStmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: Query could not be executed in the method 'DBinput.importFlowerRequestsFromCSV'");
        }
    }

    /**
     * Imports the furniture requests from a CSV file into the database
     *
     * @param filename The name of the CSV file to be imported as a String
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void importFurnitureRequestsFromCSV(String filename, int location) {

        String line = "";
        String splitBy = ",";

        BufferedReader br = null;

        try {
            try {
                br = switch (location) {
                    case 0 -> new BufferedReader(new FileReader(filename));
                    case 1 -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                    case 2 -> new BufferedReader(new FileReader(filename + ".csv"));
                    case 3 -> new BufferedReader(new FileReader("./src/main/resources/CSV Files/" + filename + ".csv"));
                    case 4 -> new BufferedReader(new FileReader("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                    default -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                };
            } catch (FileNotFoundException e) {
                System.out.println("Error reading the file in the method 'DBinput.importFurnitureRequestsFromCSV'");
                e.printStackTrace();
            }

            Statement tableStmt = DBconnection.getDBconnection().getConnection().createStatement();
            String dropFurnitureRequestsTable = "DROP TABLE IF EXISTS furnitureRequests";
            int dropUpdateFurnitureRequests = tableStmt.executeUpdate(dropFurnitureRequestsTable);

            String createTableFurnitureRequests = "CREATE TABLE furnitureRequests " +
                    "(id INT not null primary key references requests, type VARCHAR(255), model VARCHAR(255)," +
                    "assembly BOOLEAN)";
            int tableUpdateFurnitureRequests = tableStmt.executeUpdate(createTableFurnitureRequests);

            try {
                while (((line = br.readLine()) != null)) {
                    String[] requestValues = line.split(splitBy);
                    if (!(requestValues[0].equals("id"))) {
                        // System.out.println(edgeValues[0]);
                        String rowQuery = "INSERT INTO furnitureRequests (id, type, model, assembly) VALUES ("
                                + requestValues[0]
                                + ","
                                + "'"
                                + requestValues[1]
                                + "'"
                                + ","
                                + "'"
                                + requestValues[2]
                                + "'"
                                + ","
                                + requestValues[3]
                                + ");";
                        Statement rowStmt = DBconnection.getDBconnection().getConnection().createStatement();
                        int rowUpdate = rowStmt.executeUpdate(rowQuery);
                        rowStmt.close();
                    }
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error reading the file in the method 'DBinput.importFurnitureRequestsFromCSV'");
                e.printStackTrace();
            }
            tableStmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: Query could not be executed in the method 'DBinput.importFurnitureRequestsFromCSV'");
        }
    }

    /**
     * Imports the meal requests from a CSV file into the database
     *
     * @param filename The name of the CSV file to be imported as a String
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void importMealRequestsFromCSV(String filename, int location) {

        String line = "";
        String splitBy = ",";

        BufferedReader br = null;

        try {
            try {
                br = switch (location) {
                    case 0 -> new BufferedReader(new FileReader(filename));
                    case 1 -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                    case 2 -> new BufferedReader(new FileReader(filename + ".csv"));
                    case 3 -> new BufferedReader(new FileReader("./src/main/resources/CSV Files/" + filename + ".csv"));
                    case 4 -> new BufferedReader(new FileReader("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                    default -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                };
            } catch (FileNotFoundException e) {
                System.out.println("Error reading the file in the method 'DBinput.importMealRequestsFromCSV'");
                e.printStackTrace();
            }

            Statement tableStmt = DBconnection.getDBconnection().getConnection().createStatement();
            String dropMealRequestsTable = "DROP TABLE IF EXISTS mealRequests";
            int dropUpdateMealRequests = tableStmt.executeUpdate(dropMealRequestsTable);

            String createTableMealRequests = "CREATE TABLE mealRequests " +
                    "(id INT not null primary key references requests, orderFrom VARCHAR(255), food VARCHAR(255)," +
                    "drink VARCHAR(255), snack VARCHAR(255))";
            int tableUpdateMealRequests = tableStmt.executeUpdate(createTableMealRequests);

            try {
                while (((line = br.readLine()) != null)) {
                    String[] requestValues = line.split(splitBy);
                    if (!(requestValues[0].equals("id"))) {
                        // System.out.println(edgeValues[0]);
                        String rowQuery = "INSERT INTO mealRequests (id, orderfrom, food, drink, snack) VALUES ("
                                + requestValues[0]
                                + ","
                                + "'"
                                + requestValues[1]
                                + "'"
                                + ","
                                + "'"
                                + requestValues[2]
                                + "'"
                                + ","
                                + "'"
                                + requestValues[3]
                                + "'"
                                + ","
                                + "'"
                                + requestValues[4]
                                + "'"
                                + ");";
                        Statement rowStmt = DBconnection.getDBconnection().getConnection().createStatement();
                        int rowUpdate = rowStmt.executeUpdate(rowQuery);
                        rowStmt.close();
                    }
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error reading the file in the method 'DBinput.importMealRequestsFromCSV'");
                e.printStackTrace();
            }
            tableStmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: Query could not be executed in the method 'DBinput.importMealRequestsFromCSV'");
        }
    }

    /**
     * Imports the office requests from a CSV file into the database
     *
     * @param filename The name of the CSV file to be imported as a String
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void importOfficeRequestsFromCSV(String filename, int location) {

        String line = "";
        String splitBy = ",";

        BufferedReader br = null;

        try {
            try {
                br = switch (location) {
                    case 0 -> new BufferedReader(new FileReader(filename));
                    case 1 -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                    case 2 -> new BufferedReader(new FileReader(filename + ".csv"));
                    case 3 -> new BufferedReader(new FileReader("./src/main/resources/CSV Files/" + filename + ".csv"));
                    case 4 -> new BufferedReader(new FileReader("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                    default -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                };
            } catch (FileNotFoundException e) {
                System.out.println("Error reading the file in the method 'DBinput.importOfficeRequestsFromCSV'");
                e.printStackTrace();
            }

            Statement tableStmt = DBconnection.getDBconnection().getConnection().createStatement();
            String dropOfficeRequestsTable = "DROP TABLE IF EXISTS officeRequests";
            int dropUpdateOfficeRequests = tableStmt.executeUpdate(dropOfficeRequestsTable);

            String createTableOfficeRequests = "CREATE TABLE officeRequests " +
                    "(id INT not null primary key references requests, item VARCHAR(255), quantity INT," +
                    "type VARCHAR(255))";
            int tableUpdateOfficeRequests = tableStmt.executeUpdate(createTableOfficeRequests);

            try {
                while (((line = br.readLine()) != null)) {
                    String[] requestValues = line.split(splitBy);
                    if (!(requestValues[0].equals("id"))) {
                        // System.out.println(edgeValues[0]);
                        String rowQuery = "INSERT INTO officeRequests (id, item, quantity, type) VALUES ("
                                + requestValues[0]
                                + ","
                                + "'"
                                + requestValues[1]
                                + "'"
                                + ","
                                + requestValues[2]
                                + ","
                                + "'"
                                + requestValues[3]
                                + "'"
                                + ");";
                        Statement rowStmt = DBconnection.getDBconnection().getConnection().createStatement();
                        int rowUpdate = rowStmt.executeUpdate(rowQuery);
                        rowStmt.close();
                    }
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error reading the file in the method 'DBinput.importOfficeRequestsFromCSV'");
                e.printStackTrace();
            }
            tableStmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: Query could not be executed in the method 'DBinput.importOfficeRequestsFromCSV'");
        }
    }

    /**
     * Imports the alerts from a CSV file into the database
     *
     * @param filename The name of the CSV file to be imported as a String
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void importAlertsFromCSV(String filename, int location) {

        String line = "";
        String splitBy = ",";

        BufferedReader br = null;

        try {
            try {
                br = switch (location) {
                    case 0 -> new BufferedReader(new FileReader(filename));
                    case 1 -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                    case 2 -> new BufferedReader(new FileReader(filename + ".csv"));
                    case 3 -> new BufferedReader(new FileReader("./src/main/resources/CSV Files/" + filename + ".csv"));
                    case 4 -> new BufferedReader(new FileReader("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                    default -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                };
            } catch (FileNotFoundException e) {
                System.out.println("Error reading the file in the method 'DBinput.importAlertsFromCSV'");
                e.printStackTrace();
            }

            Statement tableStmt = DBconnection.getDBconnection().getConnection().createStatement();
            String dropAlertsTable = "DROP TABLE IF EXISTS alerts";
            int dropUpdateAlerts = tableStmt.executeUpdate(dropAlertsTable);

            String createTableAlerts = "CREATE TABLE alerts " +
                    "(id INT generated always as identity primary key, title TEXT, description TEXT, " +
                    "created_at TIMESTAMP, employee VARCHAR(255))";
            int tableUpdateAlerts = tableStmt.executeUpdate(createTableAlerts);

            try {
                while (((line = br.readLine()) != null)) {
                    String[] requestValues = line.split(splitBy);
                    if (!(requestValues[0].equals("id"))) {
                        // System.out.println(edgeValues[0]);
                        String rowQuery = "INSERT INTO alerts (id, title, description, created_at, employee) VALUES ("
                                + "DEFAULT"
                                + ","
                                + "'"
                                + requestValues[1]
                                + "'"
                                + ","
                                + "'"
                                + requestValues[2]
                                + "'"
                                + ","
                                + "'"
                                + requestValues[3]
                                + "'"
                                + ","
                                + "'"
                                + requestValues[4]
                                + "'"
                                + ");";
                        Statement rowStmt = DBconnection.getDBconnection().getConnection().createStatement();
                        int rowUpdate = rowStmt.executeUpdate(rowQuery);
                        rowStmt.close();
                    }
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error reading the file in the method 'DBinput.importAlertsFromCSV'");
                e.printStackTrace();
            }
            tableStmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: Query could not be executed in the method 'DBinput.importAlertsFromCSV'");
        }
    }

    /**
     * Imports the signage from a CSV file into the database
     *
     * @param filename The name of the CSV file to be imported as a String
     * @param location The location of the CSV file to be exported as an int --
     *                 int location can be 1 (root folder for program),
     *                 2 (custom location), 3
     *                 (developer: CSV Files in package), or 4 (developer: DB Sync Files in package)
     */
    public static void importSignsFromCSV(String filename, int location) {
        String line = "";
        String splitBy = ",";

        BufferedReader br = null;

        try {
            try {
                br = switch (location) {
                    case 0 -> new BufferedReader(new FileReader(filename));
                    case 1 -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                    case 2 -> new BufferedReader(new FileReader(filename + ".csv"));
                    case 3 -> new BufferedReader(new FileReader("./src/main/resources/CSV Files/" + filename + ".csv"));
                    case 4 -> new BufferedReader(new FileReader("./src/main/resources/DB Sync Files/" + filename + ".csv"));
                    default -> new BufferedReader(new FileReader("./" + filename + ".csv"));
                };
            } catch (FileNotFoundException e) {
                System.out.println("Error reading the file in the method 'DBinput.importSignageFromCSV'");
                e.printStackTrace();
            }

            Statement tableStmt = DBconnection.getDBconnection().getConnection().createStatement();
            String dropSignageTable = "DROP TABLE IF EXISTS signs";
            int dropUpdateSignage = tableStmt.executeUpdate(dropSignageTable);

            String createTableSignage = "CREATE TABLE signs (" +
                    """
                        signageGroup varchar(255) NOT NULL,
                        locationName text NOT NULL DEFAULT '',
                        direction varchar(255) NOT NULL DEFAULT 'stop here',
                        startDate date NOT NULL DEFAULT CURRENT_DATE,
                        endDate date,
                        singleBlock boolean DEFAULT true,
                        signLocation varchar(255) DEFAULT 'Info Node 19 Floor 2',
                        constraint fk_longName foreign key (signLocation) references locationnames(longName)
                        on update cascade on delete set default,
                        PRIMARY KEY (signageGroup, locationName, startDate))
                    """;
            int tableUpdateSignage = tableStmt.executeUpdate(createTableSignage);

            try {
                while (((line = br.readLine()) != null)) {
                    String[] requestValues = line.split(splitBy);
                    if (!(requestValues[0].equals("signageGroup"))) {
                        // System.out.println(edgeValues[0]);
                        String rowQuery = "INSERT INTO signs (signagegroup, locationname, direction, startdate, enddate, singleblock) VALUES ("
                                + "'"
                                + requestValues[0]
                                + "'"
                                + ","
                                + requestValues[1]
                                + ","
                                + "'"
                                + requestValues[2]
                                + "'"
                                + ","
                                + "'"
                                + requestValues[3]
                                + "'"
                                + ");";
                        Statement rowStmt = DBconnection.getDBconnection().getConnection().createStatement();
                        int rowUpdate = rowStmt.executeUpdate(rowQuery);
                        rowStmt.close();
                    }
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error reading the file in the method 'DBinput.importSignageFromCSV'");
                e.printStackTrace();
            }
            tableStmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: Query could not be executed in the method 'DBinput.importSignageFromCSV'");
        }
    }

}

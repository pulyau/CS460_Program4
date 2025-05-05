/*
 * Name: Pulat Uralov
 * Course Name: CS460
 * File Name: Prog3.java
 * Instructor: L. McCann
 * TAs: X. Guo, J. Shen
 * Due Date: April 3 2025, 3:30 pm
 * 
 * Prog1B.java - this class provides a friendly
 * user interface for running four quieries specified
 * in the documentation
 */
import java.sql.*;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class Prog3 {

    /** ***********************************************
     * 
     *                  CONSTANTS
     * 
     * ************************************************/
    private static final String NET_ID = "uralovpulya";

    private static final String ORACLE_URL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

    private static final String JDBC_DRIVER = "oracle.jdbc.OracleDriver";

    private static final int[] VALID_YEARS = {1990, 2000, 2010, 2020};

    private static final String[] VALID_CATEGORIES = {"AUTO", "BUS", "TRUCK", "MOTORCYCLE"};

    public static void main(String[] args) {

        String username = null; // Oracle DBMS username
        String password = null; // Oracle DBMS password

        // Get username/password from command line arguments
        if (args.length == 2) {
            username = args[0];
            password = args[1];
        } else {
            System.out.println("\nUsage: java Prog3 <username> <password>\n"
                    + "    where <username> is your Oracle DBMS username,\n"
                    + "    and <password> is your Oracle password.\n");
            System.exit(-1);
        }

        // load the (Oracle) JDBC driver by initializing its base
        // class, 'oracle.jdbc.OracleDriver'.
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("*** ClassNotFoundException: Error loading Oracle JDBC driver.");
            System.err.println("\tPerhaps the ojdbc jar is not on the Classpath?");
            System.err.println("\tUsed driver: " + JDBC_DRIVER);
            System.exit(-1);
        }

        // make and return a database connection to the user's
        // Oracle database
        try {
            Connection dbconn = DriverManager.getConnection(ORACLE_URL, username, password);
            Scanner input = new Scanner(System.in);

            while (true) {
                displayMenu();
                System.out.println("Enter choice: ");
                String choice = input.nextLine().trim().toLowerCase();

                if (choice.equals("a")) {
                    handleQueryA(dbconn, input);
                } else if (choice.equals("b")) {
                    handleQueryB(dbconn);
                } else if (choice.equals("c")) {
                    handleQueryC(dbconn, input);
                } else if (choice.equals("d")) {
                    handleQueryD(dbconn, input);
                } else if (choice.equals("e")) {
                    System.out.println("Exiting the program\n");
                    break;
                } else {
                    System.out.println("Invalid input, please try again\n");
                }
            }

        } catch (SQLException e) {
            System.err.println("\n*** SQLException ***");
            System.err.println("\tCould not connect to Oracle or encountered an SQL error.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        } catch (Exception e) {
            System.err.println("\n*** An unexpected error occurred ***");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * A helper method to display the main menu to the user.
     */
    private static void displayMenu() {
        System.out.println("\n---------------- Queries ----------------");
        System.out.println("(a) Top 10 States by Category/Year");
        System.out.println("(b) States with < 1 Million Registrations");
        System.out.println("(c) Truck vs. Auto Registration Percentage by State");
        System.out.println("(d) Car Registration Percentage Through Years");
        System.out.println("(e) Exit Program");
        System.out.println("-------------------------------------------");
    }

    /**
     * Helper method for handling query a: 
     * For a year and a category (auto, bus, truck, or motorcycle) entered by the user, 
     * list the ten state with the highest quantities of registrations in that category 
     * in that year, in descending order by quantity
     */
    private static void handleQueryA(Connection dbconn, Scanner input) {
        System.out.println("\n--- Top 10 States by Category/Year ---\n");
        int year = 0;
        String category = "";
        String categoryColumn = "";
        String tableName = "";

        // asking the user for a valid year
        while (true) {
            System.out.print("Enter year (1990, 2000, 2010, 2020): ");
            try {
                year = input.nextInt();
                input.nextLine();
                boolean isvalid = false;
                for (int y : VALID_YEARS) {
                    if (year == y) {
                        isvalid = true;
                        tableName = NET_ID + ".Registrations" + year;
                        break;
                    }
                }
                if (isvalid) break;
                else System.out.println("Invalid year, please try again\n");
            } catch (InputMismatchException e) {
                System.out.println("Not a number\n");
                input.nextLine();
            }
        }

        // asking the user for a valid category
        while (true) {
            System.out.print("Enter category (Auto, Bus, Truck, Motorcycle): ");
            category = input.nextLine().trim().toUpperCase();
            boolean isvalid = false;
            for (String c : VALID_CATEGORIES) {
                if (category.equals(c)) {
                    isvalid = true;
                    categoryColumn = category + "_Count";
                    break;
                }
            }
            if (isvalid) break;
            else System.out.println("Invalid category, please try again\n");
        }

        // constructing the query
        String sql = "SELECT State_Name, " + categoryColumn + " " +
                    "FROM ( " +
                    "  SELECT State_Name, " + categoryColumn + " " +
                    "  FROM " + tableName + " " +
                    "  ORDER BY " + categoryColumn + " DESC " +
                    ") " +
                    "WHERE ROWNUM <= 10";


        try {

            Statement stmt = dbconn.createStatement();
            ResultSet answer = stmt.executeQuery(sql);

            System.out.println("\nTop 10 States for " + category + " Registrations in " + year);
            System.out.println("----------------------------------------------------");

            System.out.printf("%-25s %-15s%n", "State", "Quantity");
            System.out.println("----------------------------------------------------");

            // printing the top 10 states
            while (answer.next()) {
                String stateName = answer.getString("State_Name");
                int quantity = answer.getInt(categoryColumn);
                System.out.printf("%-25s %,15d%n", stateName, quantity);
            }
            System.out.println("----------------------------------------------------");

        } catch (SQLException e) {
            System.err.println("*** SQLException:  " + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
    }

    /**
     * Helper method for handling Query B: 
     * For each of the four years, how many states had a total number of registrations 
     * across all vehicle type under one million?
     */
    private static void handleQueryB(Connection dbconn) {
        System.out.println("\n--- States with < 1 Million Total Registrations ---\n");

        // using a treemap to maintain keys in a sorted order 
        Map<Integer, Integer> results = new TreeMap<>();

        // running the query for each year table
        for (int year : VALID_YEARS) {

            String tableName = NET_ID + ".Registrations" + year;
            String sql = "SELECT COUNT(*) " +
                         "FROM " + tableName + " " +
                         "WHERE (Auto_Count + Bus_Count + " +
                         "Truck_Count + Motorcycle_Count) < 1000000";

            try {
                Statement stmt = dbconn.createStatement();
                ResultSet answer = stmt.executeQuery(sql);

                if (answer.next()) {
                    results.put(year, answer.getInt(1));
                }

            } catch (SQLException e) {
                System.err.println("*** SQLException:  " + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);
            }
        }

        // printing the results
        System.out.println("\nYear  States < 1M Regs");
        System.out.println("----  ----------------");
        for (Map.Entry<Integer, Integer> entry : results.entrySet()) {
            System.out.printf("%-4d  %-,16d%n", entry.getKey(), entry.getValue());
        }
        System.out.println("----------------------");
    }

    /**
     * Helper for handling query C: 
     * For a state given by the user, determine for each of the four years how many 
     * more (or fewer) trucks were registered than autos as a percentage.
     */
    private static void handleQueryC(Connection dbconn, Scanner input) {

        // creating a list of all states by fetching the state column from
        // one table
        Set<String> validStates = new HashSet<>();
        try {
            Statement stmt = dbconn.createStatement();
            ResultSet answer = stmt.executeQuery("SELECT DISTINCT State_Name FROM " + NET_ID + ".Registrations1990");
            while (answer.next()) {
                validStates.add(answer.getString("State_Name"));
            }
        } catch (SQLException e) {
            System.err.println("*** SQLException:  " + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }



        System.out.println("\n--- Truck vs. Auto Percentage by State ---\n");
        String stateNameInput = null;

        System.out.print("Enter State Name: ");
        stateNameInput = input.nextLine().trim();

        // input validation
        if (!validStates.contains(stateNameInput)) {
            System.out.println("Error: Invalid state name\n");
            return;
        }

        Map<Integer, Double> results = new TreeMap<>();

        for (int year : VALID_YEARS) {
            String tableName = NET_ID + ".Registrations" + year;

            String sql = "SELECT Auto_Count, Truck_Count " +
                         "FROM " + tableName + " " +
                         "WHERE State_Name = " + stateNameInput;


            try {
                PreparedStatement pstmt = dbconn.prepareStatement(sql);
                ResultSet answer = pstmt.executeQuery(); 
                if (answer.next()) {

                    int autos = answer.getInt("Auto_Count");
                    int trucks = answer.getInt("Truck_Count");

                    double percentage;

                    // check division by 0 case
                    if (autos == 0) {
                        percentage = Double.POSITIVE_INFINITY;
                    } else {
                        percentage = ((double) trucks - autos) / autos * 100.0;
                    }
                    results.put(year, percentage);
                } 
                
            } catch (SQLException e) {
                System.err.println("*** SQLException:  " + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);
            }
        }

        // printing results
        System.out.println("\nTruck vs. Auto Registration Percentage for: " + stateNameInput);
        System.out.println("---------------------------------------------");
        System.out.printf("%-6s %-20s%n", "Year", "Trucks vs Autos (%)");
        System.out.println("---------------------------------------------");
        for (Map.Entry<Integer, Double> entry : results.entrySet()) {

            Double value = entry.getValue();

            String displayValue;
            displayValue = String.format("%,.2f%%", value);
            System.out.printf("%-6d %-20s%n", entry.getKey(), displayValue);
        }
        System.out.println("---------------------------------------------");

    }

    /**
     * Helper for handling Query D: 
     * show states where car registrations increased or decreased
     * in % over a specified period
     */
    private static void handleQueryD(Connection dbconn, Scanner input) {
        System.out.println("\n--- Car Registration Growth Between Years ---\n");
        int startYear = 0; 
        int endYear = 0;
        String startTable = null;
        String endTable = null;

        // getting and validating Start Year
        while (true) {
            System.out.print("Enter start year (1990, 2000, 2010, 2020): ");
            try {
                startYear = input.nextInt();
                input.nextLine();

                int isvalid = 0;
                for (int y : VALID_YEARS) {
                    if (startYear == y) {
                        isvalid = 1;
                        break;
                    }
                }

                if (isvalid == 1) {
                    startTable = NET_ID + ".Registrations" + startYear;
                    break;
                } else {
                    System.out.println("Invalid year\n");
                    return;
                }
                
            } catch (InputMismatchException e) {
                System.out.println("Not a number\n");
                input.nextLine();
            }
        }

        // Get and validate End Year
        while (true) {
            System.out.print("Enter end year (" + (startYear + 10) + " through 2020): ");
            try {
                endYear = input.nextInt();
                input.nextLine();

                int isvalid = 0;
                for (int y : VALID_YEARS) {
                    if (endYear == y && y > startYear) {
                        isvalid = 1;
                        break;
                    }
                }

                if (isvalid == 1) {
                    endTable = NET_ID + ".Registrations" + startYear;
                    break;
                } else {
                    System.out.println("Invalid year\n");
                    return;
                }
                
            } catch (InputMismatchException e) {
                System.out.println("Not a number\n");
                input.nextLine();
            }
         }


        // consturcting the sql query
        String sql = "SELECT t_end.State_Name, " +
                    "       t_start.Auto_Count AS Start_Autos, " + 
                    "       t_end.Auto_Count AS End_Autos, " +     
                    "       ((t_end.Auto_Count - t_start.Auto_Count) * 100.0 / t_start.Auto_Count) AS Percent_Change " +
                    "FROM " + startTable + " t_start " +
                    "JOIN " + endTable + " t_end ON t_start.State_Name = t_end.State_Name " +
                    "ORDER BY Percent_Change DESC"; 


        try  {

            PreparedStatement pstmt = dbconn.prepareStatement(sql);

            ResultSet answer = pstmt.executeQuery(sql);

            System.out.println("\nAuto Registration Percentage Change (" + startYear + " to " + endYear + ")");
            System.out.println("----------------------------------------------------------------------------------");
            System.out.printf("%-25s %-15s %-15s %-15s%n", "State", "Start Autos", "End Autos", "% Change");
            System.out.println("----------------------------------------------------------------------------------");

            while (answer.next()) {
                String stateName = answer.getString("State_Name");
                
                int startAutos = answer.getInt("Start_Autos");
                int endAutos = answer.getInt("End_Autos");
                double percentChange = answer.getDouble("Percent_Change");
                
                System.out.printf("%-25s %,15d %,15d %,14.2f%%%n", stateName, startAutos, endAutos, percentChange);
            }

                System.out.println("----------------------------------------------------------------------------------");

        } catch (SQLException e) {
            System.err.println("*** SQLException:  " + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
    }

}
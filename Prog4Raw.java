import java.io.*;
import java.sql.*;
import java.util.Scanner;


public class Prog4Raw {
    private static final String ORACLE_URL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
    
    // private static String TABLE_1990;
    // private static String TABLE_2000;
    // private static String TABLE_2010;
    // private static String TABLE_2020;
    
    private static final Scanner scanner = new Scanner(System.in);
    private static Connection connection = null;

    public static void main(String[] args) {
        String username = null;
        String password = null;
        
        if (args.length == 2) {
            username = args[0];
            password = args[1];
        } else {
            System.out.println("\nUsage: java Prog3 <username> <password>\n"
                             + "    where <username> is your Oracle DBMS username,\n"
                             + "    and <password> is your Oracle password.\n");
            System.exit(-1);
        }
        
        // TABLE_1990 = username + ".MVRD1990";
        // TABLE_2000 = username + ".MVRD2000";
        // TABLE_2010 = username + ".MVRD2010";
        // TABLE_2020 = username + ".MVRD2020";
        
        try {
           
            Class.forName("oracle.jdbc.OracleDriver");
            
            System.out.println("Connecting to Oracle database...");
            connection = DriverManager.getConnection(ORACLE_URL, username, password);
            System.out.println("Connected successfully!");
            
            boolean exit = false;
            while (!exit) {
                displayMenu();
                int choice = getUserChoice();
                
                switch (choice) {
                    case 1:
                        AddTupleToTable();
                        break;
                    case 2:
                        UpdateTupleInTable();
                        break;
                    case 3:
                        DeleteTupleFromTable();
                        break;
                    case 4:
                        ProcessQueueries();
                        break;
                    case 5:
                        exit = true;
                        System.out.println("Exiting program. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("*** ClassNotFoundException: "
                + "Error loading Oracle JDBC driver.  \n"
                + "\tPerhaps the driver is not on the Classpath?");
            System.exit(-1);
        } catch (SQLException e) {
            System.err.println("*** SQLException: "
                + "Could not open JDBC connection.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("*** SQLException: "
                    + "Could not close JDBC connection.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
            }
            
            if (scanner != null) {
                scanner.close();
            }
        }
    }
    
    private static void displayMenu() {
        System.out.println("\n--- Prog 4 Menu ---");
        System.out.println("1. Add a tuple to a table");
        System.out.println("2. Update a tuple in a table");
        System.out.println("3. Delete a tuple from a table");
        System.out.println("4. Queries");
        System.out.println("5. Exit");
        System.out.print("Enter your choice (1-5): ");
    }

    private static int getUserChoice() {
        int choice = 0;
        boolean validInput = false;
        
        while (!validInput) {
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= 5) {
                    validInput = true;
                } else {
                    System.out.print("Please enter a number between 1 and 5: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number between 1 and 5: ");
            }
        }
        return choice;
    }


    private static void ProcessQueueries() {
        System.out.println("Please enter a query to process");
        System.out.println("1. For a given member, list all the ski lessons they have purchased, \n" + //
                        "including the number of remaining sessions, instructor name, and scheduled time.");
        System.out.println("2. For a given ski pass, list all lift rides and equipment rentals associated with it, \n" + //
                        "along with timestamps and return status.");
        System.out.println("List all open trails suitable for intermediate-level skiers, along with their category \n" + //
                        "and connected lifts that are currently operational.");
        System.out.println("Custom query");
        int choice = getUserChoice();
        switch (choice) {
            case (1):
                ProcessQuery1();
                break;
            case (2):
                ProcessQuery2();
                break;
            case (3):
                ProcessQuery3();
                break;
            case (4):
                ProcessQuery4();
                break;
        }
    }


    private static void ProcessQuery4() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ProcessQuery4'");
    }

    private static void ProcessQuery3() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ProcessQuery3'");
    }

    private static void ProcessQuery2() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ProcessQuery2'");
    }

    private static void ProcessQuery1() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ProcessQuery1'");
    }

    private static void DeleteTupleFromTable() {
        System.out.println("Please enter a table to delete from");
        PrintTable();
        int choice = getUserChoice();
        switch (choice) {
            case (1):
                DeleteMemberQuery();
                break;
            case (2):
                DeleteSkiPassQuery();
                break;
            case (3):
                DeleteEquipmentQuery();
            case (4):
                DeleteEquipmentRecordQuery();
            case (5):
                DeleteLessonPurchaseQuery();
        }
    }


    private static void DeleteLessonPurchaseQuery() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'DeleteLessonPurchaseQuery'");
    }

    private static void DeleteEquipmentRecordQuery() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'DeleteEquipmentRecordQuery'");
    }

    private static void DeleteEquipmentQuery() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'DeleteEquipmentQuery'");
    }

    private static void DeleteSkiPassQuery() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'DeleteSkiPassQuery'");
    }

    private static void DeleteMemberQuery() {
        System.out.println("Please enter Member ID that you wish to delete");
        int memberId = Integer.parseInt(scanner.nextLine().trim());

        // Check if member exists
        if (!checkIfExists("SELECT COUNT(*) FROM Member WHERE MemberID = " + memberId)) {
            System.out.println("Member doesn't exist!");
            return;
        }

        // Check active ski passes
        if (checkIfExists("SELECT COUNT(*) FROM SkiPass WHERE MemberID = " + memberId +
                        " AND (RemainingUses > 0 OR ExpirationDate >= SYSDATE)")) {
            System.out.println("Cannot delete member: active ski passes exist.");
            return;
        }

        // Check open rentals
        if (checkIfExists("SELECT COUNT(*) FROM EquipmentRecord WHERE MemberID = " + memberId +
                        " AND ReturnStatus = 'NotReturned'")) {
            System.out.println("Cannot delete member: open equipment rentals exist.");
            return;
        }

        // Check unused lessons
        if (checkIfExists("SELECT COUNT(*) FROM LessonPurchase WHERE MemberID = " + memberId +
                        " AND RemainingSessions > 0")) {
            System.out.println("Cannot delete member: unused lesson sessions exist.");
            return;
        }

        // All checks passed — perform deletions
        try {
            Statement stmt = connection.createStatement();

            stmt.executeUpdate("DELETE FROM LessonPurchase WHERE MemberID = " + memberId);
            stmt.executeUpdate("DELETE FROM EquipmentRecord WHERE MemberID = " + memberId);
            stmt.executeUpdate("DELETE FROM SkiPass WHERE MemberID = " + memberId);
            stmt.executeUpdate("DELETE FROM Member WHERE MemberID = " + memberId);

            System.out.println("Member and all associated records successfully deleted.");

        } catch (SQLException e) {
            System.err.println("*** SQLException: "
                + "Could not execute query.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
        }
    }

    /**
     * Executes a SELECT COUNT(*) query and returns true if the result is greater than 0.
     */
    private static boolean checkIfExists(String sql) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("*** SQLException: "
                + "Could not execute query.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
        }
        return false;
    }

    private static void UpdateTupleInTable() {
        System.out.println("Please enter a table to update");
        PrintTable();
        int choice = getUserChoice();
        switch (choice) {
            case (1):
                UpdateMemberQuery();
                break;
            case (2):
                UpdateSkiPassQuery();
                break;
            case (3):
                UpdateEquipmentQuery();
            case (4):
                UpdateEquipmentRecordQuery();
            case (5):
                UpdateLessonPurchaseQuery();
        }
    }


    private static void UpdateLessonPurchaseQuery() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'UpdateLessonPurchaseQuery'");
    }

    private static void UpdateEquipmentRecordQuery() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'UpdateEquipmentRecordQuery'");
    }

    private static void UpdateEquipmentQuery() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'UpdateEquipmentQuery'");
    }

    private static void UpdateSkiPassQuery() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'UpdateSkiPassQuery'");
    }

    private static void UpdateMemberQuery() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'UpdateMemberQuery'");
    }

    private static void AddTupleToTable() {
        System.out.println("Please enter a table to add to");
        PrintTable();
        int choice = getUserChoice();
        switch (choice) {
            case (1):
                AddMemberQuery();
                break;
            case (2):
                AddSkiPassQuery();
                break;
            case (3):
                AddEquipmentQuery();
            case (4):
                AddEquipmentQuery();
            case (5):
                AddLessonPurchaseQuery();
        }
    }

    private static void AddLessonPurchaseQuery() {
        throw new UnsupportedOperationException("Unimplemented method 'AddLessonPurchaseQuery'");
    }

    private static void AddEquipmentQuery() {
        throw new UnsupportedOperationException("Unimplemented method 'AddEquipmentQuery'");
    }

    private static void AddSkiPassQuery() {
        throw new UnsupportedOperationException("Unimplemented method 'AddSkiPassQuery'");
    }

    private static void AddMemberQuery() {
        throw new UnsupportedOperationException("Unimplemented method 'AddMemberQuery'");
    }

    private static void PrintTable() {
        System.out.println("1. Member");
        System.out.println("2. Ski Pass");
        System.out.println("3. Equipment");
        System.out.println("4. Equipment Record");
        System.out.println("5. Lesson Purchase");
    }
    

    // private static void processQuery1() {
    //     try {
    //         int year = getValidYear();
    //         String tableName = getTableNameForYear(year);
            
    //         if (tableName == null) {
    //             System.out.println("Error: Invalid year.");
    //             return;
    //         }
            
    //         String category = getValidCategory();
    //         String columnName = categoryToColumnName(category);
    //         String query = "SELECT * FROM (" +
    //                 "SELECT state, " + columnName + " AS registrations " +
    //                 "FROM " + tableName + " " +
    //                 "ORDER BY " + columnName + " DESC" +
    //                 ") WHERE ROWNUM <= 10";
            
    //         try (Statement stmt = connection.createStatement();
    //              ResultSet rs = stmt.executeQuery(query)) {
                
    //             System.out.println("\n--- Top 10 States for " + category + " Registrations in " + year + " ---");
    //             System.out.printf("%-25s %-15s\n", "State", "Registrations");
    //             System.out.println("------------------------------------------------");
                
    //             while (rs.next()) {
    //                 String state = rs.getString("state");
    //                 int registrations = rs.getInt("registrations");
    //                 System.out.printf("%-25s %-15d\n", state, registrations);
    //             }
    //         }
    //     } catch (SQLException e) {
    //         System.err.println("*** SQLException: "
    //             + "Could not execute query.");
    //         System.err.println("\tMessage:   " + e.getMessage());
    //         System.err.println("\tSQLState:  " + e.getSQLState());
    //         System.err.println("\tErrorCode: " + e.getErrorCode());
    //     }
    // }
    
    // private static void processQuery2() {
    //     try {
    //         System.out.println("\n--- States with Fewer Than 1 Million Total Registrations ---");
    //         System.out.printf("%-10s %-15s\n", "Year", "Count");
    //         System.out.println("-------------------------");
            
    //         int[] years = {1990, 2000, 2010, 2020};
            
    //         for (int year : years) {
    //             String tableName = getTableNameForYear(year);
                
    //             String query = "SELECT COUNT(*) AS state_count " +
    //                           "FROM " + tableName + " " +
    //                           "WHERE (auto + bus + truck + motorcycle) < 1000000";
                
    //             try (Statement stmt = connection.createStatement();
    //                  ResultSet rs = stmt.executeQuery(query)) {
                    
    //                 if (rs.next()) {
    //                     int count = rs.getInt("state_count");
    //                     System.out.printf("%-10d %-15d\n", year, count);
    //                 }
    //             }
    //         }
    //     } catch (SQLException e) {
    //         System.err.println("*** SQLException: "
    //             + "Could not execute query.");
    //         System.err.println("\tMessage:   " + e.getMessage());
    //         System.err.println("\tSQLState:  " + e.getSQLState());
    //         System.err.println("\tErrorCode: " + e.getErrorCode());
    //     }
    // }
    
    // private static void processQuery3() {
    //     try {
    //         String state = getValidState();
            
    //         System.out.println("\n--- Truck vs. Auto Registration Percentage for " + state + " ---");
    //         System.out.printf("%-10s %-20s\n", "Year", "Percentage Difference");
    //         System.out.println("-----------------------------------");
            
    //         int[] years = {1990, 2000, 2010, 2020};
            
    //         for (int year : years) {
    //             String tableName = getTableNameForYear(year);
                
    //             String query = "SELECT auto, truck FROM " + tableName + " WHERE UPPER(state) = '" + 
    //                           state.toUpperCase() + "'";
                
    //             try (Statement stmt = connection.createStatement();
    //                  ResultSet rs = stmt.executeQuery(query)) {
                    
    //                 if (rs.next()) {
    //                     int autos = rs.getInt("auto");
    //                     int trucks = rs.getInt("truck");
    //                     double percentage;
    //                     if (autos == 0) {
    //                         percentage = trucks > 0 ? Double.POSITIVE_INFINITY : 0;
    //                     } else {
    //                         // By Formula: ((trucks - autos) / autos) * 100
    //                         percentage = ((double)(trucks - autos) / autos) * 100;
    //                     }
    //                     if (Double.isInfinite(percentage)) {
    //                         System.out.printf("%-10d %-20s\n", year, "Infinite (No autos registered)");
    //                     } else {
    //                         String prefix = percentage >= 0 ? "+" : "";
    //                         System.out.printf("%-10d %s%.2f%%\n", year, prefix, percentage);
    //                     }
    //                 } else {
    //                     System.out.printf("%-10d %-20s\n", year, "No data available");
    //                 }
    //             }
    //         }
    //     } catch (SQLException e) {
    //         System.err.println("*** SQLException: "
    //             + "Could not execute query.");
    //         System.err.println("\tMessage:   " + e.getMessage());
    //         System.err.println("\tSQLState:  " + e.getSQLState());
    //         System.err.println("\tErrorCode: " + e.getErrorCode());
    //     }
    // }

    // private static void processQuery4() {
    //     try {
    //         System.out.println("\nAvailable regions: Northeast, Southeast, Midwest, Southwest, West");
    //         System.out.print("Enter a region: ");
    //         String region = scanner.nextLine().trim();
    //         String stateList = getStatesInRegion(region);
            
    //         if (stateList == null) {
    //             System.out.println("Error: Invalid region. Please try again.");
    //             return;
    //         }
            
    //         System.out.println("\n--- Average Motorcycle Registration Growth by Decade for " + region + " Region ---");
    //         System.out.printf("%-20s %-20s\n", "Decade Comparison", "Average Growth (%)");
    //         System.out.println("------------------------------------------------");
            
    //         // Compare 1990 to 2000
    //         compareDecades(1990, 2000, stateList);
            
    //         // Compare 2000 to 2010
    //         compareDecades(2000, 2010, stateList);
            
    //         // Compare 2010 to 2020
    //         compareDecades(2010, 2020, stateList);
            
    //     } catch (SQLException e) {
    //         System.err.println("*** SQLException: "
    //             + "Could not execute query.");
    //         System.err.println("\tMessage:   " + e.getMessage());
    //         System.err.println("\tSQLState:  " + e.getSQLState());
    //         System.err.println("\tErrorCode: " + e.getErrorCode());
    //     }
    // }

    // private static void compareDecades(int startYear, int endYear, String stateList) throws SQLException {
    //     String startTable = getTableNameForYear(startYear);
    //     String endTable = getTableNameForYear(endYear);
        
    //     String query = 
    //         "SELECT AVG((e.motorcycle - s.motorcycle) / NULLIF(s.motorcycle, 0) * 100) AS avg_growth " +
    //         "FROM " + startTable + " s JOIN " + endTable + " e ON s.state = e.state " +
    //         "WHERE UPPER(s.state) IN (" + stateList + ")";
        
    //     try (Statement stmt = connection.createStatement();
    //          ResultSet rs = stmt.executeQuery(query)) {
            
    //         if (rs.next()) {
    //             Double avgGrowth = rs.getDouble("avg_growth");
    //             if (rs.wasNull()) {
    //                 System.out.printf("%-20s %-20s\n", startYear + " to " + endYear, "N/A (Division by zero)");
    //             } else {
    //                 System.out.printf("%-20s %-20.2f%%\n", startYear + " to " + endYear, avgGrowth);
    //             }
    //         } else {
    //             System.out.printf("%-20s %-20s\n", startYear + " to " + endYear, "No data available");
    //         }
    //     }
    // }

    // private static String getStatesInRegion(String region) {
    //     region = region.toLowerCase();
        
    //     StringBuilder states = new StringBuilder();
        
    //     switch (region) {
    //         case "northeast":
    //             states.append("'MAINE', 'NEW HAMPSHIRE', 'VERMONT', 'MASSACHUSETTS', 'RHODE ISLAND', ");
    //             states.append("'CONNECTICUT', 'NEW YORK', 'NEW JERSEY', 'PENNSYLVANIA'");
    //             break;
    //         case "southeast":
    //             states.append("'DELAWARE', 'MARYLAND', 'DISTRICT OF COLUMBIA', 'VIRGINIA', 'WEST VIRGINIA', ");
    //             states.append("'NORTH CAROLINA', 'SOUTH CAROLINA', 'GEORGIA', 'FLORIDA', 'KENTUCKY', 'TENNESSEE', ");
    //             states.append("'ALABAMA', 'MISSISSIPPI', 'ARKANSAS', 'LOUISIANA'");
    //             break;
    //         case "midwest":
    //             states.append("'OHIO', 'INDIANA', 'ILLINOIS', 'MICHIGAN', 'WISCONSIN', 'MINNESOTA', ");
    //             states.append("'IOWA', 'MISSOURI', 'NORTH DAKOTA', 'SOUTH DAKOTA', 'NEBRASKA', 'KANSAS'");
    //             break;
    //         case "southwest":
    //             states.append("'OKLAHOMA', 'TEXAS', 'NEW MEXICO', 'ARIZONA'");
    //             break;
    //         case "west":
    //             states.append("'MONTANA', 'IDAHO', 'WYOMING', 'COLORADO', 'UTAH', 'NEVADA', ");
    //             states.append("'WASHINGTON', 'OREGON', 'CALIFORNIA', 'ALASKA', 'HAWAII'");
    //             break;
    //         default:
    //             return null;
    //     }
        
    //     return states.toString();
    // }
    

    // private static String getTableNameForYear(int year) {
    //     switch (year) {
    //         case 1990:
    //             return TABLE_1990;
    //         case 2000:
    //             return TABLE_2000;
    //         case 2010:
    //             return TABLE_2010;
    //         case 2020:
    //             return TABLE_2020;
    //         default:
    //             return null;
    //     }
    // }

    // private static int getValidYear() {
    //     int year = 0;
    //     boolean validInput = false;
        
    //     while (!validInput) {
    //         System.out.print("\nEnter year (1990, 2000, 2010, or 2020): ");
    //         try {
    //             year = Integer.parseInt(scanner.nextLine().trim());
    //             if (year == 1990 || year == 2000 || year == 2010 || year == 2020) {
    //                 validInput = true;
    //             } else {
    //                 System.out.println("Invalid year. Please enter 1990, 2000, 2010, or 2020.");
    //             }
    //         } catch (NumberFormatException e) {
    //             System.out.println("Invalid input. Please enter a valid year.");
    //         }
    //     }
        
    //     return year;
    // }
    
    // private static String getValidCategory() {
    //     String category = "";
    //     boolean validInput = false;
        
    //     while (!validInput) {
    //         System.out.print("Enter vehicle category (auto, bus, truck, or motorcycle): ");
    //         category = scanner.nextLine().trim().toLowerCase();
            
    //         if (category.equals("auto") || category.equals("bus") || 
    //             category.equals("truck") || category.equals("motorcycle")) {
    //             validInput = true;
    //         } else {
    //             System.out.println("Invalid category. Please enter auto, bus, truck, or motorcycle.");
    //         }
    //     }
        
    //     return category;
    // }

    // private static String getValidState() {
    //     System.out.print("\nEnter state name: ");
    //     return scanner.nextLine().trim();
    // }
    

    // private static String categoryToColumnName(String category) {
    //     return category.toLowerCase();
    // }
}
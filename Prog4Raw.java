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
                break;
            case (4):
                DeleteEquipmentRecordQuery();
                break;
            case (5):
                DeleteLessonPurchaseQuery();
                break;
        }
    }

    private static void DeleteLessonPurchaseQuery() {
        System.out.println("Enter Lesson Purchase ID to delete:");
    
        int purchaseId;
        try {
            purchaseId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid purchase ID format.");
            return;
        }
    
        if (!checkIfExists("SELECT COUNT(*) FROM LessonPurchase WHERE PurchaseID = " + purchaseId)) {
            System.out.println("Lesson purchase record does not exist.");
            return;
        }
    
        // if (!checkIfExists(
        //     "SELECT COUNT(*) FROM LessonPurchase WHERE PurchaseID = " + purchaseId +
        //     " AND RemainingSessions = TotalSessions"
        // )) {
        //     System.out.println("Cannot delete. Some sessions have already been used.");
        //     return;
        // }
    
        try {
            Statement stmt = connection.createStatement();
    
            stmt.executeUpdate("DELETE FROM LessonPurchase WHERE PurchaseID = " + purchaseId);
    
            System.out.println("Lesson purchase record successfully deleted.");
    
        } catch (SQLException e) {
            System.err.println("*** SQLException during lesson purchase deletion:");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
        }
    }
    

    private static void DeleteEquipmentRecordQuery() {
        System.out.println("Enter Rental ID of equipment record to delete:");
    
        int rentalId;
        try {
            rentalId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Rental ID format.");
            return;
        }
    
        // Step 1: Check if record exists
        if (!checkIfExists("SELECT COUNT(*) FROM EquipmentRecord WHERE RentalID = " + rentalId)) {
            System.out.println("Equipment rental record does not exist.");
            return;
        }
    
        try {
            Statement stmt = connection.createStatement();

            stmt.executeUpdate(
            "INSERT INTO ArchivedEquipmentRecord (RentalID, EMemberID, EPassID, RentalTime, ReturnStatus, EquipmentID) " +
            "SELECT RentalID, EMemberID, EPassID, RentalTime, ReturnStatus, EquipmentID " +
            "FROM EquipmentRecord WHERE RentalID = " + rentalId
        );
    
            stmt.executeUpdate("DELETE FROM EquipmentRecord WHERE RentalID = " + rentalId);
    
            System.out.println("Equipment record successfully deleted and logged.");
    
        } catch (SQLException e) {
            System.err.println("*** SQLException during equipment record deletion:");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
        }
    }
    

    private static void DeleteEquipmentQuery() {
        System.out.println("Enter Equipment ID to delete:");
    
        int equipmentId;
        try {
            equipmentId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Equipment ID format.");
            return;
        }
    
        // Step 1: Confirm equipment exists
        if (!checkIfExists("SELECT COUNT(*) FROM Equipment WHERE EquipmentID = " + equipmentId)) {
            System.out.println("Equipment does not exist.");
            return;
        }
    
        // Step 2: Check status is 'Retired' or 'Lost'
        if (!checkIfExists(
            "SELECT COUNT(*) FROM Equipment WHERE EquipmentID = " + equipmentId +
            " AND Status IN ('Retired', 'Lost')"
        )) {
            System.out.println("Equipment cannot be deleted. Status must be 'Retired' or 'Lost'.");
            return;
        }
    
        // Step 3: Check that equipment is NOT currently rented out
        if (checkIfExists(
            "SELECT COUNT(*) FROM EquipmentRecord WHERE EquipmentID = " + equipmentId +
            " AND ReturnStatus = 'Rented'"
        )) {
            System.out.println("Equipment is currently rented. Cannot delete.");
            return;
        }
    
        try {
            Statement stmt = connection.createStatement();
    
            // Step 4: Archive the equipment
            stmt.executeUpdate(
                "INSERT INTO ArchivedEquipment (EquipmentID, EquipmentType, EquipmentSize, Status) " +
                "SELECT EquipmentID, EquipmentType, EquipmentSize, Status " +
                "FROM Equipment WHERE EquipmentID = " + equipmentId
            );
    
            // Step 5: Delete from Equipment table
            stmt.executeUpdate("DELETE FROM Equipment WHERE EquipmentID = " + equipmentId);
    
            System.out.println("Equipment successfully archived and deleted.");
    
        } catch (SQLException e) {
            System.err.println("*** SQLException during equipment deletion:");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
        }
    }
    

    private static void DeleteSkiPassQuery() {
        System.out.println("Please enter Ski Pass ID to delete:");
    
        int passId;
        try {
            passId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Ski Pass ID.");
            return;
        }

         // Check if member exists
        if (!checkIfExists("SELECT COUNT(*) FROM SkiPass WHERE PassID = " + passId)) {
            System.out.println("Ski Pass doesn't exist!");
            return;
        }

        if (!checkIfExists("SELECT COUNT(*) FROM SkiPass " +
                "WHERE PassID = " + passId +
                " AND RemainingUses = 0 AND ExpirationDate < SYSDATE")) 
        {
            System.out.print("Ski pass is not expired or has remaining uses. Is this a refund? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (!response.equals("y")) {
                System.out.println("Deletion cancelled.");
                return;
            }
        }

        try {
            Statement stmt = connection.createStatement();
            // Archive the ski pass before deletion
            stmt.executeUpdate(
                "INSERT INTO ArchivedSkiPass " +
                "SELECT * FROM SkiPass WHERE PassID = " + passId
            );
    
            // Then delete it from active table
            stmt.executeUpdate("DELETE FROM SkiPass WHERE PassID = " + passId);
    
            System.out.println("Ski pass successfully deleted and archived.");
        } catch (SQLException e) {
            System.err.println("*** SQLException: "
                + "Could not execute query.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
        }
    }
    

    private static void DeleteMemberQuery() {
        System.out.println("Please enter Member ID that you wish to delete");
        int memberId;
        try {
            memberId = Integer.parseInt(scanner.nextLine().trim());
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid Ski Pass ID.");
            return;
        }

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
        if (checkIfExists("SELECT COUNT(*) FROM EquipmentRecord WHERE EMemberID = " + memberId +
                        " AND ReturnStatus = 'NotReturned'")) {
            System.out.println("Cannot delete member: open equipment rentals exist.");
            return;
        }

        // Check unused lessons
        if (checkIfExists("SELECT COUNT(*) FROM LessonPurchase WHERE LPMemberID = " + memberId +
                        " AND RemainingSessions > 0")) {
            System.out.println("Cannot delete member: unused lesson sessions exist.");
            return;
        }

        // All checks passed — perform deletions
        try {
            Statement stmt = connection.createStatement();

            stmt.executeUpdate("DELETE FROM LessonPurchase WHERE LPMemberID = " + memberId);
            stmt.executeUpdate("DELETE FROM EquipmentRecord WHERE EMemberID = " + memberId);
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
  
}
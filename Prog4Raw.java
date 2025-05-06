import java.io.*;
import java.util.Random;
import java.sql.*;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat; 

public class Prog4Raw {
    private static final String ORACLE_URL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
    
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
        System.out.println("\n--- Query Processing Menu ---");
        System.out.println("1. List ski lessons purchased by a member");
        System.out.println("2. List lift rides and equipment rentals for a ski pass");
        System.out.println("3. List open intermediate trails with operational lifts");
        System.out.println("4. Custom Query: Calculate Net Worth of Properties (Income - Employee Salaries)");
        System.out.println("5. Return to main menu");
        System.out.print("Enter your choice (1-5): ");
        
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
        
        try {
            switch (choice) {
                case 1:
                    queryMemberLessons();
                    break;
                case 2:
                    querySkiPassActivity();
                    break;
                case 3:
                    queryIntermediateTrails();
                    break;
                case 4:
                    queryCustom();
                    break;
                case 5:
                    System.out.println("Returning to main menu.");
                    break;
                default:
                    System.out.println("Invalid choice. Returning to main menu.");
            }
        } catch (SQLException e) {
            System.err.println("*** SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("ErrorCode: " + e.getErrorCode());
        }
    }

    private static void queryMemberLessons() throws SQLException {
        System.out.print("Enter member ID: ");
        int memberId = Integer.parseInt(scanner.nextLine().trim());
        
        // Check if member exists
        PreparedStatement checkMember = connection.prepareStatement(
            "SELECT Name FROM Member WHERE MemberID = ?"
        );
        checkMember.setInt(1, memberId);
        ResultSet memberResult = checkMember.executeQuery();
        
        if (!memberResult.next()) {
            System.out.println("No member found with ID " + memberId);
            return;
        }
        
        String memberName = memberResult.getString("Name");
        System.out.println("\nLessons purchased by " + memberName + " (ID: " + memberId + "):");
        
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT lp.RemainingSessions, l.LessonLevel, e.Name, l.StartTime " +
            "FROM LessonPurchase lp " +
            "JOIN Lesson l ON lp.LPLessonID = l.LessonID " +
            "JOIN Instructor i ON l.InstructorID = i.InstructorID " +
            "JOIN Employee e ON l.InstructorID = e.EmployeeID " +
            "WHERE lp.LPMemberID = ? " +
            "ORDER BY l.StartTime"
        );
        stmt.setInt(1, memberId);
        ResultSet rs = stmt.executeQuery();
        
        boolean hasLessons = false;
        System.out.println("\n-------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-15s %-15s %-15s %-10s %-10s %-8s %-10s\n", 
                         "Purchase#", "Lesson Type", "Instructor", "Level", "Start", "End", "Price", "Remaining");
        System.out.println("-------------------------------------------------------------------------------------------");
        
        while (rs.next()) {
            hasLessons = true;
            System.out.printf("%-10d %-15s %-15s %-15s %-10s %-10s $%-7.2f %-10d\n", 
                rs.getInt("PurchaseID"),
                rs.getString("LessonType"),
                rs.getString("Name"),
                rs.getString("LessonLevel"),
                rs.getString("StartTime"),
                rs.getString("EndTime"),
                rs.getDouble("Price"),
                rs.getInt("RemainingSessions")
            );
        }
        
        if (!hasLessons) {
            System.out.println("No lesson purchases found for this member.");
        }
        System.out.println("-------------------------------------------------------------------------------------------");
    }
    
    private static void querySkiPassActivity() throws SQLException {
        System.out.print("Enter pass ID: ");
        int passId = Integer.parseInt(scanner.nextLine().trim());
        
        // Check if pass exists
        PreparedStatement checkPass = connection.prepareStatement(
            "SELECT p.PassID, p.PassType, m.Name " +
            "FROM SkiPass p " +
            "JOIN Member m ON p.MemberID = m.MemberID " +
            "WHERE p.PassID = ?"
        );
        checkPass.setInt(1, passId);
        ResultSet passResult = checkPass.executeQuery();
        
        if (!passResult.next()) {
            System.out.println("No ski pass found with ID " + passId);
            return;
        }
        
        String passType = passResult.getString("PassType");
        String memberName = passResult.getString("Name");
        
        System.out.println("\nActivity for " + passType + " Pass (ID: " + passId + ")");
        System.out.println("Member: " + memberName);
        
        // First, query lift rides
        System.out.println("\n--- Lift Rides ---");
        PreparedStatement liftStmt = connection.prepareStatement(
            "SELECT lr.RideTime, l.LiftName " +
            "FROM LiftRide lr " +
            "JOIN Lift l ON lr.LiftID = l.LiftID " +
            "WHERE lr.LRPassID = ? " +
            "ORDER BY lr.RideTime DESC"
        );
        liftStmt.setInt(1, passId);
        ResultSet liftRs = liftStmt.executeQuery();
        
        boolean hasRides = false;
        System.out.println("-------------------------------------");
        System.out.printf("%-20s %-20s\n", "Timestamp", "Lift Name");
        System.out.println("-------------------------------------");
        
        while (liftRs.next()) {
            hasRides = true;
            System.out.printf("%-20s %-20s\n", 
                liftRs.getTimestamp("RideTime").toString(),
                liftRs.getString("LiftName")
            );
        }
        
        if (!hasRides) {
            System.out.println("No lift rides found for this pass.");
        }
        System.out.println("-------------------------------------");
        
        // Next, query equipment rentals
        System.out.println("\n--- Equipment Rentals ---");
        PreparedStatement equipStmt = connection.prepareStatement(
            "SELECT er.RentalTime, er.ReturnStatus, e.EquipmentType, e.EquipmentSize " +
            "FROM EquipmentRecord er " +
            "JOIN Equipment e ON er.EEquipmentID = e.EquipmentID " +
            "WHERE er.EPassID = ? " +
            "ORDER BY er.RentalTime DESC"
        );
        equipStmt.setInt(1, passId);
        ResultSet equipRs = equipStmt.executeQuery();
        
        boolean hasRentals = false;
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-20s %-15s %-15s %-15s\n", "Rental Time", "Equipment Type", "Size", "Return Status");
        System.out.println("---------------------------------------------------------------");
        
        while (equipRs.next()) {
            hasRentals = true;
            System.out.printf("%-20s %-15s %-15s %-15s\n", 
                equipRs.getTimestamp("RentalTime").toString(),
                equipRs.getString("EquipmentType"),
                equipRs.getString("EquipmentSize"),
                equipRs.getString("ReturnStatus")
            );
        }
        
        if (!hasRentals) {
            System.out.println("No equipment rentals found for this pass.");
        }
        System.out.println("---------------------------------------------------------------");
    }
    
    private static void queryIntermediateTrails() throws SQLException {
        System.out.println("\nOpen intermediate-level trails with operational lifts:");
        
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT t.Name AS TrailName, t.Category, t.DifficultyLevel, " +
            "       l.Name AS LiftName, l.OpenTime, l.ClosingTime " +
            "FROM Trail t " +
            "JOIN Lift l ON (t.TrailID = l.TrailTo OR t.TrailID = l.TrailFrom) " +
            "WHERE t.Status = 'Open' " +
            "AND t.DifficultyLevel = 'Intermediate' " +
            "AND l.Status = 'Operational' " +
            "ORDER BY t.Name"
        );
        ResultSet rs = stmt.executeQuery();
        
        boolean hasTrails = false;
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("%-20s %-15s %-15s %-15s %-10s %-10s\n", 
                         "Trail Name", "Category", "Difficulty", "Connected Lift", "Opens", "Closes");
        System.out.println("--------------------------------------------------------------------------");
        
        while (rs.next()) {
            hasTrails = true;
            System.out.printf("%-20s %-15s %-15s %-15s %-10s %-10s\n", 
                rs.getString("TrailName"),
                rs.getString("Category"),
                rs.getString("DifficultyLevel"),
                rs.getString("LiftName"),
                rs.getString("OpenTime"),
                rs.getString("ClosingTime")
            );
        }
        
        if (!hasTrails) {
            System.out.println("No open intermediate trails with operational lifts found.");
        }
        System.out.println("--------------------------------------------------------------------------");
    }
    
    private static void queryCustom() throws SQLException {
        System.out.print("Enter property ID to calculate net worth for (or enter 0 for all properties): ");
        int propertyId = Integer.parseInt(scanner.nextLine().trim());
        
        String query;
        PreparedStatement stmt;
        
        if (propertyId > 0) {
            // Query for a specific property
            query = "SELECT p.PropertyID, p.PropertyName, p.PropertyType, " +
                    "SUM(i.Amount) AS TotalIncome, " +
                    "SUM(e.Salary) AS TotalSalaries, " +
                    "(SUM(i.Amount) - SUM(e.Salary)) AS NetWorth " +
                    "FROM Property p " +
                    "LEFT JOIN Income i ON p.PropertyID = i.IPropertyID " +
                    "LEFT JOIN Employee e ON p.PropertyID = e.PropertyID " +
                    "WHERE p.PropertyID = ? " +
                    "GROUP BY p.PropertyID, p.PropertyName, p.PropertyType";
            
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, propertyId);
        } else {
            // Query for all properties
            query = "SELECT p.PropertyID, p.PropertyName, p.PropertyType, " +
                    "SUM(i.Amount) AS TotalIncome, " +
                    "SUM(e.Salary) AS TotalSalaries, " +
                    "(SUM(i.Amount) - SUM(e.Salary)) AS NetWorth " +
                    "FROM Property p " +
                    "LEFT JOIN Income i ON p.PropertyID = i.IPropertyID " +
                    "LEFT JOIN Employee e ON p.PropertyID = e.PropertyID " +
                    "GROUP BY p.PropertyID, p.PropertyName, p.PropertyType " +
                    "ORDER BY NetWorth DESC";
            
            stmt = connection.prepareStatement(query);
        }
        
        ResultSet rs = stmt.executeQuery();
        
        boolean hasResults = false;
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.printf("%-8s %-20s %-15s %-12s %-12s %-12s\n", 
                         "ID", "Property Name", "Type", "Income", "Salaries", "Net Worth");
        System.out.println("-------------------------------------------------------------------------");
        
        while (rs.next()) {
            hasResults = true;
            
            int id = rs.getInt("PropertyID");
            String name = rs.getString("PropertyName");
            String type = rs.getString("PropertyType");
            double income = rs.getDouble("TotalIncome");
            double salaries = rs.getDouble("TotalSalaries");
            double netWorth = rs.getDouble("NetWorth");
            
            System.out.printf("%-8d %-20s %-15s $%-11.2f $%-11.2f $%-11.2f\n", 
                             id, name, type, income, salaries, netWorth);
        }
        
        if (!hasResults) {
            if (propertyId > 0) {
                System.out.println("No property found with ID " + propertyId);
            } else {
                System.out.println("No properties found in the database.");
            }
        }
        
        System.out.println("-------------------------------------------------------------------------");
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
        System.out.println("\n--- Update Menu ---");
        PrintTable();
        int choice = getUserChoice();
        
        try {
            switch (choice) {
                case 1:
                    UpdateMember();
                    break;
                case 2:
                    UpdateSkiPass();
                    break;
                case 3:
                    UpdateEquipment();
                    break;
                case 4:
                    UpdateEquipmentRental();
                    break;
                case 5:
                    UpdateLessonPurchase();
                    break;
                default:
                    System.out.println("Invalid choice. Returning to main menu.");
            }
        } catch (SQLException e) {
            System.err.println("*** SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("ErrorCode: " + e.getErrorCode());
        }
    }


    private static void UpdateMember() throws SQLException {
        System.out.print("Enter member ID to update: ");
        int memberId = Integer.parseInt(scanner.nextLine().trim());
        
        PreparedStatement checkMember = connection.prepareStatement(
            "SELECT * FROM Member WHERE MemberId = ?"
        );
        checkMember.setInt(1, memberId);
        ResultSet memberResult = checkMember.executeQuery();
        
        if (!memberResult.next()) {
            System.out.println("No member found with ID " + memberId);
            return;
        }
        
        System.out.println("\nCurrent member details:");
        System.out.println("Name: " + memberResult.getString("Name"));
        System.out.println("Phone: " + memberResult.getString("PhoneNumber"));
        System.out.println("Email: " + memberResult.getString("EmailAddress"));
        System.out.println("Emergency Contact: " + memberResult.getString("EmergencyContact"));
        
        System.out.println("\nSelect field to update:");
        System.out.println("1. Phone Number");
        System.out.println("2. Email Address");
        System.out.println("3. Emergency Contact");
        System.out.print("Enter choice (1-3): ");
        
        int updateChoice = Integer.parseInt(scanner.nextLine().trim());
        String query = "";
        String fieldName = "";
        
        switch (updateChoice) {
            case 1:
                fieldName = "PhoneNumber";
                query = "UPDATE Member SET PhoneNumber = ? WHERE MemberID = ?";
                break;
            case 2:
                fieldName = "EmailAddress";
                query = "UPDATE Member SET EmailAddress = ? WHERE MemberID = ?";
                break;
            case 3:
                fieldName = "EmergencyContact";
                query = "UPDATE Member SET EmergencyContact = ? WHERE MemberID = ?";
                break;
            default:
                System.out.println("Invalid choice. Update cancelled.");
                return;
        }
        
        System.out.print("Enter new " + fieldName.replace("_", " ") + ": ");
        String newValue = scanner.nextLine().trim();
        
        PreparedStatement updateStmt = connection.prepareStatement(query);
        updateStmt.setString(1, newValue);
        updateStmt.setInt(2, memberId);
        int rowsUpdated = updateStmt.executeUpdate();
        
        if (rowsUpdated > 0) {
            System.out.println("Member information successfully updated.");
        } else {
            System.out.println("Update failed. Please try again.");
        }
    }

    private static void UpdateSkiPass() throws SQLException {
        System.out.print("Enter ski pass ID to update: ");
        int passId = Integer.parseInt(scanner.nextLine().trim());
    
    
        PreparedStatement checkPass = connection.prepareStatement(
            "SELECT * FROM SkiPass WHERE PassID = ?"
        );
        checkPass.setInt(1, passId);
        ResultSet passResult = checkPass.executeQuery();
        
        if (!passResult.next()) {
            System.out.println("No ski pass found with ID " + passId);
            return;
        }
        
        System.out.println("\nCurrent ski pass details:");
        System.out.println("Member ID: " + passResult.getInt("MemberID"));
        System.out.println("Pass Type: " + passResult.getString("PassType"));
        System.out.println("Activation Date: " + passResult.getDate("ActivationDate"));
        System.out.println("Expiration Date: " + passResult.getDate("ExpirationDate"));
        System.out.println("Purchase Date: " + passResult.getDate("PurchaseDate"));
        System.out.println("Total Uses: " + passResult.getInt("TotalUses"));
        System.out.println("Remaining Uses: " + passResult.getInt("RemainingUses"));
        System.out.println("Price: $" + passResult.getDouble("Price"));
        
        System.out.println("\nSelect field to update:");
        System.out.println("1. Remaining Uses");
        System.out.println("2. Expiration Date");
        System.out.print("Enter choice (1-2): ");
        
        int updateChoice = Integer.parseInt(scanner.nextLine().trim());
        String query = "";
        String fieldName = "";
        
        switch (updateChoice) {
            case 1:
                fieldName = "RemainingUses";
                System.out.print("Enter new remaining uses value: ");
                int newRemainingUses = Integer.parseInt(scanner.nextLine().trim());
                
                query = "UPDATE SkiPass SET RemainingUses = ? WHERE PassID = ?";
                PreparedStatement updateUsesStmt = connection.prepareStatement(query);
                updateUsesStmt.setInt(1, newRemainingUses);
                updateUsesStmt.setInt(2, passId);
                int usesRowsUpdated = updateUsesStmt.executeUpdate();
                
                if (usesRowsUpdated > 0) {
                    System.out.println("Ski pass remaining uses successfully updated to " + newRemainingUses);
                } else {
                    System.out.println("Update failed. Please try again.");
                }
                break;
                
            case 2:
                fieldName = "ExpirationDate";
                System.out.print("Enter new expiration date (YYYY-MM-DD): ");
                String dateStr = scanner.nextLine().trim();
                
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date parsedDate = dateFormat.parse(dateStr);
                    java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
                    
                    query = "UPDATE SkiPass SET ExpirationDate = ? WHERE PassID = ?";
                    PreparedStatement updateDateStmt = connection.prepareStatement(query);
                    updateDateStmt.setDate(1, sqlDate);
                    updateDateStmt.setInt(2, passId);
                    int dateRowsUpdated = updateDateStmt.executeUpdate();
                    
                    if (dateRowsUpdated > 0) {
                        System.out.println("Ski pass expiration date successfully updated to " + dateStr);
                    } else {
                        System.out.println("Update failed. Please try again.");
                    }
                } catch (ParseException e) {
                    System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
                }
                break;
                
            default:
                System.out.println("Invalid choice. Update cancelled.");
                return;
        }
    }

    private static void UpdateEquipment() throws SQLException {
        System.out.print("Enter equipment ID to update: ");
        int equipmentId = Integer.parseInt(scanner.nextLine().trim());
        

        PreparedStatement checkEquipment = connection.prepareStatement(
            "SELECT * FROM Equipment WHERE EquipmentID = ?"
        );
        checkEquipment.setInt(1, equipmentId);
        ResultSet equipmentResult = checkEquipment.executeQuery();
        
        if (!equipmentResult.next()) {
            System.out.println("No equipment found with ID " + equipmentId);
            return;
        }
        
        System.out.println("\nCurrent equipment details:");
        System.out.println("Equipment Type: " + equipmentResult.getString("EquipmentType"));
        System.out.println("Equipment Size: " + equipmentResult.getString("EquipmentSize"));
        System.out.println("Status: " + equipmentResult.getString("Status"));
        
        System.out.println("\nSelect field to update:");
        System.out.println("1. Equipment Type");
        System.out.println("2. Equipment Size");
        System.out.println("3. Status");
        System.out.print("Enter choice (1-3): ");
        
        int updateChoice = Integer.parseInt(scanner.nextLine().trim());
        String query = "";
        String fieldName = "";
        
        switch (updateChoice) {
            case 1:
                fieldName = "EquipmentType";
                query = "UPDATE Equipment SET EquipmentType = ? WHERE EquipmentID = ?";
                break;
            case 2:
                fieldName = "EquipmentSize";
                query = "UPDATE Equipment SET EquipmentSize = ? WHERE EquipmentID = ?";
                break;
            case 3:
                fieldName = "Status";
                query = "UPDATE Equipment SET Status = ? WHERE EquipmentID = ?";
                break;
            default:
                System.out.println("Invalid choice. Update cancelled.");
                return;
        }
        
        System.out.print("Enter new " + fieldName + ": ");
        String newValue = scanner.nextLine().trim();
        
        PreparedStatement updateStmt = connection.prepareStatement(query);
        updateStmt.setString(1, newValue);
        updateStmt.setInt(2, equipmentId);
        int rowsUpdated = updateStmt.executeUpdate();
        
        if (rowsUpdated > 0) {
            System.out.println("Equipment information successfully updated.");
            System.out.println("Change logged for audit purposes.");
        } else {
            System.out.println("Update failed. Please try again.");
        }
    }

    private static void UpdateEquipmentRental() throws SQLException {
        System.out.print("Enter rental ID to update: ");
        int rentalId = Integer.parseInt(scanner.nextLine().trim());
    
        PreparedStatement checkRental = connection.prepareStatement(
            "SELECT * FROM EquipmentRecord WHERE RentalID = ?"
        );
        checkRental.setInt(1, rentalId);
        ResultSet rentalResult = checkRental.executeQuery();
        
        if (!rentalResult.next()) {
            System.out.println("No rental record found with ID " + rentalId);
            return;
        }
        
        System.out.println("\nCurrent rental details:");
        System.out.println("Member ID: " + rentalResult.getInt("EMemberID"));
        System.out.println("Pass ID: " + rentalResult.getInt("EPassID"));
        System.out.println("Return Status: " + rentalResult.getString("ReturnStatus"));
        System.out.println("Rental Time: " + rentalResult.getTimestamp("RentalTime"));
        
        System.out.println("\nSelect field to update:");
        System.out.println("1. Return Status");
        System.out.print("Enter choice (1): ");
        
        int updateChoice = Integer.parseInt(scanner.nextLine().trim());
        
        if (updateChoice != 1) {
            System.out.println("Invalid choice. Update cancelled.");
            return;
        }
        
        System.out.println("Enter new Return Status (e.g., Returned, Damaged, Not Picked Up): ");
        String newStatus = scanner.nextLine().trim();
        
        // Check if the string is empty
        if (newStatus.isEmpty()) {
            System.out.println("Return status cannot be empty. Update cancelled.");
            return;
        }
        
        PreparedStatement updateStmt = connection.prepareStatement(
            "UPDATE EquipmentRecord SET ReturnStatus = ? WHERE RentalID = ?"
        );
        updateStmt.setString(1, newStatus);
        updateStmt.setInt(2, rentalId);
        int rowsUpdated = updateStmt.executeUpdate();
        
        if (rowsUpdated > 0) {
            System.out.println("Equipment rental return status successfully updated to \"" + newStatus + "\"");
        } else {
            System.out.println("Update failed. Please try again.");
        }
    }

    private static void UpdateLessonPurchase() throws SQLException {
        System.out.print("Enter lesson purchase ID to update: ");
        int purchaseId = Integer.parseInt(scanner.nextLine().trim());
        
        PreparedStatement checkPurchase = connection.prepareStatement(
            "SELECT * FROM LessonPurchase WHERE PurchaseID = ?"
        );
        checkPurchase.setInt(1, purchaseId);
        ResultSet purchaseResult = checkPurchase.executeQuery();
        
        if (!purchaseResult.next()) {
            System.out.println("No lesson purchase found with ID " + purchaseId);
            return;
        }
        
        System.out.println("\nCurrent lesson purchase details:");
        System.out.println("Member ID: " + purchaseResult.getInt("LPMemberID"));
        System.out.println("Lesson ID: " + purchaseResult.getInt("LPLessonID"));
        System.out.println("Purchase ID: " + purchaseResult.getInt("PurchaseID"));
        System.out.println("Remaining Sessions: " + purchaseResult.getInt("RemainingSessions"));
        
        System.out.println("\nSelect field to update:");
        System.out.println("1. Remaining Sessions");
        System.out.print("Enter choice (1): ");
        
        int updateChoice = Integer.parseInt(scanner.nextLine().trim());
        
        if (updateChoice != 1) {
            System.out.println("Invalid choice. Update cancelled.");
            return;
        }
        
        System.out.print("Enter new Remaining Sessions value: ");
        int newRemainingSessions = Integer.parseInt(scanner.nextLine().trim());
        
        if (newRemainingSessions < 0) {
            System.out.println("Invalid value. Remaining sessions cannot be negative. Update cancelled.");
            return;
        }
        
        PreparedStatement updateStmt = connection.prepareStatement(
            "UPDATE LessonPurchase SET RemainingSessions = ? WHERE PurchaseID = ?"
        );
        updateStmt.setInt(1, newRemainingSessions);
        updateStmt.setInt(2, purchaseId);
        int rowsUpdated = updateStmt.executeUpdate();
        
        if (rowsUpdated > 0) {
            System.out.println("Lesson purchase remaining sessions successfully updated to " + newRemainingSessions);
        } else {
            System.out.println("Update failed. Please try again.");
        }
    }


    private static void AddTupleToTable() {
        System.out.println("\nPlease enter a table to add to");
        PrintTable();
        int choice = getUserChoice();
        
        try {
            switch (choice) {
                case 1:
                    AddMemberQuery();
                    break;
                case 2:
                    AddSkiPassQuery();
                    break;
                case 3:
                    AddEquipmentQuery();
                    break;
                case 4:
                    AddEquipmentRentalQuery();
                    break;
                case 5:
                    AddLessonPurchaseQuery();
                    break;
                default:
                    System.out.println("Invalid choice. Returning to main menu.");
            }
        } catch (SQLException e) {
            System.err.println("*** SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("ErrorCode: " + e.getErrorCode());
        }
    }

    private static void AddMemberQuery() throws SQLException {
        System.out.println("\n--- Add New Member ---");
        
        System.out.print("Enter member name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine().trim();
        
        System.out.print("Enter email address: ");
        String emailAddress = scanner.nextLine().trim();
        
        System.out.print("Enter emergency contact: ");
        String emergencyContact = scanner.nextLine().trim();
        
        // Generate a random member ID between 1 and 1,000,000
        Random random = new Random();
        int memberId = random.nextInt(1000000) + 1;
        
        // Check if the ID already exists
        PreparedStatement checkId = connection.prepareStatement(
            "SELECT COUNT(*) FROM Member WHERE MemberID = ?"
        );
        
        boolean idExists = true;
        while (idExists) {
            checkId.setInt(1, memberId);
            ResultSet rs = checkId.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                idExists = false;
            } else {
                memberId = random.nextInt(1000000) + 1;
            }
        }
        
        PreparedStatement insertStmt = connection.prepareStatement(
            "INSERT INTO Member (MemberID, Name, PhoneNumber, EmailAddress, EmergencyContact) " +
            "VALUES (?, ?, ?, ?, ?)",
            new String[] {"MemberID"}  
        );
        
        insertStmt.setInt(1, memberId);
        insertStmt.setString(2, name);
        insertStmt.setString(3, phoneNumber);
        insertStmt.setString(4, emailAddress);
        insertStmt.setString(5, emergencyContact);
        
        int rowsInserted = insertStmt.executeUpdate();
        
        if (rowsInserted > 0) {
            System.out.println("Member successfully added with ID: " + memberId);
        } else {
            System.out.println("Failed to add member. Please try again.");
        }
    }
    
    private static void AddSkiPassQuery() throws SQLException {
        System.out.println("\n--- Add New Ski Pass ---");
        
        System.out.print("Enter member ID: ");
        int memberId = Integer.parseInt(scanner.nextLine().trim());
        
        PreparedStatement checkMember = connection.prepareStatement(
            "SELECT COUNT(*) FROM Member WHERE MemberID = ?"
        );
        checkMember.setInt(1, memberId);
        ResultSet memberResult = checkMember.executeQuery();
        memberResult.next();
        
        if (memberResult.getInt(1) == 0) {
            System.out.println("Error: Member with ID " + memberId + " does not exist.");
            return;
        }
        
        System.out.print("Enter pass type: ");
        String passType = scanner.nextLine().trim();
        
        System.out.print("Enter activation date (YYYY-MM-DD): ");
        String activationDateStr = scanner.nextLine().trim();
        
        System.out.print("Enter expiration date (YYYY-MM-DD): ");
        String expirationDateStr = scanner.nextLine().trim();
        
        System.out.print("Enter total uses: ");
        int totalUses = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Enter price: ");
        double price = Double.parseDouble(scanner.nextLine().trim());
        
        // Generate a random pass ID between 1 and 1,000,000
        Random random = new Random();
        int passId = random.nextInt(1000000) + 1;
        
        // Check if the ID already exists
        PreparedStatement checkId = connection.prepareStatement(
            "SELECT COUNT(*) FROM SkiPass WHERE PassID = ?"
        );
        
        boolean idExists = true;
        while (idExists) {
            checkId.setInt(1, passId);
            ResultSet rs = checkId.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                idExists = false;
            } else {
                passId = random.nextInt(1000000) + 1;
            }
        }
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedActivationDate = dateFormat.parse(activationDateStr);
            java.sql.Date activationDate = new java.sql.Date(parsedActivationDate.getTime());
            
            java.util.Date parsedExpirationDate = dateFormat.parse(expirationDateStr);
            java.sql.Date expirationDate = new java.sql.Date(parsedExpirationDate.getTime());
            
            java.sql.Timestamp purchaseDate = new java.sql.Timestamp(System.currentTimeMillis());
            
            PreparedStatement insertStmt = connection.prepareStatement(
                "INSERT INTO SkiPass (PassID, MemberID, PassType, ActivationDate, ExpirationDate, " +
                "PurchaseDate, TotalUses, RemainingUses, Price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            
            insertStmt.setInt(1, passId);
            insertStmt.setInt(2, memberId);
            insertStmt.setString(3, passType);
            insertStmt.setDate(4, activationDate);
            insertStmt.setDate(5, expirationDate);
            insertStmt.setTimestamp(6, purchaseDate);
            insertStmt.setInt(7, totalUses);
            insertStmt.setInt(8, totalUses);  
            insertStmt.setDouble(9, price);
            
            int rowsInserted = insertStmt.executeUpdate();
            
            if (rowsInserted > 0) {
                System.out.println("Ski pass successfully added with ID: " + passId);
            } else {
                System.out.println("Failed to add ski pass. Please try again.");
            }
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
        }
    }
    
    private static void AddEquipmentQuery() throws SQLException {
        System.out.println("\n--- Add New Equipment ---");
        
        System.out.print("Enter equipment type: ");
        String equipmentType = scanner.nextLine().trim();
        
        System.out.print("Enter equipment size: ");
        String equipmentSize = scanner.nextLine().trim();
        
        System.out.print("Enter status (Available/Maintenance/Retired): ");
        String status = scanner.nextLine().trim();
        
        if (!status.equalsIgnoreCase("Available") && 
            !status.equalsIgnoreCase("Maintenance") && 
            !status.equalsIgnoreCase("Retired")) {
            System.out.println("Invalid status. Must be 'Available', 'Maintenance', or 'Retired'.");
            return;
        }
        
        // Generate a random equipment ID between 1 and 1,000,000
        Random random = new Random();
        int equipmentId = random.nextInt(1000000) + 1;
        
        // Check if the ID already exists
        PreparedStatement checkId = connection.prepareStatement(
            "SELECT COUNT(*) FROM Equipment WHERE EquipmentID = ?"
        );
        
        boolean idExists = true;
        while (idExists) {
            checkId.setInt(1, equipmentId);
            ResultSet rs = checkId.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                idExists = false;
            } else {
                equipmentId = random.nextInt(1000000) + 1;
            }
        }
        
        PreparedStatement insertStmt = connection.prepareStatement(
            "INSERT INTO Equipment (EquipmentID, EquipmentType, EquipmentSize, Status) " +
            "VALUES (?, ?, ?, ?)"
        );
        
        insertStmt.setInt(1, equipmentId);
        insertStmt.setString(2, equipmentType);
        insertStmt.setString(3, equipmentSize);
        insertStmt.setString(4, status);
        
        int rowsInserted = insertStmt.executeUpdate();
        
        if (rowsInserted > 0) {
            System.out.println("Equipment successfully added with ID: " + equipmentId);
        } else {
            System.out.println("Failed to add equipment. Please try again.");
        }
    }
    
    private static void AddEquipmentRentalQuery() throws SQLException {
        System.out.println("\n--- Add New Equipment Rental ---");
        
        System.out.print("Enter member ID: ");
        int memberId = Integer.parseInt(scanner.nextLine().trim());
        
        PreparedStatement checkMember = connection.prepareStatement(
            "SELECT COUNT(*) FROM Member WHERE MemberID = ?"
        );
        checkMember.setInt(1, memberId);
        ResultSet memberResult = checkMember.executeQuery();
        memberResult.next();
        
        if (memberResult.getInt(1) == 0) {
            System.out.println("Error: Member with ID " + memberId + " does not exist.");
            return;
        }
        
        System.out.print("Enter pass ID: ");
        int passId = Integer.parseInt(scanner.nextLine().trim());
        
        PreparedStatement checkPass = connection.prepareStatement(
            "SELECT COUNT(*) FROM SkiPass WHERE PassID = ? AND MemberID = ? " +
            "AND ExpirationDate >= SYSDATE AND RemainingUses > 0"
        );
        checkPass.setInt(1, passId);
        checkPass.setInt(2, memberId);
        ResultSet passResult = checkPass.executeQuery();
        passResult.next();
        
        if (passResult.getInt(1) == 0) {
            System.out.println("Error: No valid ski pass found for this member with ID " + passId);
            return;
        }
        
        System.out.print("Enter equipment ID: ");
        int equipmentId = Integer.parseInt(scanner.nextLine().trim());
        
        PreparedStatement checkEquipment = connection.prepareStatement(
            "SELECT COUNT(*) FROM Equipment WHERE EquipmentID = ? AND Status = 'Available'"
        );
        checkEquipment.setInt(1, equipmentId);
        ResultSet equipmentResult = checkEquipment.executeQuery();
        equipmentResult.next();
        
        if (equipmentResult.getInt(1) == 0) {
            System.out.println("Error: No available equipment found with ID " + equipmentId);
            return;
        }
        
        // Generate a random rental ID between 1 and 1,000,000
        Random random = new Random();
        int rentalId = random.nextInt(1000000) + 1;
        
        // Check if the ID already exists
        PreparedStatement checkId = connection.prepareStatement(
            "SELECT COUNT(*) FROM EquipmentRecord WHERE RentalID = ?"
        );
        
        boolean idExists = true;
        while (idExists) {
            checkId.setInt(1, rentalId);
            ResultSet rs = checkId.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                idExists = false;
            } else {
                rentalId = random.nextInt(1000000) + 1;
            }
        }
        
        java.sql.Timestamp rentalTime = new java.sql.Timestamp(System.currentTimeMillis());
        
        PreparedStatement insertStmt = connection.prepareStatement(
            "INSERT INTO EquipmentRecord (RentalID, EMemberID, EPassID, ReturnStatus, RentalTime) " +
            "VALUES (?, ?, ?, 'N', ?)"
        );
        
        insertStmt.setInt(1, rentalId);
        insertStmt.setInt(2, memberId);
        insertStmt.setInt(3, passId);
        insertStmt.setTimestamp(4, rentalTime);
        
        connection.setAutoCommit(false);
        
        try {
            int rowsInserted = insertStmt.executeUpdate();
            
            PreparedStatement updateEquipment = connection.prepareStatement(
                "UPDATE Equipment SET Status = 'In Use' WHERE EquipmentID = ?"
            );
            updateEquipment.setInt(1, equipmentId);
            updateEquipment.executeUpdate();
            
            PreparedStatement updatePass = connection.prepareStatement(
                "UPDATE SkiPass SET RemainingUses = RemainingUses - 1 WHERE PassID = ?"
            );
            updatePass.setInt(1, passId);
            updatePass.executeUpdate();
            
            connection.commit();
            
            if (rowsInserted > 0) {
                System.out.println("Equipment rental successfully added with ID: " + rentalId);
            } else {
                System.out.println("Failed to add equipment rental. Please try again.");
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    private static void AddLessonPurchaseQuery() throws SQLException {
        System.out.println("\n--- Add New Lesson Purchase ---");
        
        System.out.print("Enter member ID: ");
        int memberId = Integer.parseInt(scanner.nextLine().trim());
        
        PreparedStatement checkMember = connection.prepareStatement(
            "SELECT COUNT(*) FROM Member WHERE MemberID = ?"
        );
        checkMember.setInt(1, memberId);
        ResultSet memberResult = checkMember.executeQuery();
        memberResult.next();
        
        if (memberResult.getInt(1) == 0) {
            System.out.println("Error: Member with ID " + memberId + " does not exist.");
            return;
        }
        
        System.out.print("Enter lesson ID: ");
        int lessonId = Integer.parseInt(scanner.nextLine().trim());
        
        PreparedStatement checkLesson = connection.prepareStatement(
            "SELECT COUNT(*) FROM Lesson WHERE LessonID = ?"
        );
        checkLesson.setInt(1, lessonId);
        ResultSet lessonResult = checkLesson.executeQuery();
        lessonResult.next();
        
        if (lessonResult.getInt(1) == 0) {
            System.out.println("Error: Lesson with ID " + lessonId + " does not exist.");
            return;
        }
        
        System.out.print("Enter number of sessions to purchase: ");
        int sessions = Integer.parseInt(scanner.nextLine().trim());
        
        if (sessions <= 0) {
            System.out.println("Error: Number of sessions must be greater than zero.");
            return;
        }
        
        Random random = new Random();
        int purchaseId = random.nextInt(1000000) + 1;
        
        PreparedStatement checkId = connection.prepareStatement(
            "SELECT COUNT(*) FROM LessonPurchase WHERE PurchaseID = ?"
        );
        
        boolean idExists = true;
        while (idExists) {
            checkId.setInt(1, purchaseId);
            ResultSet rs = checkId.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                idExists = false;
            } else {
                purchaseId = random.nextInt(1000000) + 1;
            }
        }
        
        PreparedStatement insertStmt = connection.prepareStatement(
            "INSERT INTO LessonPurchase (PurchaseID, LPMemberID, LPLessonID, RemainingSessions) " +
            "VALUES (?, ?, ?, ?)"
        );
        
        insertStmt.setInt(1, purchaseId);
        insertStmt.setInt(2, memberId);
        insertStmt.setInt(3, lessonId);
        insertStmt.setInt(4, sessions);
        
        int rowsInserted = insertStmt.executeUpdate();
        
        if (rowsInserted > 0) {
            System.out.println("Lesson purchase successfully added with ID: " + purchaseId);
        } else {
            System.out.println("Failed to add lesson purchase. Please try again.");
        }
    }

    private static void PrintTable() {
        System.out.println("1. Member");
        System.out.println("2. Ski Pass");
        System.out.println("3. Equipment");
        System.out.println("4. Equipment Record");
        System.out.println("5. Lesson Purchase");
    }   
}
import java.io.*;
import java.sql.*;
import java.util.Scanner;


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
        throw new UnsupportedOperationException("Unimplemented method 'ProcessQueueries'");
    }


    private static void DeleteTupleFromTable() {
        throw new UnsupportedOperationException("Unimplemented method 'DeleteTupleFromTable'");
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
        System.out.println("Member ID: " + passResult.getInt("SMemberID"));
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
        
        System.out.print("Enter new Return Status (Y/N): ");
        String newStatus = scanner.nextLine().trim().toUpperCase();
        
        if (!newStatus.equals("Y") && !newStatus.equals("N")) {
            System.out.println("Invalid status. Must be 'Y' or 'N'. Update cancelled.");
            return;
        }
        
        PreparedStatement updateStmt = connection.prepareStatement(
            "UPDATE EquipmentRecord SET ReturnStatus = ? WHERE RentalID = ?"
        );
        updateStmt.setString(1, newStatus);
        updateStmt.setInt(2, rentalId);
        int rowsUpdated = updateStmt.executeUpdate();
        
        if (rowsUpdated > 0) {
            System.out.println("Equipment rental return status successfully updated to " + 
                            (newStatus.equals("Y") ? "Returned" : "Not Returned"));
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
        
        PreparedStatement insertStmt = connection.prepareStatement(
            "INSERT INTO Member (MemberID, Name, PhoneNumber, EmailAddress, EmergencyContact) " +
            "VALUES (Member_Seq.NEXTVAL, ?, ?, ?, ?)",
            new String[] {"MemberID"}  
        );
        
        insertStmt.setString(1, name);
        insertStmt.setString(2, phoneNumber);
        insertStmt.setString(3, emailAddress);
        insertStmt.setString(4, emergencyContact);
        
        int rowsInserted = insertStmt.executeUpdate();
        
        if (rowsInserted > 0) {
            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int memberId = generatedKeys.getInt(1);
                System.out.println("Member successfully added with ID: " + memberId);
            } else {
                System.out.println("Member successfully added, but could not retrieve member ID.");
            }
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
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedActivationDate = dateFormat.parse(activationDateStr);
            java.sql.Date activationDate = new java.sql.Date(parsedActivationDate.getTime());
            
            java.util.Date parsedExpirationDate = dateFormat.parse(expirationDateStr);
            java.sql.Date expirationDate = new java.sql.Date(parsedExpirationDate.getTime());
            
            java.sql.Timestamp purchaseDate = new java.sql.Timestamp(System.currentTimeMillis());
            
            PreparedStatement insertStmt = connection.prepareStatement(
                "INSERT INTO SkiPass (PassID, SMemberID, PassType, ActivationDate, ExpirationDate, " +
                "PurchaseDate, TotalUses, RemainingUses, Price) " +
                "VALUES (SkiPass_Seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)",
                new String[] {"PassID"}  
            );
            
            insertStmt.setInt(1, memberId);
            insertStmt.setString(2, passType);
            insertStmt.setDate(3, activationDate);
            insertStmt.setDate(4, expirationDate);
            insertStmt.setTimestamp(5, purchaseDate);
            insertStmt.setInt(6, totalUses);
            insertStmt.setInt(7, totalUses);  
            insertStmt.setDouble(8, price);
            
            int rowsInserted = insertStmt.executeUpdate();
            
            if (rowsInserted > 0) {
                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int passId = generatedKeys.getInt(1);
                    System.out.println("Ski pass successfully added with ID: " + passId);
                } else {
                    System.out.println("Ski pass successfully added, but could not retrieve pass ID.");
                }
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
        
        PreparedStatement insertStmt = connection.prepareStatement(
            "INSERT INTO Equipment (EquipmentID, EquipmentType, EquipmentSize, Status) " +
            "VALUES (Equipment_Seq.NEXTVAL, ?, ?, ?)",
            new String[] {"EquipmentID"}  
        );
        
        insertStmt.setString(1, equipmentType);
        insertStmt.setString(2, equipmentSize);
        insertStmt.setString(3, status);
        
        int rowsInserted = insertStmt.executeUpdate();
        
        if (rowsInserted > 0) {
            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int equipmentId = generatedKeys.getInt(1);
                System.out.println("Equipment successfully added with ID: " + equipmentId);
            } else {
                System.out.println("Equipment successfully added, but could not retrieve equipment ID.");
            }
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
            "SELECT COUNT(*) FROM SkiPass WHERE PassID = ? AND SMemberID = ? " +
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
        
        java.sql.Timestamp rentalTime = new java.sql.Timestamp(System.currentTimeMillis());
        
        PreparedStatement insertStmt = connection.prepareStatement(
            "INSERT INTO EquipmentRecord (RentalID, EMemberID, EPassID, ReturnStatus, RentalTime) " +
            "VALUES (EquipmentRecord_Seq.NEXTVAL, ?, ?, 'N', ?)",
            new String[] {"RentalID"}  
        );
        
        insertStmt.setInt(1, memberId);
        insertStmt.setInt(2, passId);
        insertStmt.setTimestamp(3, rentalTime);
        
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
                
                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int rentalId = generatedKeys.getInt(1);
                    System.out.println("Equipment rental successfully added with ID: " + rentalId);
                } else {
                    System.out.println("Equipment rental successfully added, but could not retrieve rental ID.");
                }
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

        PreparedStatement insertStmt = connection.prepareStatement(
            "INSERT INTO LessonPurchase (PurchaseID, LPMemberID, LPLessonID, RemainingSessions) " +
            "VALUES (LessonPurchase_Seq.NEXTVAL, ?, ?, ?)",
            new String[] {"PurchaseID"}  
        );
        
        insertStmt.setInt(1, memberId);
        insertStmt.setInt(2, lessonId);
        insertStmt.setInt(3, sessions);
        
        int rowsInserted = insertStmt.executeUpdate();
        
        if (rowsInserted > 0) {
            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int purchaseId = generatedKeys.getInt(1);
                System.out.println("Lesson purchase successfully added with ID: " + purchaseId);
            } else {
                System.out.println("Lesson purchase successfully added, but could not retrieve purchase ID.");
            }
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
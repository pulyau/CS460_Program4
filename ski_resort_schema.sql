
-- CS460 Program 4: Ski Resort Database Schema

CREATE TABLE Member (
    MemberID INT PRIMARY KEY,
    MemberName VARCHAR(100),
    PhoneNumber VARCHAR(20),
    EmailAddress VARCHAR(100),
    EmergencyContact VARCHAR(100)
);

CREATE TABLE SkiPass (
    PassID INT PRIMARY KEY,
    SMemberID INT,
    PassType VARCHAR(50),
    ActivationDate DATE,
    ExpirationDate DATE,
    PurchaseDate DATE,
    TotalUses INT,
    RemainingUses INT,
    Price DECIMAL(10, 2),
    FOREIGN KEY (SMemberID) REFERENCES Member(MemberID)
);

CREATE TABLE Equipment (
    EquipmentID INT PRIMARY KEY,
    EquipmentType VARCHAR(50),
    EquipmentSize INT,
    EquipmentStatus VARCHAR(20)
);

CREATE TABLE EquipmentRecord (
    RentalID INT PRIMARY KEY,
    EMemberID INT,
    EPassID INT,
    RentalTime DATE,
    ReturnStatus VARCHAR(20),
    FOREIGN KEY (EMemberID) REFERENCES Member(MemberID),
    FOREIGN KEY (EPassID) REFERENCES SkiPass(PassID)
);

CREATE TABLE Trail (
    TrailID INT PRIMARY KEY,
    StartLocation VARCHAR(100),
    EndLocation VARCHAR(100),
    DifficultyLevel VARCHAR(20),
    TrailStatus VARCHAR(20),
    Category VARCHAR(50),
    TrailName VARCHAR(100)
);

CREATE TABLE Lift (
    LiftID INT PRIMARY KEY,
    TrailTo INT,
    TrailFrom INT,
    Name VARCHAR(100),
    OpenTime TIME,
    ClosingTime TIME,
    AbilityLevel VARCHAR(20),
    LiftStatus VARCHAR(20),
    FOREIGN KEY (TrailTo) REFERENCES Trail(TrailID),
    FOREIGN KEY (TrailFrom) REFERENCES Trail(TrailID),
);

CREATE TABLE Property (
    PropertyID INT PRIMARY KEY,
    PropertyName VARCHAR(100),
    PropertyType VARCHAR(50)
);

CREATE TABLE Income (
    IncomeID INT PRIMARY KEY,
    IPropertyID INT,
    IncomeDate DATE,
    IncomeAmount DECIMAL(10, 2),
    Source VARCHAR(100),
    FOREIGN KEY (IPropertyID) REFERENCES Property(PropertyID)
);

CREATE TABLE Employee (
    EmployeeID INT PRIMARY KEY,
    EPropertyID INT,
    Name VARCHAR(100),
    PhoneNumber VARCHAR(20),
    EmergencyContact VARCHAR(100),
    JobStartDate DATE,
    Position VARCHAR(50),
    Salary DECIMAL(10, 2),
    EmployeeStatus VARCHAR(20),
    FOREIGN KEY (EPropertyID) REFERENCES Property(PropertyID)
);

CREATE TABLE Instructor (
    InstructorID INT PRIMARY KEY,
    IEmployeeID INT,
    CertificationLevel VARCHAR(20),
    FOREIGN KEY (IEmployeeID) REFERENCES Employee(EmployeeID)
);

CREATE TABLE Lesson (
    LessonID INT PRIMARY KEY,
    LInstructorID INT,
    LessonLevel VARCHAR(20),
    LessonType VARCHAR(50),
    LessonPrice DECIMAL(10, 2),
    Duration INT,
    MaxStudents INT,
    StartTime TIME,
    EndTime TIME,
    FOREIGN KEY (LInstructorID) REFERENCES Instructor(InstructorID)
);

CREATE TABLE LessonPurchase (
    PurchaseID INT PRIMARY KEY,
    LPMemberID INT,
    LPLessonID INT,
    RemainingSessions INT,
    FOREIGN KEY (LPMemberID) REFERENCES Member(MemberID),
    FOREIGN KEY (LPLessonID) REFERENCES Lesson(LessonID)
);

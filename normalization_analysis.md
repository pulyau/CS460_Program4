# Pulat Uralov and Abdullokh Ganiev
# CSC 460
# Project 4
# Normalization Analysis

# (1) Member
MemberID -> MemberName, PhoneNumber, EmailAddress, EmergencyContact

MemberID is a superkey and all non-key attributes depend directly on MemberID.

# (2) SkiPass

PassID ->MemberID, PassType, ActivationDate, ExpirationDate, PurchaseDate, TotalUses, RemainingUses, Price

PassID is a superkey and all non-key attributes depend directly on PassID

# (3) Equipment

EquipmentID -> EquipmentType, EquipmentSize, EquipmentStatus

EquipmentID is a superkey and all non-key attributes depend directly on EquipmentID

# (4) EquipmentRecord

RentalID -> MemberID, PassID, RentalTime, ReturnStatus

RentalID is a superkey

---

## Trail

**FDs:**
- TrailID → StartLocation, EndLocation, DifficultyLevel, Status, Category, Name

**Justification:**
- TrailID is the superkey and determines all other attributes.
- Satisfies 3NF and BCNF.

---

## Lift

**FDs:**
- LiftID → Name, OpenTime, ClosingTime, AbilityLevel, Status

**Justification:**
- LiftID is a superkey.
- All attributes depend on it.
- In 3NF and BCNF.

---

## LiftTrail

**FDs:**
- {LiftID, TrailID} → none (composite primary key only)

**Justification:**
- No non-key attributes.
- Trivially satisfies 3NF and BCNF.

---

## Property

**FDs:**
- PropertyID → PropertyName, PropertyType

**Justification:**
- PropertyID is the superkey.
- 3NF and BCNF are satisfied.

---

## Income

**FDs:**
- IncomeID → PropertyID, Date, Amount, Source

**Justification:**
- IncomeID is a superkey.
- No transitive dependencies.
- Satisfies 3NF and BCNF.

---

## Employee

**FDs:**
- EmployeeID → PropertyID, Name, PhoneNumber, EmergencyContact, JobStartDate, Position, Salary, Status

**Justification:**
- EmployeeID is a superkey.
- 3NF and BCNF are both satisfied.

---

## Instructor

**FDs:**
- InstructorID → EmployeeID, CertificationLevel

**Justification:**
- InstructorID is a superkey.
- 3NF and BCNF satisfied.

---

## Lesson

**FDs:**
- LessonID → InstructorID, LessonLevel, LessonType, Price, Duration, MaxStudents, StartTime, EndTime  
- Optional: LessonType → Price, Duration, MaxStudents (if standard lesson types exist)

**Justification:**
- If the optional FD holds, then the table is only in 3NF, not BCNF.
- Otherwise, LessonID is a superkey, and all attributes are dependent on it → 3NF and BCNF.

---

## LessonPurchase

**FDs:**
- PurchaseID → MemberID, LessonID, RemainingSessions

**Justification:**
- PurchaseID is a superkey.
- No transitive dependencies.
- In 3NF and BCNF.

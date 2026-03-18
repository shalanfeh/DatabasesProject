package API.ServerAPI;

/*
Created by: Taylor Kang

Assigns a device to exactly one employee, group, or location.
Uses a single transaction and locks the device row before updating.
*/

import Drivers.ServerDriver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ServerAssignDevice extends ServerAbstract {
    private PreparedStatement CheckActiveAssignmentStatement;
    private PreparedStatement ResolveEmployeeStatement;
    private PreparedStatement ResolveGroupStatement;
    private PreparedStatement ResolveLocationStatement;
    private PreparedStatement InsertEmployeeAssignmentStatement;
    private PreparedStatement InsertGroupAssignmentStatement;
    private PreparedStatement InsertLocationAssignmentStatement;
    private PreparedStatement AssignedStatusStatement;
    private PreparedStatement UpdateDeviceStatusStatement;

    // Executes the assign-device transaction using validated input.
    public String Execute(Map<String, Object> Parameters) {
        if (Parameters == null) {
            return "Failure: Missing parameters. Please provide SerialNumber and exactly one target.";
        }

        String SerialNumber = SafeTrim(Parameters.get("SerialNumber"));
        String EmployeeEmail = SafeTrim(Parameters.get("EmployeeEmail"));
        String GroupName = SafeTrim(Parameters.get("GroupName"));
        String LocationName = SafeTrim(Parameters.get("LocationName"));

        if (SerialNumber.isEmpty()) {
            return "Failure: SerialNumber cannot be empty.";
        }

        int TargetCount = 0;
        if (!EmployeeEmail.isEmpty()) {
            TargetCount += 1;
        }
        if (!GroupName.isEmpty()) {
            TargetCount += 1;
        }
        if (!LocationName.isEmpty()) {
            TargetCount += 1;
        }
        if (TargetCount != 1) {
            return "Failure: Provide exactly one target: EmployeeEmail, GroupName, or LocationName.";
        }

        try {
            SQLStatement.setString(1, SerialNumber);
            try (ResultSet DeviceResult = SQLStatement.executeQuery()) {
                if (!DeviceResult.next()) {
                    return "Failure: Device with serial number " + SerialNumber + " does not exist.";
                }

                int DeviceId = DeviceResult.getInt("deviceid");
                String CurrentStatus = DeviceResult.getString("currentstatus");
                if (!"Available".equalsIgnoreCase(CurrentStatus)) {
                    return "Failure: Device " + SerialNumber + " is currently marked as " + CurrentStatus + ".";
                }

                CheckActiveAssignmentStatement.setInt(1, DeviceId);
                try (ResultSet ActiveAssignmentResult = CheckActiveAssignmentStatement.executeQuery()) {
                    if (ActiveAssignmentResult.next()) {
                        return "Failure: Device " + SerialNumber + " already has an active assignment.";
                    }
                }

                int AssignedStatusId = ResolveAssignedStatusId();
                if (AssignedStatusId == -1) {
                    return "Failure: Could not resolve the Assigned status id.";
                }

                String SuccessTargetLabel;
                if (!EmployeeEmail.isEmpty()) {
                    Integer EmployeeId = ResolveEmployeeId(EmployeeEmail);
                    if (EmployeeId == null) {
                        return "Failure: Employee email not found: " + EmployeeEmail;
                    }

                    InsertEmployeeAssignmentStatement.setInt(1, DeviceId);
                    InsertEmployeeAssignmentStatement.setInt(2, EmployeeId);
                    InsertEmployeeAssignmentStatement.executeUpdate();
                    SuccessTargetLabel = "employee " + EmployeeEmail;
                } else if (!GroupName.isEmpty()) {
                    Integer GroupId = ResolveGroupId(GroupName);
                    if (GroupId == null) {
                        return "Failure: Group name not found: " + GroupName;
                    }

                    InsertGroupAssignmentStatement.setInt(1, DeviceId);
                    InsertGroupAssignmentStatement.setInt(2, GroupId);
                    InsertGroupAssignmentStatement.executeUpdate();
                    SuccessTargetLabel = "group " + GroupName;
                } else {
                    Integer LocationId = ResolveLocationId(LocationName);
                    if (LocationId == null) {
                        return "Failure: Location name not found: " + LocationName;
                    }

                    InsertLocationAssignmentStatement.setInt(1, DeviceId);
                    InsertLocationAssignmentStatement.setInt(2, LocationId);
                    InsertLocationAssignmentStatement.executeUpdate();
                    SuccessTargetLabel = "location " + LocationName;
                }

                UpdateDeviceStatusStatement.setInt(1, AssignedStatusId);
                UpdateDeviceStatusStatement.setInt(2, DeviceId);
                UpdateDeviceStatusStatement.executeUpdate();

                ServerDriver.GetConnection().commit();
                return "Success: Assigned device " + SerialNumber + " to " + SuccessTargetLabel + ".";
            }
        } catch (SQLException e) {
            IO.println("Couldn't execute assign-device transaction.");
            e.printStackTrace();
            ServerDriver.RollbackTransaction();
            return "Failure: " + e.getMessage();
        }
    }

    // Prepares all statements once for reuse.
    protected void Prepare() {
        String ResolveDeviceSql = "SELECT d.DeviceID, s.CurrentStatus " +
                "FROM Device d " +
                "JOIN Status s ON d.StatusID = s.StatusID " +
                "WHERE d.SerialNumber = ? " +
                "FOR UPDATE";
        String CheckActiveAssignmentSql = "SELECT AssignmentID FROM DeviceAssignment WHERE DeviceID = ? AND ReturnDate IS NULL";
        String ResolveEmployeeSql = "SELECT EmployeeID FROM Employee WHERE Email = ?";
        String ResolveGroupSql = "SELECT GroupID FROM EmployeeGroup WHERE GroupName = ?";
        String ResolveLocationSql = "SELECT LocationID FROM SharedLocation WHERE LocationName = ?";
        String InsertEmployeeAssignmentSql = "INSERT INTO DeviceAssignment (DeviceID, EmployeeID, AssignDate) VALUES (?, ?, CURRENT_DATE)";
        String InsertGroupAssignmentSql = "INSERT INTO DeviceAssignment (DeviceID, GroupID, AssignDate) VALUES (?, ?, CURRENT_DATE)";
        String InsertLocationAssignmentSql = "INSERT INTO DeviceAssignment (DeviceID, LocationID, AssignDate) VALUES (?, ?, CURRENT_DATE)";
        String AssignedStatusSql = "SELECT StatusID FROM Status WHERE CurrentStatus = 'Assigned'";
        String UpdateDeviceStatusSql = "UPDATE Device SET StatusID = ?, LastUpdate = CURRENT_DATE WHERE DeviceID = ?";

        try {
            SQLStatement = ServerDriver.GetConnection().prepareStatement(ResolveDeviceSql);
            CheckActiveAssignmentStatement = ServerDriver.GetConnection().prepareStatement(CheckActiveAssignmentSql);
            ResolveEmployeeStatement = ServerDriver.GetConnection().prepareStatement(ResolveEmployeeSql);
            ResolveGroupStatement = ServerDriver.GetConnection().prepareStatement(ResolveGroupSql);
            ResolveLocationStatement = ServerDriver.GetConnection().prepareStatement(ResolveLocationSql);
            InsertEmployeeAssignmentStatement = ServerDriver.GetConnection().prepareStatement(InsertEmployeeAssignmentSql);
            InsertGroupAssignmentStatement = ServerDriver.GetConnection().prepareStatement(InsertGroupAssignmentSql);
            InsertLocationAssignmentStatement = ServerDriver.GetConnection().prepareStatement(InsertLocationAssignmentSql);
            AssignedStatusStatement = ServerDriver.GetConnection().prepareStatement(AssignedStatusSql);
            UpdateDeviceStatusStatement = ServerDriver.GetConnection().prepareStatement(UpdateDeviceStatusSql);
        } catch (SQLException e) {
            IO.println("Couldn't prepare assign-device statements.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Resolves an employee natural key to its surrogate key.
    private Integer ResolveEmployeeId(String EmployeeEmail) throws SQLException {
        ResolveEmployeeStatement.setString(1, EmployeeEmail);
        try (ResultSet RS = ResolveEmployeeStatement.executeQuery()) {
            if (RS.next()) {
                return RS.getInt("employeeid");
            }
        }
        return null;
    }

    // Resolves a group natural key to its surrogate key.
    private Integer ResolveGroupId(String GroupName) throws SQLException {
        ResolveGroupStatement.setString(1, GroupName);
        try (ResultSet RS = ResolveGroupStatement.executeQuery()) {
            if (RS.next()) {
                return RS.getInt("groupid");
            }
        }
        return null;
    }

    // Resolves a location natural key to its surrogate key.
    private Integer ResolveLocationId(String LocationName) throws SQLException {
        ResolveLocationStatement.setString(1, LocationName);
        try (ResultSet RS = ResolveLocationStatement.executeQuery()) {
            if (RS.next()) {
                return RS.getInt("locationid");
            }
        }
        return null;
    }

    // Resolves the Assigned status row needed for the update.
    private int ResolveAssignedStatusId() throws SQLException {
        try (ResultSet RS = AssignedStatusStatement.executeQuery()) {
            if (RS.next()) {
                return RS.getInt("statusid");
            }
        }
        return -1;
    }

    // Converts a parameter to a trimmed string safely.
    private String SafeTrim(Object Value) {
        if (Value == null) {
            return "";
        }
        return Value.toString().trim();
    }
}

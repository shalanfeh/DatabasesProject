package API.ServerAPI;

import Drivers.ServerDriver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ServerAssignDevice extends ServerAbstract {
    private PreparedStatement checkActiveAssignmentStatement;
    private PreparedStatement resolveEmployeeStatement;
    private PreparedStatement resolveGroupStatement;
    private PreparedStatement resolveLocationStatement;
    private PreparedStatement insertEmployeeAssignmentStatement;
    private PreparedStatement insertGroupAssignmentStatement;
    private PreparedStatement insertLocationAssignmentStatement;
    private PreparedStatement assignedStatusStatement;
    private PreparedStatement updateDeviceStatusStatement;

    public String Execute(Map<String, Object> Parameters) {
        if (Parameters == null) {
            return "Failure: Missing parameters. Please provide SerialNumber and exactly one target.";
        }

        String serialNumber = ((String) Parameters.get("SerialNumber")).trim();
        String employeeEmail = ((String) Parameters.get("EmployeeEmail")).trim();
        String groupName = ((String) Parameters.get("GroupName")).trim();
        String locationName = ((String) Parameters.get("LocationName")).trim();

        if (serialNumber.isEmpty()) {
            return "Failure: SerialNumber cannot be empty.";
        }

        int targetCount = 0;
        if (!employeeEmail.isEmpty()) {
            targetCount += 1;
        }
        if (!groupName.isEmpty()) {
            targetCount += 1;
        }
        if (!locationName.isEmpty()) {
            targetCount += 1;
        }
        if (targetCount != 1) {
            return "Failure: Provide exactly one assignment target: EmployeeEmail, GroupName, or LocationName.";
        }

        try {
            SQLStatement.setString(1, serialNumber);
            ResultSet deviceResult = SQLStatement.executeQuery();
            if (!deviceResult.next()) {
                return "Failure: Device with serial number " + serialNumber + " does not exist.";
            }

            int deviceId = deviceResult.getInt("deviceid");
            String currentStatus = deviceResult.getString("currentstatus");
            if (!"Available".equalsIgnoreCase(currentStatus)) {
                return "Failure: Device " + serialNumber + " is currently marked as " + currentStatus + ".";
            }

            checkActiveAssignmentStatement.setInt(1, deviceId);
            ResultSet activeAssignmentResult = checkActiveAssignmentStatement.executeQuery();
            if (activeAssignmentResult.next()) {
                return "Failure: Device " + serialNumber + " already has an active assignment.";
            }

            int assignedStatusId = resolveAssignedStatusId();
            if (assignedStatusId == -1) {
                return "Failure: Could not resolve the Assigned status id.";
            }

            String successTargetLabel;
            if (!employeeEmail.isEmpty()) {
                Integer employeeId = resolveEmployeeId(employeeEmail);
                if (employeeId == null) {
                    return "Failure: Employee email not found: " + employeeEmail;
                }

                insertEmployeeAssignmentStatement.setInt(1, deviceId);
                insertEmployeeAssignmentStatement.setInt(2, employeeId);
                insertEmployeeAssignmentStatement.executeUpdate();
                successTargetLabel = "employee " + employeeEmail;
            } else if (!groupName.isEmpty()) {
                Integer groupId = resolveGroupId(groupName);
                if (groupId == null) {
                    return "Failure: Group name not found: " + groupName;
                }

                insertGroupAssignmentStatement.setInt(1, deviceId);
                insertGroupAssignmentStatement.setInt(2, groupId);
                insertGroupAssignmentStatement.executeUpdate();
                successTargetLabel = "group " + groupName;
            } else {
                Integer locationId = resolveLocationId(locationName);
                if (locationId == null) {
                    return "Failure: Location name not found: " + locationName;
                }

                insertLocationAssignmentStatement.setInt(1, deviceId);
                insertLocationAssignmentStatement.setInt(2, locationId);
                insertLocationAssignmentStatement.executeUpdate();
                successTargetLabel = "location " + locationName;
            }

            updateDeviceStatusStatement.setInt(1, assignedStatusId);
            updateDeviceStatusStatement.setInt(2, deviceId);
            updateDeviceStatusStatement.executeUpdate();

            ServerDriver.GetConnection().commit();
            return "Success: Assigned device " + serialNumber + " to " + successTargetLabel + ".";
        } catch (SQLException e) {
            IO.println("Couldn't execute assign-device transaction.");
            e.printStackTrace();
            ServerDriver.RollbackTransaction();
            return "Failure: " + e.getMessage();
        }
    }

    protected void Prepare() {
        String resolveDeviceSql = "SELECT d.DeviceID, s.CurrentStatus " +
                "FROM Device d " +
                "JOIN Status s ON d.StatusID = s.StatusID " +
                "WHERE d.SerialNumber = ? " +
                "FOR UPDATE";
        String checkActiveAssignmentSql = "SELECT AssignmentID FROM DeviceAssignment WHERE DeviceID = ? AND ReturnDate IS NULL";
        String resolveEmployeeSql = "SELECT EmployeeID FROM Employee WHERE Email = ?";
        String resolveGroupSql = "SELECT GroupID FROM EmployeeGroup WHERE GroupName = ?";
        String resolveLocationSql = "SELECT LocationID FROM SharedLocation WHERE LocationName = ?";
        String insertEmployeeAssignmentSql = "INSERT INTO DeviceAssignment (DeviceID, EmployeeID, AssignDate) VALUES (?, ?, CURRENT_DATE)";
        String insertGroupAssignmentSql = "INSERT INTO DeviceAssignment (DeviceID, GroupID, AssignDate) VALUES (?, ?, CURRENT_DATE)";
        String insertLocationAssignmentSql = "INSERT INTO DeviceAssignment (DeviceID, LocationID, AssignDate) VALUES (?, ?, CURRENT_DATE)";
        String assignedStatusSql = "SELECT StatusID FROM Status WHERE CurrentStatus = 'Assigned'";
        String updateDeviceStatusSql = "UPDATE Device SET StatusID = ?, LastUpdate = CURRENT_DATE WHERE DeviceID = ?";

        try {
            SQLStatement = ServerDriver.GetConnection().prepareStatement(resolveDeviceSql);
            checkActiveAssignmentStatement = ServerDriver.GetConnection().prepareStatement(checkActiveAssignmentSql);
            resolveEmployeeStatement = ServerDriver.GetConnection().prepareStatement(resolveEmployeeSql);
            resolveGroupStatement = ServerDriver.GetConnection().prepareStatement(resolveGroupSql);
            resolveLocationStatement = ServerDriver.GetConnection().prepareStatement(resolveLocationSql);
            insertEmployeeAssignmentStatement = ServerDriver.GetConnection().prepareStatement(insertEmployeeAssignmentSql);
            insertGroupAssignmentStatement = ServerDriver.GetConnection().prepareStatement(insertGroupAssignmentSql);
            insertLocationAssignmentStatement = ServerDriver.GetConnection().prepareStatement(insertLocationAssignmentSql);
            assignedStatusStatement = ServerDriver.GetConnection().prepareStatement(assignedStatusSql);
            updateDeviceStatusStatement = ServerDriver.GetConnection().prepareStatement(updateDeviceStatusSql);
        } catch (SQLException e) {
            IO.println("Couldn't prepare assign-device statements.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private Integer resolveEmployeeId(String employeeEmail) throws SQLException {
        resolveEmployeeStatement.setString(1, employeeEmail);
        ResultSet rs = resolveEmployeeStatement.executeQuery();
        if (rs.next()) {
            return rs.getInt("employeeid");
        }
        return null;
    }

    private Integer resolveGroupId(String groupName) throws SQLException {
        resolveGroupStatement.setString(1, groupName);
        ResultSet rs = resolveGroupStatement.executeQuery();
        if (rs.next()) {
            return rs.getInt("groupid");
        }
        return null;
    }

    private Integer resolveLocationId(String locationName) throws SQLException {
        resolveLocationStatement.setString(1, locationName);
        ResultSet rs = resolveLocationStatement.executeQuery();
        if (rs.next()) {
            return rs.getInt("locationid");
        }
        return null;
    }

    private int resolveAssignedStatusId() throws SQLException {
        ResultSet rs = assignedStatusStatement.executeQuery();
        if (rs.next()) {
            return rs.getInt("statusid");
        }
        return -1;
    }
}

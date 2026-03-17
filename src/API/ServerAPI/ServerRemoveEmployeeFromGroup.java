package API.ServerAPI;

import Drivers.ServerDriver;

import java.sql.SQLException;
import java.util.Map;

public class ServerRemoveEmployeeFromGroup extends ServerAbstract {
    //called by client API
    public String Execute(Map<String, Object> Parameters) {
        StringBuilder Result = new StringBuilder();

        try {
            // Get the parameters from the client
            String EmployeeEmail = (String) Parameters.get("EmployeeEmail");
            String GroupName = (String) Parameters.get("GroupName");

            if (EmployeeEmail == null || GroupName == null) {
                return "Failure: Missing parameters. Please provide EmployeeEmail and GroupName.";
            }

            SQLStatement.setString(1, EmployeeEmail);
            SQLStatement.setString(2, GroupName);

            int rows = SQLStatement.executeUpdate();
            ServerDriver.GetConnection().commit();
            if (rows > 0) {
                Result.append("Success: Removed ").append(EmployeeEmail).append(" from group ").append(GroupName);
            } else {
                Result.append("Failure: No rows were deleted. Employee may not belong to group.");
            }
        } catch (SQLException e) {
            IO.println("Couldn't execute statement: \n" + SQLStatement.toString());
            e.printStackTrace();
            Result.append("Failure: ").append(e.getMessage());
        }

        return Result.toString();
    }

    //The prepare statement -- Bobby tables
    protected void Prepare() {
        // EmployeeGroupMember uses numeric keys (EmployeeId, GroupId).
        // We use USING to delete based on Employee.Email and EmployeeGroup.GroupName.
        String Sql = "DELETE FROM EmployeeGroupMember" +
                "USING Employee, EmployeeGroup " +
                "WHERE EmployeeGroupMembers.EmployeeId = Employee.EmployeeId AND EmployeeGroupMembers.GroupId = EmployeeGroup.GroupId " +
                "AND Employee.Email = ? AND EmployeeGroup.GroupName = ?";
        try {
            SQLStatement = ServerDriver.GetConnection().prepareStatement(Sql);
        } catch (SQLException e) {
            IO.println("Couldn't prepare statement: \n" + Sql);
            e.printStackTrace();
            System.exit(1);
        }
    }
}

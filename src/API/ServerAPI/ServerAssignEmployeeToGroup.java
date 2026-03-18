package API.ServerAPI;

import Drivers.ServerDriver;

import java.sql.SQLException;
import java.util.Map;

public class ServerAssignEmployeeToGroup extends ServerAbstract {
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
                Result.append("Success: Assigned ").append(EmployeeEmail).append(" to group ").append(GroupName);
            } else {
                Result.append("Failure: No rows were updated. Assignment may not have occurred.");
            }
        } catch (SQLException e) {
            IO.println("Couldn't execute statement: \n" + SQLStatement.toString());
            //e.printStackTrace();
            ServerDriver.RollbackTransaction();
            Result.append("Failure: ").append(e.getMessage());

        }

        return Result.toString();
    }

    //The prepare statement -- Bobby tables
    protected void Prepare() {
        // EmployeeGroupMember uses the numeric keys (EmployeeId, GroupId).
        // We resolve those IDs by joining against Employee (Email) and EmployeeGroup (GroupName).
        String Sql = "INSERT INTO EmployeeGroupMembers (EmployeeId, GroupId) " +
                "SELECT Employee.EmployeeId, EmployeeGroup.GroupId " +
                "FROM Employee, EmployeeGroup " +
                "WHERE Employee.Email = ? AND EmployeeGroup.GroupName = ?";
        try {
            SQLStatement = ServerDriver.GetConnection().prepareStatement(Sql);
        } catch (SQLException e) {
            IO.println("Couldn't prepare statement: \n" + Sql);
            e.printStackTrace();
            System.exit(1);
        }
    }
}

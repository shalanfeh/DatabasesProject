package API.ServerAPI;

import Drivers.ServerDriver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ServerViewGroupFromEmployee extends ServerAbstract {
    public String Execute(Map<String, Object> Parameters) {
        StringBuilder result = new StringBuilder();

        if (Parameters == null) {
            return "Failure: Missing parameters. Please provide EmployeeEmail.";
        }

        try {
            String employeeEmail = (String) Parameters.get("EmployeeEmail");
            if (employeeEmail == null || employeeEmail.isBlank()) {
                return "Failure: Missing parameters. Please provide EmployeeEmail.";
            }

            SQLStatement.setString(1, employeeEmail);
            ResultSet rs = SQLStatement.executeQuery();

            result.append("\n=== Groups for ").append(employeeEmail).append(" ===\n");
            int count = 0;
            while (rs.next()) {
                count += 1;
                result.append(count)
                      .append(") ")
                      .append(rs.getString("groupname"))
                      .append("\n");
            }

            if (count == 0) {
                return "No groups found for employee: " + employeeEmail;
            }
        } catch (SQLException e) {
            IO.println("Couldn't execute statement: \n" + SQLStatement.toString());
            e.printStackTrace();
            return "Failure: " + e.getMessage();
        }

        return result.toString();
    }

    protected void Prepare() {
        String sql = "SELECT g.GroupName " +
                "FROM Employee e " +
                "JOIN EmployeeGroupMembers gm ON e.EmployeeID = gm.EmployeeID " +
                "JOIN EmployeeGroup g ON gm.GroupID = g.GroupID " +
                "WHERE e.Email = ? " +
                "ORDER BY g.GroupName ASC";
        try {
            SQLStatement = ServerDriver.GetConnection().prepareStatement(sql);
        } catch (SQLException e) {
            IO.println("Couldn't prepare statement: \n" + sql);
            e.printStackTrace();
            System.exit(1);
        }
    }
}

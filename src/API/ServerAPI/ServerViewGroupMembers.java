package API.ServerAPI;

/*
Created by: Taylor Kang

Returns the employees who belong to a specific employee group.
Accepts GroupName as input and returns a formatted string, not a ResultSet.
*/

import Drivers.ServerDriver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ServerViewGroupMembers extends ServerAbstract {

    // Executes the prepared query using the provided GroupName and formats results.
    public String Execute(Map<String, Object> Parameters) {
        StringBuilder result = new StringBuilder();

        if (Parameters == null) {
            return "Failure: Missing parameters. Please provide GroupName.";
        }

        try {
            String groupName = (String) Parameters.get("GroupName");
            if (groupName == null || groupName.isBlank()) {
                return "Failure: Missing parameters. Please provide GroupName.";
            }

            SQLStatement.setString(1, groupName.trim());
            ResultSet rs = SQLStatement.executeQuery();

            result.append("\n=== Members of ").append(groupName.trim()).append(" ===\n");

            int count = 0;
            while (rs.next()) {
                count += 1;
                result.append(count)
                        .append(") ")
                        .append(rs.getString("firstname"))
                        .append(" ")
                        .append(rs.getString("lastname"))
                        .append(" | ")
                        .append(rs.getString("email"))
                        .append("\n");
            }

            if (count == 0) {
                return "No members found for group: " + groupName.trim();
            }
        } catch (SQLException e) {
            IO.println("Couldn't execute statement: \n" + SQLStatement.toString());
            e.printStackTrace();
            return "Failure: " + e.getMessage();
        }

        return result.toString();
    }

    // Prepares the SQL statement once and reuses it for each execution.
    protected void Prepare() {
        String sql = "SELECT e.FirstName, e.LastName, e.Email " +
                "FROM EmployeeGroup g " +
                "JOIN EmployeeGroupMembers gm ON g.GroupID = gm.GroupID " +
                "JOIN Employee e ON gm.EmployeeID = e.EmployeeID " +
                "WHERE g.GroupName = ? " +
                "ORDER BY e.LastName ASC, e.FirstName ASC";

        try {
            SQLStatement = ServerDriver.GetConnection().prepareStatement(sql);
        } catch (SQLException e) {
            IO.println("Couldn't prepare statement: \n" + sql);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
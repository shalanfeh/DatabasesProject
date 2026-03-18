package API.ServerAPI;

/*
Created by: Taylor Kang

Returns the employees who belong to a specific employee group.
Accepts GroupName as input and returns a formatted string.
*/

import Drivers.ServerDriver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ServerViewGroupMembers extends ServerAbstract {
    // Executes the prepared query using GroupName and formats the results.
    public String Execute(Map<String, Object> Parameters) {
        StringBuilder Result = new StringBuilder();

        if (Parameters == null) {
            return "Failure: Missing parameters. Please provide GroupName.";
        }

        String GroupName = SafeTrim(Parameters.get("GroupName"));
        if (GroupName.isEmpty()) {
            return "Failure: Missing parameters. Please provide GroupName.";
        }

        try {
            SQLStatement.setString(1, GroupName);
            try (ResultSet RS = SQLStatement.executeQuery()) {
                Result.append("\n=== Members of ").append(GroupName).append(" ===\n");

                int Count = 0;
                while (RS.next()) {
                    Count += 1;
                    Result.append(Count)
                            .append(") ")
                            .append(RS.getString("firstname"))
                            .append(" ")
                            .append(RS.getString("lastname"))
                            .append(" | ")
                            .append(RS.getString("email"))
                            .append("\n");
                }

                if (Count == 0) {
                    return "No members found for group: " + GroupName;
                }
            }
        } catch (SQLException e) {
            IO.println("Couldn't execute statement: \n" + SQLStatement.toString());
            e.printStackTrace();
            return "Failure: " + e.getMessage();
        }

        return Result.toString();
    }

    // Prepares the SQL statement once for reuse.
    protected void Prepare() {
        String Sql = "SELECT e.FirstName, e.LastName, e.Email " +
                "FROM EmployeeGroup g " +
                "JOIN EmployeeGroupMembers gm ON g.GroupID = gm.GroupID " +
                "JOIN Employee e ON gm.EmployeeID = e.EmployeeID " +
                "WHERE g.GroupName = ? " +
                "ORDER BY e.LastName ASC, e.FirstName ASC";

        try {
            SQLStatement = ServerDriver.GetConnection().prepareStatement(Sql);
        } catch (SQLException e) {
            IO.println("Couldn't prepare statement: \n" + Sql);
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Converts a parameter to a trimmed string safely.
    private String SafeTrim(Object Value) {
        if (Value == null) {
            return "";
        }
        return Value.toString().trim();
    }
}

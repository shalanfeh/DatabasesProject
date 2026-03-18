package API.ServerAPI;

/*
Created by: Taylor Kang

Returns the groups for a specific employee email.
*/

import Drivers.ServerDriver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ServerViewGroupFromEmployee extends ServerAbstract {
    // Executes the prepared query using EmployeeEmail and formats the results.
    public String Execute(Map<String, Object> Parameters) {
        StringBuilder Result = new StringBuilder();

        if (Parameters == null) {
            return "Failure: Missing parameters. Please provide EmployeeEmail.";
        }

        String EmployeeEmail = SafeTrim(Parameters.get("EmployeeEmail"));
        if (EmployeeEmail.isEmpty()) {
            return "Failure: Missing parameters. Please provide EmployeeEmail.";
        }

        try {
            SQLStatement.setString(1, EmployeeEmail);
            try (ResultSet RS = SQLStatement.executeQuery()) {
                Result.append("\n=== Groups for ").append(EmployeeEmail).append(" ===\n");
                int Count = 0;
                while (RS.next()) {
                    Count += 1;
                    Result.append(Count)
                            .append(") ")
                            .append(RS.getString("groupname"))
                            .append("\n");
                }

                if (Count == 0) {
                    return "No groups found for employee: " + EmployeeEmail;
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
        String Sql = "SELECT g.GroupName " +
                "FROM Employee e " +
                "JOIN EmployeeGroupMembers gm ON e.EmployeeID = gm.EmployeeID " +
                "JOIN EmployeeGroup g ON gm.GroupID = g.GroupID " +
                "WHERE e.Email = ? " +
                "ORDER BY g.GroupName ASC";
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

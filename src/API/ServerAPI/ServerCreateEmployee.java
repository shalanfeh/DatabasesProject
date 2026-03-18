/*
Created by Louis Nguyen
*/
package API.ServerAPI;

import Drivers.ServerDriver;

import java.sql.SQLException;
import java.util.Map;

public class ServerCreateEmployee extends ServerAbstract {
    //called by client API
    public String Execute(Map<String, Object> Parameters) {
        StringBuilder Result = new StringBuilder();

        try {
            // Get the parameters from the client
            String FirstName = (String) Parameters.get("FirstName");
            String LastName = (String) Parameters.get("LastName");
            String EmployeeEmail = (String) Parameters.get("EmployeeEmail");

            if (FirstName == null || LastName == null || EmployeeEmail == null) {
                return "Failure: Missing parameters. Please provide FirstName, LastName, and Email.";
            }

            SQLStatement.setString(1, FirstName);
            SQLStatement.setString(2, LastName);
            SQLStatement.setString(3, EmployeeEmail);


            int rows = SQLStatement.executeUpdate();
            ServerDriver.GetConnection().commit();
            if (rows > 0) {
                Result.append("Success: Created employee ").append(EmployeeEmail);
            } else {
                Result.append("Failure: No rows were inserted. Employee may already exist.");
            }
        } catch (SQLException e) {
            IO.println("Couldn't execute statement: \n" + SQLStatement.toString());
            e.printStackTrace();
            Result.append("Failure: ").append(e.getMessage());
            ServerDriver.RollbackTransaction();
        }

        return Result.toString();
    }

    //The prepare statement -- Bobby tables
    protected void Prepare() {
        String Sql = "INSERT INTO Employee (FirstName, LastName, Email) VALUES (?, ?, ?)";
        try {
            SQLStatement = ServerDriver.GetConnection().prepareStatement(Sql);
        } catch (SQLException e) {
            IO.println("Couldn't prepare statement: \n" + Sql);
            e.printStackTrace();
            System.exit(1);
        }
    }
}

/*
Created by Ismail Shalanfeh
*/
package API.ServerAPI;

import Drivers.ServerDriver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class ServerUpdateEmployee extends ServerAbstract {
    private PreparedStatement StmFUpdate;
    private PreparedStatement StmLUpdate;

    public String Execute(Map<String, Object> Parameters) {
        // Get the parameters from the client
        String FirstName = (String) Parameters.get("FirstName");
        String LastName = (String) Parameters.get("LastName");
        String EmployeeEmail = (String) Parameters.get("email");

        StringBuilder Result = new StringBuilder();

        try {
            //select for update
            SQLStatement.setString(1, EmployeeEmail);
            SQLStatement.executeQuery();

            Result.append(EmployeeEmail);
            Result.append(" Updated to have:");

            //Do the update
            if (!FirstName.isEmpty()) {
                StmFUpdate.setString(1, FirstName);
                StmFUpdate.setString(2, EmployeeEmail);
                StmFUpdate.executeUpdate();
                Result.append(" First Name: ");
                Result.append(FirstName);
                Result.append(" ");
            }
            if (!LastName.isEmpty()) {
                StmLUpdate.setString(1, LastName);
                StmLUpdate.setString(2, EmployeeEmail);
                StmLUpdate.executeUpdate();
                Result.append(" Last Name: ");
                Result.append(LastName);
            }

            //Commit
            ServerDriver.GetConnection().commit();
        } catch (SQLException e) {
            IO.println("Couldn't complete update");
            e.printStackTrace();
            Result.append("Failure: ").append(e.getMessage());
            ServerDriver.RollbackTransaction();
        }
        return Result.toString();
    }

    /*
    1 Mandatory SQL query
    3 possible SQL updates here.

    Mandatory select for update:
    SELECT *
    FROM Employee
    WHERE email = 'Tom@gmail.com'
    FOR UPDATE;

    -if no first name, only update last name:
    UPDATE Employee
    SET LastName = 'Updooted'
    WHERE email = 'Tom@gmail.com';

    -if no last name, only update first name:
    UPDATE Employee
    SET FirstName = 'Updooted'
    WHERE email = 'Tom@gmail.com';
     */
    protected void Prepare() {
        //Select For Update
        String SFUQuery = "SELECT * FROM Employee " +
                "WHERE email = ? FOR UPDATE";

        //First, Last
        String FUpdate = "UPDATE Employee " +
                "SET FirstName = ? WHERE email = ?";

        String LUpdate = "UPDATE Employee " +
                "SET LastName = ? WHERE email = ?";

        try {
            Connection Conn = ServerDriver.GetConnection();
            SQLStatement = Conn.prepareStatement(SFUQuery);
            StmFUpdate = Conn.prepareStatement(FUpdate);
            StmLUpdate = Conn.prepareStatement(LUpdate);

        } catch (SQLException e) {
            IO.println("Couldn't prepare statements for Update Employee");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

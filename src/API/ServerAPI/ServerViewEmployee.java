/*
Created by Ismail Shalanfeh
*/
package API.ServerAPI;

import Drivers.ServerDriver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ServerViewEmployee extends ServerAbstract {

    public String Execute(Map<String, Object> Parameters) {
        StringBuilder Result = new StringBuilder();

        try {
            if (((String) Parameters.get("email")).equals("")) {
                throw new SQLException();
            }

            SQLStatement.setString(1, (String) Parameters.get("email"));
            ResultSet RS = SQLStatement.executeQuery();
            while (RS.next()) {
                Result.append("First Name: ");
                Result.append(RS.getString(1));
                Result.append("\n");

                Result.append("Last Name: ");
                Result.append(RS.getString(2));
                Result.append("\n");

                Result.append("Email: ");
                Result.append(RS.getString(3));
                Result.append("\n");
            }
        } catch (SQLException e) {
            IO.println("Couldn't find employee with email " + (String) Parameters.get("email"));
            //e.printStackTrace();
        }

        return Result.toString();
    }


    /*
    SELECT FirstName as "First Name", LastName as "Last Name", Email
    FROM Employee
    WHERE Email = 'Tom@gmail.com';
     */
    protected void Prepare() {
        Connection Conn = ServerDriver.GetConnection();
        String Sql = "SELECT FirstName as \"First Name\", LastName as \"Last Name\", Email " +
                "FROM Employee " +
                "WHERE Email = ?";
        try {
            SQLStatement = Conn.prepareStatement(Sql);
        } catch (SQLException e) {
            IO.println("Couldn't prepare statement: \n" + Sql);
            e.printStackTrace();
            System.exit(1);
        }
    }

}

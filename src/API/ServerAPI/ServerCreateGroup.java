/*
Created by Ismail Shalanfeh

Server-side class for Create Group API.
 */
package API.ServerAPI;

import Drivers.ServerDriver;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Map;

public class ServerCreateGroup extends ServerAbstract {

    public String Execute(Map<String, Object> Parameters) {
        StringBuilder Result = new StringBuilder();

        try {
            if (((String) Parameters.get("GroupName")).isEmpty()) {
                throw new SQLException();
            }

            SQLStatement.setString(1, (String) Parameters.get("GroupName"));
            SQLStatement.executeUpdate();
            Result.append("Created Group " + (String) Parameters.get("GroupName") + " Successfully");
            ServerDriver.GetConnection().commit();

        } catch (SQLException e) {
            IO.println("Couldn't create group " + (String) Parameters.get("GroupName"));
            ServerDriver.RollbackTransaction();
        }

        return Result.toString();
    }

    protected void Prepare() {
        Connection Conn = ServerDriver.GetConnection();
        String Sql = "INSERT INTO EmployeeGroup (GroupName) " +
                "VALUES (?)";
        try {
            SQLStatement = Conn.prepareStatement(Sql);
            Conn.commit();
        } catch (SQLException e) {
            ServerDriver.RollbackTransaction();
            IO.println("Couldn't prepare statement: \n" + Sql);
            e.printStackTrace();
            System.exit(1);
        }
    }
}

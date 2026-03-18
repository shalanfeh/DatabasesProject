package API.ServerAPI;

import Drivers.ServerDriver;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.util.Map;

public class ServerViewGroups extends ServerAbstract {
    //called by client API
    public String Execute(Map<String, Object> Parameters) {
        StringBuilder Result = new StringBuilder();

        try {
            ResultSet RS = SQLStatement.executeQuery();

            Result.append("\n=== Group names ===\n");

            while (RS.next()) {
                Result.append(RS.getString("name"));
                Result.append("\n");
            }
        } catch (SQLException e) {
            IO.println("Couldn't execute statement: \n" + SQLStatement.toString());
            //e.printStackTrace();
        }

        return Result.toString();
    }

    //The prepare statement -- Bobby tables
    protected void Prepare() {
        String Sql = "SELECT GroupName AS Name " +
                "FROM EmployeeGroup " +
                "ORDER BY GroupName ASC";
        try {
            SQLStatement = ServerDriver.GetConnection().prepareStatement(Sql);
        } catch (SQLException e) {
            IO.println("Couldn't prepare statement: \n" + Sql);
            e.printStackTrace();
            System.exit(1);
        }
    }
}

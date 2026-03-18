package API.ServerAPI;

import Drivers.ServerDriver;

import java.sql.SQLException;
import java.util.Map;

public class ServerCreateDevice extends ServerAbstract{
    //called by client API
    public String Execute(Map<String, Object> Parameters) {
        StringBuilder Result = new StringBuilder();

        try {
            String DeviceType = (String) Parameters.get("DeviceType");
            String SerialNumber = (String) Parameters.get("SerialNumber");

            if (DeviceType == null || SerialNumber == null) {
                return "Failure: Missing parameters. Please provide DeviceType and SerialNumber.";
            }

            SQLStatement.setString(1, DeviceType);
            SQLStatement.setString(2, SerialNumber);

            int rows = SQLStatement.executeUpdate();
            ServerDriver.GetConnection().commit();
            if (rows > 0) {
                Result.append("Success: Created device ").append(SerialNumber);
            } else {
                Result.append("Failure: No rows were inserted. Device may already exist.");
            }
        } catch (SQLException e) {
            IO.println("Couldn't execute statement: \n" + SQLStatement.toString());
            // e.printStackTrace();  // removed to avoid leaking internal details
            Result.append("Failure: ").append(e.getMessage());
            ServerDriver.RollbackTransaction();
        }

        return Result.toString();
    }

    //The prepare statement -- Bobby tables
    protected void Prepare() {
        String Sql = "INSERT INTO device (devicetype, serialnumber, statusid, lastupdate) " +
                "SELECT ?, ?, s.statusid, CURRENT_DATE " +
                "FROM status s " +
                "WHERE LOWER(s.currentstatus) = 'available'";
        try {
            SQLStatement = ServerDriver.GetConnection().prepareStatement(Sql);
        } catch (SQLException e) {
            IO.println("Couldn't prepare statement: \n" + Sql);
            e.printStackTrace();
            System.exit(1);
        }
    }
}

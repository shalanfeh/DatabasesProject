package API.ServerAPI;

import Drivers.ServerDriver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ServerViewAllDevices extends ServerAbstract {
    public String Execute(Map<String, Object> Parameters) {
        StringBuilder result = new StringBuilder();

        try {
            ResultSet rs = SQLStatement.executeQuery();

            result.append("\n=== All Devices ===\n");
            int count = 0;
            while (rs.next()) {
                count += 1;
                result.append(count)
                      .append(") ")
                      .append(rs.getString("devicetype"))
                      .append(" | Serial: ")
                      .append(rs.getString("serialnumber"))
                      .append(" | Status: ")
                      .append(rs.getString("currentstatus"))
                      .append(" | Last Update: ")
                      .append(rs.getDate("lastupdate"))
                      .append("\n");
            }

            if (count == 0) {
                return "No devices found.";
            }
        } catch (SQLException e) {
            IO.println("Couldn't execute statement: \n" + SQLStatement.toString());
            e.printStackTrace();
            return "Failure: " + e.getMessage();
        }

        return result.toString();
    }

    protected void Prepare() {
        String sql = "SELECT d.DeviceType, d.SerialNumber, s.CurrentStatus, d.LastUpdate " +
                "FROM Device d " +
                "JOIN Status s ON d.StatusID = s.StatusID " +
                "ORDER BY d.DeviceType ASC, d.SerialNumber ASC";
        try {
            SQLStatement = ServerDriver.GetConnection().prepareStatement(sql);
        } catch (SQLException e) {
            IO.println("Couldn't prepare statement: \n" + sql);
            e.printStackTrace();
            System.exit(1);
        }
    }
}

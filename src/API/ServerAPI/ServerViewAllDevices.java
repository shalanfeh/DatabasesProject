package API.ServerAPI;

/*
Created by: Taylor Kang

Returns devices and their current status.
Optional filters are DeviceType and CurrentStatus. Blank values return all devices.
*/

import Drivers.ServerDriver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ServerViewAllDevices extends ServerAbstract {
    // Executes the device query with optional filters.
    public String Execute(Map<String, Object> Parameters) {
        StringBuilder Result = new StringBuilder();
        String DeviceType = "";
        String CurrentStatus = "";

        if (Parameters != null) {
            DeviceType = SafeTrim(Parameters.get("DeviceType"));
            CurrentStatus = SafeTrim(Parameters.get("CurrentStatus"));
        }

        try {
            SQLStatement.setString(1, DeviceType);
            SQLStatement.setString(2, DeviceType);
            SQLStatement.setString(3, CurrentStatus);
            SQLStatement.setString(4, CurrentStatus);

            try (ResultSet RS = SQLStatement.executeQuery()) {
                Result.append("\n=== Devices ===\n");
                int Count = 0;
                while (RS.next()) {
                    Count += 1;
                    Result.append(Count)
                            .append(") ")
                            .append(RS.getString("devicetype"))
                            .append(" | Serial: ")
                            .append(RS.getString("serialnumber"))
                            .append(" | Status: ")
                            .append(RS.getString("currentstatus"))
                            .append(" | Last Update: ")
                            .append(RS.getDate("lastupdate"))
                            .append("\n");
                }

                if (Count == 0) {
                    return "No devices found for the selected filter.";
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
        String Sql = "SELECT d.DeviceType, d.SerialNumber, s.CurrentStatus, d.LastUpdate " +
                "FROM Device d " +
                "JOIN Status s ON d.StatusID = s.StatusID " +
                "WHERE (? = '' OR d.DeviceType = ?) " +
                "AND (? = '' OR s.CurrentStatus = ?) " +
                "ORDER BY d.DeviceType ASC, d.SerialNumber ASC";
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

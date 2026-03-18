/*
Created by Lucas
*/
package API.ServerAPI;

import Drivers.ServerDriver;

import java.sql.SQLException;
import java.util.Map;

public class ServerViewDevice extends ServerAbstract{
    //Called by client API
    public String Execute(Map<String, Object> Parameters) {
        StringBuilder Result = new StringBuilder();

        try {
            String SerialNumber = (String) Parameters.get("SerialNumber");
            if (SerialNumber == null) {return "Failure: Missing parameters. Please provide SerialNumber.";}

            SQLStatement.setString(1, SerialNumber);

            var rs = SQLStatement.executeQuery();

            if (!rs.next()) {return "Failure: Device with serial number " + SerialNumber + " does not exist.";}

            String deviceType = rs.getString("devicetype");
            String status = rs.getString("currentstatus");
            var lastUpdate = rs.getDate("lastupdate");

            Result.append("Device Type: ").append(deviceType).append("\n");
            Result.append("Status: ").append(status).append("\n");
            Result.append("Last Update: ").append(lastUpdate).append("\n");

            if (status.equalsIgnoreCase("assigned")) {
                String email = rs.getString("email");
                String group = rs.getString("groupname");
                String location = rs.getString("locationname");

                Result.append("Assigned to: ");

                if (email != null) {
                    Result.append("Employee - ").append(email);
                } else if (group != null) {
                    Result.append("Group - ").append(group);
                } else if (location != null) {
                    Result.append("Location - ").append(location);
                } else {
                    Result.append("Unknown");
                }
                Result.append("\n");
            }
        } catch (SQLException e) {
            IO.println("Couldn't execute statement: \n" + SQLStatement.toString());
            // e.printStackTrace();  // removed to avoid leaking internal detailse.printStackTrace();
            Result.append("Failure: ").append(e.getMessage());
        }
        return Result.toString();
    }

    protected void Prepare() {
        String Sql =
        "SELECT d.devicetype, s.currentstatus, d.lastupdate, " +
        "e.email, g.groupname, l.locationname " +
        "FROM device d " +
        "   JOIN status s ON d.statusid = s.statusid " +
        "   LEFT JOIN deviceassignment da ON da.assignmentid = ( " +
        "       SELECT da2.assignmentid " +
        "       FROM deviceassignment da2 " +
        "       WHERE da2.deviceid = d.deviceid " +
        "       ORDER BY da2.assigndate DESC " +
        "       LIMIT 1 " +
        ") " +
        "   LEFT JOIN employee e ON da.employeeid = e.employeeid " +
        "   LEFT JOIN employeegroup g ON da.groupid = g.groupid " +
        "   LEFT JOIN sharedlocation l ON da.locationid = l.locationid " +
        "WHERE d.serialnumber = ?";
        try {
            SQLStatement = ServerDriver.GetConnection().prepareStatement(Sql);
        } catch (SQLException e) {
            IO.println("Couldn't prepare statement: \n" + Sql);
            // e.printStackTrace();  // removed to avoid leaking internal details
            System.exit(1);
        }
    }
}

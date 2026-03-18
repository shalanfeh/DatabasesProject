package API.ServerAPI;

import Drivers.ServerDriver;

import java.sql.SQLException;
import java.util.Map;

public class ServerViewAssignedDevices extends ServerAbstract {
    // called by client API
    public String Execute(Map<String, Object> Parameters) {
        StringBuilder Result = new StringBuilder();

        try {
            //  Get the parameters from the client
            String email = (String) Parameters.get("EmployeeEmail");
            String group = (String) Parameters.get("GroupName");
            String location = (String) Parameters.get("LocationName");

            // Normalize inputs (empty → null)
            if (email != null && email.isEmpty()) email = null;
            if (group != null && group.isEmpty()) group = null;
            if (location != null && location.isEmpty()) location = null;

            //  Validate exactly ONE input
            int countInputs =
                    (email != null ? 1 : 0) +
                            (group != null ? 1 : 0) +
                            (location != null ? 1 : 0);

            if (countInputs == 0) {
                return "Failure: Provide one input (employee, group, or location).";
            }

            if (countInputs > 1) {
                return "Failure: Provide ONLY ONE input (employee, group, or location).";
            }

            // Choose query based on input
            if (email != null) {
                SQLStatement = ServerDriver.GetConnection().prepareStatement(
                        "SELECT d.serialnumber " +
                                "FROM device d " +
                                "JOIN deviceassignment da ON d.deviceid = da.deviceid " +
                                "JOIN employee e ON da.employeeid = e.employeeid " +
                                "WHERE e.email = ?"
                );
                SQLStatement.setString(1, email);

            } else if (group != null) {
                SQLStatement = ServerDriver.GetConnection().prepareStatement(
                        "SELECT d.serialnumber " +
                                "FROM device d " +
                                "JOIN deviceassignment da ON d.deviceid = da.deviceid " +
                                "JOIN employeegroup g ON da.groupid = g.groupid " +
                                "WHERE g.groupname = ?"
                );
                SQLStatement.setString(1, group);

            } else { // location
                SQLStatement = ServerDriver.GetConnection().prepareStatement(
                        "SELECT d.serialnumber " +
                                "FROM device d " +
                                "JOIN deviceassignment da ON d.deviceid = da.deviceid " +
                                "JOIN sharedlocation l ON da.locationid = l.locationid " +
                                "WHERE l.locationname = ?"
                );
                SQLStatement.setString(1, location);
            }

            // Execute query
            var rs = SQLStatement.executeQuery();

            int count = 0;

            while (rs.next()) {
                Result.append(rs.getString("serialnumber")).append("\n");
                count++;
            }

            if (count == 0) {
                return "No devices found.";
            }

        } catch (SQLException e) {
            IO.println("Couldn't execute statement:\n" + SQLStatement.toString());
            e.printStackTrace();
            Result.append("Failure: ").append(e.getMessage());
        }

        return Result.toString();
    }

    // Prepare statement - bobbytables
    // Not used since query is dynamic here
    protected void Prepare() {}
}
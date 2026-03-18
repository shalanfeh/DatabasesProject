package API.ServerAPI;

import Drivers.ServerDriver;

import java.sql.SQLException;
import java.util.Map;

public class ServerUpdateDevice extends ServerAbstract{
    //called by client API
    public String Execute(Map<String, Object> Parameters) {
        StringBuilder Result = new StringBuilder();

        try {
            // Get the parameters from the client
            String SerialNumber = (String) Parameters.get("SerialNumber");
            String DeviceType = (String) Parameters.get("DeviceType");
            String Status = (String) Parameters.get("Status");

            if (SerialNumber == null) { // SerialNumber is required, allow nulls for optionals (DeviceType and Status)
                return "Failure: SerialNumber is required.";
            }

            // Normalize optional inputs
            // Convert empty strings into null for COALESCE to work
            if (DeviceType != null && DeviceType.isEmpty()) DeviceType = null;
            if (Status != null && Status.isEmpty()) Status = null;
            boolean updatedType = (DeviceType != null);
            boolean updatedStatus = (Status != null);

            if (!updatedType && !updatedStatus) {return "No changes provided.";}

            SQLStatement.setString(1, DeviceType);
            SQLStatement.setString(2, Status);
            SQLStatement.setString(3, SerialNumber);

            int rows = SQLStatement.executeUpdate();

            if (rows == 0) {return "Failure: No updates were made. Device may not exist or is currently assigned.";}

            // Fetch updated row
            String fetchSql =
                    "SELECT d.devicetype, s.currentstatus, d.lastupdate " +
                    "FROM device d " +
                    "   JOIN status s ON (d.statusid = s.statusid) " +
                    "WHERE d.serialnumber = ? ";
            var fetchStatement = ServerDriver.GetConnection().prepareStatement(fetchSql);
            fetchStatement.setString(1, SerialNumber);

            var rs = fetchStatement.executeQuery();
            if (!rs.next()) {
                ServerDriver.RollbackTransaction();
                return "Failure: Could not fetch updated device.";
            }
            String actualType = rs.getString("devicetype");
            String actualStats = rs.getString("currentstatus");

            boolean typechanged = updatedType && DeviceType.equalsIgnoreCase(actualType);
            boolean statuschanged = updatedStatus && Status.equalsIgnoreCase(actualStats);
            // If nothing actually changed (e.g., invalid status or blocked update)
            if (!typechanged && !statuschanged) {
                ServerDriver.RollbackTransaction();
                return "No changes were made.";
            }
            Result.append("Success: Updated device.\n");
            if (typechanged) {
                Result.append("Device Type: ").append(actualType).append("\n");
            } else if (updatedType) {
                Result.append("Device Type was not changed.\n");
            }
            if (statuschanged) {
                Result.append("Status: ").append(actualStats).append("\n");
            } else if (updatedStatus) {
                Result.append("Status was not changed (invalid value or device is assigned).\n");
            }
            Result.append("Last Update: ").append(rs.getDate("lastupdate")).append("\n");

            ServerDriver.GetConnection().commit();

        } catch (SQLException e) {
            // Handle SQL errors and rollback
            IO.println("Couldn't execute statement: \n" + SQLStatement.toString());
            // e.printStackTrace();  // removed to avoid leaking internal details
            Result.append("Failure: ").append(e.getMessage());
            ServerDriver.RollbackTransaction();
        }
        return Result.toString();
    }

    protected void Prepare() {
        // Keep old value if input is null
        // CASE:
        // * If device is currently assigned, black status changes
        // * Else, allow valid status updates (except 'assigned')
        String Sql =
                "UPDATE device SET " +
                "devicetype = COALESCE(?, devicetype), " +
                "statusid = CASE " +
                "   WHEN statusid = ( " +
                "       SELECT statusid FROM status WHERE LOWER(currentstatus) = 'assigned' " +
                "   ) THEN statusid " +
                "   ELSE COALESCE(( " +
                "       SELECT statusid FROM status " +
                "       WHERE LOWER(currentstatus) = LOWER(?) " +
                "           AND LOWER(currentstatus) != 'assigned' " +
                "   ), statusid) " +
                "END, " +
                "lastupdate = CURRENT_DATE " +
                "WHERE serialnumber = ?";
        try {
            SQLStatement = ServerDriver.GetConnection().prepareStatement(Sql);
        } catch (SQLException e) {
            IO.println("Couldn't prepare statement: \n" + Sql);
            // e.printStackTrace();  // removed to avoid leaking internal details
            System.exit(1);
        }
    }
}

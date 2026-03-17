package Drivers;/*
Created by: Ismail Shalanfeh

In-charge of connecting and disconnecting from the database.
we use 1 connection and 1 connection only!
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ServerDriver {
    static private String Url = "jdbc:postgresql://localhost:5432/itdevicemanagement";
    static private String User = "postgres";
    static private String Password = "password";

    static private Connection DatabaseConnection;

    //connects to the database
    static public void SetUp() {
        //Getting user input for the database connection
        IO.println("== Database Connecting ==");
        IO.println("Leave empty for default");

        IO.println("Insert URl -- Default: jdbc:postgresql://localhost:5432/itdevicemanagement");
        String InputUrl = IO.readln("Input: ");

        IO.println("Insert User -- Default: postgres");
        String InputUser = IO.readln("Input: ");

        //DEFINITELY not a good idea, but here for convenience
        IO.println("Insert Password -- Default: password");
        String InputPassword = IO.readln("Input: ");

        if (!InputUrl.isEmpty()) {
            Url = InputUrl;
        }
        if (!InputUser.isEmpty()) {
            User = InputUser;
        }
        if (!InputPassword.isEmpty()) {
            Password = InputPassword;
        }

        //Connecting to the database
        try {
            DatabaseConnection = DriverManager.getConnection(Url, User, Password);
        } catch (SQLException e) {
            IO.println("Couldn't connect to the database :(");
            e.printStackTrace();

            System.exit(1);
        }
    }

    //disconnects from the database
    static public void TearDown() {
        try {
            DatabaseConnection.close();
        } catch (SQLException e) {
            IO.println("Couldn't disconnect from the database D:");
            e.printStackTrace();

            System.exit(1);
        }
    }

    static public Connection GetConnection() {
        return DatabaseConnection;
    }
}

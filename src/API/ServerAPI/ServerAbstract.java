/*
Created by: Ismail Shalanfeh

Prepare statement called by API.ServerDriver.setUp() which is called by main.
Takes in query parameters from API.ClientAPI.
Gets connection from serverDriver.
Returns a string for printing; Can't return ReturnSet due to having a db connection!
handles everything related to the ReturnSet.

Must Start and End a transaction!
 */
package API.ServerAPI;

import java.util.Map;
import java.sql.PreparedStatement;

public abstract class ServerAbstract {
    protected PreparedStatement SQLStatement;

    //called by client API
    abstract public String Execute(Map<String, Object> Parameters);

    //The prepare statement -- Bobby tables
    abstract protected void Prepare();

    ServerAbstract() {
        Prepare();
    }
}
/*
Created by: Ismail Shalanfeh

Prepare statement called by ServerDriver.setUp() which is called by main.
Takes in query parameters from ClientAPI.
Gets connection from serverDriver.
Returns a string for printing; Can't return ReturnSet due to having a db connection!
handles everything related to the ReturnSet.

Must Start and End a transaction!
 */
package ServerAPI;

import java.util.Map;

public abstract class ServerAbstract {
    //called by client API
    abstract public String Execute(Map<String, Object> Parameters);

    //The prepare statement -- Bobby tables
    abstract protected void Prepare();

    ServerAbstract() {
        Prepare();
    }
}
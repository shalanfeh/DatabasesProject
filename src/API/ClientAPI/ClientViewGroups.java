/*
Created by Ismail Shalanfeh
*/
package API.ClientAPI;

import API.APIRegistry;

public class ClientViewGroups implements ClientInterface {
    //Returns the name for the API registry
    public String GetName() {
        return "View Groups";
    }

    //Returns display text for the client driver.
    //The name is already displayed, think of this like a description
    public String GetDisplayText() {
        return "Returns all current employee groups that exist.";
    }

    //Asks the user for input before calling API.ServerAPI.Exectute().
    //SQL is printed.
    public void GetAndProcessUserInput() {
        //No input required for this one.
        IO.println(APIRegistry.GetAPI(GetName()).Server.Execute(null));
    }
}

/*
Created by Louis Nguyen
*/
package API.ClientAPI;

import API.APIRegistry;
import Utilities.ParameterGrabber;

public class ClientCreateEmployee implements ClientInterface {
    //Returns the name for the API registry
    public String GetName() {
        return "Create Employee";
    }

    //Returns display text for the client driver.
    //The name is already displayed, think of this like a description
    public String GetDisplayText() {
        return "Creates a new employee by email.";
    }

    //Asks the user for input before calling API.ServerAPI.Execute().
    //SQL is printed.
    public void GetAndProcessUserInput() {
        Utilities.ParameterGrabber grabber = new Utilities.ParameterGrabber();
        grabber.AddParameter("FirstName", Utilities.Type.STRING);
        grabber.AddParameter("LastName", Utilities.Type.STRING);
        grabber.AddParameter("EmployeeEmail", Utilities.Type.STRING);
        grabber.GrabParameters();

        IO.println(APIRegistry.GetAPI(GetName()).Server.Execute(grabber.GrabbedParameters));
    }
}

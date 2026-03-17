package API.ClientAPI;

import API.APIRegistry;
import Utilities.ParameterGrabber;

public class ClientRemoveEmployeeFromGroup implements ClientInterface {
    //Returns the name for the API registry
    public String GetName() {
        return "Remove Employee From Group";
    }

    //Returns display text for the client driver.
    //The name is already displayed, think of this like a description
    public String GetDisplayText() {
        return "Removes an employee from a specific group.";
    }

    //Asks the user for input before calling API.ServerAPI.Execute().
    //SQL is printed.
    public void GetAndProcessUserInput() {
        Utilities.ParameterGrabber grabber = new Utilities.ParameterGrabber();
        grabber.AddParameter("EmployeeEmail", Utilities.Type.STRING);
        grabber.AddParameter("GroupName", Utilities.Type.STRING);
        grabber.GrabParameters();

        IO.println(APIRegistry.GetAPI(GetName()).Server.Execute(grabber.GrabbedParameters));
    }
}

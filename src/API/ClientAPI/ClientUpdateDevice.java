package API.ClientAPI;

import API.APIRegistry;
import Utilities.ParameterGrabber;

public class ClientUpdateDevice implements ClientInterface{
    //Returns the name for the API registry
    public String GetName() { return "Update Device";}

    //Returns display text for the client driver.
    //The name is already displayed, think of this like a description
    public String GetDisplayText() { return "Updates a device's information.";}

    //Asks the user for input before calling API.ServerAPI.Exectute().
    //SQL is printed.
    public void GetAndProcessUserInput() {
        Utilities.ParameterGrabber grabber = new Utilities.ParameterGrabber();
        grabber.AddParameter("SerialNumber", Utilities.Type.STRING);
        grabber.AddParameter("DeviceType", Utilities.Type.STRING);
        grabber.AddParameter("Status", Utilities.Type.STRING);
        grabber.GrabParameters();

        IO.println(APIRegistry.GetAPI(GetName()).Server.Execute(grabber.GrabbedParameters));
    }
}

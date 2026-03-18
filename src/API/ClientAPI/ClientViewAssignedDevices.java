/*
Created by Lucas
*/
package API.ClientAPI;

import API.APIRegistry;
import Utilities.ParameterGrabber;

public class ClientViewAssignedDevices implements ClientInterface {

    public String GetName() { return "View Assigned Devices"; }

    public String GetDisplayText() {
        return "Lists devices assigned to an employee, group, or location.";
    }

    public void GetAndProcessUserInput() {
        ParameterGrabber grabber = new ParameterGrabber();

        grabber.AddParameter("EmployeeEmail", Utilities.Type.STRING);
        grabber.AddParameter("GroupName", Utilities.Type.STRING);
        grabber.AddParameter("LocationName", Utilities.Type.STRING);

        grabber.GrabParameters();

        IO.println(APIRegistry.GetAPI(GetName()).Server.Execute(grabber.GrabbedParameters));
    }
}
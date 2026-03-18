package API.ClientAPI;

import API.APIRegistry;
import Utilities.ParameterGrabber;

public class ClientViewGroupMembers implements ClientInterface {
    public String GetName() {
        return "View Group Members";
    }

    public String GetDisplayText() {
        return "Returns the employees who belong to a specific group.";
    }

    public void GetAndProcessUserInput() {
        ParameterGrabber grabber = new ParameterGrabber();
        grabber.AddParameter("GroupName", Utilities.Type.STRING);
        grabber.GrabParameters();

        String groupName = ((String) grabber.GrabbedParameters.get("GroupName")).trim();
        if (groupName.isEmpty()) {
            IO.println("Failure: GroupName cannot be empty.");
            return;
        }

        grabber.GrabbedParameters.put("GroupName", groupName);
        IO.println(APIRegistry.GetAPI(GetName()).Server.Execute(grabber.GrabbedParameters));
    }
}

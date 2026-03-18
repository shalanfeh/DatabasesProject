package API.ClientAPI;

/*
Created by: Taylor Kang

Collects input for the View Group Members API.
Requires a group name.
*/

import API.APIRegistry;
import Utilities.ParameterGrabber;

public class ClientViewGroupMembers implements ClientInterface {
    // Returns the registry name for this API.
    public String GetName() {
        return "View Group Members";
    }

    // Returns a short description for the menu.
    public String GetDisplayText() {
        return "Returns the employees who belong to a specific group.";
    }

    // Collects and validates user input before calling the server API.
    public void GetAndProcessUserInput() {
        ParameterGrabber Grabber = new ParameterGrabber();
        Grabber.AddParameter("GroupName", Utilities.Type.STRING);
        Grabber.GrabParameters();

        String GroupName = SafeTrim(Grabber.GrabbedParameters.get("GroupName"));
        if (GroupName.isEmpty()) {
            IO.println("Failure: GroupName cannot be empty.");
            return;
        }

        Grabber.GrabbedParameters.put("GroupName", GroupName);
        IO.println(APIRegistry.GetAPI(GetName()).Server.Execute(Grabber.GrabbedParameters));
    }

    // Converts a parameter to a trimmed string safely.
    private String SafeTrim(Object Value) {
        if (Value == null) {
            return "";
        }
        return Value.toString().trim();
    }
}

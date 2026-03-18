package API.ClientAPI;

/*
Created by: Taylor Kang

Collects input for the View Group From Employee API.
Requires a valid employee email.
*/

import API.APIRegistry;
import Utilities.ParameterGrabber;

public class ClientViewGroupFromEmployee implements ClientInterface {
    // Returns the registry name for this API.
    public String GetName() {
        return "View Group From Employee";
    }

    // Returns a short description for the menu.
    public String GetDisplayText() {
        return "Returns the groups assigned to a specific employee email.";
    }

    // Collects and validates user input before calling the server API.
    public void GetAndProcessUserInput() {
        ParameterGrabber Grabber = new ParameterGrabber();
        Grabber.AddParameter("EmployeeEmail", Utilities.Type.STRING);
        Grabber.GrabParameters();

        String EmployeeEmail = SafeTrim(Grabber.GrabbedParameters.get("EmployeeEmail"));
        if (EmployeeEmail.isEmpty()) {
            IO.println("Failure: EmployeeEmail cannot be empty.");
            return;
        }
        if (!EmployeeEmail.contains("@")) {
            IO.println("Failure: EmployeeEmail must contain '@'.");
            return;
        }

        Grabber.GrabbedParameters.put("EmployeeEmail", EmployeeEmail);
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

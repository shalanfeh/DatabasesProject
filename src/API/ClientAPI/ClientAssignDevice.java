package API.ClientAPI;

/*
Created by: Taylor Kang

Collects input for the Assign Device API.
The user must provide a SerialNumber and exactly one target:
EmployeeEmail, GroupName, or LocationName.
*/

import API.APIRegistry;
import Utilities.ParameterGrabber;

public class ClientAssignDevice implements ClientInterface {
    // Returns the registry name for this API.
    public String GetName() {
        return "Assign Device";
    }

    // Returns a short description for the menu.
    public String GetDisplayText() {
        return "Assigns a device to exactly one employee, group, or location.";
    }

    // Collects and validates user input before calling the server API.
    public void GetAndProcessUserInput() {
        ParameterGrabber Grabber = new ParameterGrabber();
        Grabber.AddParameter("SerialNumber", Utilities.Type.STRING);
        Grabber.AddParameter("EmployeeEmail", Utilities.Type.STRING);
        Grabber.AddParameter("GroupName", Utilities.Type.STRING);
        Grabber.AddParameter("LocationName", Utilities.Type.STRING);
        Grabber.GrabParameters();

        String SerialNumber = SafeTrim(Grabber.GrabbedParameters.get("SerialNumber"));
        String EmployeeEmail = SafeTrim(Grabber.GrabbedParameters.get("EmployeeEmail"));
        String GroupName = SafeTrim(Grabber.GrabbedParameters.get("GroupName"));
        String LocationName = SafeTrim(Grabber.GrabbedParameters.get("LocationName"));

        if (SerialNumber.isEmpty()) {
            IO.println("Failure: SerialNumber cannot be empty.");
            return;
        }

        int FilledTargets = 0;
        if (!EmployeeEmail.isEmpty()) {
            FilledTargets += 1;
        }
        if (!GroupName.isEmpty()) {
            FilledTargets += 1;
        }
        if (!LocationName.isEmpty()) {
            FilledTargets += 1;
        }

        if (FilledTargets != 1) {
            IO.println("Failure: Provide exactly one target: EmployeeEmail, GroupName, or LocationName.");
            return;
        }

        if (!EmployeeEmail.isEmpty() && !EmployeeEmail.contains("@")) {
            IO.println("Failure: EmployeeEmail must contain '@'.");
            return;
        }

        // Send normalized values to the server API.
        Grabber.GrabbedParameters.put("SerialNumber", SerialNumber);
        Grabber.GrabbedParameters.put("EmployeeEmail", EmployeeEmail);
        Grabber.GrabbedParameters.put("GroupName", GroupName);
        Grabber.GrabbedParameters.put("LocationName", LocationName);

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

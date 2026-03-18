package API.ClientAPI;

import API.APIRegistry;
import Utilities.ParameterGrabber;

public class ClientAssignDevice implements ClientInterface {
    public String GetName() {
        return "Assign Device";
    }

    public String GetDisplayText() {
        return "Assigns an available device to exactly one employee, group, or location.";
    }

    public void GetAndProcessUserInput() {
        ParameterGrabber grabber = new ParameterGrabber();
        grabber.AddParameter("SerialNumber", Utilities.Type.STRING);
        grabber.AddParameter("EmployeeEmail", Utilities.Type.STRING);
        grabber.AddParameter("GroupName", Utilities.Type.STRING);
        grabber.AddParameter("LocationName", Utilities.Type.STRING);
        grabber.GrabParameters();

        String serialNumber = ((String) grabber.GrabbedParameters.get("SerialNumber")).trim();
        String employeeEmail = ((String) grabber.GrabbedParameters.get("EmployeeEmail")).trim();
        String groupName = ((String) grabber.GrabbedParameters.get("GroupName")).trim();
        String locationName = ((String) grabber.GrabbedParameters.get("LocationName")).trim();

        if (serialNumber.isEmpty()) {
            IO.println("Failure: SerialNumber cannot be empty.");
            return;
        }

        int filledTargets = 0;
        if (!employeeEmail.isEmpty()) {
            filledTargets += 1;
        }
        if (!groupName.isEmpty()) {
            filledTargets += 1;
        }
        if (!locationName.isEmpty()) {
            filledTargets += 1;
        }

        if (filledTargets != 1) {
            IO.println("Failure: Provide exactly one assignment target: EmployeeEmail, GroupName, or LocationName.");
            return;
        }
        if (!employeeEmail.isEmpty() && !employeeEmail.contains("@")) {
            IO.println("Failure: EmployeeEmail must contain '@'.");
            return;
        }

        grabber.GrabbedParameters.put("SerialNumber", serialNumber);
        grabber.GrabbedParameters.put("EmployeeEmail", employeeEmail);
        grabber.GrabbedParameters.put("GroupName", groupName);
        grabber.GrabbedParameters.put("LocationName", locationName);

        IO.println(APIRegistry.GetAPI(GetName()).Server.Execute(grabber.GrabbedParameters));
    }
}

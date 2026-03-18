package API.ClientAPI;

import API.APIRegistry;
import Utilities.ParameterGrabber;

public class ClientViewGroupFromEmployee implements ClientInterface {
    public String GetName() {
        return "View Group From Employee";
    }

    public String GetDisplayText() {
        return "Returns the groups assigned to a specific employee email.";
    }

    public void GetAndProcessUserInput() {
        ParameterGrabber grabber = new ParameterGrabber();
        grabber.AddParameter("EmployeeEmail", Utilities.Type.STRING);
        grabber.GrabParameters();

        String employeeEmail = ((String) grabber.GrabbedParameters.get("EmployeeEmail")).trim();
        if (employeeEmail.isEmpty()) {
            IO.println("Failure: EmployeeEmail cannot be empty.");
            return;
        }
        if (!employeeEmail.contains("@")) {
            IO.println("Failure: EmployeeEmail must contain '@'.");
            return;
        }

        grabber.GrabbedParameters.put("EmployeeEmail", employeeEmail);
        IO.println(APIRegistry.GetAPI(GetName()).Server.Execute(grabber.GrabbedParameters));
    }
}

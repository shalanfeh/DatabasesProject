package API.ClientAPI;

/*
Created by: Taylor Kang

Collects optional filters for the View All Devices API.
Blank values return all devices.
*/

import API.APIRegistry;
import Utilities.ParameterGrabber;

public class ClientViewAllDevices implements ClientInterface {
    // Returns the registry name for this API.
    public String GetName() {
        return "View All Devices";
    }

    // Returns a short description for the menu.
    public String GetDisplayText() {
        return "Returns devices, optionally filtered by DeviceType and/or CurrentStatus.";
    }

    // Collects optional filters before calling the server API.
    public void GetAndProcessUserInput() {
        ParameterGrabber Grabber = new ParameterGrabber();
        Grabber.AddParameter("DeviceType", Utilities.Type.STRING);
        Grabber.AddParameter("CurrentStatus", Utilities.Type.STRING);
        Grabber.GrabParameters();

        String DeviceType = SafeTrim(Grabber.GrabbedParameters.get("DeviceType"));
        String CurrentStatus = SafeTrim(Grabber.GrabbedParameters.get("CurrentStatus"));

        Grabber.GrabbedParameters.put("DeviceType", DeviceType);
        Grabber.GrabbedParameters.put("CurrentStatus", CurrentStatus);

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

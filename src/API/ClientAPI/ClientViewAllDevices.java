package API.ClientAPI;

import API.APIRegistry;

public class ClientViewAllDevices implements ClientInterface {
    public String GetName() {
        return "View All Devices";
    }

    public String GetDisplayText() {
        return "Returns all devices and their current status.";
    }

    public void GetAndProcessUserInput() {
        IO.println(APIRegistry.GetAPI(GetName()).Server.Execute(null));
    }
}

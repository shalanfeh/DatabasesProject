/*
Created by Ismail Shalanfeh

Client-side class for Create Group API.
 */
package API.ClientAPI;

import Utilities.ParameterGrabber;

import API.APIRegistry;
import Utilities.Type;

public class ClientCreateGroup implements ClientInterface {

    public String GetName() {
        return "Create Group";
    }

    public String GetDisplayText() {
        return "Creates a logical grouping of employees for update deployment and management";
    }

    public void GetAndProcessUserInput() {
        ParameterGrabber Grabber = new ParameterGrabber();

        Grabber.AddParameter("GroupName", Type.STRING);

        Grabber.GrabParameters();

        IO.println(APIRegistry.GetAPI(GetName()).Server.Execute(Grabber.GrabbedParameters));
    }
}

/*
Created by Ismail Shalanfeh
*/
package API.ClientAPI;

import API.APIRegistry;
import Utilities.ParameterGrabber;
import Utilities.Type;

public class ClientViewEmployee implements ClientInterface {

    public String GetName() {
        return "View Employee";
    }


    public String GetDisplayText() {
        return "Given an email, provides listed information regarding an employee";
    }


    public void GetAndProcessUserInput() {
        ParameterGrabber Grabber = new ParameterGrabber();

        Grabber.AddParameter("email", Type.STRING);

        Grabber.GrabParameters();

        IO.println(APIRegistry.GetAPI(GetName()).Server.Execute(Grabber.GrabbedParameters));
    }
}

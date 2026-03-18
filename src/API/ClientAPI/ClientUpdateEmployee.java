/*
Created by Ismail Shalanfeh
*/
package API.ClientAPI;

import API.APIRegistry;
import Utilities.ParameterGrabber;
import Utilities.Type;

public class ClientUpdateEmployee implements ClientInterface {
    @Override
    public String GetName() {
        return "Update Employee";
    }

    @Override
    public String GetDisplayText() {
        return "Given an email, optionally update an employee's first or last name";
    }

    @Override
    public void GetAndProcessUserInput() {
        ParameterGrabber Grabber = new ParameterGrabber();

        Grabber.AddParameter("email", Type.STRING);
        Grabber.AddParameter("FirstName", Type.STRING);
        Grabber.AddParameter("LastName", Type.STRING);

        Grabber.GrabParameters();

        //Both parameters are nullable, situation where nothing changes
        if ( (((String) Grabber.GrabbedParameters.get("FirstName")).isEmpty()) &&
                (((String) Grabber.GrabbedParameters.get("LastName")).isEmpty()) ) {
            IO.println("No input received, No changes made");
            return;
        }

        IO.println(APIRegistry.GetAPI(GetName()).Server.Execute(Grabber.GrabbedParameters));
    }
}

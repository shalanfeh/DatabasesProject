package API;
/*
Created by: Ismail Shalanfeh

Contains a list of all the API's.

Why do we need this?
Because Drivers.ClientDriver needs access to client APIs, and instead of
importing the entire package and putting a line for each and every single
API in the client driver to get its information, we can instead move that to
here and have the clientDriver loop over that list.

APIs must be instantiated. I really tried to make this work without instantiating,
but I couldn't get it to work due to how the static keyword works :(

API.ClientAPI stores the name!
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;

import API.ClientAPI.*;
import API.ServerAPI.*;

public class APIRegistry {
    //Place all AddAPI calls here.
    public static void SetUp() {
        //AddAPI(ClientClass.new(), ServerClass.new())
        AddAPI(new ClientViewGroups(), new ServerViewGroups());
        AddAPI(new ClientAssignEmployeeToGroup(), new ServerAssignEmployeeToGroup());
        AddAPI(new ClientCreateEmployee(), new ServerCreateEmployee());
        AddAPI(new ClientRemoveEmployeeFromGroup(), new ServerRemoveEmployeeFromGroup());
        AddAPI(new ClientCreateDevice(), new ServerCreateDevice());
        AddAPI(new ClientCreateGroup(), new ServerCreateGroup());
        AddAPI(new ClientViewEmployee(), new ServerViewEmployee());
        AddAPI(new ClientUpdateDevice(), new ServerUpdateDevice());
        //Create KeyNames once so things don't change randomly due to sets being unordered
        AddAPI(new ClientUpdateEmployee(), new ServerUpdateEmployee());
        AddAPI(new ClientViewGroupMembers(), new ServerViewGroupMembers());
        AddAPI(new ClientViewGroupFromEmployee(), new ServerViewGroupFromEmployee());
        AddAPI(new ClientViewAllDevices(), new ServerViewAllDevices());
        AddAPI(new ClientAssignDevice(), new ServerAssignDevice());
        AddAPI(new ClientViewDevice(), new ServerViewDevice());
        AddAPI(new ClientViewAssignedDevices(), new ServerViewAssignedDevices());
        // Create KeyNames once so things don't change randomly due to sets being
        // unordered
        KeyNames = Register.keySet().toArray(new String[0]);
        Arrays.sort(KeyNames);
    }

    //Returns the API.PairAPI or null... it's a hashmap.get() call
    public static PairAPI GetAPI(String APIName) {
        return Register.get(APIName);
    }

    public static String[] GetAPINames() {
        return KeyNames;
    }
    //==== end of public methods ====

    //Stores the API correlations by name
    private static HashMap<String, PairAPI> Register = new HashMap<String, PairAPI>();
    private static String[] KeyNames;

    //Adds an API to the registry.
    //Throws an exception when API already exists!
    private static void AddAPI(ClientInterface ClientAPI, ServerAbstract ServerAPI) {
        //Check if API already exists
        if (Register.containsKey(ClientAPI.GetName())) {
            IO.println("Could not create API for name: " + ClientAPI.GetName());
            throw new InputMismatchException("API already exists under the same name.");
        }

        //Create the API.PairAPI and add it to the hashmap
        Register.put(ClientAPI.GetName(), new PairAPI(ClientAPI, ServerAPI));
    }


}

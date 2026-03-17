/*
Created by Ismail Shalanfeh

Why is this?
This is a class for grabbing parameters from the user within a clientAPI.

Why use this?
To standardize the prompts within clientAPIs.
 */
package Utilities;

import java.util.*;

public class ParameterGrabber {
    //Variables
    private List<String> GrabOrder;
    private Map<String, Type> ParametersToGrab;
    public Map<String, Object> GrabbedParameters; //send this to serverAPI

    private String DefaultText = "";
    private int DefaultNumber = 0;

    //Logic
    public void AddParameter(String ParamName, Type ParamType) {
        GrabOrder.add(ParamName);
        ParametersToGrab.put(ParamName, ParamType);
    }

    //Call this to prompt the user
    public void GrabParameters() {
        String TypeToPrint = "";
        IO.println("\n==== Parameter Grabber ====");
        IO.println("Defaults: string = " + DefaultText + " | integer = " + DefaultNumber);
        IO.println("Enter nothing to use default");

        //for each parameter to grab
        for (String Param : GrabOrder) {
            Type ParamType = ParametersToGrab.get(Param);

            IO.println("\n");

            //Fill out default
            if (ParamType == Type.STRING) {
                GrabbedParameters.put(Param, DefaultText);
                TypeToPrint = "string";
            } else if (ParamType == Type.NUMBER) {
                GrabbedParameters.put(Param, DefaultNumber);
                TypeToPrint = "integer";
            }

            //Prompt for parameter
            boolean Accepted = false;
            while (!Accepted) {
                IO.println("Input for Parameter: " + Param + " | Type = " + TypeToPrint);
                String Input = IO.readln("Input: ");
                Object Result = null;
                //Try to convert it to a number
                if (ParamType == Type.NUMBER) {
                    try {
                        Result = Integer.parseInt(Input);
                    } catch (NumberFormatException e) {
                        IO.println("Invalid input. Try Again");
                        continue;
                    }
                } else if (ParamType == Type.STRING) {
                    Result = Input;
                }
                //If execution gets here, the result is valid
                GrabbedParameters.put(Param, Result);
                Accepted = true;
            }
        }
    }


    //=== Constructor logic ===

    private void Init() {
        GrabOrder = new ArrayList<String>();
        ParametersToGrab = new HashMap<String, Type>();
        GrabbedParameters = new HashMap<String, Object>();
    }

    public ParameterGrabber() {
        Init();
    }

    public ParameterGrabber(int DefaultInteger) {
        Init();
        DefaultNumber = DefaultInteger;
    }

    public ParameterGrabber(String DefaultString) {
        Init();
        DefaultText = DefaultString;
    }

    public ParameterGrabber(int DefaultInteger, String DefaultString) {
        Init();
        DefaultNumber = DefaultInteger;
        DefaultText = DefaultString;
    }
}

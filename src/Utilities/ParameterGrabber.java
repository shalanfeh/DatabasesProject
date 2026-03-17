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
    private Map<String, Type> ParametersToGrab;
    public Map<String, Object> GrabbedParameters; //send this to serverAPI

    private String DefaultText = "";
    private int DefaultNumber = 0;

    //Logic
    public void AddParameter(String ParamName, Type ParamType) {
        ParametersToGrab.put(ParamName, ParamType);
    }

    //Call this to prompt the user
    public void GrabParameters() {
        String TypeToPrint = "";
        IO.println("\n==== Parameter Grabber ====");
        IO.println("Defaults: string = " + DefaultText + " | integer = " + DefaultNumber);
        IO.println("Enter nothing to use default");

        //for each parameter to grab
        for (Map.Entry<String, Type> entry : ParametersToGrab.entrySet()) {
            IO.println("\n");

            //Fill out default
            if (entry.getValue() == Type.STRING) {
                GrabbedParameters.put(entry.getKey(), DefaultText);
                TypeToPrint = "string";
            } else if (entry.getValue() == Type.NUMBER) {
                GrabbedParameters.put(entry.getKey(), DefaultNumber);
                TypeToPrint = "integer";
            }

            //Prompt for parameter
            boolean Accepted = false;
            while (!Accepted) {
                IO.println("Input for Parameter: " + entry.getKey() + " | Type = " + TypeToPrint);
                String Input = IO.readln("Input: ");
                Object Result = null;
                //Try to convert it to a number
                if (entry.getValue() == Type.NUMBER) {
                    try {
                        Result = Integer.parseInt(Input);
                    } catch (NumberFormatException e) {
                        IO.println("Invalid input. Try Again");
                        continue;
                    }
                } else if (entry.getValue() == Type.STRING) {
                    Result = Input;
                }
                //If execution gets here, the result is valid
                GrabbedParameters.put(entry.getKey(), Result);
                Accepted = true;
            }
        }
    }


    //=== Constructor logic ===

    private void Init() {
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

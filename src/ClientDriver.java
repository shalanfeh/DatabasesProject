/*
Created by: Ismail Shalanfeh

Displays all client API's, assigns a number to each API for user selection.
Calls ClientAPI.GetAndProcessUserInput() when its correlating number is given.
 */

public class ClientDriver {
    //Prints out all of the available API's and the related command
    static private void GetDisplay() {
        String[] Keys = APIRegistry.GetAPINames();

        if (Keys.length == 0) {
            IO.println("No API's to display");
            return;
        }

        int Index = 0;
        for (String Key : Keys) {
            Index += 1;
            PairAPI API = APIRegistry.GetAPI(Key);

            IO.println("\n" + Index + ") " + Key + " -- " + API.Client.GetDisplayText());
        }
    }

    static private boolean SelectAPI(String Selection) {
        String[] Keys = APIRegistry.GetAPINames();
        int Choice;

        //Invalid input
        try {
            Choice = Integer.parseInt(Selection);
        } catch(NumberFormatException e) {
            IO.println("Couldn't execute command, expected a number");
            return false;
        }

        //Out-of-bounds error
        if ((Choice > Keys.length) || (Choice <= 0)) {
            IO.println("Couldn't execute command, Out-of-Bounds error");
            return false;
        }

        //Execute the API
        APIRegistry.GetAPI(Keys[Choice]).Client.GetAndProcessUserInput();

        return true;
    }

    static public void Process() {
        Boolean Running = true;
        while (Running) {
            IO.println("\n============");
            GetDisplay();
            IO.println("============");
            IO.println("\n Commands: Number = API, Nothing = exit");

            String Input = IO.readln("Input: ");
            if (Input.length() == 0) {
                return;
            }

            SelectAPI(Input);
            IO.readln("Continue");
        }
    }
}

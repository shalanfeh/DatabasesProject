/*
Created by: Ismail Shalanfeh

In-charge of telling the server driver to set up and tear down, registering the API's,
and giving control over to the client driver outside of exiting the program.
 */

import API.APIRegistry;
import Drivers.ClientDriver;
import Drivers.ServerDriver;

void main() {
    //start the server
    ServerDriver.SetUp();

    //Set up the API's
    APIRegistry.SetUp();

    //Start the client
    ClientDriver.Process();

    //close the server
    ServerDriver.TearDown();
}

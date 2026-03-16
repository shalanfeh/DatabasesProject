package ClientAPI;

public interface ClientInterface {
    //Returns the name for the API registry
    public String GetName();

    //Returns display text for the client driver.
    //The name is already displayed, think of this like a description
    public String GetDisplayText();

    //Asks the user for input before calling ServerAPI.Exectute().
    //SQL is printed.
    public void GetAndProcessUserInput();
}

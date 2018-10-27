package Validation;

public class PortValidator
{
    public boolean validatePort(String serverPort, int serverPortInt)
    {
        //regular expression for checking if string contains only numbers
        String regex = "\\d+";

        //checks if entered port matches regex and if port is within proper range
        if(serverPort.matches(regex) && checkServerPort(serverPortInt,serverPort))
        {
            return true;
        }

        return false;
    }

    //checks range of port
    private boolean checkServerPort(int serverPortInt, String serverPort)
    {
        if (serverPortInt >= 1023 && serverPortInt <= 65535 && serverPort.length() >= 4 && serverPort.length() <=5)
        {
            return true;
        }

        return false;
    }

    //returns an int converted from a string
    public int convertPortToInt(String serverPort)
    {
        try
        {
            return Integer.parseInt(serverPort);
        }
        catch (NumberFormatException nfEx)
        {
            return 0;
        }

    }
}

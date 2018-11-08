package Strategy;

//class containing the logic behind validating port numbers
//part of strategy pattern
public class PortValidationStrategy implements ValidationStrategy
{
    @Override
    public boolean validationStrategy(String serverPort)
    {
        //regular expression for checking if string contains only numbers
        String regex = "\\d+";

        return (serverPort.matches(regex) && checkServerPort(convertPortToInt(serverPort),serverPort));
    }

    //checks range of port
    private boolean checkServerPort(int serverPortInt, String serverPort)
    {
        return (serverPortInt >= 1023 && serverPortInt <= 65535 &&
                serverPort.length() >= 4 && serverPort.length() <=5);
    }

    //returns an int converted from a string
    private int convertPortToInt(String serverPort)
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

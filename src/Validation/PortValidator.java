package Validation;

import Strategy.PortValidationStrategy;

//class used for validating port numbers
//part of strategy pattern
public class PortValidator extends Validator
{

    private static PortValidator portValidator;

    private PortValidator()
    {
        super();

        setValidationStrategy(new PortValidationStrategy());
    }

    public static PortValidator getPortValidator()
    {
        synchronized (PortValidator.class)
        {
            if(portValidator == null)
            {
                portValidator = new PortValidator();
            }

            return portValidator;
        }
    }

    public boolean validatePort(String serverPort)
    {
        return validate(serverPort);
    }

    //checks range of port
    /*private boolean checkServerPort(int serverPortInt, String serverPort)
    {
        return (serverPortInt >= 1023 && serverPortInt <= 65535 &&
                serverPort.length() >= 4 && serverPort.length() <=5);
    }*/

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

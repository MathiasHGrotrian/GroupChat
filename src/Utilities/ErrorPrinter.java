package Utilities;

public class MessagePrinter
{
    public void IPAndPortErPrinter(boolean IPIsValid, boolean portIsValid)
    {
        //help messages if input isn't valid
        if(!IPIsValid && !portIsValid)
        {
            badIPAndPort();
        }

        else if(!IPIsValid)
        {
            badIP();
        }

        else if(!portIsValid)
        {
            badPort();
        }
    }

    public String duplicateUsername()
    {
        return "J_ER 401: Duplicate username";
    }

    public void otherError()
    {
        System.out.println("J_ER 500: Other error");
    }

    public void unknownCommand()
    {
        System.out.println("J_ER 501: Unknown command");
    }

    public String badCommand()
    {
        return "J_ER 502: Bad command";
    }

    public void unexpectedServerShutdown()
    {
        System.out.println("J_ER 503: Server shut down");
    }

    public void unexpectedClientShutdown()
    {
        System.out.println("J_ER 504: Client disconnected unexpectedly");
    }

    public void badIPAndPort()
    {
        System.out.println("J_ER 505: Invalid IP address and serverport number\n" +
                "Please make sure you are using an IPv4 address and the format: 0-255.0-255.0-255.0-255\n" +
                "Please use a port number between 1023 - 65535\n");
    }

    public void cantEstablishConnection()
    {
        System.out.println("J_ER 506: Can't connect to server. \n" +
                "Make sure you have the right IPv4 address and port number\n");
    }

    public void badIP()
    {
        System.out.println("J_ER 507: Invalid IP address\n" +
                "Please make sure you are using an IPv4 address and the format: 0-255.0-255.0-255.0-255\n");
    }

    public void badPort()
    {
        System.out.println("J_ER 508: Invalid serverport number. No letters or symbols allowed\n" +
                "Please use a port number between 1023-65535. No letters or symbols allowed in port number\n");
    }

}

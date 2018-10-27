package Validation;

public class IPv4Validator
{
    public boolean validateIP(String ip)
    {
        //checks if given ip is a valid IPv4 address and returns true if it is
        return ip.matches("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    }
}

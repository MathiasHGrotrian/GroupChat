package Strategy;

//class containing logic behind validating ipv4 addresses
//part of strategy pattern
public class IPv4ValidationStrategy implements ValidationStrategy
{
    @Override
    public boolean validationStrategy(String ipv4Address)
    {
        //meaning of regex: ^ means the start of the expression, $ means the end
        //\\d means a number from 0-9
        // | means or
        //either the input must start with either 0 or 1 and may then be followed by two numbers
        //or the input must start with two, which must then be followed by a number between 0-4, the followed by a number between 0-9
        //or the number must start with 25 then followed by a number between 0-5 followed by .
        //{3} means to do this 3 times
        //then the last part of the regex is just a repeat of the first part without the . at the end
        return ipv4Address.matches("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    }
}

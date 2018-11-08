package Validation;

import Strategy.IPv4ValidationStrategy;

//class used for validating ipv4 addresses
//part of strategy pattern
public class IPv4Validator extends Validator
{
    private static IPv4Validator iPv4Validator;

    private IPv4Validator()
    {
        super();

        setValidationStrategy(new IPv4ValidationStrategy());

    }

    public static IPv4Validator getIPv4Validator()
    {
        synchronized (IPv4Validator.class)
        {
            if(iPv4Validator == null)
            {
                iPv4Validator = new IPv4Validator();
            }

            return iPv4Validator;
        }
    }

    public boolean validateIP(String ip)
    {
        return validate(ip);
    }

}

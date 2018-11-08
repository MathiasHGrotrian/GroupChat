package Validation;

import Strategy.IPv4ValidationStrategy;

//class used for validating ipv4 addresses
//part of strategy pattern
public class IPv4Validator extends Validator
{
    public IPv4Validator()
    {
        super();

        setValidationStrategy(new IPv4ValidationStrategy());

    }

    public boolean validateIP(String ip)
    {
        return validate(ip);
    }

}

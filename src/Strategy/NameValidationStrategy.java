package Strategy;

//class containing logic behind validating names
//part of strategy pattern
public class NameValidationStrategy implements ValidationStrategy
{
    @Override
    public boolean validationStrategy(String userName)
    {
        return userName.matches("[\\w]{1,12}");
    }
}

package Strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//class containing logic behind validating names
//doesn't quite work as intended, seems to only validate one character
//meaning it is possible to write multiple "@" and get accepted
//part of strategy pattern
public class NameValidationStrategy implements ValidationStrategy
{
    @Override
    public boolean validationStrategy(String userName)
    {
        Pattern namePattern = Pattern.compile("[a-z0-9_-æøå]", Pattern.CASE_INSENSITIVE);
        Matcher nameMatcher = namePattern.matcher(userName);

        return nameMatcher.find();
    }
}

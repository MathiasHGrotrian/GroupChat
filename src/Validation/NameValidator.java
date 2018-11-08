package Validation;

import ServerSide.Broadcaster;
import ServerSide.ClientHandler;
import Strategy.NameValidationStrategy;
import Utilities.ErrorPrinter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//class used for validating names of clienthandlers
//part of strategy pattern
public class NameValidator extends Validator
{
    public NameValidator()
    {
        super();

        setValidationStrategy(new NameValidationStrategy());

    }

    public boolean validateName(String username)
    {
        return validate(username);
    }

    /*public boolean validateName(String userName)
    {
        Pattern namePattern = Pattern.compile("[a-z0-9_-æøå]", Pattern.CASE_INSENSITIVE);
        Matcher nameMatcher = namePattern.matcher(userName);

        return nameMatcher.find();
    }*/

    public boolean checkName(String userName, DataOutputStream outputStream,
                             ClientHandler clientHandler, ArrayList<ClientHandler> clientList)
    {
        Broadcaster broadcaster = Broadcaster.getBroadcaster();

        ErrorPrinter errorPrinter = ErrorPrinter.getErrorPrinter();

        try
        {

            //for each loop to run through list of clienthandlers
            for(ClientHandler handler : clientList)
            {
                //checks if any clienthandlers already have the username entered by the user
                //gives duplicate username error to user and loop starts over
                if(handler.getUsername().equals(userName))
                {
                    outputStream.writeUTF(errorPrinter.duplicateUsername());

                    return false;
                }
            }

            if(userName.length() > 0 && userName.length() <= 12
                    && !userName.equalsIgnoreCase("quit")
                    && !userName.equalsIgnoreCase("imav"))
            {
                return true;
            }

            outputStream.writeUTF(errorPrinter.badCommand());

            return false;

        }
        catch (IOException ioEx)
        {
            errorPrinter.unexpectedClientShutdown();

            clientList.remove(clientHandler);

            try
            {
                broadcaster.alertUsersOfChanges(clientList, outputStream, clientHandler);

                clientHandler.getSocket().close();

            } catch (IOException e)
            {
                e.printStackTrace();
            }

            return false;
        }
    }
}

package Validation;

import ServerSide.ClientHandler;
import ServerSide.Server;
import Utilities.ErrorPrinter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameValidator
{
    private static ErrorPrinter errorPrinter = new ErrorPrinter();
    public boolean validateName(String userName)
    {
        Pattern namePattern = Pattern.compile("[a-z0-9_-æøå]", Pattern.CASE_INSENSITIVE);
        Matcher nameMatcher = namePattern.matcher(userName);

        return nameMatcher.find();
    }

    public boolean checkName(String userName, DataOutputStream outputStream, ClientHandler clientHandler)
    {
        try
        {

            //for each loop to run through list of clienthandlers
            for(ClientHandler handler : Server.clientList)
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

            Server.clientList.remove(clientHandler);

            try
            {
                clientHandler.alertUsersOfChanges(Server.clientList, outputStream);

                clientHandler.getSocket().close();

            } catch (IOException e)
            {
                e.printStackTrace();
            }

            return false;
        }
    }
}

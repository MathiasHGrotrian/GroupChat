package Validation;

import ServerSide.Broadcaster;
import ServerSide.ClientHandler;
import Utilities.ClientHandlerContainer;
import Utilities.ErrorPrinter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//class used for validating messages
//not yet integrated as part of strategy pattern
public class MessageValidator
{
    private static MessageValidator messageValidator;

    private MessageValidator() {}

    public static MessageValidator getMessageValidator()
    {
        synchronized (MessageValidator.class)
        {
            if(messageValidator == null)
            {
                messageValidator = new MessageValidator();
            }

            return messageValidator;
        }
    }

    //checks contents of messages and responds depending on the contents
    public boolean checkMessage(String received, ClientHandler clientHandler) throws IOException
    {
        ClientHandlerContainer clientHandlerContainer = ClientHandlerContainer.getClientContainer();

        ArrayList<ClientHandler> clientList = clientHandlerContainer.getClientHandlers();

        DataOutputStream outputStream = clientHandler.getOutputStream();

        Broadcaster broadcaster = Broadcaster.getBroadcaster();

        ErrorPrinter errorPrinter = ErrorPrinter.getErrorPrinter();

        //quit statement
        if(received.equals("QUIT"))
        {
            //removes clienthandler from the list of clienthandlers currently connected to server
            clientHandlerContainer.removeClient(clientHandler);

            //sends a message to every client informing them of the client quitting
            for(ClientHandler handler : clientList)
            {
                handler.getOutputStream().writeUTF(clientHandler.getUsername() + " has quit");
            }

            System.out.println("QUIT " + clientHandler.getUsername());

            clientHandler.getSocket().close();

            return false;
        }

        //prevents users from sending acknowledgement messages to each other
        if(received.equalsIgnoreCase("j_ok"))
        {
            outputStream.writeUTF(errorPrinter.badCommand());

            return true;
        }
        else if(received.equalsIgnoreCase("imav"))
        {
            return true;
        }

        broadcaster.sendMessages(received, clientHandler);

        return true;
    }
}

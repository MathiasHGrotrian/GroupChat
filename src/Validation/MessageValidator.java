package Validation;

import ServerSide.Broadcaster;
import ServerSide.ClientHandler;
import Utilities.ErrorPrinter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//class used for validating messages
//not yet integrated as part of strategy pattern
public class MessageValidator
{
    //checks contents of messages and responds depending on the contents
    public boolean checkMessage(String received, DataOutputStream outputStream,
                                ClientHandler clientHandler, ArrayList<ClientHandler> clientList) throws IOException
    {

        Broadcaster broadcaster = Broadcaster.getBroadcaster();

        ErrorPrinter errorPrinter = ErrorPrinter.getErrorPrinter();

        //quit statement
        if(received.equals("QUIT"))
        {
            //removes clienthandler from the list of clienthandlers currently connected to server
            clientList.remove(clientHandler);

            for(ClientHandler handler : clientList)
            {
                handler.getOutputStream().writeUTF(clientHandler.getUsername() + " has quit");
            }

            //prints a list of every clienthandler connected to the server, to every client
            //is updated when a client disconnects from server
            broadcaster.alertUsersOfChanges(clientList, outputStream, clientHandler);

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
            outputStream.writeUTF(errorPrinter.badCommand());

            return true;
        }

        broadcaster.sendMessages(received, clientList, clientHandler);

        return true;
    }
}

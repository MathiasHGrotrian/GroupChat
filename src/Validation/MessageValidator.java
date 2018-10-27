package Validation;

import ServerSide.ClientHandler;
import ServerSide.Server;
import Utilities.ErrorPrinter;

import java.io.DataOutputStream;
import java.io.IOException;

public class MessageValidator
{

    private static ErrorPrinter errorPrinter = new ErrorPrinter();

    //checks contents of messages and responds depending on the contents
    public boolean checkMessage(String received, DataOutputStream outputStream,
                                 ClientHandler clientHandler) throws IOException
    {
        //quit statement
        if(received.equals("QUIT"))
        {
            //removes clienthandler from the list of clienthandlers currently connected to server
            Server.clientList.remove(clientHandler);

            for(ClientHandler handler : Server.clientList)
            {
                handler.getOutputStream().writeUTF(clientHandler.getUsername() + " has quit");
            }

            //prints a list of every clienthandler connected to the server, to every client
            //is updated when a client disconnects from server
            clientHandler.alertUsersOfChanges(Server.clientList, outputStream);

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

        clientHandler.sendMessages(received);

        return true;
    }
}

package ServerSide;

import Utilities.ErrorPrinter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//singleton class used for sending messages to all clients connected
public class Broadcaster
{
    private static Broadcaster broadcaster;

    private Broadcaster(){}

    public static Broadcaster getBroadcaster()
    {
        synchronized (Broadcaster.class)
        {
            if(broadcaster == null)
            {
                broadcaster = new Broadcaster();
            }
        }

        return broadcaster;
    }

    //sends messages to all connected clients
    public void sendMessages(String received, ArrayList<ClientHandler> clientList,
                             ClientHandler handler) throws IOException
    {
        //sending message to other clients using a for each loop
        for (ClientHandler clientHandler : clientList)
        {
            //the if-statment makes sure that the same client doesn't gets its own message back
            //prints out message to all other clients
            if(!clientHandler.getUsername().equals(handler.getUsername()))
            {
                clientHandler.getOutputStream().writeUTF("DATA " + handler.getUsername() + " : " + received);
            }
        }

    }

    //prints an arraylist of clienthandlers out in a readable format
    //is used every time a client connects to, or disconnects from the server
    public void alertUsersOfChanges(ArrayList<ClientHandler> clientList, DataOutputStream outputStream,
                                    ClientHandler handler) throws IOException
    {
        ErrorPrinter errorPrinter = ErrorPrinter.getErrorPrinter();

        String listOfClients = "";

        for (ClientHandler clienthandler : clientList)
        {
            if(clienthandler.getUsername().length() != 0)
            {
                listOfClients += clienthandler.getUsername() + " ";
            }
        }
        for(ClientHandler clientHandler : clientList)
        {
            try
            {
                clientHandler.getOutputStream().writeUTF("LIST "
                        + listOfClients);
            } catch (IOException ioEx)
            {
                errorPrinter.unexpectedClientShutdown();

                clientList.remove(handler);

                broadcaster.alertUsersOfChanges(clientList, outputStream, handler);

                handler.getSocket().close();
            }
        }

    }
}

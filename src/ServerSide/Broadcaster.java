package ServerSide;

import Utilities.ClientHandlerContainer;

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
    public void sendMessages(String received, ClientHandler clientHandler) throws IOException
    {
        ArrayList<ClientHandler> clientList = ClientHandlerContainer.getClientContainer().getClientList();

        //sending message to other clients using a for each loop
        for (ClientHandler handler : clientList)
        {
            //the if-statment makes sure that the same client doesn't gets its own message back
            //prints out message to all other clients
            if(!handler.getUsername().equals(clientHandler.getUsername()))
            {
                handler.getOutputStream().writeUTF("DATA " + clientHandler.getUsername() + " : " + received);
            }
        }

    }

    //prints an arraylist of clienthandlers out in a readable format
    //is used every time a client connects to, or disconnects from the server
    public void alertUsersOfChanges() throws IOException
    {
        ArrayList<ClientHandler> clientList = ClientHandlerContainer.getClientContainer().getClientList();

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
            clientHandler.getOutputStream().writeUTF("LIST "
                    + listOfClients);
        }

    }
}

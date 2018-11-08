package Utilities;

import ServerSide.Broadcaster;
import ServerSide.ClientHandler;

import java.io.IOException;
import java.util.ArrayList;

//singleton class used for holding instance of arraylist containing clienthandlers
//acts as subject in observer pattern, not sure if implemented correctly
public class ClientHandlerContainer
{
    private Broadcaster broadcaster;

    private static ClientHandlerContainer clientHandlerContainer;

    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private ClientHandlerContainer()
    {
        this.broadcaster = Broadcaster.getBroadcaster();
    }

    public static ClientHandlerContainer getClientContainer()
    {
        //makes the initialization of class thread safe
        synchronized (ClientHandlerContainer.class)
        {
            if(clientHandlerContainer == null)
            {
                clientHandlerContainer = new ClientHandlerContainer();
            }
        }

        return clientHandlerContainer;
    }

    public ArrayList<ClientHandler> getClientHandlers()
    {
        return clientHandlerContainer.clientHandlers;
    }

    public void notifyObservers() throws IOException
    {
        broadcaster.alertUsersOfChanges();
    }

    public void removeClient(ClientHandler clientHandler) throws IOException
    {
        clientHandlers.remove(clientHandler);

        notifyObservers();
    }

    public void addClient(ClientHandler clientHandler) throws IOException
    {
        clientHandlers.add(clientHandler);

    }
}

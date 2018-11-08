package Utilities;

import ServerSide.ClientHandler;
import java.util.ArrayList;

//singleton class used for holding instance of arraylist containing clienthandlers
public class ClientHandlerContainer
{
    private static ClientHandlerContainer clientHandlerContainer;

    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private ClientHandlerContainer(){}

    public static ClientHandlerContainer getListOfClients()
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
}

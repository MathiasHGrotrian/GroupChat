package Utilities;

import ServerSide.ClientHandler;

import java.util.ArrayList;

public class SingletonList
{
    private static SingletonList singletonList;

    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private SingletonList(){}

    public static SingletonList getListOfClients()
    {
        synchronized (SingletonList.class)
        {
            if(singletonList == null)
            {
                singletonList = new SingletonList();
            }
        }

        return singletonList;
    }

    public ArrayList<ClientHandler> getClientHandlers()
    {
        return singletonList.clientHandlers;
    }
}

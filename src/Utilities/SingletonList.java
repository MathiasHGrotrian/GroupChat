import ServerSide.ClientHandler;

import java.util.ArrayList;

public class SingletonList
{
    private static SingletonList singletonList = null;

    private ArrayList<ClientHandler> clientHandlers;

    private SingletonList(){}

    public static SingletonList getInstance()
    {
        if(singletonList == null)
        {
            singletonList = new SingletonList();
        }

        return singletonList;
    }

    public ArrayList<ClientHandler> getClientHandlers()
    {
        return singletonList.clientHandlers;
    }

    public ArrayList<ClientHandler> getClientHandler()
    {
        return clientHandlers;
    }
}

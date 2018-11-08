package ServerSide;

import Utilities.ClientHandlerContainer;

import java.io.*;
import java.util.*;
import java.net.*;

public class Server
{
    // ArrayList to store active clients
    //public static ArrayList<ClientHandler> clientList = new ArrayList<>();

    public static void main(String[] args) throws IOException
    {
        System.out.println("Server is running...");

        startServer();
    }

    private static void startServer() throws IOException
    {
        ClientHandlerContainer clientHandlerContainer = ClientHandlerContainer.getClientContainer();
        
        //create a socket for server and an open socket
        //one socket for server and one for ClientHandler
        ServerSocket serverSocket = new ServerSocket(1234);
        Socket socket;

        //while-loop for getting client request
        while (true)
        {
            //accept the incoming request and listen for clients trying to connect on socket
            socket = serverSocket.accept();

            //initiate input and output streams
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            //create a new handler object for handling this request
            ClientHandler clientHandler = new ClientHandler(socket, inputStream, outputStream);

            //create a new Thread with the clientHandler object
            Thread thread = new Thread(clientHandler);

            //adds clienthandler to list of clienthandlers
            clientHandlerContainer.addClient(clientHandler);

            //starts the clienthandler thread
            thread.start();
        }
    }

    private static void updateListOfUsers(ArrayList<ClientHandler> clientHandlers)
    {
        for(ClientHandler clientHandler : clientHandlers)
        {
            clientHandler.setUserList(clientHandlers);
        }
    }

}


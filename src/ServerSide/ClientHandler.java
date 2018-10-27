package ServerSide;

import Utilities.CountDown;
import Utilities.ErrorPrinter;
import Validation.MessageValidator;
import Validation.NameValidator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable
{

    private static ErrorPrinter errorPrinter = new ErrorPrinter();

    private static MessageValidator messageValidator = new MessageValidator();

    private static HeartBeatListener heartBeatListener = new HeartBeatListener();

    //variables
    private String username;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private Socket socket;
    private ArrayList<ClientHandler> userList;

    //constructor
    ClientHandler(Socket socket, DataInputStream inputStream, DataOutputStream outputStream)
    {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.socket = socket;
        this.username = "";
        this.userList = new ArrayList<>();
    }


    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public Socket getSocket() { return socket; }

    public DataOutputStream getOutputStream() { return outputStream; }

    public void setUserList(ArrayList<ClientHandler> userList)
    {
        this.userList = userList;
    }

    @Override
    public void run() {

        //variable for receiving message
        String received;

        boolean clientHandlerRunning = true;

        try
        {
            nameClient();
        }
        catch (IOException ioEx)
        {
            errorPrinter.otherError();
        }

        //thread to check if clients is alive
        CountDown countDown = new CountDown();
        Thread thread = new Thread(countDown);
        thread.start();

        while(clientHandlerRunning)
        {
            try
            {
                received = inputStream.readUTF();

                if(!heartBeatListener.checkImAlive(received, countDown, this))
                {
                    clientHandlerRunning = false;
                }

                if (!messageValidator.checkMessage(received, outputStream, this))
                {
                    clientHandlerRunning = false;
                }

            }
            //in case of unexpected errors
            catch (IOException ioEx)
            {
                errorPrinter.otherError();

                try
                {
                    Server.clientList.remove(this);

                    alertUsersOfChanges(Server.clientList, outputStream);

                    socket.close();

                    countDown.setOn(false);

                    clientHandlerRunning = false;

                }
                catch (IOException e)
                {
                    errorPrinter.otherError();
                }

            }

        }

        //closing resources for safety
        try
        {
            countDown.setOn(false);
            this.inputStream.close();
            this.outputStream.close();

        } catch(IOException e)
        {
            errorPrinter.otherError();
        }

    }

    //prints an arraylist of clienthandlers out in a readable format
    //is used every time a client connects to, or disconnects from the server
    public void alertUsersOfChanges(ArrayList<ClientHandler> clientHandlers, DataOutputStream outputStream) throws IOException
    {
        String listOfClients = "";

        for (ClientHandler clienthandler : clientHandlers)
        {
            if(clienthandler.getUsername().length() != 0)
            {
                listOfClients += clienthandler.getUsername() + " ";
            }
        }
        for(ClientHandler clientHandler : clientHandlers)
        {
            try
            {
                clientHandler.outputStream.writeUTF("LIST "
                        + listOfClients);
            } catch (IOException ioEx)
            {
                errorPrinter.unexpectedClientShutdown();

                Server.clientList.remove(this);

                alertUsersOfChanges(Server.clientList, outputStream);

                socket.close();
            }
        }

    }

    private void nameClient() throws IOException
    {
        NameValidator nameValidator = new NameValidator();
        boolean isBeingNamed = true;

        //string for receiving server ip and port number
        String received = inputStream.readUTF();

        while (isBeingNamed)
        {

            try
            {
                //variable used for naming clientHandler objects
                String userName;

                //sends a request about a username
                outputStream.writeUTF("Type username");

                //receive a string, userName
                userName = inputStream.readUTF();

                if(nameValidator.validateName(userName))
                {
                    if(nameValidator.checkName(userName, outputStream, this))
                    {
                        setUsername(userName);

                        //prints a join message to the server with username
                        //start of threeway handshake, syn
                        System.out.println("JOIN " + username + received);

                        //middle of threeway handshake, send syn/ack
                        outputStream.writeUTF("J_OK");

                        //receive ack
                        String ack = inputStream.readUTF();

                        //end of threeway handshake, print out ack
                        System.out.println(ack);

                        //prints list of clienthandlers as clienthandler has been succesfully named and added to list
                        alertUsersOfChanges(Server.clientList, outputStream);

                        //breaks out of loop when username is ok
                        isBeingNamed = false;
                    }
                }
            }
            catch (IOException ioEx)
            {
                errorPrinter.unexpectedClientShutdown();

                Server.clientList.remove(this);

                alertUsersOfChanges(Server.clientList, outputStream);

                socket.close();

                isBeingNamed = false;
            }

        }
    }

    //sends messages to all connected clients
    public void sendMessages(String received) throws IOException
    {

        //sending message to other clients using a for each loop
        for (ClientHandler clientHandler : Server.clientList)
        {
            //the if-statment makes sure that the same client doesn't gets its own message back
            //prints out message to all other clients
            if(!clientHandler.getUsername().equals(this.username))
            {
                clientHandler.outputStream.writeUTF("DATA " + this.username +" : " + received);
            }
        }

    }
}

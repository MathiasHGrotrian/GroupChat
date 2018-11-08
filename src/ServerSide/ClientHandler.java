package ServerSide;

import Utilities.ClientNamer;
import Utilities.CountDown;
import Utilities.ErrorPrinter;
import Utilities.ClientHandlerContainer;
import Validation.MessageValidator;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable
{
    private static MessageValidator messageValidator = new MessageValidator();

    private static HeartBeatListener heartBeatListener = new HeartBeatListener();

    //variables
    private String username;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private Socket socket;
    private ArrayList<ClientHandler> userList;
    private Broadcaster broadcaster;
    private ClientNamer clientNamer;

    //constructor
    ClientHandler(Socket socket, DataInputStream inputStream, DataOutputStream outputStream)
    {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.socket = socket;
        this.username = "";
        this.userList = new ArrayList<>();
        this.broadcaster = Broadcaster.getBroadcaster();
        this.clientNamer = new ClientNamer();
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

    void setUserList(ArrayList<ClientHandler> userList)
    {
        this.userList = userList;
    }

    @Override
    public void run() {

        //list of clienthandlers stored i singleton
        ArrayList<ClientHandler> clientList = ClientHandlerContainer.getListOfClients().getClientHandlers();

        ErrorPrinter errorPrinter = ErrorPrinter.getErrorPrinter();

        //variable for receiving message
        String received;

        boolean clientHandlerRunning = true;

        try
        {
            clientNamer.nameClient(clientList, inputStream, outputStream, this);
            //nameClient(clientList);
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

                if(!heartBeatListener.checkImAlive(received, countDown, this, clientList))
                {
                    clientHandlerRunning = false;
                }

                if (!messageValidator.checkMessage(received, outputStream, this, clientList))
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
                    clientList.remove(this);

                    broadcaster.alertUsersOfChanges(clientList, outputStream, this);

                    //alertUsersOfChanges(clientList, outputStream);

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
}

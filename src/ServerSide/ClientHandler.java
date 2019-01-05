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

//class for handling connection between client and server
public class ClientHandler implements Runnable
{
    //variables
    private String username;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private Socket socket;
    private ClientNamer clientNamer;
    private MessageValidator messageValidator;
    private HeartBeatListener heartBeatListener;
    private ErrorPrinter errorPrinter;
    private ClientHandlerContainer clientHandlerContainer;

    //constructor
    ClientHandler(Socket socket, DataInputStream inputStream, DataOutputStream outputStream)
    {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.socket = socket;
        this.username = "";
        this.clientNamer = new ClientNamer();
        this.messageValidator = MessageValidator.getMessageValidator();
        this.heartBeatListener = new HeartBeatListener();
        this.errorPrinter = ErrorPrinter.getErrorPrinter();
        this.clientHandlerContainer = ClientHandlerContainer.getClientContainer();
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

    public DataInputStream getInputStream() { return inputStream; }

    @Override
    public void run() {

        //variable for receiving message
        String received;

        boolean clientHandlerRunning = true;

        try
        {
            clientNamer.nameClient(this);
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

                if (!messageValidator.checkMessage(received, this))
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
                    clientHandlerContainer.removeClient(this);

                    this.socket.close();

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

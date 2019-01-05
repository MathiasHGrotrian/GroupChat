package ClientSide;

import Utilities.ErrorPrinter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

//class used for receiving messages from the server and displaying them to the client
class MessageReceiver implements Runnable
{

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private ErrorPrinter errorPrinter;

    MessageReceiver(Socket socket,
                    DataInputStream inputStream,
                    DataOutputStream outputStream)
    {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.errorPrinter = ErrorPrinter.getErrorPrinter();
    }

    //starts a thread for reading messages from other clients and the server
    public void run()
    {
        boolean isReading = true;

        while(isReading)
        {
            try
            {
                //reads the message from the servers outputstrem
                String message = inputStream.readUTF();

                //prints out the message received from server
                System.out.println(message);

                //if the received message is "QUIT", you are kicked out by admin (^^)
                if(message.equals("QUIT"))
                {
                    socket.close();

                    System.exit(1);
                }

                if(message.equals("J_OK"))
                {
                    outputStream.writeUTF("J_OK");
                }

            }
            //in case of unexpected errors
            catch (IOException ioEx)
            {
                try
                {
                    isReading = false;

                    socket.close();

                    errorPrinter.unexpectedServerShutdown();

                    System.exit(1);
                }
                catch (IOException e)
                {
                    errorPrinter.unexpectedServerShutdown();
                }
            }
        }
    }
}

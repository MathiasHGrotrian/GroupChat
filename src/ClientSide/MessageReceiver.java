package ClientSide;

import Utilities.ErrorPrinter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class MessageReceiver
{
    //starts a thread for reading messages from other clients and the server
    public void readMessage(DataInputStream inputStream, Socket socket,
                            DataOutputStream outputStream, ErrorPrinter errorPrinter)
    {
        //thread for reading messages
        Thread readMessage = new Thread(() ->
        {
            while (true)
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
                        socket.close();

                        System.exit(1);
                    }
                    catch (IOException e)
                    {
                        errorPrinter.unexpectedServerShutdown();
                    }
                }
            }
        });

        readMessage.start();
    }
}
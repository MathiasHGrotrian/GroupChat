package ClientSide;

import Utilities.ErrorPrinter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

//class used for sending messages from client to server
class MessageSender
{
    //starts a thread for sending messages to other clients
    void sendMessages(DataOutputStream outputStream, Scanner scanner, Socket socket)
    {
        Thread sendMessage = new Thread(() ->
        {
            ErrorPrinter errorPrinter = ErrorPrinter.getErrorPrinter();

            boolean isSending = true;

            while (isSending)
            {
                //sets the message to users input from scanner
                String message = scanner.nextLine();

                try
                {

                    //sends message to server by writing on the output stream
                    outputStream.writeUTF(message);

                    //checks if exit message has been typed
                    //closes connection and shuts down if it has
                    if(message.equals("QUIT"))
                    {
                        isSending = false;

                        socket.close();

                        errorPrinter.unexpectedServerShutdown();

                        System.exit(1);
                    }
                } catch (IOException e)
                {
                    errorPrinter.unexpectedServerShutdown();
                }
            }
        });

        sendMessage.start();
    }
}

package ClientSide;

import Utilities.ErrorPrinter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

//class used for sending messages from client to server
class MessageSender implements Runnable
{
    private DataOutputStream outputStream;
    private Socket socket;
    private Scanner scanner;
    private ErrorPrinter errorPrinter;

    MessageSender(DataOutputStream outputStream, Socket socket, Scanner scanner)
    {
        this.outputStream = outputStream;
        this.socket = socket;
        this.scanner = scanner;
        this.errorPrinter = ErrorPrinter.getErrorPrinter();
    }
    @Override
    public void run()
    {
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
    }
}

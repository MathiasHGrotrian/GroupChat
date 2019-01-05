package ClientSide;

import Utilities.ErrorPrinter;
import java.io.DataOutputStream;
import java.io.IOException;

//class used to send heartbeat to server
class HeartBeatSender implements Runnable
{
    private DataOutputStream outputStream;
    private ErrorPrinter errorPrinter;
    
    HeartBeatSender(DataOutputStream outputStream)
    {
        this.outputStream = outputStream;
        this.errorPrinter = ErrorPrinter.getErrorPrinter();
    }

    //starts a thread which sends out a heartbeat to the server every 60 seconds
    @Override
    public void run()
    {
        boolean isAlive = true;

        while(isAlive)
        {
            try
            {
                //puts thread to sleep for a specified amount of time
                Thread.sleep(60000);

                //after thread has woken up, sends out I'm alive message to server
                outputStream.writeUTF("IMAV");

            } catch (InterruptedException | IOException iEx)
            {
                errorPrinter.unexpectedServerShutdown();

                isAlive = false;
            }
        }
    }
}

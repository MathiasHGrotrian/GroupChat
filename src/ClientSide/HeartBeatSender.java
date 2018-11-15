package ClientSide;

import Utilities.ErrorPrinter;
import java.io.DataOutputStream;
import java.io.IOException;

//class used to send heartbeat to server
class HeartBeatSender
{
    //starts a thread which sends out a heartbeat to the server every 60 seconds
    void imAlive(DataOutputStream outputStream)
    {
        //thread for sending I'm alive messages each time 60 seconds have passed
        Thread imAlive = new Thread(new Runnable()
        {
            ErrorPrinter errorPrinter = ErrorPrinter.getErrorPrinter();

            boolean isAlive = true;

            @Override
            public void run()
            {
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
        });

        imAlive.start();
    }
}

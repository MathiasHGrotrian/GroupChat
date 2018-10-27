package ClientSide;

import Utilities.ErrorPrinter;

import java.io.DataOutputStream;
import java.io.IOException;

class HeartBeatSender
{
    //starts a thread which sends out a heartbeat to the server every 60 seconds
    public void imAlive(DataOutputStream outputStream, ErrorPrinter errorPrinter)
    {
        //thread for sending I'm alive messages each time 60 seconds have passed
        Thread imAlive = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        //puts thread to sleep for a specified amount of time
                        Thread.sleep(60000);

                        //after thread has woken up, sends out I'm alive message to server
                        outputStream.writeUTF("IMAV");

                    } catch (InterruptedException iEx)
                    {
                        errorPrinter.unexpectedServerShutdown();
                    }
                    catch (IOException ioEx)
                    {
                        errorPrinter.unexpectedServerShutdown();

                    }
                }

            }
        });

        imAlive.start();
    }
}

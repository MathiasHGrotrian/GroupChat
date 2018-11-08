package ServerSide;

import Utilities.ClientHandlerContainer;
import Utilities.CountDown;
import Utilities.ErrorPrinter;

import java.io.IOException;
import java.util.ArrayList;

class HeartBeatListener
{
    //checks if clients have sent heartbeats and terminates their connection if they haven't
    boolean checkImAlive(String received, CountDown countDown, ClientHandler clientHandler) throws IOException
    {
        ClientHandlerContainer clientHandlerContainer = ClientHandlerContainer.getClientContainer();

        ErrorPrinter errorPrinter = ErrorPrinter.getErrorPrinter();

        //checks is client is alive, if not stop while loop and close socket
        //given a margin of 5 seconds
        if (countDown.getSecondsPassed() >= 65)
        {
            //stop timer in countdown
            countDown.setOn(false);

            //removes clienthandler from the list of clienthandlers currently connected to server
            clientHandlerContainer.removeClient(clientHandler);

            try
            {
                System.out.println("QUIT " + clientHandler.getUsername());

                //to quit client
                clientHandler.getOutputStream().writeUTF("QUIT");

                clientHandler.getSocket().close();

                return false;
            }
            catch (IOException ioEx)
            {
                errorPrinter.unexpectedClientShutdown();

                clientHandlerContainer.removeClient(clientHandler);

                clientHandler.getSocket().close();
            }
        }

        if(received.equalsIgnoreCase("IMAV"))
        {
            countDown.setSecondsPassed(0);
        }

        return true;

    }
}

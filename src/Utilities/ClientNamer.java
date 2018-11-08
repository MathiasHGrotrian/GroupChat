package Utilities;

import ServerSide.Broadcaster;
import ServerSide.ClientHandler;
import Validation.NameValidator;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//class used to name clienthandlers
public class ClientNamer
{
    public void nameClient(ArrayList<ClientHandler> clientList, DataInputStream inputStream,
                           DataOutputStream outputStream, ClientHandler handler) throws IOException
    {
        Broadcaster broadcaster = Broadcaster.getBroadcaster();

        ErrorPrinter errorPrinter = ErrorPrinter.getErrorPrinter();

        NameValidator nameValidator = new NameValidator();

        boolean isBeingNamed = true;

        //string for receiving server ip and port number
        String received = inputStream.readUTF();

        while (isBeingNamed)
        {

            try
            {
                //variable used for naming clientHandler objects
                String userName;

                //sends a request about a username
                outputStream.writeUTF("Type username");

                //receive a string, userName
                userName = inputStream.readUTF();

                if(nameValidator.validateName(userName))
                {
                    if(nameValidator.checkName(userName, outputStream, handler, clientList))
                    {
                        handler.setUsername(userName);

                        //prints a join message to the server with username
                        //start of threeway handshake, syn
                        System.out.println("JOIN " + handler.getUsername() + received);

                        //middle of threeway handshake, send syn/ack
                        outputStream.writeUTF("J_OK");

                        //receive ack
                        String ack = inputStream.readUTF();

                        //end of threeway handshake, print out ack
                        System.out.println(ack);

                        //prints list of clienthandlers as clienthandler has been succesfully named and added to list
                        broadcaster.alertUsersOfChanges(clientList, outputStream, handler);

                        //breaks out of loop when username is ok
                        isBeingNamed = false;
                    }
                }
            }
            catch (IOException ioEx)
            {
                errorPrinter.unexpectedClientShutdown();

                clientList.remove(handler);

                broadcaster.alertUsersOfChanges(clientList, outputStream, handler);

                handler.getSocket().close();

                isBeingNamed = false;
            }

        }
    }
}

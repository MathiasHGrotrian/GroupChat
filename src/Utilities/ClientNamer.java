package Utilities;

import ServerSide.Broadcaster;
import ServerSide.ClientHandler;
import Validation.NameValidator;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//class used to name clienthandlers
public class ClientNamer
{
    public void nameClient(ClientHandler clientHandler) throws IOException
    {
        ErrorPrinter errorPrinter = ErrorPrinter.getErrorPrinter();

        NameValidator nameValidator = NameValidator.getNameValidator();

        DataOutputStream outputStream = clientHandler.getOutputStream();

        DataInputStream inputStream = clientHandler.getInputStream();

        ClientHandlerContainer clientHandlerContainer = ClientHandlerContainer.getClientContainer();

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
                outputStream.writeUTF("Type username\n" +
                        "Only letters, digits, - and _ allowed");

                //receive a string, userName
                userName = inputStream.readUTF();

                if(nameValidator.validateName(userName))
                {
                    if(nameValidator.checkName(userName, clientHandler))
                    {
                        clientHandler.setUsername(userName);

                        //prints a join message to the server with username
                        //start of threeway handshake, syn
                        System.out.println("JOIN " + clientHandler.getUsername() + received);

                        //middle of threeway handshake, send syn/ack
                        outputStream.writeUTF("J_OK");

                        //receive ack
                        String ack = inputStream.readUTF();

                        //end of threeway handshake, print out ack
                        System.out.println(ack);

                        clientHandlerContainer.addClient(clientHandler);

                        //breaks out of loop when username is ok
                        isBeingNamed = false;
                    }
                }
            }
            catch (IOException ioEx)
            {
                errorPrinter.unexpectedClientShutdown();

                clientHandlerContainer.removeClient(clientHandler);

                clientHandler.getSocket().close();

                isBeingNamed = false;
            }

        }
    }
}

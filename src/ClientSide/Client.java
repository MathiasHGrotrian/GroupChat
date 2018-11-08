package ClientSide;

import Utilities.ErrorPrinter;
import Validation.IPv4Validator;
import Validation.PortValidator;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    public static void main(String args[])
    {
        ErrorPrinter errorPrinter = ErrorPrinter.getErrorPrinter();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Please choose an option\n" +
                "1. Local host server\n" +
                "2. Custom server");

        String serverOption = scanner.nextLine();

        switch (serverOption)
        {
            case "1":
            {
                localHostServer();

                break;
            }

            case "2":
            {
                customServer();

                break;
            }

            default:
            {
                errorPrinter.unknownCommand();

                break;
            }
        }
    }

    //sets up inputstream and socket and starts threads
    private static void setupChat(Socket socket, Scanner scanner, DataOutputStream outputStream)
    {
        ErrorPrinter errorPrinter = ErrorPrinter.getErrorPrinter();

        try
        {
            MessageSender messageSender = new MessageSender();

            MessageReceiver messageReceiver = new MessageReceiver();

            HeartBeatSender heartBeatSender = new HeartBeatSender();

            //initializing inputstream
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            messageSender.sendMessages(outputStream, scanner, socket);

            messageReceiver.readMessage(inputStream, socket, outputStream);

            heartBeatSender.imAlive(outputStream);
        }
        catch (IOException ioEx)
        {
            errorPrinter.otherError();
        }
    }

    //set up for local server
    private static void localHostServer()
    {
        ErrorPrinter errorPrinter = ErrorPrinter.getErrorPrinter();

        Scanner scanner = new Scanner(System.in);

        int serverPort = 1234;

        try
        {
            //setting localhost as ip address
            InetAddress ipAddress = InetAddress.getByName("localhost");

            //establish the socket connection
            Socket socket = new Socket(ipAddress, serverPort);

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.writeUTF("");

            setupChat(socket, scanner, outputStream);
        }
        catch (IOException ioEx)
        {
            errorPrinter.otherError();
        }

    }

    //set up for custom server
    private static void customServer()
    {
        ErrorPrinter errorPrinter = ErrorPrinter.getErrorPrinter();

        IPv4Validator iPv4Validator = new IPv4Validator();

        PortValidator portValidator = new PortValidator();

        //scanner used for getting server ip and port from user
        Scanner scanner = new Scanner(System.in);

        while(true)
        {
            //string for storing storing the server ip
            String ip, serverPort;

            //int for storing the serverport
            //used when setting up socket
            int serverPortInt;

            boolean IPIsValid, portIsValid;

            System.out.println("Please enter the IPv4 address of the server you would like to connect to.");

            ip = scanner.nextLine();

            IPIsValid = iPv4Validator.validateIP(ip);

            System.out.println("Please enter the serverport number.");

            serverPort = scanner.nextLine();

            serverPortInt = portValidator.convertPortToInt(serverPort);

            portIsValid = portValidator.validatePort(serverPort);

            /*if (portIsValid)
            {
                serverPortInt = portValidator.convertPortToInt(serverPort);
            }*/

            errorPrinter.IPAndPortErPrinter(IPIsValid, portIsValid);

            //checks if all inputs are valid and sets up connection if true
            if(IPIsValid && portIsValid)
            {
                //new scanner to be passed as argument in sendMessage method
                //done to prevent duplicate username error when user is prompted for username
                Scanner input = new Scanner(System.in);

                try
                {
                    //setting localhost as ip address
                    InetAddress ipAddress = InetAddress.getByName(ip);

                    //establish the socket connection
                    Socket socket = new Socket(ipAddress, serverPortInt);

                    //set up outputstream
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

                    //send information to be used in join message to server
                    outputStream.writeUTF(", " + ip + " : " + serverPort);

                    setupChat(socket, input, outputStream);

                    break;

                } catch (IOException ioEx)
                {
                    errorPrinter.cantEstablishConnection();
                }
            }
        }
    }


}

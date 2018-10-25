import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client
{
    public static void main(String args[])
    {
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
                    System.out.println("J_ER 501: Unknown command");

                    break;
                }
            }
    }

    //sets up inputstream and socket and starts threads
    private static void initializerSetup(Socket socket, Scanner scanner, DataOutputStream outputStream)
    {
        try
        {
            //initializing inputstream
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            sendMessage(outputStream, scanner, socket);

            readMessage(inputStream, socket, outputStream);

            imAlive(outputStream);
        }
        catch (IOException ioEx)
        {
            System.out.println("J_ER 500: Other error");
        }
    }

    //set up for local server
    private static void localHostServer()
    {
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

            initializerSetup(socket, scanner, outputStream);
        }
        catch (IOException ioEx)
        {
            System.out.println("J_ER 500: Other error");
        }

    }

    //set up for custom server
    private static void customServer()
    {
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

            //pattern for validating ipv4 addresses
            //found on stack overflow, link below
            //https://stackoverflow.com/questions/5667371/validate-ipv4-address-in-java
            /*Pattern IPPattern =
                    Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

            //matcher for the ip pattern
            Matcher IPMatcher = IPPattern.matcher(ip);

            //boolean used to check if a match is found
            boolean IPIsValid = IPMatcher.find();*/

            System.out.println("Please enter the serverport number.");

            serverPort = scanner.nextLine();

            serverPortInt = portValidator.convertPortToInt(serverPort);

            portIsValid = portValidator.validatePort(serverPort, serverPortInt);

            if (portIsValid)
            {
                serverPortInt = portValidator.convertPortToInt(serverPort);
            }

            //pattern used to make sure only digits are entered
            /*Pattern portPattern = Pattern.compile("[\\d]");

            Matcher portMatcher = portPattern.matcher(serverPort);

            boolean portIsValid = portMatcher.find();

            //stores the port number in an integer so it can be used to set up socket
            int serverPortInt = 0;

            //checks if entered port matches pattern and parses the string to an int if true
            if(portIsValid)
            {
                serverPortInt = Integer.parseInt(serverPort);

                //checks if serverport is within proper range and sets portIsValid to false if not
                if (!checkServerPort(serverPortInt,serverPort))
                {
                    portIsValid = false;
                }
            }*/

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

                    initializerSetup(socket, input, outputStream);

                    break;

                } catch (IOException ioEx)
                {
                    System.out.println("J_ER 506: Can't connect to server. \n" +
                            "Make sure you have the right IPv4 address and port number\n");
                }

            }

            //help messages if input isn't valid
            if(!IPIsValid && !portIsValid)
            {
                System.out.println("J_ER 509: Invalid IP address and serverport number\n" +
                        "Please make sure you are using an IPv4 address and the format: 0-255.0-255.0-255.0-255\n" +
                        "Please use a port number between 1023 - 65535\n");
            }

            else if(!IPIsValid)
            {
                System.out.println("J_ER 507: Invalid IP address\n" +
                        "Please make sure you are using an IPv4 address and the format: 0-255.0-255.0-255.0-255\n");
            }

            else if(!portIsValid)
            {
                System.out.println("J_ER 508: Invalid serverport number. No letters or symbols allowed\n" +
                        "Please use a port number between 1023-65535\n");
            }


        }
    }

    //checks if serverport number is within range
    private static boolean checkServerPort(int serverPortInt, String serverPort)
    {
        if (serverPortInt >= 1023 && serverPortInt <= 65535 && serverPort.length() >= 4 && serverPort.length() <=5)
        {
            return true;
        }

        return false;
    }

    //starts a thread for sending messages to other clients
    private static void sendMessage(DataOutputStream outputStream, Scanner scanner, Socket socket)
    {
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                boolean isSendingMessages = true;

                while (isSendingMessages)
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
                            socket.close();

                            System.exit(1);
                        }
                    } catch (IOException e)
                    {
                        System.out.println("J_ER 503: Server shut down");
                    }
                }
            }


        });

        sendMessage.start();
    }

    //starts a thread for reading messages from other clients and the server
    private static void readMessage(DataInputStream inputStream, Socket socket, DataOutputStream outputStream)
    {
        //thread for reading messages
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run()
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
                            System.out.println("J_ER 503: Server shut down");
                        }
                    }
                }
            }
        });

        readMessage.start();
    }

    //starts a thread which sends out a heartbeat to the server every 60 seconds
    private static void imAlive(DataOutputStream outputStream)
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
                        System.out.println("J_ER 503: Server shut down");
                    }
                    catch (IOException ioEx)
                    {
                        System.out.println("J_ER 503: Server shut down");

                    }
                }

            }
        });

        imAlive.start();
    }

}

import java.io.*;
import java.net.*;
import java.util.Scanner;

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
                System.out.println("501 : Unknown command");

                break;
            }
        }


    }

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

    private static void customServer()
    {
        //string for storing storing the server ip
        String ip;

        //int for storing the serverport
        int serverPort;

        //scanner used for getting server ip and port from user
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the IP address of the server you would like to connect to.");

        ip = scanner.nextLine();

        System.out.println("Please enter the serverport number");

        serverPort = scanner.nextInt();

        //new scanner to be passed as argument in sendMessage method
        //done to prevent duplicate username error when user is prompted for username
        Scanner input = new Scanner(System.in);

        try
        {
            //setting localhost as ip address
            InetAddress ipAddress = InetAddress.getByName(ip);

            //establish the socket connection
            Socket socket = new Socket(ipAddress, serverPort);

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.writeUTF(", " + ip + " : " + serverPort);

            initializerSetup(socket, input, outputStream);
        }
        catch (IOException ioEx)
        {
            System.out.println("J_ER 500: Other error");
        }
    }

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

                    } catch (IOException ioEx)
                    {
                        try
                        {
                            System.out.println("J_ER 503: Server shut down");

                            socket.close();

                            System.exit(1);
                        } catch (IOException e)
                        {
                            System.out.println("J_ER 503: Server shut down");
                        }
                    }
                }
            }
        });

        readMessage.start();
    }

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

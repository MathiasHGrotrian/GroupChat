import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    public static void main(String args[]) throws IOException, UnknownHostException
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

    private static void localHostServer() throws IOException
    {
        Scanner scanner = new Scanner(System.in);

        int serverPort = 1234;

        //setting localhost as ip address
        InetAddress ipAddress = InetAddress.getByName("localhost");

        //establish the socket connection
        Socket socket = new Socket(ipAddress, serverPort);

        //initiating input and out streams
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        sendMessage(outputStream, scanner, socket);

        readMessage(inputStream, socket);

        imAlive(outputStream);
    }

    private static void customServer() throws IOException
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

        //setting localhost as ip address
        InetAddress ipAddress = InetAddress.getByName(ip);

        //establish the socket connection
        Socket socket = new Socket(ipAddress, serverPort);

        //initiating input and out streams
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        sendMessage(outputStream, input, socket);

        readMessage(inputStream, socket);

        imAlive(outputStream);
    }

    private static void sendMessage(DataOutputStream outputStream, Scanner scanner, Socket socket)
    {
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
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
                        e.printStackTrace();
                    }
                }
            }


        });

        sendMessage.start();
    }

    private static void readMessage(DataInputStream inputStream, Socket socket)
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

                    } catch (IOException e)
                    {
                        e.printStackTrace();
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
                        Thread.sleep(10000);

                        //after thread has woken up, sends out I'm alive message to server
                        outputStream.writeUTF("IMAV");

                    } catch (InterruptedException iEx)
                    {
                        iEx.printStackTrace();
                    } catch (IOException ioEx)
                    {
                        ioEx.printStackTrace();
                    }
                }

            }
        });

        imAlive.start();
    }

}

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    final static int ServerPort = 1234;

    public static void main(String args[]) throws IOException
    {
        Scanner scanner = new Scanner(System.in);

        //setting localhost as ip address
        InetAddress ipAddress = InetAddress.getByName("localhost");

        //establish the socket connection
        Socket socket = new Socket(ipAddress, ServerPort);

        //initiating input and out streams
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        //sendMessage thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    //sets the message to input from scanner
                    String message = scanner.nextLine();

                    try
                    {
                        //write on the output stream
                        outputStream.writeUTF(message);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        //readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    try
                    {
                        //read the message
                        String message = inputStream.readUTF();
                        System.out.println(message);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        //starts threads
        sendMessage.start();
        readMessage.start();
    }
}

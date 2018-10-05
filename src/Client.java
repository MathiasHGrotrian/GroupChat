import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Timer;

public class Client
{
    final static int ServerPort = 1234;

    public static void main(String args[]) throws IOException, UnknownHostException
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

        Thread imAlive = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        Thread.sleep(60000);
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

        //starts threads
        sendMessage.start();
        readMessage.start();
        //imAlive.start();
    }


}

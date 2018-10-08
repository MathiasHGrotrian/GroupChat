import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Scanner;

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

        //thread for sending messages
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

                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

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
                        //puts thread to sleep for a specified ammount of time
                        Thread.sleep(5000);

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

        //starts threads
        sendMessage.start();
        readMessage.start();
        imAlive.start();
    }


}

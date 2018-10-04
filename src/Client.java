// Java implementation for multithreaded chat client
// Save file as Client.java

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    final static int ServerPort = 1234;

    public static void main(String args[]) throws UnknownHostException, IOException
    {
        Scanner scanner = new Scanner(System.in);

        // getting localhost ip
        InetAddress ipAddress = InetAddress.getByName("localhost");

        // establish the connection
        Socket socket = new Socket(ipAddress, ServerPort);

        // obtaining input and out streams
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {

                    // read the message to deliver.
                    String message = scanner.nextLine();

                    try {
                        // write on the output stream
                        outputStream.writeUTF(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client
                        String message = inputStream.readUTF();
                        System.out.println(message);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
}

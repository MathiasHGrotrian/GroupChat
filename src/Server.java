
// Java implementation of Server side
// It contains two classes : Server and ClientHandler
import java.io.*;
import java.util.*;
import java.net.*;

// Server class
public class Server
{
    // ArrayList to store active clients
    static ArrayList<ClientHandler> clientList = new ArrayList<>();

    public static void main(String[] args) throws IOException
    {
        //create a socket for sever and a open socket
        ServerSocket serverSocket = new ServerSocket(1234);
        Socket socket;

        //while-loop for getting client request
        while (true)
        {
            //Accept the incoming request
            socket = serverSocket.accept();

            System.out.println("J_OK");

            //initiate input and output streams
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());


            // Create a new handler object for handling this request.
            //indsæt eget navn i clienthandler
            ClientHandler clientHandler = new ClientHandler(socket,"new inout client ", inputStream, outputStream);

            // Create a new Thread with the clientHandler object
            Thread thread = new Thread(clientHandler);

            //husk, lav en socket.out til alle om en ny bruger

            //client add to list
            clientList.add(clientHandler);

            thread.start();
        }
    }
}

//ClientHandler class
class ClientHandler implements Runnable
{
    private String username;
    final DataInputStream inputStream;
    final DataOutputStream outputStream;
    Socket socket;
    boolean isAlive;

    //constructor
    public ClientHandler(Socket socket, String username, DataInputStream inputStream, DataOutputStream outputStream)
    {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.username = username;
        this.socket = socket;
        this.isAlive =true;
    }

    @Override
    public void run() {

        String received;
        while (true)
        {
            try
            {
                // receive the string
                received = inputStream.readUTF();
                System.out.println(received);

                if(received.equals("QUIT")){
                    this.isAlive =false;
                    this.socket.close();
                    break;
                }



                // search for the recipient in the connected devices list.
                // clientList is the vector storing client of active users
                for (ClientHandler mc : Server.clientList)
                {
                    // if the recipient is found, write on its
                    // output stream

                    if(!mc.username.equals(this.username))
                    {
                        mc.outputStream.writeUTF(this.username +" : "+received);
                    }



                }
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
        try
        {
            // closing resources
            this.inputStream.close();
            this.outputStream.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

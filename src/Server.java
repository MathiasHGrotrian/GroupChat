
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
        //one socket for Server and one for ClientHandler
        ServerSocket serverSocket = new ServerSocket(1234);
        Socket socket;

        //while-loop for getting client request
        while (true)
        {
            //Accept the incoming request
            socket = serverSocket.accept();

            //Vi sletter dig senere!!!!
            System.out.println("J_OK");

            //initiate input and output streams
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());


            // Create a new handler object for handling this request.
            //indsæt eget navn i clienthandler
            ClientHandler clientHandler = new ClientHandler(socket, inputStream, outputStream);

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
    //variabler
    private String username;
    final DataInputStream inputStream;
    final DataOutputStream outputStream;
    Socket socket;
    boolean isAlive;

    //constructor
    public ClientHandler(Socket socket, DataInputStream inputStream, DataOutputStream outputStream)
    {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.socket = socket;
        this.isAlive =true;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @Override
    public void run() {

        String received;
        String nameNew = "";
        Scanner scanner = new Scanner(System.in);

        while (nameNew.length() == 0)
        {
            try
            {
                //sends a request about a username
                outputStream.writeUTF("indtast navn");
                //receive a string,  nameNew
                nameNew = inputStream.readUTF();
                if(!(nameNew.length() < 0) && !(nameNew.length() > 12))
                {
                    setUsername(nameNew);
                }
            }catch (IOException e)
            {
                e.printStackTrace();
            }

        }

        while (true)
        {
            try
            {
                //receive a string, (readUTF can read standard format).
                received = inputStream.readUTF();
                System.out.println(received);

                //quit statment
                if(received.equals("QUIT")){
                    this.isAlive =false;
                    this.socket.close();
                    break;
                }

                //sending message to other clients by a for-loop
                for (ClientHandler clientHandler : Server.clientList)
                {
                    //the if-statment makes sure that the same client don´t gets it´s own message back.
                    if(!clientHandler.username.equals(this.username))
                    {
                        clientHandler.outputStream.writeUTF(this.username +" : "+received);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //closing resources for safety
        try
        {
            this.inputStream.close();
            this.outputStream.close();
        } catch(IOException e){
            e.printStackTrace();
        }


    }
}

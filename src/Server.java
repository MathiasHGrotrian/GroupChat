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
            //accept the incoming request
            socket = serverSocket.accept();

            //Vi sletter dig senere!!!!
            System.out.println("J_OK");

            //initiate input and output streams
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            //create a new handler object for handling this request
            ClientHandler clientHandler = new ClientHandler(socket, inputStream, outputStream);

            //create a new Thread with the clientHandler object
            Thread thread = new Thread(clientHandler);

            //adds clienthandler to list of clienthandlers
            clientList.add(clientHandler);

            //starts the clienthandler thread
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

    //constructor
    public ClientHandler(Socket socket, DataInputStream inputStream, DataOutputStream outputStream)
    {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.socket = socket;
        this.username = "";
    }


    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    @Override
    public void run() {

        //variable for receiving message
        String received;

        boolean isTrue = true;

        while (isTrue)
        {
            try
            {
                //variable used for naming clienthandler
                String nameNew;

                //sends a request about a username
                outputStream.writeUTF("Type username");

                //receive a string, nameNew
                nameNew = inputStream.readUTF();

                //for each loop to run through list of clienthandlers
                for(ClientHandler handler : Server.clientList)
                {
                    //checks to make sure client hasn't entered imav as name
                    //imav is treated as a bad command and loop starts over
                    if(nameNew.equalsIgnoreCase("imav"))
                    {
                        outputStream.writeUTF("502: Bad command");

                        nameNew = "";

                        break;
                    }

                    //checks if any clienthandlers already have the username entered by the user
                    //gives duplicate username error to user and loop starts over
                    if(handler.getUsername().equals(nameNew))
                    {
                        outputStream.writeUTF("401: Duplicate username");

                        nameNew = "";

                        break;
                    }
                }

                //performs checks to length of entered username to make sure it fits restrictions
                if(!(nameNew.length() == 0) && !(nameNew.length() > 12))
                {

                    setUsername(nameNew);

                    outputStream.writeUTF("J_OK");

                    //prints a join message to the server with username
                    //MAY NEED TO ADD PORT NUMBER AND IP ADDRESS TO JOIN MESSAGE
                    System.out.println("JOIN " + username);

                    //prints list of clienthandlers as clienthandler has been succesfully named and added to list
                    for(ClientHandler clientHandler : Server.clientList)
                    {
                        clientHandler.outputStream.writeUTF("UPDATED LIST OF ACTIVE USERS: \n"
                                + listToString(Server.clientList));
                    }

                    //breaks out of loop when username is ok
                    isTrue = false;

                }
            }catch (IOException e)
            {
                e.printStackTrace();
            }

        }

        //thread to check if clients is alive
        CountDown countDown = new CountDown();
        Thread thread = new Thread(countDown);
        thread.start();

        while (true)
        {
            try
            {
                //checks is client is alive, if not stop while loop and close socket
                if (countDown.getSecondsPassed() >= 60)
                {
                    //stop timer in countdown
                    countDown.setOn(false);

                    //removes clienthandler from the list of clienthandlers currently connected to server
                    Server.clientList.remove(this);

                    //prints a list of every clienthandler connected to the server, to every client
                    for(ClientHandler clientHandler : Server.clientList)
                    {
                        clientHandler.outputStream.writeUTF("UPDATED LIST OF ACTIVE USERS: \n"
                                + listToString(Server.clientList));
                    }

                    System.out.println("QUIT " + username);

                    //to quit client
                    outputStream.writeUTF("QUIT");
                    this.socket.close();

                    break;
                }


                //receive a string from clients outputstream, (readUTF can read standard format).
                received = inputStream.readUTF();

                //prints out message on server
                System.out.println(received);

                if(received.equalsIgnoreCase("IMAV"))
                {
                    System.out.println(countDown.getSecondsPassed());
                   // countDown.setSecondsPassed(0);
                }


                //quit statement
                if(received.equals("QUIT"))
                {

                    //removes clienthandler from the list of clienthandlers currently connected to server
                    Server.clientList.remove(this);

                    //prints a list of every clienthandler connected to the server, to every client
                    //is updated when a client disconnects from server
                    for(ClientHandler clientHandler : Server.clientList)
                    {
                        clientHandler.outputStream.writeUTF("UPDATED LIST OF ACTIVE USERS: \n"
                                + listToString(Server.clientList));
                    }

                    System.out.println("QUIT " + username);

                    this.socket.close();

                    break;
                }

                if(!received.equalsIgnoreCase("imav"))
                {
                    //sending message to other clients using a for each loop
                    for (ClientHandler clientHandler : Server.clientList)
                    {
                        //the if-statment makes sure that the same client doesn't gets its own message back
                        //prints out message to all other clients
                        if(!clientHandler.username.equals(this.username))
                        {
                            clientHandler.outputStream.writeUTF("DATA " + this.username +" : " + received);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("connection closed");

        //closing resources for safety
        try
        {
            System.out.println("closed connection");
            this.inputStream.close();
            this.outputStream.close();

        } catch(IOException e){
            e.printStackTrace();
        }


    }

    //prints an arraylist of clienthandlers out in a readable format
    //is used every time a client connects to, or disconnects from the server
    private String listToString(ArrayList<ClientHandler> list)
    {
        String listOfClients = "";

        for (ClientHandler clienthandler : list)
        {
            if(clienthandler.getUsername().length() != 0)
            {
                listOfClients += clienthandler.getUsername() + "\n";
            }
        }
        return listOfClients;
    }
}

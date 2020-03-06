import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ChatServer {
    // global variables
    // stores socket info for the server
    private ServerSocket in;
    // list store all connected clients' details
    private List<Socket> connectedSockets = new LinkedList<>();
    // list to store the running threads
    private List<ServerClientThread> threadsRunning = new LinkedList<>();
    // boolean to know whether the server should be shutdown or not
    private Boolean keepAlive = true;

    // instantiates a new instance of server
    public ChatServer(int port) {
        // attempts to allow a server connection through the designated port
        try {
            in = new ServerSocket(port);
        } catch (IOException e) {
            // if an error occurs it outputs an error message
            System.out.println("Could not listen on port, please try again");
        }
    }

    public static void main(String[] args) {
        // setup default port number
        int port = 14001;
        // if there are arguments given
        if (args.length != 0)
        {
            // regex pattern to ensure port number is a number
            String regexPattern = "[0-9]+";
            // while there are an even amount of arguments
            if (args.length % 2 != 0)
            {
                // if not then output an error message and exit the program
                System.out.println("Invalid amount of parameters, shutting down.");
                System.exit(0);
            }
            // otherwise if the argument given is -csp and the second argument is a number then parse this number as the port number to setup the server on
            else if (args[1].matches(regexPattern) && args[0].equals("-csp")) {
                port = Integer.parseInt(args[1]);
            }
            // if none of the above applies then output an error message and quit
            else
            {
                System.out.println("Error with parameters");
                System.exit(0);
            }
        }
        // create instance of server and call the clientAccept method
        ChatServer server = new ChatServer(port);
        server.clientAccept();
    }

    // deals with accepting new clients
    public void clientAccept() {
        // setup & start thread to listen for input to the server console to shut down the server
        Thread consoleListener = new Thread(new consoleListener());
        consoleListener.start();
        // outputs the details for connecting to the server
        System.out.println("Server listening, details: " + in);
        // while the server is being kept online
        while(keepAlive) {
            try {
                // allow clients to connect through the socket given in the server object instantiation
                Socket s = in.accept();
                // when a client connects outputs their detials
                System.out.println("New client request received: " + s);
                // add the connected clients details to the list
                connectedSockets.add(s);
                // start a new thread to deal with the new clients output
                ServerClientThread sct = new ServerClientThread(this, s);
                sct.start();
                threadsRunning.add(sct);
            } catch (IOException e) {
                // if error occurs at any point output error message
                System.out.println("Accept failed.");
            }
        }
    }

    // thread to listen for a message on the server's console
    class consoleListener implements Runnable {
        public void run() {
            // infinite loop
            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                // attempt to read the user's message
                try {
                    // if the message is EXIT then shut the server down
                    if (br.readLine().toUpperCase().equals("EXIT"))
                    {
                        // broadcast to the client and the server console that a shutdown has begun
                        System.out.println("Shutdown initiated.");
                        broadcast("Server shutdown.");
                        keepAlive = false;
                        // loops through all client threads and shuts them down cleanly
                        threadsRunning.forEach(ServerClientThread::interrupt);
                        // checks each thread is dead, if not then sleep for 200ms and check again, repeat
                        // done so that we know all threads have been exited cleanly and that the socket connection to each client is closed nicely
                        /*while (threadsRunning.size() != 0) {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }*/
                        // checks each thread is dead, if not then sleep for 200ms and check again, repeat
                        // done so that we know all threads have been exited cleanly and that the socket connection to each client is closed nicely
//                        threadsRunning.forEach(thread -> {
//                            while (thread.isAlive()) {
//                                try {
//                                    Thread.sleep(200);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }t
//                        });
                        System.exit(0);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading server input");
                }
            }
        }
    }

    // broadcasts a message to all clients bar the client which sent
    public void broadcast(String message, Socket clientSocket) {
        // loops through each connected client
        connectedSockets.forEach(socket -> {
            // if client who sent message is the same as the current looped client then miss this client out // do not broadcast to them
            if (socket != clientSocket) {
                // attempts to write to the connected client
                try {
                    PrintWriter clientOut = new PrintWriter(socket.getOutputStream(), true);
                    clientOut.println(message);
                // if it fails then it outputs the error stack
                } catch (IOException e) {
                    System.out.println("Error broadcasting to users.");
                }
            }
        });
    }

    // broadcasts a message to all connected clients
    public void broadcast(String message) {
        broadcast(message, null);
    }

    public void removeClient(Socket clientSocket) {
        connectedSockets.remove(clientSocket);
    }

    public void removeThread(ServerClientThread thread) {
        threadsRunning.remove(thread);
    }


    // method to close the server down
    public void quitServer() throws IOException {
        in.close();
    }
}

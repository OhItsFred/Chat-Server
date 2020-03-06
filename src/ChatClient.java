import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    private Socket server;
    private String nick;

    // instantiates client object with their address and port number and connect to the server
    public ChatClient(String address, int port) {
        try {
            server = new Socket(address,port);
        } catch (IOException e) {
            // if error occurs output error message
            System.out.println("Cannot connect right now, try later.");
        }
    }

    // returns the server socket details
    public Socket getServerSocket()
    {
        return server;
    }

    // thread to listen for output from the server (messages from other users to output on the client's console
    class serverListener implements Runnable {
        BufferedReader serverIn;

        {
            // setup buffer reader
            try {
                serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
            } catch (IOException e) {
                System.out.println("Error setting up buffer reader to server");
            }
        }

        public void run() {
            // infinite looping checking for output from server
            while (true) {
                try {
                    // if there is output output to the console
                    String serverRes = serverIn.readLine();
                    if (serverRes == null) {
                        System.exit(0);
                    }
                    System.out.println(serverRes);
                } catch (IOException e) {
                    if (!Thread.interrupted()) {
                        // if errors occur & thread hasn't been interrupted then output error message
                        System.out.println("Error with server output.");
                    }
                    // close thread
                    return;
                }
            }
        }
    }

    public void go() {
        try {
            boolean stayAlive = true;
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            // setup thread to listen for output from console & start
            Thread serverListenerThread = new Thread(new serverListener());
            serverListenerThread.start();
            // gets the user's personal nickname
            getNick();
            while (stayAlive) {
                // gets the user's message
                String userInput = userIn.readLine();
                // if the message is quit ...
                if (userInput.toUpperCase().equals("QUIT")) {
                    // state the specific user has quit the server
                    sendMessage(nick + " has quit the chat room");
                    stayAlive = false;
                    // send to server so it can close serverListenerThread thread
                    sendMessage("QUIT");
                }
                // otherwise send the user's message with their specific nickname
                else {
                    sendMessage(nick + ": " + userInput);
                }
            }
        } catch (IOException e) {
            // if error occurs output error message
            System.out.println("Error reading input.");
        }
        finally {
            try {
                // shut downs the client's connection and then exits the program
                System.out.print("Connection closing...");
                server.close();
                System.out.println("SUCCESS");
                System.out.println("Closing client program...");
                System.exit(0);
            } catch (IOException e) {
                // error message if error occurs
                System.out.println("Error with shutdown");
            }
        }
    }

    // method to get the user's nickname
    private void getNick() {
        // setup buffer reader
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter your nickname: ");
        try {
            // attempt to read user's message
            nick = br.readLine();
        } catch (IOException e) {
            // if error occurs output error and quit
            System.out.println("Error reading input, quitting...");
            System.exit(0);
        }
    }

    // method to send a message to the server
    public void sendMessage(String message) {
        try {
            // setup print writer to the server's details
            PrintWriter serverOut = new PrintWriter(server.getOutputStream(), true);
            // send the message using the print writer
            serverOut.println(message);
        } catch (IOException e) {
            // if error occurs output error message
            System.out.println("Error sending message to the server.");
        }

    }


    public static void main(String[] args) {
        // gets the arguments written by the user
        String[] arguments = new String[1];
        arguments = getArgs(args);

        // setup client object with the address and port given by user (or default)
        new ChatClient(arguments[0], Integer.parseInt(arguments[1])).go();
    }

    public static String[] getArgs(String[] args) {
        String[] arguments = new String[2];
        // setup default address to connect to server
        String address = "localhost";
        // setup default port to connect to server
        String port = "14001";
        // variables to store whether each argument has been written
        boolean isCca = false;
        boolean isCcp = false;
        // loops through each argument, if it is equal to one of the arguments allowed then is sets its corresponding boolean varibale to true
        for (int i = 0; i<args.length; i++) {
            if (args[i].equals ("-cca")) {
                isCca = true;
                isCcp = false;
            } else if (args[i].equals ("-ccp")) {
                isCcp = true;
                isCca = false;
                // if argument variable is true then the current argument must be the desginated argument so set it for port/address
            } else if (isCcp) {
                port = args[i];
            } else if (isCca) {
                address = args[i];
            }
        }
        arguments[0] = address;
        arguments[1] = port;
        return arguments;
    }

    // above code based on https://stackoverflow.com/questions/25121307/how-can-i-give-command-line-arguments-in-any-order


}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

public class ChatBot extends ChatClient {

    private Socket server;
    private String currentMessage;
    private boolean newMessage = false;

    // uses inherited object instantiation method
    public ChatBot(String address, int port) {
        super(address, port);
    }

    // thread to listen for messages from the server
    class serverListener implements Runnable {
        BufferedReader serverIn;
        {
            try {
                serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while (true) {
                try {
                    // update current message with the input from the server
                    currentMessage = serverIn.readLine();
                    // if the message is not null then set newMessage to true
                    if (currentMessage != null) { newMessage = true; }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void go() {
        // using parent class get the socket details for the bot
        server = super.getServerSocket();
        // setup thread to listen for output from server
        Thread serverListenerThread = new Thread(new serverListener());
        serverListenerThread.start();
        String botMessage;
        // infinite loop
        while (true) {
            // set bot message equal to the bot response to the message from the server
            botMessage = botResponse(currentMessage);
            // if the returned message is not null, and the server message is a new message
            if (botMessage != null && newMessage) {
                // send the message to the server
                super.sendMessage("Chatbot: " + botMessage);
                // output the message to the console so the admin can see what response was chosen
                System.out.println(botMessage);
                // set new message to false (as the message has now been responded to)
                newMessage = false;
            }
        }
    }

    // returns the message the bot should output when the user types a message
    private String botResponse(String message) {
        // hashmap to store the message that the bot will respond to and their response
        HashMap<String, String> responses = new HashMap<String, String>();
        // some default responses for the bot to respond to
        responses.put("hey", "Heya!");
        responses.put("favourite colour", "Black!");
        responses.put("how are you", "I'm doing great thank you, and yourself?");
        responses.put("where are you", "I'm running on a computer at the moment, my connection details are " + server);
        responses.put("what are you", "I'm a chatbot designed to run on a multithreaded Java server designed and coded for PoP2 coursework 1");

        // loops through each of the hashmap keys
        for (String i : responses.keySet()) {
            try {
                // if the user's message contains one of the keywords from the hashmap keys then
                if (message.contains(i)) {
                    // return the associated data with the key
                    return responses.get(i);
                }
            }
            catch (NullPointerException e) {
                continue;
            }

        }
        // if no responses match then return null as no message to be outputted
        return null;
    }

    // same code as in client class, see commenting there
    public static void main(String[] args) {
        String[] arguments = new String[1];
        arguments = getArgs(args);
        // instantiates chatbot object with the designated address and port numbers
        new ChatBot(arguments[0], Integer.parseInt(arguments[1])).go();
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerClientThread extends Thread {
    ChatServer server;
    Socket socket;
    BufferedReader clientIn;

    // instantiates a ServerClientThread object
    ServerClientThread(ChatServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public void run() {
        try {
            // setup input reader to read client's message
            this.clientIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            // read client's message
            String clientMessage = this.clientIn.readLine();
            // if the client's message isn't quit
            while (!this.socket.isInputShutdown() && !clientMessage.toUpperCase().equals("QUIT")) {
                // broadcast this message to all connected clients bar the one sending it
                this.server.broadcast(clientMessage, this.socket);
                // read the next message
                clientMessage = this.clientIn.readLine();
            }
        } catch (IOException e) {
            if (!Thread.interrupted()) {
                // if errors occur & thread hasn't been interrupted then output error message
                System.out.println("Error reading client input");
            }
        }
        finally {
            try {
                // remove socket from socket list
                this.server.removeClient(socket);
                // remove thread from thread list
                this.server.removeThread(this);
                // close connection & thread
                this.socket.close();
            }
            catch (IOException e) {
                System.out.println("Error closing client connection");
            }
        }
    }

    @Override
    public void interrupt() {
        try {
            this.socket.shutdownInput();
            //this.socket.close();
        } catch (IOException e) {
            System.out.println("Error with shutdown");
        }
    }
}

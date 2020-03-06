import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;

public class DoDClient extends ChatClient {


    private Socket server;
    private BufferedReader serverIn;
    private String userInput = "";

    public DoDClient(String address, int port) {
        super(address, port);
    }

    // thread to listen for messages from the server
    class DoDGame implements Runnable {
        public void run() {
            // variable to store whos turn it currently is
            boolean whosTurn = true;
            Map map = new Map();
            // stores the start location of the bot and player
            int[] startLocation = {0, 0};
            // instantiate a new instance of the humanPlayer class
            HumanPlayer player = new HumanPlayer(0, startLocation);
            // set the start location to result of function findPlayerLocation
            startLocation = player.findPlayerLocation(map);
            // set the players location to this value
            player.setlocation(startLocation);
            // update the game map to remove the P and replace it with a empty tile
            map.getMap()[startLocation[0]][startLocation[1]] = '.';

            // ditto code as the above section for bot
            startLocation = new int[]{0, 0};
            BotPlayer bot = new BotPlayer(0, startLocation);
            startLocation = bot.findBotLocation(map);
            bot.setlocation(startLocation);
            map.getMap()[startLocation[0]][startLocation[1]] = '.';

            // instantiate the game logic class
            GameLogic logic = new GameLogic(map, player, bot, server);
            String[] input = new String[1];
            while (true) {
                // if the players location and bot are the same
                if (Arrays.toString(player.getLocation()).equals(Arrays.toString(bot.getLocation()))) {
                    // output that they were caught and quit the game
                    sendMessage("You were caught by the bot!");
                    logic.quitGame();
                }
                // get the player's input
                input = getPlayerInput();
                // select the command that will be completed based upon this input and call it
                logic.playerCommandSelection(input);
                input = null;
                userInput = null;
            }
        }
    }

    public String[] getPlayerInput() {
        try {
            userInput = serverIn.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] splitInput = userInput.split(" ");
        splitInput[1] = splitInput[1].toLowerCase();
        return splitInput;
    }

    public void go() {
        server = super.getServerSocket();
        while (true) {
            // setup buffer reader
            try {
                serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
                // reads from the server
                String serverOut = serverIn.readLine();
                // if the player command is JOIN
                if (serverOut.toUpperCase().contains("JOIN")) {
                    // output message saying which player has joined
                    String[] splitInput = serverOut.split(" ");
                    sendMessage(splitInput[0] + " Joined the dungeon");
                    // create the DoD game
                    createGame();
                }
            } catch (IOException e) {
                System.out.println("Error reading server output");
            }

        }
    }

    private void createGame() {
        // create a new thread to deal with the DoD game being run
        Thread DoDGame = new Thread(new DoDGame());
        DoDGame.start();
    }

    // same code as in client class, see commenting there
    public static void main(String[] args) {
        String[] arguments = new String[1];
        arguments = getArgs(args);
        // instantiates DoDClient object with the designated address and port numbers
        new DoDClient(arguments[0], Integer.parseInt(arguments[1])).go();
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

/**
 * Contains the main logic part of the game, as it processes.
 *
 */
public class GameLogic {

    /** Stores the current game map that is being upon. */
    private Map map;
    /** Stores the current information of the player that is playing the game. */
    private HumanPlayer player;
    /** Stores the current information of the bot that is playing the game. */
    private BotPlayer bot;
    private Socket server;

    /**
     * Default constructor
     * @param map The map that is being played on
     * @param player The data that is held about the player
     * @param bot   The data that is held about the bot
     */
    public GameLogic(Map map, HumanPlayer player, BotPlayer bot, Socket server)
    {
        this.map = map;
        this.player = player;
        this.bot = bot;
        this.server = server;
    }


    /**
     * Returns the gold required to win.
     *
     * @return Gold required to win.
     */
    protected int hello() {
        return map.getGoldRequired();
    }

    /**
     * Returns the gold currently owned by the player.
     *
     * @return Gold currently owned.
     */
    protected int gold()
    {
        return player.getGold();
    }

    /**
     * Checks if movement is legal and updates player's/bot's location on the map.
     *
     * @param direction The direction of the movement.
     * @param playerType The type of player that is moving (i.e. whether it is a bot or player)
     * @return Protocol if success or not.
     */
    protected boolean move(char direction, char playerType) {
        int xCoord = 0;
        int yCoord = 0;
        // if the user to move is the player then get the player's coordinates and update the x & y coord variables
        if (playerType == 'P')
        {
            xCoord = player.getLocation()[0];
            yCoord = player.getLocation()[1];
        }
        // else if the user to move is the bot then get the bot's coordinates and update the x & y coord variables
        else if (playerType == 'B')
        {
            xCoord = bot.getLocation()[0];
            yCoord = bot.getLocation()[1];
        }
        // char to store what the contents of the map is at their next position
        char newPosition;
        // integer array to store the final coordinates of the move
        int[] finalPos = new int[2];
        // 2D char array to store the game map
        char[][] mapLevel = map.getMap();

        // depending upon the direction that is to be moved
        switch (direction) {
            case 'E':
                // gets the data of the tile that the player is about to move to
                newPosition = mapLevel[xCoord][yCoord + 1];
                // if the tile is a hash then return false
                if (newPosition == '#') {
                    return false;
                } else {
                    // otherwise update the finalPos variable with the coordinates of the move
                    finalPos[0] = xCoord;
                    finalPos[1] = yCoord + 1;
                    // if the user is the player then update the player's final position
                    if (playerType == 'P')
                    {
                        player.setlocation(finalPos);

                    }
                    // else if the user is the bot update the bot's final position
                    else if (playerType == 'B')
                    {
                        bot.setlocation(finalPos);
                    }
                    // return true as the move was successful
                    return true;
                }
                /** Ditto comment as the above case for the next 3 cases so will not comment due to repetiton. */
            case 'W':
                newPosition = mapLevel[xCoord][yCoord - 1];
                if (newPosition == '#') {
                    return false;
                } else {
                    finalPos[0] = xCoord;
                    finalPos[1] = yCoord - 1;
                    if (playerType == 'P')
                    {
                        player.setlocation(finalPos);

                    }
                    else if (playerType == 'B')
                    {
                        bot.setlocation(finalPos);
                    }
                    return true;
                }
            case 'N':
                newPosition = mapLevel[xCoord - 1][yCoord];
                if (newPosition == '#') {
                    return false;
                } else {
                    finalPos[0] = xCoord - 1;
                    finalPos[1] = yCoord;
                    if (playerType == 'P')
                    {
                        player.setlocation(finalPos);

                    }
                    else if (playerType == 'B')
                    {
                        bot.setlocation(finalPos);
                    }
                    return true;
                }
            case 'S':
                newPosition = mapLevel[xCoord + 1][yCoord];
                if (newPosition == '#') {
                    return false;
                } else {
                    finalPos[0] = xCoord + 1;
                    finalPos[1] = yCoord;
                    if (playerType == 'P')
                    {
                        player.setlocation(finalPos);

                    }
                    else if (playerType == 'B')
                    {
                        bot.setlocation(finalPos);
                    }
                    return true;
                }
                // if the direction is not N, S, E, W then output that the direction is invalid
            default:
                sendMessage("Invalid direction");
        }
        return false;
    }

    /**
     * look
     * Converts the map from a 2D char array to a single String that contains a 5x5 view of the board around the piece that initiated the move.
     *
     * @param  playerPiece The piece that initiated the LOOK command and therefore will be in the center of the 5x5 String.
     * @return A String representation of the game map.
     */
    protected String look(char playerPiece)
    {
        // variables that are used to store the bot's and player's location
        int[] userLocation;
        int[] botLocation;
        userLocation = player.getLocation();
        botLocation = bot.getLocation();
        // stores the game map as a 2D char array
        char[][] gameMap = map.getMap();
        // stores the piece that is currently at the location of the bot and player
        char botMapPiece = gameMap[botLocation[0]][botLocation[1]];
        char userMapPiece = gameMap[userLocation[0]][userLocation[1]];
        String usersMapView = "";
        int[] currentLocation = new int[2];
        // if the user is the player then set the currentLocation variable to the player's location
        if (playerPiece == 'P')
        {
            currentLocation = userLocation;
        }
        // else if the user is the bot then set the currentLocation variable to the bot's location
        else if (playerPiece == 'B')
        {
            currentLocation = botLocation;
        }
        // update the game map to include the P and B within the game map char array
        gameMap[userLocation[0]][userLocation[1]] = 'P';
        gameMap[botLocation[0]][botLocation[1]] = 'B';
        // create the 5x5 string around the user
        for (int i = currentLocation[0] - 2; i < currentLocation[0] + 3; i++)
        {
            for (int j = currentLocation[1] - 2; j < currentLocation[1] + 3; j++)
            {
                usersMapView += gameMap[i][j] + " ";
            }
            // after every 5 tiles add a new line
            usersMapView += "\n";
        }
        // update the game map to remove the P and B that were added in and replace with what was there previously
        gameMap[userLocation[0]][userLocation[1]] = userMapPiece;
        gameMap[botLocation[0]][botLocation[1]] = botMapPiece;
        // return the view around the user
        return usersMapView;
    }

    /**
     * Processes the player's pickup command, updating the map and the player's gold amount.
     * If the player cannot pickup then it prints a FAIL message to the console.
     *
     */
    protected void pickup()
    {
        // gets the players current location
        int[] playerLocation = player.getLocation();
        // gets the item that is on the tile that the player is currently standing on
        char itemOnMap = map.getMap()[playerLocation[0]][playerLocation[1]];
        // store the amount of gold that the player has
        int gold = player.getGold();
        // if the item is a piece of G then add 1 to gold, update the player's gold to this new value, remove the gold from the map and print a success message
        if (itemOnMap == 'G')
        {
         gold ++;
         player.setGold(gold);
         map.getMap()[playerLocation[0]][playerLocation[1]] = '.';
            sendMessage("SUCCESS. Gold owned: " + gold);
        }
        // otherwise print a fail message
        else
        {
            sendMessage("FAIL. Gold owned: " + gold);
        }
    }

    /**
     * Quits the game, shutting down the application.
     * If the player has the required amount of gold as dictated by the game map, and is on an EXIT ('E') tile then print a success message, stating they have won.
     * If the player quits and hasen't completed both the above requirements then a FAIL message will be printed.
     * The program will quit no matter whether the player has lost or won.
     *
     */
    protected void quitGame()
    {
        // gets the game map as a 2D char array
        char[][] gameMap = map.getMap();
        // gets the player's current location
        int[] currentLocation = player.getLocation();
        // if the player's current gold count is the same as the amount required by the map, and the tile the player is on is an exit tile
        if (player.getGold() == map.getGoldRequired() && gameMap[currentLocation[0]][currentLocation[1]] == 'E')
        {
            // print a win message
            sendMessage("WIN");
            sendMessage("Congratulations on winning Dungeons of Doom, you win a hard earned break.");
        }
        // otherwise print a lose message
        else
        {
            sendMessage("LOSE");

        }
        // exit the game
        System.exit(0);
    }

    /**
     * Calls the correct move function/procedure given the player's input move.
     * If the player's move is invalid then an error message is displayed.
     *
     * @param input String array storing the player's input that contains their move.
     */
    public void playerCommandSelection(String[] input)
    {
        switch (input[1])
        {
                // if the input is a move command
            case "move":
                // if the move as dictated by the player is invalid then print an error message stating they have made an invalid move
                if (!move(input[2].toUpperCase().charAt(0), 'P'))
                {
                    sendMessage("Invalid move, turn wasted!");
                }
                break;
                // if the input is a look command
            case "look":
                // output the 5x5 grid given from the look function
                sendMessage(look('P'));
                break;
                // if the input is a pickup command
            case "pickup":
                // call the pickup function
                pickup();
                break;
                // if the input is a quit command
            case "quit":
                // call the quitGame function
                quitGame();
                break;
                // if the input a gold command
            case "gold":
                // output the amount gold the player currently holds
                sendMessage("Gold owned: " + gold());
                break;
                // if the input is a hello command
            case "hello":
                // output the amount of gold the player requires to win
                sendMessage("Gold to win: " + hello());
                break;
                // if the input is none of these commands
            default:
                // output a invalid command message
                sendMessage("Invalid command");
                break;
        }
    }

    /**
     * Calls the next correct move for the bot.
     *
     * @param input String array that stores the bot's next move
     */
    public void botCommandSelection(String[] input)
    {
        switch (input[0])
        {
            // if the input is a move command
            case "move":
                boolean move;
                do
                {
                    move = true;
                    // if the move produced is invalid then repeat the move until a valid move is made
                    if (!move(input[1].charAt(0), 'B'))
                    {
                        bot.setMoveCounter(1);
                        input = bot.getNextAction();
                        move = false;
                    }
                } while (!move);
                break;
                // if the input is a look command
            case "look":
                // call the look command and get the returned output
                String botsView = look('B');
                // remove all line breaks
                botsView = botsView.replace("\n", "");
                // splits the bots view into a string array
                String[] botLookMap = botsView.split(" ");
                int playersPosition = 25;
                // loop through the bots view, if the player is within the view then break, making the playerPosition variable equal to the index within the array of the bots v iew
                for (int i = 0; i < botLookMap.length; i++) {
                    if (botLookMap[i].equals("P")) {
                        playersPosition = i;
                        break;
                    }
                }
                /**
                 * This short piece of code works out whether the player is within the bots view.
                 * If it is then it will move towards the player on its next move, otherwise it will move in a random direction.
                 */
                // if the players position is less than 9 i.e. above the bot then set the bots move counter to 2
                if (playersPosition <= 9)
                {
                    bot.setMoveCounter(2);
                }
                // else if the players position is 10 or 11 i.e. to the left of the bot then set the bots move counter to 3
                else if (playersPosition == 10 || playersPosition == 11)
                {
                    bot.setMoveCounter(3);
                }
                // else if the players position is 13 or 14 i.e. to the right of the bot then set the bots move counter to 4
                else if (playersPosition == 13 || playersPosition == 14)
                {
                    bot.setMoveCounter(4);
                }
                // else if the players position is more than 15 but less than 25 i.e. below the bot then set the bots move counter to 5
                else if (playersPosition >= 15 && playersPosition <= 24)
                {
                    bot.setMoveCounter(5);
                }
                // otherwise set the bots move counter to 1
                if (playersPosition == 25)
                {
                    bot.setMoveCounter(1);
                }
                break;
            // if the command is neither of these commands are called then a fatal error has occured so output error message
            default:
                sendMessage("FATAL ERROR");
                break;
        }
    }

    public void sendMessage(String message) {
        try {
            // setup print writer to the server's details
            PrintWriter serverOut = new PrintWriter(server.getOutputStream(), true);
            // send the message using the print writer
            // sends over two lines as the LOOK command formatting would mess up otherwise
            serverOut.println("DoDClient:");
            serverOut.println(message);
        } catch (IOException e) {
            // if error occurs output error message
            System.out.println("Error sending message to the server.");
        }
    }
}
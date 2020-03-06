import java.util.Random;

/**
 * Runs the game with a bot player and contains code needed to read the decide upon the bot's next move.
 *
 */
public class BotPlayer
{
    /** The current move that the bot is on within its move cycle. */
    private int moveCounter = 0;
    /** The location of the bot within the game map. */
    private int[] location = {0 , 0};

    /**
     * BotPlayer
     * Default constructor for the BotPlayer class.
     * @param moveCounter The current move that the bot is on within its cycle of moves
     * @param location The current location of the bot within the game map
     */
    public BotPlayer(int moveCounter, int[] location)
    {
        this.moveCounter = moveCounter;
        this.location = location;
    }

    /**
     * setLocation
     * Updates the bots location array with the new location that is given as a parameter.
     * @param location The location where the bot is now located.
     */
    public void setlocation(int[] location)
    {
        this.location[0] = location[0];
        this.location[1] = location[1];
    }

    /**
     * getLocation
     *
     * @return The location that the bot is currently at as coordinates.
     */
    public int[] getLocation()
    {
        return location;
    }

    /**
     * getMoveCounter
     *
     * @return The current move count that the bot is on within its move cycle.
     */
    public int getMoveCounter() { return moveCounter; }

    /**
     * setMoveCounter
     * Updates the bots move counter to a new value.
     * @param moveCounter The value that the move counter will be updated to.
     */
    public void setMoveCounter(int moveCounter)
    {
        this.moveCounter = moveCounter;
    }

    /**
     * getNextAction
     * Using the moveCounter variable it returns the move that the bot should complete.
     * Chooses between a LOOK action or a MOVE action, with the MOVE action being in a random direction or to a directed direction.
     *
     * @return The move that the bot will complete as a String array
     * The second index will be unused for a LOOK command, whereas for a MOVE command it will be used to specifiy the diorection.
     */
    public String[] getNextAction()
    {
        String[] move = new String[2];
        // if the moveCounter is 0 then return the LOOK action
        if (moveCounter == 0) {
            move[0] = "look";
            //move[1] = "";
            return move;
        }
        // if the moveCounter is 1 then return the MOVE action in a random direction, afterwards set the moveCoutner to 0
        else if (moveCounter == 1)
        {
            move[0] = "move";
            String[] direction = {"N", "S", "E", "W"};
            Random rand = new Random();
            int randomNum = rand.nextInt(4);
            move[1] = direction[randomNum];
            moveCounter = 0;
            return move;
        }
        // if the moveCounter = 2 then return the MOVE action in a NORTH direction, afterwards set the moveCounter to 0
        else if (moveCounter == 2)
        {
            move[0] = "move";
            move[1] = "N";
            moveCounter = 0;
            return move;
        }
        // if the moveCounter = 3 then return the MOVE action in a WEST direction, afterwards set the moveCounter to 0
        else if (moveCounter == 3)
        {
            move[0] = "move";
            move[1] = "W";
            moveCounter = 0;
            return move;
        }
        // if the moveCounter = 4 then return the MOVE action in a EAST direction, afterwards set the moveCounter to 0
        else if (moveCounter == 4)
        {
            move[0] = "move";
            move[1] = "E";
            moveCounter = 0;
            return move;
        }
        // if the moveCounter = 5 then return the MOVE action in a SOUTH direction, afterwards set the moveCounter to 0
        else if (moveCounter == 5)
        {
            move[0] = "move";
            move[1] = "S";
            moveCounter = 0;
            return move;
        }
        // if the move counter is none of the above values then return null
        return null;
    }

    /**
     * findBotLocation
     * Finds the bots location within the game map and returns the coordiantes that the bot is place located at.
     *
     * @param map The game map that is being used for the current game.
     * @return The coordinates of the bot within the map as an integer array.
     */
    public int[] findBotLocation(Map map)
    {
        // gets the game map
        char[][] gameMap = map.getMap();
        // loops through the game map and checks if each tile contains the bot
        for (int i = 2; i < gameMap.length - 2; i++)
        {
            for (int j = 2; j < gameMap[0].length - 2; j++)
            {
                if (gameMap[i][j] == 'B')
                {
                    // if it does then return the coordinates of the bot
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }


}

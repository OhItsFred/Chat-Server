import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Runs the game with a human player and contains code needed to read the user's inputs.
 *
 */
public class HumanPlayer {

    /** Total amount of gold the player has. */
    private int totalGold = 0;
    /** Current location of the player within the map, initalised to 0, 0 as a default value. */
    private int[] location = {0 , 0};

    /**
     * HumanPlayer
     * Default constructor for the HumanPlayer class
     *
     * @param totalGold Stores the total gold the the player current has in their inventory.
     * @param location Stores the player's current position within the level as an integer array.
     */
    public HumanPlayer(int totalGold, int[] location)
    {
        this.totalGold = totalGold;
        this.location[0] = location[0];
        this.location[1] = location[1];
    }

    /**
     * setGold
     * Updates the player's total amount of gold to the value specified by the parameter.
     * @param totalGold The amount of gold that the player's total gold should be updated to.
     */
    public void setGold(int totalGold)
    {
        this.totalGold = totalGold;
    }

    /**
     * getGold
     *
     * @return The amount of gold the player currently has as an integer.
     */
    public int getGold()
    {
        return totalGold;
    }

    /**
     * setLocation
     * Updates the player's location to a new coordinate.
     * @param location The location where the player is now located.
     */
    public void setlocation(int[] location)
    {
        this.location[0] = location[0];
        this.location[1] = location[1];
    }

    /**
     * getLocation
     *
     * @return The current location of the player within the game map as an integer array.
     */
    public int[] getLocation()
    {
        return location;
    }

    /**
     * findPlayerLocation
     * Finds the player's coordinates within the game map and returns.
     *
     * @param map The current game map that is being played upon.
     * @return The player's coordinate/position within the map as an integer array.
     */
    public int[] findPlayerLocation(Map map)
    {
        // gets the game map
        char[][] gameMap = map.getMap();
        // loops through the game map and checks if each tile contains the player
        for (int i = 2; i < gameMap.length - 2; i++)
        {
            for (int j = 2; j < gameMap[0].length - 2; j++)
            {
                if (gameMap[i][j] == 'P')
                {
                    // if it does then return the coordinates of the player
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
}
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Reads and contains in memory the map of the game.
 *
 */
public class Map {

	/* Representation of the map */
	private char[][] map;
	
	/* Map name */
	private String mapName;
	
	/* Gold required for the human player to win */
	private int goldRequired;
	
	/**
	 * Map
	 * Default constructor, creates the default map "Very small Labyrinth of doom".
	 */
	public Map() {
		mapName = "Very small Labyrinth of Doom";
		goldRequired = 2;
		map = new char[][]{
		{'#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#'},
		{'#','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','#'},
		{'#','.','.','.','.','.','.','G','.','.','.','.','.','.','.','.','.','E','.','#'},
		{'#','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','#'},
		{'#','.','.','E','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','#'},
		{'#','.','.','.','.','.','.','.','.','.','.','.','G','.','.','.','.','.','.','#'},
		{'#','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','#'},
		{'#','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','#'},
		{'#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#'}
		};
		mapBegin();
	}

	/**
	 * createMap
	 * Adds a additional border of hashes to the game map.
	 * This is done so when the user/bot completes a LOOK command next to the edge there will be no Array.IndexOf error message.
	 *
	 * @param gameMap : The current game map that is being changed to add a border of hashes to.
	 * @return New updated game map that is has a height and width 2 larger to store the new border of hashes.
	 */
	private char[][] createMap(char[][] gameMap)
	{
		// create a new game map that has an extra 2 rows and 2 columns
		char[][] newGameMap = new char[map.length + 2][map[0].length + 2];
		// change line 0 and last line to hashes
		for (int i = 0; i < newGameMap[0].length; i++)
		{
			newGameMap[0][i] = '#';
			newGameMap[newGameMap.length - 1][i] = '#';
		}
		// change column 0 and last column to hashse
		for (int i = 0; i < newGameMap.length; i++)
		{
			newGameMap[i][0] = '#';
			newGameMap[i][newGameMap[0].length - 1] = '#';
		}
		// copy old array into new array's unused area
		for (int i = 0; i < map.length; i++)
		{
			for (int j = 0; j < map[0].length; j++)
			{
				newGameMap[i+1][j+1] = map[i][j];
			}
		}
		// return the new game map
		return newGameMap;
	}

	/**
	 * generateRandomPosition
	 * Produces a set of random coordinates within the game map to place the bot and player in when the game is started.
	 *
	 * @return integer array storing the random position coordinates that were chosen.
	 */
	private int[] generateRandomPosition()
	{
		// get the maps width and height
		int xWidth = map.length;
		int yWidth = map[0].length;
		Random rand = new Random();
		int xCoord = 0;
		int yCoord = 0;
		// infinite loop
		while (true) {
			// randomly select a position within the map
			xCoord = rand.nextInt(xWidth);
			yCoord = rand.nextInt(yWidth);
			// if this position is a empty tile
			if (map[xCoord][yCoord] == '.') {
				// return the coordinates
				int[] positionCoords = {xCoord, yCoord};
				return positionCoords;
			}
			// otherwise loop back to the beginning within the infinite loop and keep attempting until a valid location si chosen
		}
	}

	/**
	 * Constructor that accepts a map to read in from.
	 *
	 * @param fileName The filename of the map file.
	 */
	public Map(String fileName)
	{
		readMap(fileName);
	}

    /**
	 * getGoldRequired
     * @return Gold required to exit the current map.
     */
    protected int getGoldRequired()
	{
        return goldRequired;
    }

    /**
	 * getMap
     * @return The map as stored in memory as a 2D char array.
     */
    protected char[][] getMap()
	{
        return map;
    }

    /**
	 * getMapName
     * @return The name of the current map as a String.
     */
    protected String getMapName()
	{
        return mapName;
    }


    /**
	 * readMap
     * Reads the map from a given file location.
	 * Updates the mapName, goldRequired and map with the data read from the map file
     *
     * @param fileName Name of the map's file.
     */
    public void readMap(String fileName){
    	// stores the directory that contains the maps
		String mapDirectory = "../src/maps/";

		try
		{
			FileReader fr = new FileReader(mapDirectory + fileName + ".txt");
			BufferedReader br = new BufferedReader(fr);
			// read the first line of the file
			String name = br.readLine();
			// remove 'name ' from the beginning of the file
			name = name.replace("name ", "");
			// update the mapName with the name read within the file
			mapName = name;
			// read the second line of the file
			String gold = br.readLine();
			// remove 'win ' from the beginning of the file
			gold = gold.replace("win ", "");
			// attempt to turn the value to an integer and update the goldRequired with this value
			try {
				goldRequired = Integer.parseInt(gold);
			}
			// if the value is not an integer then print an error message and quit
			catch (NumberFormatException e)
			{
				System.out.println("The map file has an invalid gold required number.");
				System.exit(0);
			}
			List<String> mapData = new ArrayList<String>();
			String mapLine = "";
			// while the line read is not null
			while ((mapLine = br.readLine()) != null)
			{
				// add the line the mapData string list
				mapData.add(mapLine);
			}
			// create a new map with the height of the mapData list size and the width of first index of the list
			map = new char[mapData.size()][mapData.get(0).length()];
			for (int i = 0; i < mapData.size(); i++)
			{
				// convert each line of the mapData list to a char array and make this equal to the map
				map[i] = mapData.get(i).toCharArray();
			}
			// close the buffered reader
			br.close();
		}
		catch (IOException e) {
			// if an error reading the map occurs then print an error message and exit
			System.out.println("Error reading map, please try again.");
			System.exit(0);
		}
		// call the mapBegin procedure
		mapBegin();
    }


	/**
	 * mapBegin
	 * Generates a random position within the current map for both the player and bot, and places them within the map.
	 */
	private void mapBegin() {
		// generate a random position and set the position to a P
		int[] startPos = generateRandomPosition();
		map[startPos[0]][startPos[1]] = 'P';
		// generate another random position and set the position to a B
		startPos = generateRandomPosition();
		map[startPos[0]][startPos[1]] = 'B';
		// call the createMap function with the current map as a argument
		map = createMap(map);
	}



}

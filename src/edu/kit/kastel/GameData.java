package edu.kit.kastel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class responsible for parsing command line arguments and storing
 * the game's configuration data, such as file paths, settings, and symbols.
 * @author uxuwg
 * @version 0.9
 */
public final class GameData {

    /**
     * The symbol representing a standard player unit on the board.
     */
    public static String playerUnitSymbol = " x ";

    /**
     * The symbol representing a standard enemy unit on the board.
     */
    public static String enemyUnitSymbol = " y ";

    /**
     * The symbol representing the player's Farmer King on the board.
     */
    public static String playerKingSymbol = " X ";

    /**
     * The symbol representing the enemy's Farmer King on the board.
     */
    public static String enemyKingSymbol = " Y ";

    /**
     * The random seed used for generating random numbers.
     */
    public static int seed;

    /**
     * The parsed theme string for the game board rendering.
     */
    public static String boardData;

    /**
     * The list of raw string data representing all available units.
     */
    public static List<String> unitData;

    /**
     * The list of raw string data representing the player's deck.
     */
    public static List<String> deck1Data;

    /**
     * The list of raw string data representing the enemy's deck.
     */
    public static List<String> deck2Data;

    /**
     * The verbosity level for the game output.
     */
    public static String verbosity;

    /**
     * The display name of the first team (default: "Player").
     */
    public static String team1Name = "Player";

    /**
     * The display name of the second team (default: "Enemy").
     */
    public static String team2Name = "Enemy";

    private static final String REGEX_EQUALS = "=";
    private static final String ERROR_IO_EXCEPTION = "ERROR: Something went wrong in IO!";
    private static Map<String, String> argInfo = null;

    private GameData() {
    }

    /**
     * Parses the array of command line arguments and populates the configuration fields.
     * @param args the command line arguments passed to the application
     * @return true if all mandatory arguments are present and valid, false otherwise
     */
    public static boolean extractArgumentInfo(String[] args) {
        argInfo = new HashMap<>();
        for (String arg : args) {
            String[] keyValue = arg.split(REGEX_EQUALS);
            if (keyValue.length == 2) {
                argInfo.put(keyValue[0], keyValue[1]);
            }
        }

        if (!containsMandatoryKeys(argInfo)) {
            System.err.println("ERROR: Mandatory arguments missing or invalid!");
            return false;
        }

        for (String key : argInfo.keySet()) {
            parseKeyValue(key);
        }
        return true;
    }

    private static void parseKeyValue(String key) {
        switch (key) {
            case "seed":
                seed = Integer.parseInt(argInfo.get(key));
                break;
            case "board":
                boardData = extractBoardKeySet(argInfo.get(key));
                break;
            case "units":
                unitData = extractFilePath(argInfo.get(key));
                break;
            case "deck":
                deck1Data = extractFilePath(argInfo.get(key));
                deck2Data = extractFilePath(argInfo.get(key));
                break;
            case "deck1":
                deck1Data = extractFilePath(argInfo.get(key));
                break;
            case "deck2":
                deck2Data = extractFilePath(argInfo.get(key));
                break;
            case "team1":
                team1Name = argInfo.get(key);
                break;
            case "team2":
                team2Name = argInfo.get(key);
                break;
            case "verbosity":
                verbosity = argInfo.get(key);
                break;
            default:
                System.err.println("Repeat");
        }
    }

    private static String extractBoardKeySet(String filePath) {
        String keySet = null;
        try {
            keySet = Files.readString(Path.of(filePath));
        } catch (IOException e) {
            System.err.println(ERROR_IO_EXCEPTION);
        }
        return keySet;
    }

    private static List<String> extractFilePath(String filePath) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Path.of(filePath));
        } catch (IOException e) {
            System.err.println(ERROR_IO_EXCEPTION);
        }
        return lines;
    }

    private static boolean containsMandatoryKeys(Map<String, String> argInfo) {
        if (!argInfo.containsKey("seed") || !argInfo.containsKey("units")) {
            return false;
        }

        boolean hasDeck = argInfo.containsKey("deck");
        boolean hasDeck1And2 = argInfo.containsKey("deck1") && argInfo.containsKey("deck2");

        if (hasDeck && hasDeck1And2) {
            return false;
        }
        if (!hasDeck && !hasDeck1And2) {
            return false;
        }

        if (!argInfo.containsKey("verbosity")) {
            verbosity = "all";
        }

        return true;
    }
}

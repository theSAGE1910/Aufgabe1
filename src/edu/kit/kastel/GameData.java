package edu.kit.kastel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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
    public static String team1Name = DEFAULT_PLAYER;

    /**
     * The display name of the second team (default: "Enemy").
     */
    public static String team2Name = DEFAULT_ENEMY;

    private static final String SEED = "seed";
    private static final String TEAM_1 = "team1";
    private static final String TEAM_2 = "team2";
    private static final String VERBOSITY = "verbosity";
    private static final String UNITS = "units";
    private static final String DECK = "deck";
    private static final String DECK_1 = "deck1";
    private static final String DECK_2 = "deck2";
    private static final String COMPACT = "compact";
    private static final String DEFAULT_PLAYER = "Player";
    private static final String DEFAULT_ENEMY = "Enemy";

    private static final String ERROR_UNIT_FILE_IS_EMPTY = "ERROR: Unit file is empty.";
    private static final String ERROR_INVALID_UNIT_FORMAT_DETECTED = "ERROR: Invalid unit format detected.";
    private static final String ERROR_DUPLICATE_ARGUMENT_PROVIDED = "ERROR: Duplicate argument provided.";
    private static final String ERROR_INVALID_ARGUMENT_FORMAT = "ERROR: Invalid argument format: ";
    private static final String ERROR_UNKNOWN_PARAMETER_PROVIDED = "ERROR: Unknown parameter provided: ";
    private static final String ERROR_BOARD_FILE_IS_EMPTY = "ERROR: Board file is empty.";

    private static final String ERROR_MANDATORY_ARGUMENTS_MISSING_OR_INVALID
            = "ERROR: Mandatory arguments missing or invalid!";
    private static final String ERROR_INVALID_VERBOSITY_VALID_OPTIONS_ARE_ALL_OR_COMPACT
            = "ERROR: Invalid verbosity! Valid options are 'all' or 'compact'.";
    private static final String ERROR_TOO_MANY_UNITS_DEFINED_A_MAXIMUM_OF_40_UNIT_TYPES_IS_ALLOWED
            = "ERROR: Too many units defined! A maximum of 40 unit types is allowed.";
    private static final String ERROR_TEAM_NAMES_MUST_BE_AT_MOST_14_CHARACTERS_LONG
            = "ERROR: Team names must be at most 14 characters long.";
    private static final String ERROR_BOARD_KEY_SET_MUST_BE_EXACTLY_29_CHARACTERS_LONG
            = "ERROR: Board key set must be exactly 29 characters long.";

    private static final String UNIT_DATA_REGEX = "^([^;]+);([^;]+);([0-9]+);([0-9]+)$";
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
                if (argInfo.containsKey(keyValue[0])) {
                    System.err.println(ERROR_DUPLICATE_ARGUMENT_PROVIDED);
                    return false;
                }
                argInfo.put(keyValue[0], keyValue[1]);
            } else {
                System.err.println(ERROR_INVALID_ARGUMENT_FORMAT + arg);
                return false;
            }
        }

        List<String> validKeys = Arrays.asList(SEED, TEAM_1, TEAM_2, VERBOSITY, GameMessages.BOARD, UNITS, DECK, DECK_1, DECK_2);
        for (String key : argInfo.keySet()) {
            if (!validKeys.contains(key)) {
                System.err.println(ERROR_UNKNOWN_PARAMETER_PROVIDED + key);
                return false;
            }
        }

        if (!containsMandatoryKeys(argInfo)) {
            System.err.println(ERROR_MANDATORY_ARGUMENTS_MISSING_OR_INVALID);
            return false;
        }

        String[] orderedKeys = {SEED, GameMessages.BOARD, UNITS, DECK, DECK_1, DECK_2, TEAM_1, TEAM_2, VERBOSITY};
        for (String key : orderedKeys) {
            if (argInfo.containsKey(key)) {
                if (!parseKeyValue(key)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean parseKeyValue(String key) {
        switch (key) {
            case SEED:
                seed = Integer.parseInt(argInfo.get(key));
                break;
            case GameMessages.BOARD:
                if (handleBoardData(key)) {
                    return false;
                }
                break;
            case UNITS:
                unitData = extractFilePath(argInfo.get(key));
                if (handleUnitData()) {
                    return false;
                }
                break;
            case DECK:
                List<String> deckData = extractFilePath(argInfo.get(key));
                if (printData(deckData)) {
                    return false;
                }
                deck1Data = deckData;
                deck2Data = deckData;
                break;
            case DECK_1:
                deck1Data = extractFilePath(argInfo.get(key));
                if (printData(deck1Data)) {
                    return false;
                }
                break;
            case DECK_2:
                deck2Data = extractFilePath(argInfo.get(key));
                if (printData(deck2Data)) {
                    return false;
                }
                break;
            case TEAM_1:
                team1Name = argInfo.get(key);
                if (checkTeamNameLength(team1Name)) {
                    return false;
                }
                break;
            case TEAM_2:
                team2Name = argInfo.get(key);
                if (checkTeamNameLength(team2Name)) {
                    return false;
                }
                break;
            case VERBOSITY:
                verbosity = argInfo.get(key);
                if (!verbosity.equals(GameMessages.ALL) && !verbosity.equals(COMPACT)) {
                    System.err.println(ERROR_INVALID_VERBOSITY_VALID_OPTIONS_ARE_ALL_OR_COMPACT);
                    return false;
                }
                break;
            default:
                break;
        }
        return true;
    }

    private static boolean handleUnitData() {

        if (unitData == null) {
            return true;
        }

        if (unitData.isEmpty()) {
            System.err.println(ERROR_UNIT_FILE_IS_EMPTY);
            return true;
        }

        if (printData(unitData)) {
            return false;
        } else if (unitData.size() > 80) {
            System.err.println(ERROR_TOO_MANY_UNITS_DEFINED_A_MAXIMUM_OF_40_UNIT_TYPES_IS_ALLOWED);
            return true;
        }

        for (String line : unitData) {
            if (!line.matches(UNIT_DATA_REGEX)) {
                System.err.println(ERROR_INVALID_UNIT_FORMAT_DETECTED);
                return true;
            }
        }

        return false;
    }

    private static boolean checkTeamNameLength(String teamName) {
        if (teamName.length() > 15) {
            System.err.println(ERROR_TEAM_NAMES_MUST_BE_AT_MOST_14_CHARACTERS_LONG);
            return true;
        }
        return false;
    }

    private static boolean handleBoardData(String key) {
        boardData = extractBoardKeySet(argInfo.get(key));
        if (boardData == null) {
            return true;
        }
        System.out.println(boardData);
        if (boardData.length() != BoardTheme.MIN_KEYSET_LENGTH) {
            System.err.println(ERROR_BOARD_KEY_SET_MUST_BE_EXACTLY_29_CHARACTERS_LONG);
            return true;
        }
        return false;
    }

    private static boolean printData(List<String> data) {
        if (data == null) {
            return true;
        }
        for (String line : data) {
            System.out.println(line);
        }
        return false;
    }

    private static String extractBoardKeySet(String filePath) {

        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));
            if (lines.isEmpty()) {
                System.err.println(ERROR_BOARD_FILE_IS_EMPTY);
                return null;
            }
            return lines.get(0);
        } catch (IOException e) {
            System.err.println(ERROR_IO_EXCEPTION);
            return null;
        }
    }

    private static List<String> extractFilePath(String filePath) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(filePath));
        } catch (IOException e) {
            System.err.println(ERROR_IO_EXCEPTION);
            return null;
        }
        return lines;
    }

    private static boolean containsMandatoryKeys(Map<String, String> argInfo) {
        if (!argInfo.containsKey(SEED) || !argInfo.containsKey(UNITS)) {
            return false;
        }

        boolean hasDeck = argInfo.containsKey(DECK);
        boolean hasDeck1And2 = argInfo.containsKey(DECK_1) && argInfo.containsKey(DECK_2);

        if (hasDeck && hasDeck1And2) {
            return false;
        }
        if (!hasDeck && !hasDeck1And2) {
            return false;
        }

        if (!argInfo.containsKey(VERBOSITY)) {
            verbosity = GameMessages.ALL;
        }

        return true;
    }
}

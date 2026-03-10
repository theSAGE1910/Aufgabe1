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

    private static final int MAX_UNITS = 80;
    private static final int INITIAL_SEED = 0;
    private static final int EXPECTED_ARG_LENGTH = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int FIRST_ELEMENT_INDEX = 0;

    private static int seed = INITIAL_SEED;
    private static String boardData = null;
    private static List<String> unitData = null;
    private static List<String> deck1Data = null;
    private static List<String> deck2Data = null;
    private static String verbosity = null;

    private static final String DEFAULT_PLAYER = "Player";
    private static final String DEFAULT_ENEMY = "Enemy";

    private static String team1Name = DEFAULT_PLAYER;

    private static String team2Name = DEFAULT_ENEMY;

    private static final String SEED = "seed";
    private static final String TEAM_1 = "team1";
    private static final String TEAM_2 = "team2";
    private static final String VERBOSITY = "verbosity";
    private static final String UNITS = "units";
    private static final String DECK = "deck";
    private static final String DECK_1 = "deck1";
    private static final String DECK_2 = "deck2";
    private static final String COMPACT = "compact";

    private static final int MAX_TEAM_NAME_CHAR = 15;

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
    private static final String ERROR_TOO_MANY_UNITS_DEFINED_A_MAXIMUM_OF_80_UNIT_TYPES_IS_ALLOWED
            = "ERROR: Too many units defined! A maximum of 80 unit types is allowed.";
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
        boolean success = parseArguments(args);

        if (success) {
            success = validateArgKeys();
        }

        if (success) {
            if (!containsMandatoryKeys(argInfo)) {
                System.err.println(ERROR_MANDATORY_ARGUMENTS_MISSING_OR_INVALID);
                success = false;
            }
        }

        if (success) {
            success = processOrderedKeys();
        }

        return success;
    }

    private static boolean processOrderedKeys() {
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

    private static boolean validateArgKeys() {
        List<String> validKeys = Arrays.asList(SEED, TEAM_1, TEAM_2, VERBOSITY, GameMessages.BOARD, UNITS, DECK, DECK_1, DECK_2);
        for (String key : argInfo.keySet()) {
            if (!validKeys.contains(key)) {
                System.err.println(ERROR_UNKNOWN_PARAMETER_PROVIDED + key);
                return false;
            }
        }
        return true;
    }

    private static boolean parseArguments(String[] args) {
        for (String arg : args) {
            String[] keyValue = arg.split(REGEX_EQUALS);
            if (keyValue.length == EXPECTED_ARG_LENGTH) {
                if (argInfo.containsKey(keyValue[KEY_INDEX])) {
                    System.err.println(ERROR_DUPLICATE_ARGUMENT_PROVIDED);
                    return false;
                }
                argInfo.put(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
            } else {
                System.err.println(ERROR_INVALID_ARGUMENT_FORMAT + arg);
                return false;
            }
        }
        return true;
    }

    private static boolean parseKeyValue(String key) {
        switch (key) {
            case SEED, VERBOSITY -> {
                return parseSettings(key);
            }
            case TEAM_1, TEAM_2 -> {
                return parseTeams(key);
            }
            case DECK, DECK_1, DECK_2 -> {
                return parseDecks(key);
            }
            case GameMessages.BOARD, UNITS -> {
                return parseGameAssets(key);
            }
            default -> {
                System.err.println(ERROR_UNKNOWN_PARAMETER_PROVIDED + key);
                return false;
            }
        }
    }

    private static boolean parseSettings(String key) {
        if (key.equals(SEED)) {
            seed = Integer.parseInt(argInfo.get(key));
        } else if (key.equals(VERBOSITY)) {
            verbosity = argInfo.get(key);
            if (!getVerbosity().equals(GameMessages.ALL) && !getVerbosity().equals(COMPACT)) {
                System.err.println(ERROR_INVALID_VERBOSITY_VALID_OPTIONS_ARE_ALL_OR_COMPACT);
                return false;
            }
        }
        return true;
    }

    private static boolean parseTeams(String key) {
        if (key.equals(TEAM_1)) {
            team1Name = argInfo.get(key);
            return checkTeamNameLength(getTeam1Name());
        } else if (key.equals(TEAM_2)) {
            team2Name = argInfo.get(key);
            return checkTeamNameLength(getTeam2Name());
        }
        return false;
    }

    private static boolean parseDecks(String key) {
        List<String> deckData = extractFilePath(argInfo.get(key));
        if (printData(deckData)) {
            return false;
        }

        if (key.equals(DECK)) {
            deck1Data = deckData;
            deck2Data = deckData;
        } else if (key.equals(DECK_1)) {
            deck1Data = deckData;
        } else if (key.equals(DECK_2)) {
            deck2Data = deckData;
        }
        return true;
    }

    private static boolean parseGameAssets(String key) {
        if (key.equals(GameMessages.BOARD)) {
            return !handleBoardData(key);
        } else if (key.equals(UNITS)) {
            unitData = extractFilePath(argInfo.get(key));
            return !handleUnitData();
        }
        return true;
    }

    private static boolean handleUnitData() {
        boolean hasError = false;

        if (getUnitData() == null) {
            hasError = true;
        } else if (getUnitData().isEmpty()) {
            System.err.println(ERROR_UNIT_FILE_IS_EMPTY);
            hasError = true;
        } else if (!printData(getUnitData())) {
            if (getUnitData().size() > MAX_UNITS) {
                System.err.println(ERROR_TOO_MANY_UNITS_DEFINED_A_MAXIMUM_OF_80_UNIT_TYPES_IS_ALLOWED);
                hasError = true;
            } else {
                for (String line : getUnitData()) {
                    if (!line.matches(UNIT_DATA_REGEX)) {
                        System.err.println(ERROR_INVALID_UNIT_FORMAT_DETECTED);
                        hasError = true;
                        break;
                    }
                }
            }
        }
        return hasError;
    }

    private static boolean checkTeamNameLength(String teamName) {
        if (teamName.length() > MAX_TEAM_NAME_CHAR) {
            System.err.println(ERROR_TEAM_NAMES_MUST_BE_AT_MOST_14_CHARACTERS_LONG);
            return false;
        }
        return true;
    }

    private static boolean handleBoardData(String key) {
        boardData = extractBoardKeySet(argInfo.get(key));
        if (getBoardData() == null) {
            return true;
        }
        System.out.println(getBoardData());
        if (getBoardData().length() != BoardTheme.MIN_KEYSET_LENGTH) {
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
            return lines.get(FIRST_ELEMENT_INDEX);
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

    /**
     * The random seed used for generating random numbers.
     * @return the integer seed value
     */
    public static int getSeed() {
        return seed;
    }

    /**
     * The parsed theme string for the game board rendering.
     * @return the board key set string
     */
    public static String getBoardData() {
        return boardData;
    }

    /**
     * The list of raw string data representing all available units.
     * @return the list of strings for the unit data
     */
    public static List<String> getUnitData() {
        return unitData;
    }

    /**
     * The list of raw string data representing the player's deck.
     * @return the list of strings for the player's deck
     */
    public static List<String> getDeck1Data() {
        return deck1Data;
    }

    /**
     * The list of raw string data representing the enemy's deck.
     * @return the list of strings for the enemy deck
     */
    public static List<String> getDeck2Data() {
        return deck2Data;
    }

    /**
     * The verbosity level for the game output.
     * @return the verbosity level, either "all" or "compact"
     */
    public static String getVerbosity() {
        return verbosity;
    }

    /**
     * The display name of the first team (default: "Player").
     * @return the name of the first team
     */
    public static String getTeam1Name() {
        return team1Name;
    }

    /**
     * The display name of the second team (default: "Enemy").
     * @return the name of the second team
     */
    public static String getTeam2Name() {
        return team2Name;
    }
}
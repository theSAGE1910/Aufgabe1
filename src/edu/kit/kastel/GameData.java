package edu.kit.kastel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
    private static String verbosity = null;

    private static String[] unitData = null;
    private static String[] deck1Data = null;
    private static String[] deck2Data = null;


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

    private GameData() {
    }

    /**
     * Parses the array of command line arguments and populates the configuration fields.
     * @param args the command line arguments passed to the application
     * @return true if all mandatory arguments are present and valid, false otherwise
     */
    public static boolean extractArgumentInfo(String[] args) {
        Map<String, String> argInfo = new HashMap<>();

        if (!parseArguments(args, argInfo)) {
            return false;
        }

        boolean hasDeck = argInfo.containsKey(DECK);
        boolean hasDeck1And2 = argInfo.containsKey(DECK_1) && argInfo.containsKey(DECK_2);

        if (!argInfo.containsKey(SEED) || !argInfo.containsKey(UNITS) || hasDeck == hasDeck1And2) {
            System.err.println(ERROR_MANDATORY_ARGUMENTS_MISSING_OR_INVALID);
            return false;
        }

        if (!argInfo.containsKey(VERBOSITY)) {
            verbosity = GameMessages.ALL;
        }

        String[] orderedKeys = {SEED, GameMessages.BOARD, UNITS, DECK, DECK_1, DECK_2, TEAM_1, TEAM_2, VERBOSITY};
        for (String key : orderedKeys) {
            if (argInfo.containsKey(key)) {
                if (!parseKeyValue(key, argInfo.get(key))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean parseArguments(String[] args, Map<String, String> argInfo) {
        List<String> validKeys = Arrays.asList(SEED, TEAM_1, TEAM_2, VERBOSITY, GameMessages.BOARD, UNITS, DECK, DECK_1, DECK_2);
        for (String arg : args) {
            String[] keyValue = arg.split(REGEX_EQUALS);
            if (keyValue.length == EXPECTED_ARG_LENGTH) {
                String key = keyValue[KEY_INDEX];
                if (!validKeys.contains(key)) {
                    System.err.println(ERROR_UNKNOWN_PARAMETER_PROVIDED + key);
                    return false;
                }
                if (argInfo.containsKey(key)) {
                    System.err.println(ERROR_DUPLICATE_ARGUMENT_PROVIDED);
                    return false;
                }
                argInfo.put(key, keyValue[VALUE_INDEX]);
            } else {
                System.err.println(ERROR_INVALID_ARGUMENT_FORMAT + arg);
                return false;
            }
        }
        return true;
    }

    private static boolean parseKeyValue(String key, String value) {
        switch (key) {
            case SEED, VERBOSITY, TEAM_1, TEAM_2 -> {
                return parseConfig(key, value);
            }
            default -> {
                return (key.equals(GameMessages.BOARD) || key.equals(UNITS) || key.equals(DECK)
                        || key.equals(DECK_1) || key.equals(DECK_2)) && parseAssets(key, value);
            }
        }
    }

    private static boolean parseConfig(String key, String value) {
        if (key.equals(SEED)) {
            seed = Integer.parseInt(value);
        } else if (key.equals(VERBOSITY)) {
            verbosity = value;
            if (!verbosity.equals(GameMessages.ALL) && !verbosity.equals(COMPACT)) {
                System.err.println(ERROR_INVALID_VERBOSITY_VALID_OPTIONS_ARE_ALL_OR_COMPACT);
                return false;
            }
        } else {
            if (value.length() > MAX_TEAM_NAME_CHAR) {
                System.err.println(ERROR_TEAM_NAMES_MUST_BE_AT_MOST_14_CHARACTERS_LONG);
                return false;
            }
            if (key.equals(TEAM_1)) {
                team1Name = value;
            } else {
                team2Name = value;
            }
        }
        return true;
    }

    private static boolean parseAssets(String key, String value) {
        String[] lines = readFileLines(value);
        if (key.equals(UNITS)) {
            unitData = lines;
            return !handleUnitData();
        }

        if (lines == null) {
            return false;
        }

        if (key.equals(GameMessages.BOARD)) {
            if (lines.length == 0) {
                System.err.println(ERROR_BOARD_FILE_IS_EMPTY);
                return false;
            }

            boardData = lines[FIRST_ELEMENT_INDEX];
            System.out.println(boardData);
            if (boardData.length() != BoardTheme.MIN_KEYSET_LENGTH) {
                System.err.println(ERROR_BOARD_KEY_SET_MUST_BE_EXACTLY_29_CHARACTERS_LONG);
                return false;
            }
        } else {
            for (String line : lines) {
                System.out.println(line);
            }
            if (key.equals(DECK)) {
                deck1Data = lines;
                deck2Data = lines;
            } else if (key.equals(DECK_1)) {
                deck1Data = lines;
            } else {
                deck2Data = lines;
            }
        }
        return true;
    }

    private static boolean handleUnitData() {
        if (unitData == null) {
            return true;
        }
        if (unitData.length == 0) {
            System.err.println(ERROR_UNIT_FILE_IS_EMPTY);
            return true;
        }
        for (String line : unitData) {
            System.out.println(line);
        }
        if (unitData.length > MAX_UNITS) {
            System.err.println(ERROR_TOO_MANY_UNITS_DEFINED_A_MAXIMUM_OF_80_UNIT_TYPES_IS_ALLOWED);
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

    private static String[] readFileLines(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));
            return lines.toArray(new String[0]);
        } catch (IOException e) {
            System.err.println(ERROR_IO_EXCEPTION);
            return null;
        }
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
        return unitData == null
                ? new ArrayList<>() : new ArrayList<>(Arrays.asList(unitData));
    }

    /**
     * The list of raw string data representing the player's deck.
     * @return the list of strings for the player's deck
     */
    public static List<String> getDeck1Data() {
        return deck1Data == null
                ? new ArrayList<>() : new ArrayList<>(Arrays.asList(deck1Data));
    }

    /**
     * The list of raw string data representing the enemy's deck.
     * @return the list of strings for the enemy deck
     */
    public static List<String> getDeck2Data() {
        return deck2Data == null
                ? new ArrayList<>() : new ArrayList<>(Arrays.asList(deck2Data));
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
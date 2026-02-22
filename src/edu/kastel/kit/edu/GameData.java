package edu.kastel.kit.edu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameData {

    static String[] path = "seed=-4022738 deck=input/decks/default.txt verbosity=compact units=input/units/default.txt".split(" ");

    private static final String ERROR_IO_EXCEPTION = "ERROR: Something went wrong in IO!";
    public static final String REGEX_EQUALS = "=";

    static List<String> mandatoryArgs = Arrays.asList("seed", "deck", "verbosity", "units");

    public static String playerUnitSymbol = " x ";
    public static String enemyUnitSymbol = " y ";
    public static String playerKingSymbol = " X ";
    public static String enemyKingSymbol = " Y ";

    static int seed;
    static String boardData;
    static List<String> unitData;
    static List<String> deck1Data;
    static List<String> deck2Data;
    public static String verbosity;
    public static String team1Name;
    public static String team2Name;

    static Map<String, String> argInfo;

    public static void parseKeyValue(String key) {
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

    public static String extractBoardKeySet(String filePath) {
        String keySet = null;
        try {
            keySet = Files.readString(Path.of(filePath));
        } catch (IOException e) {
            System.err.println(ERROR_IO_EXCEPTION);
        }
        return keySet;
    }

    public static List<String> extractFilePath(String filePath) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Path.of(filePath));
        } catch (IOException e) {
            System.err.println(ERROR_IO_EXCEPTION);
        }
        return lines;
    }

    static boolean containsMandatoryKeys(Map<String, String> argInfo) {
        if (!argInfo.containsKey("seed") || !argInfo.containsKey("units") || !argInfo.containsKey("deck2")) {
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

//        public static void main(String[] args) {
//        extractArgumentInfo(path);
//        System.out.println(seed);
//        System.out.println(deckFilePath);
//        System.out.println(unitFilePath);
//        System.out.println(verbosity);
//    }

}

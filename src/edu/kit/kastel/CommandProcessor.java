package edu.kit.kastel;

import edu.kit.kastel.commands.Command;
import edu.kit.kastel.commands.BlockCommand;
import edu.kit.kastel.commands.BoardCommand;
import edu.kit.kastel.commands.FlipCommand;
import edu.kit.kastel.commands.HandCommand;
import edu.kit.kastel.commands.MoveCommand;
import edu.kit.kastel.commands.PlaceCommand;
import edu.kit.kastel.commands.QuitCommand;
import edu.kit.kastel.commands.SelectCommand;
import edu.kit.kastel.commands.ShowCommand;
import edu.kit.kastel.commands.StateCommand;
import edu.kit.kastel.commands.YieldCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for processing user input commands and executing the corresponding actions in the game.
 * @author uxuwg
 * @version 0.9
 */
public final class CommandProcessor {

    private static final String REGEX_SPACE = " ";
    private static final Map<String, Command> COMMANDS = new HashMap<>();
    private static final String ERROR_NO_CARD_INDEX_PROVIDED = "ERROR: No card index provided.";
    private static final String ERROR_INVALID_COMMAND = "ERROR: Invalid command";
    private static final String ERROR_INVALID_COMMAND_FORMAT = "ERROR: Invalid command format";
    private static final String ERROR_NO_COMMAND_PROVIDED = "ERROR: No command provided";

    private static final String SELECT = "select";
    private static final String MOVE = "move";
    private static final String FLIP = "flip";
    private static final String BLOCK = "block";
    private static final String HAND = "hand";
    private static final String PLACE = "place";
    private static final String YIELD = "yield";
    private static final String STATE = "state";
    private static final String QUIT = "quit";

    private static final int INDEX_ZERO = 0;
    private static final int INDEX_ONE = 1;
    private static final int INDEX_TWO = 2;
    private static final int INVALID_INDEX = -1;

    private CommandProcessor() {
    }

    /**
     * Initializes the command processor by populating the command map with available commands and their corresponding handlers.
     */
    public static void initialise() {
        if (COMMANDS.isEmpty()) {
            COMMANDS.put(SELECT, new SelectCommand());
            COMMANDS.put(GameMessages.BOARD, new BoardCommand());
            COMMANDS.put(MOVE, new MoveCommand());
            COMMANDS.put(FLIP, new FlipCommand());
            COMMANDS.put(BLOCK, new BlockCommand());
            COMMANDS.put(HAND, new HandCommand());
            COMMANDS.put(PLACE, new PlaceCommand());
            COMMANDS.put(GameMessages.SHOW, new ShowCommand());
            COMMANDS.put(YIELD, new YieldCommand());
            COMMANDS.put(STATE, new StateCommand());
            COMMANDS.put(QUIT, new QuitCommand());
        }
    }

    /**
     * Parses the user input, identifies the command and its argument (if any), and executes the corresponding action.
     * @param input the raw string input from the user
     */
    public static void processCommands(String input) {
        String[] words = input.split(REGEX_SPACE);
        if (words.length == INDEX_ZERO) {
            System.err.println(ERROR_NO_COMMAND_PROVIDED);
            return;
        }

        String key = words[INDEX_ZERO].toLowerCase();
        String argument = null;

        if (words.length == INDEX_TWO) {
            argument = words[INDEX_ONE].toUpperCase().trim();
        } else if (words.length > INDEX_TWO) {
            if (key.equalsIgnoreCase(PLACE)) {
                argument = input.trim().substring(key.length()).toUpperCase();
            } else {
                System.err.println(ERROR_INVALID_COMMAND_FORMAT);
                return;
            }
        }

        Command command = COMMANDS.get(key);
        if (command != null) {
            command.execute(argument);
        } else {
            System.err.println(ERROR_INVALID_COMMAND);
        }
    }

    /**
     * Parses the provided string argument to determine the zero-based index of the card to discard from the current hand.
     * @param argument the string argument representing the card index provided by the user
     * @param currentHand the current hand of the active team, used to validate the provided index
     * @return the zero-based index of the card to discard if the argument is valid, or -1 if the argument is invalid
     */
    public static int parseHandIndex(String argument, Hand currentHand) {
        if (argument == null) {
            System.err.println(ERROR_NO_CARD_INDEX_PROVIDED);
            return INVALID_INDEX;
        }

        try {
            int index = Integer.parseInt(argument);
            if (index < INDEX_ONE || index > currentHand.getHand().size()) {
                System.err.println(GameMessages.ERROR_INVALID_CARD_INDEX);
                return INVALID_INDEX;
            }
            return index - INDEX_ONE;
        } catch (NumberFormatException e) {
            System.err.println(GameMessages.ERROR_INVALID_CARD_INDEX);
            return INVALID_INDEX;
        }
    }
}
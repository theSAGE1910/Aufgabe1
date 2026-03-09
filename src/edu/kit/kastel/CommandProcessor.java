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

    private CommandProcessor() {
    }


    /**
     * Initializes the command processor by populating the command map with available commands and their corresponding handlers.
     */
    public static void initialise() {

        if (COMMANDS.isEmpty()) {
            COMMANDS.put("select", new SelectCommand());
            COMMANDS.put("board", new BoardCommand());
            COMMANDS.put("move", new MoveCommand());
            COMMANDS.put("flip", new FlipCommand());
            COMMANDS.put("block", new BlockCommand());
            COMMANDS.put("hand", new HandCommand());
            COMMANDS.put("place", new PlaceCommand());
            COMMANDS.put("show", new ShowCommand());
            COMMANDS.put("yield", new YieldCommand());
            COMMANDS.put("state", new StateCommand());
            COMMANDS.put("quit", new QuitCommand());
        }
    }

    /**
     * Parses the user input, identifies the command and its argument (if any), and executes the corresponding action.
     * @param input the raw string input from the user
     */
    public static void processCommands(String input) {
        String[] words = input.split(REGEX_SPACE);
        if (words.length == 0) {
            System.err.println("ERROR: No command provided");
            return;
        }

        String key = words[0].toLowerCase();
        String argument = null;

        if (words.length == 2) {
            argument = words[1].toUpperCase().trim();
        } else if (words.length > 2) {
            if (key.equalsIgnoreCase("place")) {
                argument = input.trim().substring(key.length()).toUpperCase();
            } else {
                System.err.println("ERROR: Invalid command format");
                return;
            }
        }

        Command command = COMMANDS.get(key);
        if (command != null) {
            command.execute(argument);
        } else {
            System.err.println("ERROR: Invalid command");
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
            System.err.println("ERROR: No card index provided.");
            return -1;
        }

        try {
            int index = Integer.parseInt(argument);
            if (index < 1 || index > currentHand.getHand().size()) {
                System.err.println("ERROR: Invalid card index.");
                return -1;
            }
            return index - 1;
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid card index.");
            return -1;
        }
    }


}
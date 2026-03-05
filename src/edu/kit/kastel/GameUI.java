package edu.kit.kastel;

import java.util.Scanner;

/**
 * Handles the user interface and input loop for the game.
 * This class continuously prompts the user for commands and passes them
 * to the Commands processor until the game is terminated.
 * @author uxuwg
 * @version 0.9
 */
public final class GameUI {
    private static Scanner scanner = null;

    private static final String HELPING_TEXT
            = "Use one of the following commands: select, board, move, flip, block, hand, place, show, yield, state, quit.";

    private GameUI() {
    }

    /**
     * Starts the main interactive command-line loop.
     * Prints the initial help text, continuously prompts the user with "> ",
     * reads their input, and delegates it to the Commands class.
     */
    public static void getInput() {
        String input;
        System.out.println(HELPING_TEXT);
        try {
            scanner = new Scanner(System.in);
            while (Commands.isRunning) {
                System.out.print("> ");
                input = scanner.nextLine();
                Commands.processCommands(input);
            }
        } finally {
            scanner.close();
        }
    }
}

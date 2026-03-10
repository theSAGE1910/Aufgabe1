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

    private GameUI() {
    }

    /**
     * Starts the main interactive command-line loop.
     * Prints the initial help text, continuously prompts the user with "> ",
     * reads their input, and delegates it to the Commands class.
     */
    public static void getInput() {
        String input;
        try (Scanner scanner = new Scanner(System.in)) {
            while (GameState.isIsRunning()) {
                input = scanner.nextLine();
                if (input == null || input.trim().isEmpty()) {
                    continue;
                }
                CommandProcessor.processCommands(input);
            }
        }
    }

    /**
     * Updates the terminal display by implicitly calling the board and show commands.
     */
    public static void updateDisplay() {
        CommandProcessor.processCommands(GameMessages.BOARD);
        if (GameState.getSelectedSquare() != null) {
            CommandProcessor.processCommands(GameMessages.SHOW);
        }
    }
}

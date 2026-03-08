package edu.kit.kastel.commands;

import edu.kit.kastel.GameState;

/**
 * Command to quit the game. Sets the isRunning flag in GameState to false, which will cause the main game loop to exit.
 * @author uxuwg
 * @version 0.9
 */
public class QuitCommand implements Command {
    @Override
    public void execute(String argument) {
        if (argument != null) {
            System.err.println("ERROR: Command does not take any arguments.");
        }
        GameState.isRunning = false;
    }
}

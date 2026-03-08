package edu.kit.kastel.commands;

import edu.kit.kastel.GameState;

/**
 * Interface representing a command that can be executed in the game.
 * @author uxuwg
 * @version 0.9
 */
public interface Command {
    /**
     * Executes the command.
     * @param argument The argument provided with the command.
     * @param gameState The current state of the game.
     */
    void execute(String argument, GameState gameState);
}

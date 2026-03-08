package edu.kit.kastel.commands;

/**
 * Interface representing a command that can be executed in the game.
 * @author uxuwg
 * @version 0.9
 */
public interface Command {
    /**
     * Executes the command.
     *
     * @param argument The argument provided with the command.
     */
    void execute(String argument);
}

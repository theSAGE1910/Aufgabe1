package edu.kit.kastel.commands;

import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameMessages;
import edu.kit.kastel.Output;

/**
 * Command to show the hand of the active team.
 * @author uxuwg
 * @version 0.9
 */
public class HandCommand implements Command {
    @Override
    public void execute(String argument) {
        if (argument != null) {
            System.err.println(GameMessages.ERROR_COMMAND_DOES_NOT_TAKE_ANY_ARGUMENTS);
            return;
        }
        Output.printHand(GameEngine.getActiveTeam().getHand());
    }
}

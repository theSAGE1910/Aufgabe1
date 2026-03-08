package edu.kit.kastel.commands;

import edu.kit.kastel.GameEngine;
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
            System.err.println("ERROR: Command does not take any arguments.");
            return;
        }
        Output.printHand(GameEngine.activeTeam.getHand());
    }
}

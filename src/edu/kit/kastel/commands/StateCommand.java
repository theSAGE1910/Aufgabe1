package edu.kit.kastel.commands;

import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameMessages;
import edu.kit.kastel.Output;
import edu.kit.kastel.GameUI;

/**
 * Command to display the current state of the game, including the board and the units of both teams.
 * @author uxuwg
 * @version 0.9
 */
public class StateCommand implements Command {
    @Override
    public void execute(String argument) {
        if (argument != null) {
            System.err.println(GameMessages.ERROR_COMMAND_DOES_NOT_TAKE_ANY_ARGUMENTS);
            return;
        }
        Output.printState(GameEngine.getTeam1(), GameEngine.getTeam2());
        GameUI.updateDisplay();
    }
}

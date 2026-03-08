package edu.kit.kastel.commands;

import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameState;
import edu.kit.kastel.Output;
import edu.kit.kastel.GameUI;

/**
 * Command to display the current state of the game, including the board and the units of both teams.
 * @author uxuwg
 * @version 0.9
 */
public class StateCommand implements Command {
    @Override
    public void execute(String argument, GameState gameState) {
        if (argument != null) {
            System.err.println("ERROR: Command does not take any arguments.");
            return;
        }
        Output.printState(GameEngine.team1, GameEngine.team2);
        GameUI.updateDisplay();
    }
}

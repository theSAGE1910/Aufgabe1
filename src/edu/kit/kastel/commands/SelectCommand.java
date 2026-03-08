package edu.kit.kastel.commands;

import edu.kit.kastel.GameBoard;
import edu.kit.kastel.GameState;
import edu.kit.kastel.GameUI;

/**
 * Command to select a square on the game board.
 * @author uxuwg
 * @version 0.9
 */
public class SelectCommand implements Command {
    @Override
    public void execute(String argument, GameState gameState) {
        if (argument == null) {
            System.err.println("ERROR: Command requires an argument.");
        } else if (argument.length() == 2) {
            gameState.selectedSquare = argument;
            int[] coordinates = GameBoard.getCoordinates(argument);
            gameState.selectedRow = coordinates[0];
            gameState.selectedColumn = coordinates[1];
            GameUI.updateDisplay();
        } else {
            System.err.println("ERROR: Invalid square selected.");
        }
    }
}

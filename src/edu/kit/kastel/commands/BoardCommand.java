package edu.kit.kastel.commands;

import edu.kit.kastel.GameBoard;
import edu.kit.kastel.GameState;

/**
 * Command to display the game board.
 * @author uxuwg
 * @version 0.9
 */
public class BoardCommand implements Command {
    @Override
    public void execute(String argument, GameState gameState) {
        if (argument != null) {
            System.err.println("ERROR: Command does not take any arguments.");
            return;
        }
        if (GameState.selectedSquare != null && GameState.selectedSquare.length() == 2) {
            GameBoard.showGameBoard(GameState.selectedSquare.charAt(0),
                    Character.getNumericValue(GameState.selectedSquare.charAt(1)));
        } else {
            GameBoard.showGameBoard('Z', 0);
        }
    }
}

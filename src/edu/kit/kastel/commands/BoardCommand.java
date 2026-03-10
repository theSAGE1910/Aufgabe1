package edu.kit.kastel.commands;

import edu.kit.kastel.BoardRenderer;
import edu.kit.kastel.GameMessages;
import edu.kit.kastel.GameState;

/**
 * Command to display the game board.
 * @author uxuwg
 * @version 0.9
 */
public class BoardCommand implements Command {
    @Override
    public void execute(String argument) {
        if (argument != null) {
            System.err.println(GameMessages.ERROR_COMMAND_DOES_NOT_TAKE_ANY_ARGUMENTS);
            return;
        }
        if (GameState.getSelectedSquare() != null && GameState.getSelectedSquare().length() == 2) {
            BoardRenderer.showGameBoard(GameState.getSelectedSquare().charAt(0),
                    Character.getNumericValue(GameState.getSelectedSquare().charAt(1)));
        } else {
            BoardRenderer.showGameBoard();
        }
    }
}

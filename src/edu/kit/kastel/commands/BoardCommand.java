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

    private static final int VALID_ARG_LENGTH = 2;
    private static final int COL_CHAR_INDEX = 0;
    private static final int ROW_CHAR_INDEX = 1;

    @Override
    public void execute(String argument) {
        if (argument != null) {
            System.err.println(GameMessages.ERROR_COMMAND_DOES_NOT_TAKE_ANY_ARGUMENTS);
            return;
        }
        if (GameState.getSelectedSquare() != null && GameState.getSelectedSquare().length() == VALID_ARG_LENGTH) {
            BoardRenderer.showGameBoard(GameState.getSelectedSquare().charAt(COL_CHAR_INDEX),
                    Character.getNumericValue(GameState.getSelectedSquare().charAt(ROW_CHAR_INDEX)));
        } else {
            BoardRenderer.showGameBoard();
        }
    }
}
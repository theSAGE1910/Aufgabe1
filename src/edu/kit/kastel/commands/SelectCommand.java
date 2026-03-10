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

    private static final String REGEX = "^[A-G][1-7]$";
    private static final String ERROR_COMMAND_REQUIRES_AN_ARGUMENT = "ERROR: Command requires an argument.";
    private static final String ERROR_INVALID_SQUARE_SELECTED = "ERROR: Invalid square selected.";

    private static final int VALID_ARG_LENGTH = 2;
    private static final int ROW_INDEX = 0;
    private static final int COL_INDEX = 1;

    @Override
    public void execute(String argument) {
        if (argument == null) {
            System.err.println(ERROR_COMMAND_REQUIRES_AN_ARGUMENT);
        } else if (argument.length() == VALID_ARG_LENGTH && argument.toUpperCase().matches(REGEX)) {
            GameState.setSelectedSquare(argument);
            int[] coordinates = GameBoard.getCoordinates(argument);
            GameState.setSelectedRow(coordinates[ROW_INDEX]);
            GameState.setSelectedColumn(coordinates[COL_INDEX]);
            GameUI.updateDisplay();
        } else {
            System.err.println(ERROR_INVALID_SQUARE_SELECTED);
        }
    }
}
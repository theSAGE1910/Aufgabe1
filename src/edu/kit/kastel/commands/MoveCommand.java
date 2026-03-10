package edu.kit.kastel.commands;

import edu.kit.kastel.GameBoard;
import edu.kit.kastel.GameState;
import edu.kit.kastel.MovementController;
import edu.kit.kastel.Unit;

/**
 * Command to move a unit on the game board.
 * @author uxuwg
 * @version 0.9
 */
public class MoveCommand implements Command {

    private static final int ROW_INDEX = 0;
    private static final int COL_INDEX = 1;

    @Override
    public void execute(String argument) {
        if (!MovementController.isArgumentValid(argument)) {
            return;
        }

        Unit movingUnit = GameState.getValidatedActiveUnit();
        if (movingUnit == null) {
            return;
        }

        int targetRow = GameBoard.getCoordinates(argument)[ROW_INDEX];
        int targetCol = GameBoard.getCoordinates(argument)[COL_INDEX];

        if (!MovementController.isDistanceValid(targetRow, targetCol)) {
            return;
        }

        Unit targetUnit = GameBoard.getUnitAt(targetRow, targetCol);

        if (!MovementController.isValidKingInteraction(movingUnit, targetUnit)) {
            return;
        }

        MovementController.executeMove(argument, targetUnit, targetRow, targetCol, movingUnit);
    }
}
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
    @Override
    public void execute(String argument, GameState gameState) {
        if (!MovementController.isArgumentValid(argument)) {
            return;
        }

        Unit movingUnit = GameState.getValidatedActiveUnit();
        if (movingUnit == null) {
            return;
        }

        int targetRow = GameBoard.getCoordinates(argument)[0];
        int targetCol = GameBoard.getCoordinates(argument)[1];

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

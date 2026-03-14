package edu.kit.kastel.commands;

import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameMessages;
import edu.kit.kastel.GameState;
import edu.kit.kastel.Output;
import edu.kit.kastel.Unit;
import edu.kit.kastel.GameUI;

/**
 * Command to set the active unit to 'block'.
 * @author uxuwg
 * @version 0.9
 */
public class BlockCommand implements Command {

    private static final String ERROR_THE_FARMER_KING_CANNOT_BLOCK = "ERROR: The Farmer King cannot block.";
    private static final String ERROR_YOU_CAN_ONLY_BLOCK_YOUR_OWN_UNITS = "ERROR: You can only block your own units.";

    @Override
    public void execute(String argument) {
        if (argument != null) {
            System.err.println(GameMessages.ERROR_COMMAND_DOES_NOT_TAKE_ANY_ARGUMENTS);
            return;
        }
        Unit unitToBlock = GameState.getValidatedActiveUnit();
        if (unitToBlock == null) {
            return;
        }
        if (Unit.isKing(unitToBlock)) {
            System.err.println(ERROR_THE_FARMER_KING_CANNOT_BLOCK);
            return;
        }
        if (!unitToBlock.getTeam().equals(GameEngine.getActiveTeam())) {
            System.err.println(ERROR_YOU_CAN_ONLY_BLOCK_YOUR_OWN_UNITS);
            return;
        }
        unitToBlock.setBlocking(true);
        unitToBlock.setMovedThisTurn(true);
        Output.printBlockStatus(unitToBlock.getUnitName(), GameState.getSelectedSquare(), true);
        GameUI.updateDisplay();
    }
}

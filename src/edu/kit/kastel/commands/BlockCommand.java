package edu.kit.kastel.commands;

import edu.kit.kastel.GameEngine;
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
    @Override
    public void execute(String argument, GameState gameState) {
        if (argument != null) {
            System.err.println("ERROR: Command does not take any arguments.");
            return;
        }
        Unit unitToBlock = GameState.getValidatedActiveUnit();
        if (unitToBlock == null) {
            return;
        }
        if (Unit.isKing(unitToBlock)) {
            System.err.println("ERROR: The Farmer King cannot block.");
            return;
        }
        if (!unitToBlock.getTeam().equals(GameEngine.activeTeam)) {
            System.err.println("ERROR: You can only block your own units.");
            return;
        }
        if (unitToBlock.isBlocking()) {
            System.err.println("ERROR: Unit is already blocking.");
            return;
        }
        unitToBlock.setBlocking(true);
        unitToBlock.setHasMovedThisTurn(true);
        Output.printBlock(unitToBlock.getUnitName(), GameState.selectedSquare);
        GameUI.updateDisplay();
    }
}

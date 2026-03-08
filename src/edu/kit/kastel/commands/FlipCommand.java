package edu.kit.kastel.commands;

import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameState;
import edu.kit.kastel.Output;
import edu.kit.kastel.Unit;
import edu.kit.kastel.GameUI;

/**
 * Command to flip a unit face up.
 * @author uxuwg
 * @version 0.9
 */
public class FlipCommand implements Command {
    @Override
    public void execute(String argument, GameState gameState) {
        if (argument != null) {
            System.err.println("ERROR: Command does not take any arguments.");
            return;
        }
        Unit unitToFlip = GameState.getValidatedActiveUnit();
        if (unitToFlip == null) {
            return;
        }
        if (Unit.isKing(unitToFlip)) {
            System.err.println("ERROR: The Farmer King cannot be flipped.");
            return;
        }
        if (!unitToFlip.getTeam().equals(GameEngine.activeTeam)) {
            System.err.println("ERROR: You can only flip your own units.");
            return;
        }
        if (unitToFlip.isFaceUp()) {
            System.err.println("ERROR: Unit is already face up.");
            return;
        }
        unitToFlip.setFaceUp(true);
        Output.printFlip(unitToFlip.getUnitName(), unitToFlip.getAtk(), unitToFlip.getDef(), GameState.selectedSquare);
        GameUI.updateDisplay();
    }
}

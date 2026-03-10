package edu.kit.kastel.commands;

import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameMessages;
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

    private static final String ERROR_THE_FARMER_KING_CANNOT_BE_FLIPPED = "ERROR: The Farmer King cannot be flipped.";
    private static final String ERROR_YOU_CAN_ONLY_FLIP_YOUR_OWN_UNITS = "ERROR: You can only flip your own units.";
    private static final String ERROR_UNIT_IS_ALREADY_FACE_UP = "ERROR: Unit is already face up.";

    @Override
    public void execute(String argument) {
        if (argument != null) {
            System.err.println(GameMessages.ERROR_COMMAND_DOES_NOT_TAKE_ANY_ARGUMENTS);
        } else {
            Unit unitToFlip = GameState.getValidatedActiveUnit();

            if (unitToFlip != null) {
                if (Unit.isKing(unitToFlip)) {
                    System.err.println(ERROR_THE_FARMER_KING_CANNOT_BE_FLIPPED);
                } else if (!unitToFlip.getTeam().equals(GameEngine.getActiveTeam())) {
                    System.err.println(ERROR_YOU_CAN_ONLY_FLIP_YOUR_OWN_UNITS);
                } else if (unitToFlip.isFaceUp()) {
                    System.err.println(ERROR_UNIT_IS_ALREADY_FACE_UP);
                } else {
                    unitToFlip.setFaceUp(true);
                    Output.printFlip(unitToFlip.getUnitName(), unitToFlip.getAtk(), unitToFlip.getDef(), GameState.getSelectedSquare());
                    GameUI.updateDisplay();
                }
            }
        }
    }
}

package edu.kit.kastel.commands;

import edu.kit.kastel.GameBoard;
import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameMessages;
import edu.kit.kastel.GameState;
import edu.kit.kastel.Unit;
import edu.kit.kastel.Output;

/**
 * Command to show the details of the currently selected square.
 * @author uxuwg
 * @version 0.9
 */
public class ShowCommand implements Command {
    private static final String NO_UNIT = "<no unit>";

    @Override
    public void execute(String argument) {
        if (GameState.getSelectedSquare() == null) {
            System.out.println(GameMessages.ERR_NO_SQUARE);
            return;
        }

        Unit unitToShow = GameBoard.getUnitAt(GameState.getSelectedRow(), GameState.getSelectedColumn());
        if (unitToShow == null) {
            System.out.println(NO_UNIT);
        } else {
            if (Unit.isKing(unitToShow)) {
                Output.printFarmerKing(unitToShow);
            } else if (!unitToShow.isFaceUp() && !unitToShow.getTeam().equals(GameEngine.getActiveTeam())) {
                Output.printHiddenUnit(unitToShow);
            } else {
                Output.printVisibleUnit(unitToShow);
            }
        }
    }
}

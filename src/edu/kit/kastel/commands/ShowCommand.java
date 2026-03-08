package edu.kit.kastel.commands;

import edu.kit.kastel.GameBoard;
import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameState;
import edu.kit.kastel.Unit;
import edu.kit.kastel.Output;

/**
 * Command to show the details of the currently selected square.
 * @author uxuwg
 * @version 0.9
 */
public class ShowCommand implements Command {
    @Override
    public void execute(String argument) {
        if (GameState.selectedSquare == null) {
            System.out.println("ERROR: No square selected.");
            return;
        }

        Unit unitToShow = GameBoard.getUnitAt(GameState.selectedRow, GameState.selectedColumn);
        if (unitToShow == null) {
            System.out.println("<no unit>");
        } else {
            if (Unit.isKing(unitToShow)) {
                Output.printFarmerKing(unitToShow);
            } else if (!unitToShow.isFaceUp() && !unitToShow.getTeam().equals(GameEngine.activeTeam)) {
                Output.printHiddenUnit(unitToShow);
            } else {
                Output.printVisibleUnit(unitToShow);
            }
        }
    }
}

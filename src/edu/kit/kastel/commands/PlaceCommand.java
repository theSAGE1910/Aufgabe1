package edu.kit.kastel.commands;

import edu.kit.kastel.CommandProcessor;
import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameState;
import edu.kit.kastel.GameBoard;
import edu.kit.kastel.Hand;
import edu.kit.kastel.Output;
import edu.kit.kastel.Unit;
import edu.kit.kastel.GameUI;

/**
 * Command implementation for placing a unit from the player's hand onto the board.
 * @author uxuwg
 * @version 0.9
 */
public class PlaceCommand implements Command {
    @Override
    public void execute(String argument) {
        if (GameState.hasPlacedThisTurn) {
            System.err.println("ERROR: You can only place one unit per turn.");
            return;
        }
        if (GameState.selectedSquare == null) {
            System.err.println("ERROR: No square selected.");
            return;
        }

        Unit targetUnit = GameBoard.getUnitAt(GameState.selectedRow, GameState.selectedColumn);

        if (targetUnit != null) {
            if (targetUnit.getTeam().equals(GameEngine.activeTeam) || targetUnit.getRole().equals("King")) {
                System.err.println("ERROR: Square already occupied.");
            }
            return;
        }

        int[] kingPosition = GameEngine.activeTeam.equals(GameEngine.team1)
                ? GameBoard.getPlayerKingPosition()
                : GameBoard.getEnemyKingPosition();
        if (kingPosition != null) {
            int rowDiff = Math.abs(GameState.selectedRow - kingPosition[0]);
            int colDiff = Math.abs(GameState.selectedColumn - kingPosition[1]);
            if (rowDiff > 1 || colDiff > 1) {
                System.err.println("ERROR: Target square must be adjacent to the Farmer King.");
                return;
            }
        }

        Hand currentHand = GameEngine.activeTeam.getHand();
        int handIndex = CommandProcessor.parseHandIndex(argument, currentHand);
        if (handIndex == -1) {
            return;
        }

        Unit unitToPlace = currentHand.getHand().get(handIndex);
        Output.printPlacement(GameEngine.activeTeam.getName(), unitToPlace, GameState.selectedSquare);

        unitToPlace.setTeam(GameEngine.activeTeam);
        unitToPlace.setHasMovedThisTurn(false);
        currentHand.removeUnitFromHand(unitToPlace);

        int boardCount = Output.getBoardCount(GameEngine.activeTeam);
        if (boardCount >= 5) {
            GameBoard.setUnitAt(GameState.selectedRow, GameState.selectedColumn, null);
            Output.printElimination(unitToPlace.getUnitName());
        } else {
            GameBoard.setUnitAt(GameState.selectedRow, GameState.selectedColumn, unitToPlace);
        }
        GameState.hasPlacedThisTurn = true;
        GameUI.updateDisplay();
    }
}

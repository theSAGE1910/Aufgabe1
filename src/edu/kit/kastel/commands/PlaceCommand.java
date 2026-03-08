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
        if (isValidState()) {
            return;
        }

        Unit targetSquareUnit = GameBoard.getUnitAt(GameState.selectedRow, GameState.selectedColumn);

        if (!isValidTargetSquare(targetSquareUnit) || !isAdjacentToKing()) {
            return;
        }

        Hand currentHand = GameEngine.activeTeam.getHand();
        int handIndex = CommandProcessor.parseHandIndex(argument, currentHand);
        if (handIndex == -1) {
            return;
        }

        Unit unitToPlace = currentHand.getHand().get(handIndex);
        Output.printPlacement(GameEngine.activeTeam.getName(), unitToPlace, GameState.selectedSquare);

        prepareUnitForPlacement(unitToPlace, currentHand);

        if (targetSquareUnit != null) {
            executeMergePlacement(unitToPlace, targetSquareUnit);
        } else {
            executeStandardPlacement(unitToPlace);
        }

        GameState.hasPlacedThisTurn = true;
        GameUI.updateDisplay();
    }

    private static void executeStandardPlacement(Unit unitToPlace) {
        int boardCount = Output.getBoardCount(GameEngine.activeTeam);
        if (boardCount >= 5) {
            GameBoard.setUnitAt(GameState.selectedRow, GameState.selectedColumn, null);
            Output.printElimination(unitToPlace.getUnitName());
        } else {
            GameBoard.setUnitAt(GameState.selectedRow, GameState.selectedColumn, unitToPlace);
        }
    }

    private static void executeMergePlacement(Unit unitToPlace, Unit targetSquareUnit) {
        Output.printMerge(unitToPlace.getUnitName(), targetSquareUnit.getUnitName(), GameState.selectedSquare);

        Unit mergedUnit = unitToPlace.mergeUnits(unitToPlace, targetSquareUnit);

        if (mergedUnit != null) {
            GameBoard.setUnitAt(GameState.selectedRow, GameState.selectedColumn, mergedUnit);
            mergedUnit.setHasMovedThisTurn(false);
            System.out.println("Success!");
        } else {
            GameBoard.setUnitAt(GameState.selectedRow, GameState.selectedColumn, unitToPlace);
            unitToPlace.setHasMovedThisTurn(false);
            Output.printMergeFail(targetSquareUnit.getUnitName());
        }
    }

    private static void prepareUnitForPlacement(Unit unitToPlace, Hand currentHand) {
        unitToPlace.setTeam(GameEngine.activeTeam);
        unitToPlace.setHasMovedThisTurn(false);
        currentHand.removeUnitFromHand(unitToPlace);
    }

    private static boolean isAdjacentToKing() {
        int[] kingPosition = GameEngine.activeTeam.equals(GameEngine.team1)
                ? GameBoard.getPlayerKingPosition()
                : GameBoard.getEnemyKingPosition();
        if (kingPosition != null) {
            int rowDiff = Math.abs(GameState.selectedRow - kingPosition[0]);
            int colDiff = Math.abs(GameState.selectedColumn - kingPosition[1]);
            if (rowDiff > 1 || colDiff > 1) {
                System.err.println("ERROR: Target square must be adjacent to the Farmer King.");
                return false;
            }
        }
        return true;
    }

    private static boolean isValidTargetSquare(Unit targetSquareUnit) {
        if (targetSquareUnit != null) {
            if (!targetSquareUnit.getTeam().equals(GameEngine.activeTeam) || targetSquareUnit.getRole().equals("King")) {
                System.err.println("ERROR: Square already occupied.");
                return false;
            }
        }
        return true;
    }

    private static boolean isValidState() {
        if (GameState.hasPlacedThisTurn) {
            System.err.println("ERROR: You can only place one unit per turn.");
            return true;
        }
        if (GameState.selectedSquare == null) {
            System.err.println("ERROR: No square selected.");
            return true;
        }
        return false;
    }
}

package edu.kit.kastel.commands;

import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameState;
import edu.kit.kastel.GameBoard;
import edu.kit.kastel.Hand;
import edu.kit.kastel.Output;
import edu.kit.kastel.Unit;
import edu.kit.kastel.GameUI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        Unit initialTargetUnit = GameBoard.getUnitAt(GameState.selectedRow, GameState.selectedColumn);
        if (!isValidTargetSquare(initialTargetUnit) || !isAdjacentToKing()) {
            return;
        }

        if (argument == null) {
            System.err.println("ERROR: No hand index provided.");
            return;
        }

        String[] arguments = argument.trim().split(" ");
        Hand currentHand = GameEngine.activeTeam.getHand();

        List<Unit> unitsToPlace = new ArrayList<>();
        Set<Integer> duplicateChecker = new HashSet<>();

        for (String arg : arguments) {
            int index = parseIndex(arg, currentHand);
            if (index == -1) {
                return;
            }
            if (!duplicateChecker.add(index)) {
                System.err.println("ERROR: Duplicate index provided.");
                return;
            }
            unitsToPlace.add(currentHand.getHand().get(index));
        }

        executePlacement(unitsToPlace, currentHand);

        GameState.hasPlacedThisTurn = true;
        GameUI.updateDisplay();
    }

    private static void executePlacement(List<Unit> unitsToPlace, Hand currentHand) {
        for (Unit unit : unitsToPlace) {
            Output.printPlacement(GameEngine.activeTeam.getName(), unit, GameState.selectedSquare);
            prepareUnitForPlacement(unit, currentHand);

            Unit targetSquareUnit = GameBoard.getUnitAt(GameState.selectedRow, GameState.selectedColumn);
            if (targetSquareUnit != null) {
                executeMergePlacement(unit, targetSquareUnit);
            } else {
                executeStandardPlacement(unit);
            }
        }
    }

    private static int parseIndex(String argument, Hand currentHand) {
        try {
            int index = Integer.parseInt(argument);
            if (index < 1 || index > currentHand.getHand().size()) {
                System.err.println("ERROR: Invalid card index.");
                return -1;
            }
            return index - 1;
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid card index.");
            return -1;
        }
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

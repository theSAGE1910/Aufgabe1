package edu.kit.kastel.commands;

import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameMessages;
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

    private static final String ERROR_NO_HAND_INDEX_PROVIDED = "ERROR: No hand index provided.";
    private static final String ERROR_DUPLICATE_INDEX_PROVIDED = "ERROR: Duplicate index provided.";
    private static final String ERROR_TARGET_SQUARE_ADJACENT_TO_FARMER_KING = "ERROR: Target square must be adjacent to the Farmer King.";
    private static final String ERROR_SQUARE_ALREADY_OCCUPIED = "ERROR: Square already occupied.";
    private static final String ERROR_ONE_UNIT_PER_TURN = "ERROR: You can only place one unit per turn.";
    private static final String SPACE_DELIMITER = " ";

    private static final int INVALID_INDEX_FLAG = -1;
    private static final int MIN_CARD_INDEX = 1;
    private static final int INDEX_OFFSET = 1;
    private static final int MAX_BOARD_UNITS = 5;
    private static final int ROW_INDEX = 0;
    private static final int COL_INDEX = 1;
    private static final int MAX_ADJACENT_DISTANCE = 1;

    @Override
    public void execute(String argument) {
        if (!isValidState()) {

            Unit initialTargetUnit = GameBoard.getUnitAt(GameState.getSelectedRow(), GameState.getSelectedColumn());

            if (isValidTargetSquare(initialTargetUnit) && isAdjacentToKing()) {

                if (argument == null) {
                    System.err.println(ERROR_NO_HAND_INDEX_PROVIDED);
                } else {
                    String[] arguments = argument.trim().split(SPACE_DELIMITER);
                    Hand currentHand = GameEngine.getActiveTeam().getHand();

                    List<Unit> unitsToPlace = new ArrayList<>();
                    Set<Integer> duplicateChecker = new HashSet<>();

                    boolean parseSuccess = true;

                    for (String arg : arguments) {
                        int index = parseIndex(arg, currentHand);
                        if (index == INVALID_INDEX_FLAG) {
                            parseSuccess = false;
                            break;
                        }
                        if (!duplicateChecker.add(index)) {
                            System.err.println(ERROR_DUPLICATE_INDEX_PROVIDED);
                            parseSuccess = false;
                            break;
                        }
                        unitsToPlace.add(currentHand.getHand().get(index));
                    }

                    if (parseSuccess) {
                        executePlacement(unitsToPlace, currentHand);
                        GameState.setHasPlacedThisTurn(true);
                        GameUI.updateDisplay();
                    }
                }
            }
        }
    }

    private static void executePlacement(List<Unit> unitsToPlace, Hand currentHand) {
        for (Unit unit : unitsToPlace) {
            Output.printPlacement(GameEngine.getActiveTeam().getName(), unit, GameState.getSelectedSquare());
            prepareUnitForPlacement(unit, currentHand);

            Unit targetSquareUnit = GameBoard.getUnitAt(GameState.getSelectedRow(), GameState.getSelectedColumn());
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
            if (index < MIN_CARD_INDEX || index > currentHand.getHand().size()) {
                System.err.println(GameMessages.ERROR_INVALID_CARD_INDEX);
                return INVALID_INDEX_FLAG;
            }
            return index - INDEX_OFFSET;
        } catch (NumberFormatException e) {
            System.err.println(GameMessages.ERROR_INVALID_CARD_INDEX);
            return INVALID_INDEX_FLAG;
        }
    }

    private static void executeStandardPlacement(Unit unitToPlace) {
        int boardCount = Output.getBoardCount(GameEngine.getActiveTeam());
        if (boardCount >= MAX_BOARD_UNITS) {
            GameBoard.setUnitAt(GameState.getSelectedRow(), GameState.getSelectedColumn(), null);
            Output.printElimination(unitToPlace.getUnitName());
        } else {
            GameBoard.setUnitAt(GameState.getSelectedRow(), GameState.getSelectedColumn(), unitToPlace);
        }
    }

    private static void executeMergePlacement(Unit unitToPlace, Unit targetSquareUnit) {
        Output.printMerge(unitToPlace.getUnitName(), targetSquareUnit.getUnitName(), GameState.getSelectedSquare());

        Unit mergedUnit = unitToPlace.mergeUnits(unitToPlace, targetSquareUnit);

        if (mergedUnit != null) {
            GameBoard.setUnitAt(GameState.getSelectedRow(), GameState.getSelectedColumn(), mergedUnit);
            mergedUnit.setHasMovedThisTurn(false);
            System.out.println(GameMessages.SUCCESS_MESSAGE);
        } else {
            GameBoard.setUnitAt(GameState.getSelectedRow(), GameState.getSelectedColumn(), unitToPlace);
            unitToPlace.setHasMovedThisTurn(false);
            Output.printMergeFail(targetSquareUnit.getUnitName());
        }
    }

    private static void prepareUnitForPlacement(Unit unitToPlace, Hand currentHand) {
        unitToPlace.setTeam(GameEngine.getActiveTeam());
        unitToPlace.setHasMovedThisTurn(false);
        currentHand.removeUnitFromHand(unitToPlace);
    }

    private static boolean isAdjacentToKing() {
        int[] kingPosition = GameEngine.getActiveTeam().equals(GameEngine.getTeam1())
                ? GameBoard.getPlayerKingPosition()
                : GameBoard.getEnemyKingPosition();
        if (kingPosition != null) {
            int rowDiff = Math.abs(GameState.getSelectedRow() - kingPosition[ROW_INDEX]);
            int colDiff = Math.abs(GameState.getSelectedColumn() - kingPosition[COL_INDEX]);
            if (rowDiff > MAX_ADJACENT_DISTANCE || colDiff > MAX_ADJACENT_DISTANCE) {
                System.err.println(ERROR_TARGET_SQUARE_ADJACENT_TO_FARMER_KING);
                return false;
            }
        }
        return true;
    }

    private static boolean isValidTargetSquare(Unit targetSquareUnit) {
        if (targetSquareUnit != null) {
            if (!targetSquareUnit.getTeam().equals(GameEngine.getActiveTeam()) || targetSquareUnit.getRole().equals(GameMessages.KING)) {
                System.err.println(ERROR_SQUARE_ALREADY_OCCUPIED);
                return false;
            }
        }
        return true;
    }

    private static boolean isValidState() {
        if (GameState.isHasPlacedThisTurn()) {
            System.err.println(ERROR_ONE_UNIT_PER_TURN);
            return true;
        }
        if (GameState.getSelectedSquare() == null) {
            System.err.println(GameMessages.ERR_NO_SQUARE);
            return true;
        }
        return false;
    }
}
package edu.kit.kastel.ai;

import edu.kit.kastel.GameBoard;
import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameMessages;
import edu.kit.kastel.GameState;
import edu.kit.kastel.GameUI;
import edu.kit.kastel.RandomGenerator;
import edu.kit.kastel.Unit;
import edu.kit.kastel.Output;

import java.util.ArrayList;
import java.util.List;

import static edu.kit.kastel.Output.getBoardCount;

/**
 * Utility class responsible for handling the AI's unit placement logic.
 * Evaluates valid squares around the AI's Farmer King and selects the optimal
 * unit from the hand to place based on a weighted random draw of attack values.
 * @author uxuwg
 * @version 0.7
 */
public final class AIPlacement {

    private static final int INITIAL_VALUE = 0;
    private static final int START_INDEX = 0;
    private static final int ROW_INDEX = 0;
    private static final int COL_INDEX = 1;
    private static final int ENEMIES_INDEX = 0;
    private static final int FELLOWS_INDEX = 1;

    private static final int SINGLE_TARGET = 1;
    private static final int RANDOM_MIN_BOUND = 1;
    private static final int RANDOM_OFFSET = 1;

    private static final int MAX_BOARD_UNITS = 5;
    private static final int MIN_COORDINATE = 0;
    private static final int ENEMY_SCORE_MULTIPLIER = 2;

    private static final int[][] CLOCK_DIRS = {
            {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}
    };
    private static final int[][] ORTH_DIRS = {
            {-1, 0}, {0, 1}, {1, 0}, {0, -1}
    };

    private AIPlacement() {
    }

    /**
     * Executes the unit placement phase for the AI.
     * Identifies the best valid target squares around the AI's Farmer King,
     * selects one, and places a weighted randomly chosen unit from the AI's hand.
     */
    public static void placeUnit() {
        int[] aiKingPos = GameBoard.getEnemyKingPosition();
        int[] playerKingPos = GameBoard.getPlayerKingPosition();
        if (aiKingPos == null || playerKingPos == null) {
            return;
        }

        List<TargetSquare> validTargets = getBestPlacementSquares(aiKingPos, playerKingPos);
        if (validTargets.isEmpty()) {
            return;
        }

        TargetSquare targetSquare = validTargets.get(START_INDEX);
        if (validTargets.size() > SINGLE_TARGET) {
            int draw = RandomGenerator.randomIntegerPick(RANDOM_MIN_BOUND, validTargets.size() + RANDOM_OFFSET);
            targetSquare = validTargets.get(draw - RANDOM_OFFSET);
        }

        List<Unit> hand = GameEngine.getTeam2().getHand().getHand();
        if (hand.isEmpty()) {
            return;
        }

        Unit unitToPlace = selectCardToPlace(hand);
        String coord = AIMovement.getCoordinateString(targetSquare.getRow(), targetSquare.getCol());

        Output.printPlacement(GameEngine.getTeam2().getName(), unitToPlace, coord);

        unitToPlace.setTeam(GameEngine.getTeam2());
        unitToPlace.setHasMovedThisTurn(false);
        hand.remove(unitToPlace);

        Unit targetSquareUnit = GameBoard.getUnitAt(targetSquare.getRow(), targetSquare.getCol());

        if (targetSquareUnit != null) {
            Output.printMerge(unitToPlace.getUnitName(), targetSquareUnit.getUnitName(), coord);
            Unit mergedUnit = unitToPlace.mergeUnits(unitToPlace, targetSquareUnit);
            if (mergedUnit != null) {
                GameBoard.setUnitAt(targetSquare.getRow(), targetSquare.getCol(), mergedUnit);
                mergedUnit.setHasMovedThisTurn(false);
                System.out.println(GameMessages.SUCCESS_MESSAGE);
            } else {
                GameBoard.setUnitAt(targetSquare.getRow(), targetSquare.getCol(), unitToPlace);
                unitToPlace.setHasMovedThisTurn(false);
                Output.printMergeFail(targetSquareUnit.getUnitName());
            }
        } else {
            int boardCount = getBoardCount(GameEngine.getTeam2());
            if (boardCount >= MAX_BOARD_UNITS) {
                GameBoard.setUnitAt(targetSquare.getRow(), targetSquare.getCol(), null);
                Output.printElimination(unitToPlace.getUnitName());
            } else {
                GameBoard.setUnitAt(targetSquare.getRow(), targetSquare.getCol(), unitToPlace);
            }
        }
        GameState.setSelectedSquare(coord);
        GameState.setSelectedRow(targetSquare.getRow());
        GameState.setSelectedColumn(targetSquare.getCol());
        GameUI.updateDisplay();
    }

    private static Unit selectCardToPlace(List<Unit> hand) {
        List<Integer> weights = new ArrayList<>();
        int totalWeight = INITIAL_VALUE;

        for (Unit unit : hand) {
            int weight = unit.getAtk();
            weights.add(weight);
            totalWeight += weight;
        }

        int selectedCardIndex = INITIAL_VALUE;
        if (totalWeight > INITIAL_VALUE) {
            int randomWeight = RandomGenerator.randomIntegerPick(RANDOM_MIN_BOUND, totalWeight + RANDOM_OFFSET);
            int runningSum = INITIAL_VALUE;
            for (int i = START_INDEX; i < weights.size(); i++) {
                runningSum += weights.get(i);
                if (randomWeight <= runningSum) {
                    selectedCardIndex = i;
                    break;
                }
            }
        }

        return hand.get(selectedCardIndex);
    }

    private static List<TargetSquare> getBestPlacementSquares(int[] aiKingPos, int[] playerKingPos) {
        List<TargetSquare> validTargets = new ArrayList<>();
        int maxScore = Integer.MIN_VALUE;

        for (int[] dir : CLOCK_DIRS) {
            int targetRow = aiKingPos[ROW_INDEX] + dir[ROW_INDEX];
            int targetCol = aiKingPos[COL_INDEX] + dir[COL_INDEX];

            if (targetRow < MIN_COORDINATE || targetRow >= GameBoard.DIMENSION
                    || targetCol < MIN_COORDINATE || targetCol >= GameBoard.DIMENSION) {
                continue;
            }

            Unit targetUnit = GameBoard.getUnitAt(targetRow, targetCol);
            if (targetUnit != null) {
                if (targetUnit.getTeam().equals(GameEngine.getTeam1()) || targetUnit.getRole().equals(GameMessages.KING)) {
                    continue;
                }
            }

            int steps = Math.abs(targetRow - playerKingPos[ROW_INDEX]) + Math.abs(targetCol - playerKingPos[COL_INDEX]);

            int[] counts = countAdjacentUnits(targetRow, targetCol);
            int enemies = counts[ENEMIES_INDEX];
            int fellows = counts[FELLOWS_INDEX];

            int score = -steps + (ENEMY_SCORE_MULTIPLIER * enemies) - fellows;

            if (score > maxScore) {
                maxScore = score;
                validTargets.clear();
                validTargets.add(new TargetSquare(targetRow, targetCol));
            } else if (score == maxScore) {
                validTargets.add(new TargetSquare(targetRow, targetCol));
            }
        }
        return validTargets;
    }

    private static int[] countAdjacentUnits(int row, int col) {
        int enemies = INITIAL_VALUE;
        int fellows = INITIAL_VALUE;

        for (int[] dir : ORTH_DIRS) {
            int adjRow = row + dir[ROW_INDEX];
            int adjCol = col + dir[COL_INDEX];

            if (adjRow >= MIN_COORDINATE && adjRow < GameBoard.DIMENSION
                    && adjCol >= MIN_COORDINATE && adjCol < GameBoard.DIMENSION) {
                Unit adjacentUnit = GameBoard.getUnitAt(adjRow, adjCol);
                if (adjacentUnit != null) {
                    if (adjacentUnit.getTeam().equals(GameEngine.getTeam1())) {
                        enemies++;
                    } else if (adjacentUnit.getTeam().equals(GameEngine.getTeam2())) {
                        fellows++;
                    }
                }
            }
        }
        return new int[]{enemies, fellows};
    }
}
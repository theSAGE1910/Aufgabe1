package edu.kit.kastel.ai;

import edu.kit.kastel.GameBoard;
import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameMessages;
import edu.kit.kastel.GameState;
import edu.kit.kastel.GameUI;
import edu.kit.kastel.MovementController;
import edu.kit.kastel.RandomGenerator;
import edu.kit.kastel.Unit;
import edu.kit.kastel.Output;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class handling the AI's movement phase.
 * It is responsible for evaluating the board state to move the AI's Farmer King
 * and subsequently determining the optimal actions for all other active AI units.
 * @author uxuwg
 * @version 0.7
 */
public final class AIMovement {

    private static final int INITIAL_VALUE = 0;
    private static final int START_INDEX = 0;
    private static final int ROW_INDEX = 0;
    private static final int COL_INDEX = 1;
    private static final int ENEMIES_INDEX = 0;
    private static final int FELLOWS_INDEX = 1;

    private static final int SINGLE_TARGET = 1;
    private static final int RANDOM_MIN_BOUND = 1;
    private static final int RANDOM_OFFSET = 1;

    private static final int SAME_SQUARE_DISTANCE = 0;
    private static final int ADJACENT_DISTANCE = 1;
    private static final int FELLOW_PRESENT_VALUE = 1;

    private static final int ENEMY_PENALTY_MULTIPLIER = 2;
    private static final int FELLOW_KING_PRESENCE_PENALTY = 3;

    private static final int MIN_COORDINATE = 0;
    private static final int ROW_INVERSION_BASE = 7;

    private static final int MIN_MOVE_ACTION_INDEX = 0;
    private static final int MAX_MOVE_ACTION_INDEX = 3;
    private static final int BLOCK_ACTION_INDEX = 4;
    private static final int STAY_ACTION_INDEX = 5;
    private static final int INVALID_ACTION_INDEX = -1;

    private static final int WEIGHT_FLOOR = 0;

    private static final int DIR_NEG = -1;
    private static final int DIR_ZERO = 0;
    private static final int DIR_POS = 1;

    private static final int[][] KING_NEIGHBOUR_DIRS = {
            {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}
    };
    private static final int[][] UNIT_ACTION_DIRS = {
            {-1, 0}, {0, 1}, {1, 0}, {0, -1}
    };

    private AIMovement() {
    }

    /**
     * Executes the movement of the AI's Farmer King.
     * Evaluates valid adjacent squares based on scoring and moves the King.
     * If multiple squares share the highest score, one is chosen randomly.
     */
    public static void moveFarmerKing() {
        int[] enemyKingPos = GameBoard.getEnemyKingPosition();
        if (enemyKingPos == null) {
            return;
        }

        int enemyKingRow = enemyKingPos[ROW_INDEX];
        int enemyKingCol = enemyKingPos[COL_INDEX];
        Unit king = GameBoard.getUnitAt(enemyKingRow, enemyKingCol);

        List<TargetSquare> validTargets = getBestKingTargets(enemyKingRow, enemyKingCol, king);

        TargetSquare targetSquare = null;
        if (validTargets.size() == SINGLE_TARGET) {
            targetSquare = validTargets.get(START_INDEX);
        } else if (validTargets.size() > SINGLE_TARGET) {
            int draw = RandomGenerator.randomIntegerPick(RANDOM_MIN_BOUND, validTargets.size() + RANDOM_OFFSET);
            targetSquare = validTargets.get(draw - RANDOM_OFFSET);
        }

        if (targetSquare != null) {
            String startCoord = getCoordinateString(enemyKingRow, enemyKingCol);
            int targetRow = targetSquare.getRow();
            int targetCol = targetSquare.getCol();
            String targetCoord = getCoordinateString(targetRow, targetCol);

            GameState.setSelectedSquare(startCoord);
            GameState.setSelectedRow(enemyKingRow);
            GameState.setSelectedColumn(enemyKingCol);

            Unit targetUnit = GameBoard.getUnitAt(targetRow, targetCol);
            MovementController.executeMove(targetCoord, targetUnit, targetRow, targetCol, king);
        }
    }

    /**
     * Executes the action phase for all standard AI units.
     * Iteratively evaluates all movable AI units, calculates their possible action
     * scores (Move, Block, or stay En Place), and executes the action for the
     * unit that holds the highest weight.
     */
    public static void moveUnits() {
        while (GameState.isIsRunning()) {
            List<Unit> movableUnits = getMovableUnits();

            if (movableUnits.isEmpty()) {
                break;
            }

            Unit bestUnit = null;
            int maxScore = Integer.MIN_VALUE;
            List<Integer> bestUnitScores = null;

            for (Unit unit : movableUnits) {
                int row = GameBoard.getUnitRow(unit);
                int col = GameBoard.getUnitCol(unit);

                int totalScore = INITIAL_VALUE;

                List<Integer> scores = calculateUnitScores(unit, row, col);

                for (int score : scores) {
                    if (score > GameMessages.MIN_INT) {
                        totalScore += score;
                    }
                }

                if (bestUnit == null || totalScore > maxScore) {
                    maxScore = totalScore;
                    bestUnit = unit;
                    bestUnitScores = scores;
                }
            }

            if (bestUnit == null) {
                break;
            }

            int selectedActionIndex = getSelectedActionIndex(bestUnitScores);

            int bestUnitRow = GameBoard.getUnitRow(bestUnit);
            int bestUnitCol = GameBoard.getUnitCol(bestUnit);
            String startCoord = getCoordinateString(bestUnitRow, bestUnitCol);

            GameState.setSelectedSquare(startCoord);
            GameState.setSelectedRow(bestUnitRow);
            GameState.setSelectedColumn(bestUnitCol);

            executeUnitAction(selectedActionIndex, bestUnitRow, bestUnitCol, bestUnit, startCoord);
        }
    }

    private static List<TargetSquare> getBestKingTargets(int enemyKingRow, int enemyKingCol, Unit king) {
        List<TargetSquare> validTargets = new ArrayList<>();
        int maxScore = Integer.MIN_VALUE;

        for (int i = START_INDEX; i < GameLogicAI.DIRECTIONS.length; i++) {

            int targetRow = enemyKingRow + GameLogicAI.DIRECTIONS[i][ROW_INDEX];
            int targetCol = enemyKingCol + GameLogicAI.DIRECTIONS[i][COL_INDEX];

            if (targetRow < MIN_COORDINATE || targetRow >= GameBoard.DIMENSION
                    || targetCol < MIN_COORDINATE || targetCol >= GameBoard.DIMENSION) {
                continue;
            }

            Unit targetUnit = GameBoard.getUnitAt(targetRow, targetCol);

            if (targetUnit != null && targetUnit.getTeam().equals(GameEngine.getTeam1())) {
                continue;
            }

            int score = getKingScore(targetUnit, king, targetRow, targetCol);
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

    private static int getKingScore(Unit targetUnit, Unit king, int targetRow, int targetCol) {
        int[] aiKingPos = GameBoard.getEnemyKingPosition();
        int distance = (targetRow == aiKingPos[ROW_INDEX] && targetCol == aiKingPos[COL_INDEX])
                ? SAME_SQUARE_DISTANCE : ADJACENT_DISTANCE;
        int fellowsPresent = INITIAL_VALUE;

        if (targetUnit != null && targetUnit.getTeam().equals(GameEngine.getTeam2()) && targetUnit != king) {
            fellowsPresent = FELLOW_PRESENT_VALUE;
        }

        int[] counts = countKingNeighbours(targetRow, targetCol, king);
        int enemies = counts[ENEMIES_INDEX];
        int fellows = counts[FELLOWS_INDEX];

        return fellows - (ENEMY_PENALTY_MULTIPLIER * enemies) - distance - (FELLOW_KING_PRESENCE_PENALTY * fellowsPresent);
    }

    private static int[] countKingNeighbours(int targetRow, int targetCol, Unit king) {
        int enemies = INITIAL_VALUE;
        int fellows = INITIAL_VALUE;

        for (int[] dir : KING_NEIGHBOUR_DIRS) {

            Unit adjacentUnit = getValidAdjacentUnit(targetRow + dir[ROW_INDEX], targetCol + dir[COL_INDEX], king);

            if (adjacentUnit != null) {
                if (adjacentUnit.getTeam().equals(GameEngine.getTeam1())) {
                    enemies++;
                } else if (adjacentUnit.getTeam().equals(GameEngine.getTeam2())) {
                    fellows++;
                }
            }
        }
        return new int[]{enemies, fellows};
    }

    private static Unit getValidAdjacentUnit(int row, int col, Unit king) {
        if (row >= MIN_COORDINATE && row < GameBoard.DIMENSION && col >= MIN_COORDINATE && col < GameBoard.DIMENSION) {
            Unit unit = GameBoard.getUnitAt(row, col);
            if (unit != null && unit != king) {
                return unit;
            }
        }
        return null;
    }

    private static List<Unit> getMovableUnits() {
        List<Unit> movableUnits = new ArrayList<>();
        for (int row = START_INDEX; row < GameBoard.DIMENSION; row++) {
            for (int col = START_INDEX; col < GameBoard.DIMENSION; col++) {
                Unit unit = GameBoard.getUnitAt(row, col);
                if (unit != null && unit.getTeam().equals(GameEngine.getTeam2())
                        && !unit.getRole().equals(GameMessages.KING) && !unit.hasMovedThisTurn()) {
                    movableUnits.add(unit);
                }
            }
        }
        return movableUnits;
    }

    /**
     * Converts a row and column index into a standard board coordinate string.
     * @param row the row index (0-6)
     * @param col the column index (0-6)
     * @return the formatted coordinate string (e.g., "A7", "D4")
     */
    static String getCoordinateString(int row, int col) {
        char colChar = (char) (GameMessages.CHAR_BASE + col);
        int rowNum = ROW_INVERSION_BASE - row;

        return "" + colChar + rowNum;
    }

    private static void executeUnitAction(int selectedActionIndex, int bestUnitRow, int bestUnitCol, Unit bestUnit, String startCoord) {
        if (selectedActionIndex >= MIN_MOVE_ACTION_INDEX && selectedActionIndex <= MAX_MOVE_ACTION_INDEX) {
            int targetRow = bestUnitRow + UNIT_ACTION_DIRS[selectedActionIndex][ROW_INDEX];
            int targetCol = bestUnitCol + UNIT_ACTION_DIRS[selectedActionIndex][COL_INDEX];
            String targetCoord = getCoordinateString(targetRow, targetCol);

            Unit targetUnit = GameBoard.getUnitAt(targetRow, targetCol);
            MovementController.executeMove(targetCoord, targetUnit, targetRow, targetCol, bestUnit);
        } else if (selectedActionIndex == BLOCK_ACTION_INDEX) {
            bestUnit.setBlocking(true);
            bestUnit.setHasMovedThisTurn(true);
            Output.printBlock(bestUnit.getUnitName(), startCoord);
            GameUI.updateDisplay();
        } else if (selectedActionIndex == STAY_ACTION_INDEX) {
            Unit targetUnit = GameBoard.getUnitAt(bestUnitRow, bestUnitCol);
            MovementController.executeMove(startCoord, targetUnit, bestUnitRow, bestUnitCol, bestUnit);
        }
    }

    private static List<Integer> calculateUnitScores(Unit unit, int row, int col) {
        List<Integer> scores = new ArrayList<>();

        scores.add(AIScoreCalculator.getDirectionalScore(unit, row, col, DIR_NEG, DIR_ZERO));
        scores.add(AIScoreCalculator.getDirectionalScore(unit, row, col, DIR_ZERO, DIR_POS));
        scores.add(AIScoreCalculator.getDirectionalScore(unit, row, col, DIR_POS, DIR_ZERO));
        scores.add(AIScoreCalculator.getDirectionalScore(unit, row, col, DIR_ZERO, DIR_NEG));
        scores.add(AIScoreCalculator.getBlockScore(unit, row, col));
        scores.add(AIScoreCalculator.getEnPlaceScore(unit, row, col));
        return scores;
    }

    private static int getSelectedActionIndex(List<Integer> bestUnitScores) {
        int selectedActionIndex = INVALID_ACTION_INDEX;
        int totalWeight = INITIAL_VALUE;
        List<Integer> validWeights = new ArrayList<>();

        for (int score : bestUnitScores) {
            int weight = Math.max(WEIGHT_FLOOR, score);
            validWeights.add(weight);
            totalWeight += weight;
        }

        if (totalWeight == INITIAL_VALUE) {
            selectedActionIndex = BLOCK_ACTION_INDEX;
        } else {
            int randomWeight = RandomGenerator.randomIntegerPick(RANDOM_MIN_BOUND, totalWeight + RANDOM_OFFSET);
            int runningSum = INITIAL_VALUE;
            for (int i = START_INDEX; i < validWeights.size(); i++) {
                runningSum += validWeights.get(i);
                if (randomWeight <= runningSum && validWeights.get(i) > WEIGHT_FLOOR) {
                    selectedActionIndex = i;
                    break;
                }
            }
        }
        return selectedActionIndex;
    }
}
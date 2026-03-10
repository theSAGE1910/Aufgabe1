package edu.kit.kastel.ai;

import edu.kit.kastel.GameBoard;
import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameMessages;
import edu.kit.kastel.Unit;

/**
 * Utility class for calculating heuristic scores for various AI unit actions.
 * The AI uses these scores to evaluate and compare the desirability of different
 * moves, such as moving to an empty square, attacking, merging, blocking, or
 * staying in place.
 * @author uxuwg
 * @version 0.7
 */
public final class AIScoreCalculator {

    private static final int UNKNOWN_TARGET_PENALTY = 500;

    private static final int MIN_COORDINATE = 0;
    private static final int INITIAL_VALUE = 0;

    private static final int ROW_INDEX = 0;
    private static final int COL_INDEX = 1;

    private static final int EMPTY_SQUARE_STEP_MULTIPLIER = 10;
    private static final int COMBAT_SCORE_MULTIPLIER = 2;
    private static final int SCORE_DIVISOR = 100;

    private static final int MIN_BLOCK_SCORE = 1;
    private static final int MIN_EN_PLACE_SCORE = 0;

    private static final int[][] ORTHOGONAL_DIRS = {
            {-1, 0}, {0, 1}, {1, 0}, {0, -1}
    };

    private AIScoreCalculator() {
    }

    /**
     * Calculates the heuristic score for moving a unit in a specific direction.
     * Evaluates the target square to determine if the move results in moving to an
     * empty space, merging with a friendly unit, or engaging in combat.
     * @param unit the AI unit evaluating the move
     * @param row the current row index of the unit
     * @param col the current column index of the unit
     * @param rowDir the row direction modifier (e.g., -1 for up, 1 for down)
     * @param colDir the column direction modifier (e.g., -1 for left, 1 for right)
     * @return the calculated score for the action, or -9999999 if the target is out of bounds
     */
    static int getDirectionalScore(Unit unit, int row, int col, int rowDir, int colDir) {
        int targetRow = row + rowDir;
        int targetCol = col + colDir;

        if (targetRow < MIN_COORDINATE || targetRow >= GameBoard.DIMENSION
                || targetCol < MIN_COORDINATE || targetCol >= GameBoard.DIMENSION) {
            return GameMessages.MIN_INT;
        }

        int score;
        Unit target = GameBoard.getUnitAt(targetRow, targetCol);

        if (target == null) {
            score = getEmptySquareScore(unit, targetRow, targetCol);
        } else {
            boolean isMoverKing = Unit.isKing(unit);
            boolean isTargetKing = Unit.isKing(target);
            boolean isSameTeam = unit.getTeam().equals(target.getTeam());

            if (isMoverKing && !isSameTeam || !isMoverKing && isTargetKing && isSameTeam) {
                score = GameMessages.MIN_INT;
            } else if (isSameTeam) {
                score = getMergeScore(unit, target);
            } else {
                score = getCombatScore(unit, target);
            }
        }

        return score;
    }

    private static int getEmptySquareScore(Unit unit, int targetRow, int targetCol) {
        int[] playerKing = GameBoard.getPlayerKingPosition();
        if (playerKing == null) {
            return INITIAL_VALUE;
        }
        int steps = Math.abs(targetRow - playerKing[ROW_INDEX]) + Math.abs(targetCol - playerKing[COL_INDEX]);
        int enemies = INITIAL_VALUE;

        for (int[] dir : ORTHOGONAL_DIRS) {
            int adjRow = targetRow + dir[ROW_INDEX];
            int adjCol = targetCol + dir[COL_INDEX];

            if (adjRow >= MIN_COORDINATE && adjRow < GameBoard.DIMENSION
                    && adjCol >= MIN_COORDINATE && adjCol < GameBoard.DIMENSION) {
                Unit adjUnit = GameBoard.getUnitAt(adjRow, adjCol);
                if (adjUnit != null && unit.getTeam().equals(GameEngine.getTeam1())) {
                    enemies++;
                }
            }
        }
        return (EMPTY_SQUARE_STEP_MULTIPLIER * steps) - enemies;
    }

    private static int getCombatScore(Unit unit, Unit target) {
        int unitAtk = unit.getAtk();
        if (Unit.isKing(unit)) {
            return unitAtk;
        } else if (!target.isFaceUp()) {
            return unitAtk - UNKNOWN_TARGET_PENALTY;
        } else if (target.isBlocking()) {
            return unitAtk - target.getDef();
        } else {
            return COMBAT_SCORE_MULTIPLIER * (unitAtk - target.getAtk());
        }
    }

    private static int getMergeScore(Unit unit, Unit target) {
        Unit merged = unit.mergeUnits(unit, target);
        if (merged != null) {
            return merged.getAtk() + merged.getDef() - unit.getAtk() - unit.getDef();
        } else {
            return -target.getAtk() - target.getDef();
        }
    }

    /**
     * Calculates the heuristic score for an AI unit choosing to take the block action.
     * The score is determined by comparing the unit's defense against the highest
     * attack value of any enemy unit in its line of sight.
     * @param unit the AI unit evaluating the block action
     * @param row the current row index of the unit
     * @param col the current column index of the unit
     * @return the calculated score for blocking (minimum of 1)
     */
    static int getBlockScore(Unit unit, int row, int col) {
        int maxEnemyAtk = getHighestEnemyAtkInLine(row, col);
        return Math.max(MIN_BLOCK_SCORE, (unit.getDef() - maxEnemyAtk) / SCORE_DIVISOR);
    }

    /**
     * Calculates the heuristic score for an AI unit choosing to stay en place
     * (not moving or taking any action). Evaluated against the highest enemy attack
     * in its line of sight.
     * @param unit the AI unit evaluating the en place action
     * @param row the current row index of the unit
     * @param col the current column index of the unit
     * @return the calculated score for staying en place (minimum of 0)
     */
    static int getEnPlaceScore(Unit unit, int row, int col) {
        int maxEnemyAtk = getHighestEnemyAtkInLine(row, col);
        return Math.max(MIN_EN_PLACE_SCORE, (unit.getAtk() - maxEnemyAtk) / SCORE_DIVISOR);
    }

    private static int getHighestEnemyAtkInLine(int row, int col) {
        int maxAtk = INITIAL_VALUE;
        for (int[] dir : ORTHOGONAL_DIRS) {
            int targetRow = row + dir[ROW_INDEX];
            int targetCol = col + dir[COL_INDEX];

            while (targetRow >= MIN_COORDINATE && targetRow < GameBoard.DIMENSION
                    && targetCol >= MIN_COORDINATE && targetCol < GameBoard.DIMENSION) {
                Unit target = GameBoard.getUnitAt(targetRow, targetCol);

                if (target != null) {
                    if (target.getTeam().equals(GameEngine.getTeam1())) {
                        int perceivedAtk = target.isFaceUp() ? target.getAtk() : INITIAL_VALUE;
                        if (perceivedAtk > maxAtk) {
                            maxAtk = perceivedAtk;
                        }
                    }
                    break;
                }

                targetRow += dir[ROW_INDEX];
                targetCol += dir[COL_INDEX];
            }
        }
        return maxAtk;
    }
}
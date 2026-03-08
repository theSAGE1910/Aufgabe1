package edu.kit.kastel.ai;

import edu.kit.kastel.GameBoard;
import edu.kit.kastel.GameEngine;
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

        if (targetRow < 0 || targetRow >= GameBoard.DIMENSION || targetCol < 0 || targetCol >= GameBoard.DIMENSION) {
            return -9999999;
        }

        Unit target = GameBoard.getUnitAt(targetRow, targetCol);

        if (target != null) {
            boolean isMoverKing = Unit.isKing(unit);
            boolean isTargetKing = Unit.isKing(target);
            boolean isSameTeam = unit.getTeam().equals(target.getTeam());

            if (isMoverKing && !isSameTeam) {
                return -9999999;
            }
            if (!isMoverKing && isTargetKing && isSameTeam) {
                return -9999999;
            }
        } else {
            return getEmptySquareScore(unit, targetRow, targetCol);
        }

        if (target.getTeam().equals(unit.getTeam())) {
            return getMergeScore(unit, target);
        } else {
            return getCombatScore(unit, target);
        }
    }

    private static int getEmptySquareScore(Unit unit, int targetRow, int targetCol) {
        int[] playerKing = GameBoard.getPlayerKingPosition();
        if (playerKing == null) {
            return 0;
        }
        int steps = Math.abs(targetRow - playerKing[0]) + Math.abs(targetCol - playerKing[1]);
        int enemies = 0;
        int[][] ortho = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        for (int[] dir : ortho) {
            int adjRow = targetRow + dir[0];
            int adjCol = targetCol + dir[1];

            if (adjRow >= 0 && adjRow < GameBoard.DIMENSION && adjCol >= 0 && adjCol < GameBoard.DIMENSION) {
                Unit adjUnit = GameBoard.getUnitAt(adjRow, adjCol);
                if (adjUnit != null && adjUnit.getTeam().equals(GameEngine.team1)) {
                    enemies++;
                }
            }
        }
        return (10 * steps) - enemies;
    }

    private static int getCombatScore(Unit unit, Unit target) {
        int unitAtk = unit.getAtk();
        if (Unit.isKing(unit)) {
            return unitAtk;
        } else if (!target.isFaceUp()) {
            return unitAtk - 500;
        } else if (target.isBlocking()) {
            return unitAtk - target.getDef();
        } else {
            return 2 * (unitAtk - target.getAtk());
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
        return Math.max(1, (unit.getDef() - maxEnemyAtk) / 100);
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
        return Math.max(0, (unit.getAtk() - maxEnemyAtk) / 100);
    }

    private static int getHighestEnemyAtkInLine(int row, int col) {
        int maxAtk = 0;
        int[][] dirs = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        for (int[] dir : dirs) {
            int targetRow = row + dir[0];
            int targetCol = col + dir[1];
            while (targetRow >= 0 && targetRow < GameBoard.DIMENSION && targetCol >= 0 && targetCol < GameBoard.DIMENSION) {
                Unit target = GameBoard.getUnitAt(targetRow, targetCol);

                if (target != null) {
                    if (target.getTeam().equals(GameEngine.team1)) {
                        if (target.getAtk() > maxAtk) {
                            maxAtk = target.getAtk();
                        }
                    }
                    break;
                }

                targetRow += dir[0];
                targetCol += dir[1];
            }
        }
        return maxAtk;
    }
}

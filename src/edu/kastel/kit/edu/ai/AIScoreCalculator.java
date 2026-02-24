package edu.kastel.kit.edu.ai;

import edu.kastel.kit.edu.Commands;
import edu.kastel.kit.edu.GameBoard;
import edu.kastel.kit.edu.GameEngine;
import edu.kastel.kit.edu.Unit;

public class AIScoreCalculator {
    private static int getDirectionalScore(Unit unit, int row, int col, int rowDir, int colDir) {
        int targetRow = row + rowDir;
        int targetCol = col + colDir;
        if (targetRow < 0 || targetRow >= GameBoard.DIMENSION || targetCol < 0 || targetCol >= GameBoard.DIMENSION) {
            return -9999999;
        }

        Unit target = GameBoard.getUnitAt(targetRow, targetCol);
        if (target != null) {
            if (target.getTeam().equals(unit.getTeam())) {
                Unit merged = unit.mergeUnits(unit, target);
                if (merged != null) {
                    return merged.getAtk() + merged.getDef() - unit.getAtk() - unit.getDef();
                } else {
                    return -target.getAtk() - target.getDef();
                }
            } else {
                if (Commands.isKing(unit)) {
                    return unit.getAtk();
                } else if (!target.isFaceUp()) {
                    return unit.getAtk() - 500;
                } else if (target.isBlocking()) {
                    return unit.getAtk() - target.getDef();
                } else {
                    return 2 * (unit.getAtk() - target.getAtk());
                }
            }
        } else {
            int[] playerKing = getPlayerKingPosition();
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
                    if (adjUnit != null && unit.getTeam().equals(GameEngine.team1)) {
                        enemies++;
                    }
                }
                return (10 * steps) - enemies;
            }
        }
        return 0;
    }

    private static int getBlockScore(Unit unit, int row, int col) {
        int maxEnemyAtk = getHighestEnemyAtkInLine(unit, row, col);
        return Math.max(1, (unit.getDef() - maxEnemyAtk) / 100);
    }

    private static int getEnPlaceScore(Unit unit, int row, int col) {
        int maxEnemyAtk = getHighestEnemyAtkInLine(unit, row, col);
        return Math.max(0, (unit.getAtk() - maxEnemyAtk) / 100);
    }

    private static int getHighestEnemyAtkInLine(Unit unit, int row, int col) {
        int maxAtk = 0;
        int[][] dirs = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        for (int[] dir : dirs) {
            int targetRow = row + dir[0];
            int targetCol = col + dir[1];
            if (targetRow >= 0 && targetRow < GameBoard.DIMENSION && targetCol >= 0 && targetCol < GameBoard.DIMENSION) {
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

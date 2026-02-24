package edu.kastel.kit.edu.ai;

import edu.kastel.kit.edu.*;

import java.util.ArrayList;
import java.util.List;

public class AIMovement {
    public static void moveFarmerKing() {
        int[] enemyKingPos = GameBoard.getEnemyKingPosition();
        if (enemyKingPos == null) {
            return;
        }

        int enemyKingRow = enemyKingPos[0];
        int enemyKingCol = enemyKingPos[1];
        Unit king = GameBoard.getUnitAt(enemyKingRow, enemyKingCol);

        List<TargetSquare> validTargets = getBestKingTargets(enemyKingRow, enemyKingCol, king);

        TargetSquare targetSquare = null;
        if (validTargets.size() == 1) {
            targetSquare = validTargets.getFirst();
        } else if (validTargets.size() > 1) {
            int draw = RandomGenerator.randomIntegerPick(1, validTargets.size() + 1);
            targetSquare = validTargets.get(draw - 1);
        }

        if (targetSquare != null) {
            String startCoord = GameLogicAI.getCoordinateString(enemyKingRow, enemyKingCol);
            String targetCoord = GameLogicAI.getCoordinateString(targetSquare.row, targetSquare.col);

            Commands.selectedSquare = startCoord;
            Commands.selectedRow = enemyKingRow;
            Commands.selectedColumn = enemyKingCol;

            MovementController.handleMove(targetCoord);
        }
    }

    public static void moveUnits() {
        while (Commands.isRunning) {
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

                int totalScore = 0;

                List<Integer> scores = calculateUnitScores(unit, row, col);

                for (int score : scores) {
                    if (score > -999999) {
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
            String startCoord = GameLogicAI.getCoordinateString(bestUnitRow, bestUnitCol);

            Commands.selectedSquare = startCoord;
            Commands.selectedRow = bestUnitRow;
            Commands.selectedColumn = bestUnitCol;

            executeUnitAction(selectedActionIndex, bestUnitRow, bestUnitCol, bestUnit, startCoord);
        }
    }

    private static List<TargetSquare> getBestKingTargets(int enemyKingRow, int enemyKingCol, Unit king) {
        List<TargetSquare> validTargets = new ArrayList<>();
        int maxScore = Integer.MIN_VALUE;

        for (int i = 0; i < GameLogicAI.DIRECTIONS.length; i++) {
            int targetRow = enemyKingRow + GameLogicAI.DIRECTIONS[i][0];
            int targetCol = enemyKingCol + GameLogicAI.DIRECTIONS[i][1];

            if (targetRow < 0 || targetRow >= GameBoard.DIMENSION || targetCol < 0 || targetCol >= GameBoard.DIMENSION) {
                continue;
            }

            Unit targetUnit = GameBoard.getUnitAt(targetRow, targetCol);

            if (targetUnit != null && targetUnit.getTeam().equals(GameEngine.team1)) {
                continue;
            }

            int score = getKingScore(i, targetUnit, king, targetRow, targetCol);
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

    private static int getKingScore(int dirIndex, Unit targetUnit, Unit king, int targetRow, int targetCol) {
        int distance = (dirIndex == 4) ? 0 : 1;
        int fellowsPresent = 0;

        if (targetUnit != null && targetUnit.getTeam().equals(GameEngine.team2) && targetUnit != king) {
            fellowsPresent = 1;
        }

        int[] counts = countKingNeighbours(targetRow, targetCol);
        int enemies = counts[0];
        int fellows = counts[1];

        return -fellows - 2 * enemies - distance - 3 * fellowsPresent;
    }

    private static int[] countKingNeighbours(int targetRow, int targetCol) {
        int enemies = 0;
        int fellows = 0;

        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) {
                if (row == 0 && col == 0) {
                    continue;
                }
                int adjRow = targetRow + row;
                int adjCol = targetCol + col;

                if (adjRow >= 0 && adjRow < GameBoard.DIMENSION && adjCol >= 0 && adjCol < GameBoard.DIMENSION) {
                    Unit adjacentUnit = GameBoard.getUnitAt(adjRow, adjCol);
                    if (adjacentUnit != null) {
                        if (adjacentUnit.getTeam().equals(GameEngine.team1)) {
                            enemies++;
                        } else if (adjacentUnit.getTeam().equals(GameEngine.team2)) {
                            fellows++;
                        }
                    }
                }
            }
        }
        return new int[]{enemies, fellows};
    }

    private static List<Unit> getMovableUnits() {
        List<Unit> movableUnits = new ArrayList<>();
        for (int row = 0; row < GameBoard.DIMENSION; row++) {
            for (int col = 0; col < GameBoard.DIMENSION; col++) {
                Unit unit = GameBoard.getUnitAt(row, col);
                if (unit != null && unit.getTeam().equals(GameEngine.team2) && !unit.getRole().equals("King") && !unit.hasMovedThisTurn()) {
                    movableUnits.add(unit);
                }
            }
        }
        return movableUnits;
    }

    private static void executeUnitAction(int selectedActionIndex, int bestUnitRow, int bestUnitCol, Unit bestUnit, String startCoord) {
        if (selectedActionIndex >= 0 && selectedActionIndex <= 3) {
            int[][] dirs = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
            int targetRow = bestUnitRow + dirs[selectedActionIndex][0];
            int targetCol = bestUnitCol + dirs[selectedActionIndex][1];
            String targetCoord = GameLogicAI.getCoordinateString(targetRow, targetCol);
            MovementController.handleMove(targetCoord);
        } else if (selectedActionIndex == 4) {
            bestUnit.setBlocking(true);
            bestUnit.setHasMovedThisTurn(true);
            Output.printBlock(bestUnit.getUnitName(), startCoord);
            Commands.updateDisplay();
        } else if (selectedActionIndex == 5) {
            MovementController.handleMove(startCoord);
        }
    }

    private static List<Integer> calculateUnitScores(Unit unit, int row, int col) {
        List<Integer> scores = new ArrayList<>();

        scores.add(AIScoreCalculator.getDirectionalScore(unit, row, col, -1, 0));
        scores.add(AIScoreCalculator.getDirectionalScore(unit, row, col, 0, 1));
        scores.add(AIScoreCalculator.getDirectionalScore(unit, row, col, 1, 0));
        scores.add(AIScoreCalculator.getDirectionalScore(unit, row, col, 0, -1));
        scores.add(AIScoreCalculator.getBlockScore(unit, row, col));
        scores.add(AIScoreCalculator.getEnPlaceScore(unit, row, col));
        return scores;
    }

    private static int getSelectedActionIndex(List<Integer> bestUnitScores) {
        int selectedActionIndex = -1;
        int totalWeight = 0;
        List<Integer> validWeights = new ArrayList<>();

        for (int score : bestUnitScores) {
            int weight = Math.max(0, score);
            if (score <= -999999) {
                weight = 0;
            }
            validWeights.add(weight);
            totalWeight += weight;
        }

        if (totalWeight == 0) {
            selectedActionIndex = 4;
        } else {
            int randomWeight = RandomGenerator.randomIntegerPick(1, totalWeight + 1);
            int runningSum = 0;
            for (int i = 0; i < validWeights.size(); i++) {
                runningSum += validWeights.get(i);
                if (randomWeight <= runningSum && validWeights.get(i) > 0) {
                    selectedActionIndex = i;
                    break;
                }
            }
        }
        return selectedActionIndex;
    }
}

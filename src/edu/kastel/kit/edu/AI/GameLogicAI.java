package edu.kastel.kit.edu.AI;

import edu.kastel.kit.edu.*;

import java.util.ArrayList;
import java.util.List;

public class GameLogicAI {
    private static int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}, {0, 0}};

    public static void executeTurn() {
        if (!Commands.isRunning) {
            return;
        }
        moveFarmerKing();

        if (!Commands.isRunning) {
            return;
        }
        placeUnit();

        if (!Commands.isRunning) {
            return;
        }
        moveUnits();

        if (!Commands.isRunning) {
            return;
        }
        discardCard();
    }

    public static void moveFarmerKing() {
        int[] enemyKingPos = getEnemyKingPosition();
        if (enemyKingPos == null) {
            return;
        }

        int enemyKingRow = enemyKingPos[0];
        int enemyKingCol = enemyKingPos[1];
        Unit king = GameBoard.getUnitAt(enemyKingRow, enemyKingCol);

        List<TargetSquare> validTargets = new ArrayList<>();
        int maxScore = Integer.MIN_VALUE;

        for (int i = 0; i < DIRECTIONS.length; i++) {
            int targetRow = enemyKingRow + DIRECTIONS[i][0];
            int targetCol = enemyKingCol + DIRECTIONS[i][1];

            if (targetRow < 0 || targetRow >= GameBoard.DIMENSION || targetCol < 0 || targetCol >= GameBoard.DIMENSION) {
                continue;
            }

            Unit targetUnit = GameBoard.getUnitAt(targetRow, targetCol);

            if (targetUnit != null && targetUnit.getTeam().equals(GameEngine.team1)) {
                continue;
            }

            int distance = (i == 4) ? 0 : 1;
            int enemies = 0;
            int fellows = 0;
            int fellowsPresent = 0;

            if (targetUnit != null && targetUnit.getTeam().equals(GameEngine.team2) && targetUnit != king) {
                fellowsPresent = 1;
            }

            for (int row = -1; row <= 1; row++) {
                for (int col = -1; col <= 1; col++) {
                    if (row == 0 && col == 0) {
                        continue;
                    }
                    int adjRow = targetRow + row;
                    int adjCol = targetCol + col;

                    if (adjRow >= 0 && adjRow <= 6 && adjCol >= 0 && adjCol <= 6) {
                        Unit adjUnit = GameBoard.getUnitAt(adjRow, adjCol);
                        if (adjUnit != null) {
                            if (adjUnit.getTeam().equals(GameEngine.team1)) {
                                enemies++;
                            } else if (adjUnit.getTeam().equals(GameEngine.team2) && adjUnit != king) {
                                fellows++;
                            }
                        }
                    }
                }
            }

            int score = -fellows - 2 * enemies - distance - 3 * fellowsPresent;
            if (score > maxScore) {
                maxScore = score;
                validTargets.clear();
                validTargets.add(new TargetSquare(targetRow, targetCol));
            } else if (score == maxScore) {
                validTargets.add(new TargetSquare(targetRow, targetCol));
            }
        }

        TargetSquare selectedTarget = null;
        if (validTargets.size() == 1) {
            selectedTarget = validTargets.get(0);
        } else if (validTargets.size() > 1) {
            int draw = RandomGenerator.randomIntegerPick(1, validTargets.size() + 1);
            selectedTarget = validTargets.get(draw - 1);
        }

        if (selectedTarget != null) {
            String startCoord = getCoordinateString(enemyKingRow, enemyKingCol);
            String targetCoord = getCoordinateString(selectedTarget.row, selectedTarget.col);

            Commands.selectedSquare = startCoord;
            Commands.selectedRow = enemyKingRow;
            Commands.selectedColumn = enemyKingCol;

            MovementController.handleMove(targetCoord);
        }
    }

    public static void placeUnit() {

    }

    public static void moveUnits() {

    }

    public static void discardCard() {

    }

    private static int[] getEnemyKingPosition() {
        for (int row = 0; row < GameBoard.DIMENSION; row++) {
            for (int col = 0; col < GameBoard.DIMENSION; col++) {
                Unit unit = GameBoard.getUnitAt(row, col);
                if (unit != null && unit.getTeam().equals(GameEngine.team2) && unit.getRole().equals("King")) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    private static String getCoordinateString(int row, int col) {
        char colChar = (char) ('A' + col);
        int rowNum = 7 - row;

        return "" + colChar + rowNum;
    }

    private static class TargetSquare {
        int row;
        int col;

        public TargetSquare(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}

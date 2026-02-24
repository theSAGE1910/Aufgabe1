package edu.kastel.kit.edu.ai;

import edu.kastel.kit.edu.*;

import java.util.ArrayList;
import java.util.List;

import static edu.kastel.kit.edu.Output.getBoardCount;

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

        TargetSquare targetSquare = null;
        if (validTargets.size() == 1) {
            targetSquare = validTargets.get(0);
        } else if (validTargets.size() > 1) {
            int draw = RandomGenerator.randomIntegerPick(1, validTargets.size() + 1);
            targetSquare = validTargets.get(draw - 1);
        }

        if (targetSquare != null) {
            String startCoord = getCoordinateString(enemyKingRow, enemyKingCol);
            String targetCoord = getCoordinateString(targetSquare.row, targetSquare.col);

            Commands.selectedSquare = startCoord;
            Commands.selectedRow = enemyKingRow;
            Commands.selectedColumn = enemyKingCol;

            MovementController.handleMove(targetCoord);
        }
    }

    public static void placeUnit() {
        int[] aiKingPos = getEnemyKingPosition();
        int[] playerKingPos = getPlayerKingPosition();

        int[][] clockDirs = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
        int[][] orthDirs = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

        List<TargetSquare> validTargets = new ArrayList<>();
        int maxScore = Integer.MIN_VALUE;

        for (int[] dir : clockDirs) {
            int targetRow = aiKingPos[0] + dir[0];
            int targetCol = aiKingPos[1] + dir[1];

            if (targetRow < 0 || targetRow >= GameBoard.DIMENSION || targetCol < 0 || targetCol >= GameBoard.DIMENSION) {
                continue;
            }
            if (GameBoard.getUnitAt(targetRow, targetCol) != null) {
                continue;
            }

            int steps = Math.abs(targetRow - playerKingPos[0]) + Math.abs(targetCol - playerKingPos[1]);
            int enemies = 0;
            int fellows = 0;

            for (int[] orthDir : orthDirs) {
                int adjRow = targetRow + orthDir[0];
                int adjCol = targetCol + orthDir[1];

                if (adjRow >= 0 && adjRow < GameBoard.DIMENSION && adjCol >= 0 && adjCol < GameBoard.DIMENSION) {
                    Unit adjUnit = GameBoard.getUnitAt(adjRow, adjCol);
                    if (adjUnit != null) {
                        if (adjUnit.getTeam().equals(GameEngine.team1)) {
                            enemies++;
                        } else if (adjUnit.getTeam().equals(GameEngine.team2)) {
                            fellows++;
                        }
                    }
                }
            }

            int score = -steps + (2 * enemies) - fellows;

            if (score > maxScore) {
                maxScore = score;
                validTargets.clear();
                validTargets.add(new TargetSquare(targetRow, targetCol));
                } else if (score == maxScore) {
                validTargets.add(new TargetSquare(targetRow, targetCol));
            }
        }
        if (validTargets.isEmpty()) {
            return;
        }

        TargetSquare targetSquare = null;
        if (validTargets.size() == 1) {
            targetSquare = validTargets.get(0);
        } else if (validTargets.size() > 1) {
            int draw = RandomGenerator.randomIntegerPick(1, validTargets.size() + 1);
            targetSquare = validTargets.get(draw - 1);
        }

        List<Unit> hand = GameEngine.team2.hand.hand;

        if (hand.isEmpty()) {
            return;
        }

        List<Integer> weights = new ArrayList<>();
        int totalWeight = 0;

        for (Unit unit : hand) {
            int weight = unit.getAtk();
            weights.add(weight);
            totalWeight += weight;
        }

        int selectedCardIndex = 0;
        if (totalWeight > 0) {
            int randomWeight = RandomGenerator.randomIntegerPick(1, totalWeight + 1);
            int runningSum = 0;
            for (int i = 0; i < weights.size(); i++) {
                runningSum += weights.get(i);
                if (randomWeight < runningSum) {
                    selectedCardIndex = i;
                    break;
                }
            }
        }

        Unit unitToPlace = hand.get(selectedCardIndex);
        String coord = getCoordinateString(targetSquare.row, targetSquare.col);

        System.out.println(GameEngine.team2.getName() + " places " + unitToPlace.getUnitName() + " to " + coord + ".");

        unitToPlace.setTeam(GameEngine.team2);
        unitToPlace.setHasMovedThisTurn(true);
        hand.remove(unitToPlace);

        int boardCount = getBoardCount(GameEngine.team2);
        if (boardCount >= 5) {
            GameBoard.setUnitAt(targetSquare.row, targetSquare.col, null);
        } else {
            GameBoard.setUnitAt(targetSquare.row, targetSquare.col, unitToPlace);
        }

        Commands.selectedSquare = coord;
        Commands.selectedRow = targetSquare.row;
        Commands.selectedColumn = targetSquare.col;
        Commands.updateDisplay();
    }

    public static void moveUnits() {
        while (Commands.isRunning) {
            List<Unit> movableUnits = new ArrayList<>();
            for (int row = 0; row < GameBoard.DIMENSION; row++) {
                for (int col = 0; col < GameBoard.DIMENSION; col++) {
                    Unit unit = GameBoard.getUnitAt(row, col);
                    if (unit != null && unit.getTeam().equals(GameEngine.team2) && !unit.getRole().equals("King") && !unit.hasMovedThisTurn()) {
                        movableUnits.add(unit);
                    }
                }
            }

            if (movableUnits.isEmpty()) {
                break;
            }

            Unit bestUnit = null;
            int maxScore = Integer.MIN_VALUE;
            List<Integer> bestUnitScores = null;

            for (Unit unit : movableUnits) {
                int row = getUnitRow(unit);
                int col = getUnitCol(unit);

                List<Integer> scores = new ArrayList<>();
                int totalScore = 0;

                scores.add(getDirectionalScore(unit, row, col, -1, 0));
                scores.add(getDirectionalScore(unit, row, col, 0, 1));
                scores.add(getDirectionalScore(unit, row, col, 1, 0));
                scores.add(getDirectionalScore(unit, row, col, 0, -1));
                scores.add(getBlockScore(unit, row, col));
                scores.add(getEnPlaceScore(unit, row, col));

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

            int bestUnitRow = getUnitRow(bestUnit);
            int bestUnitCol = getUnitCol(bestUnit);
            String startCoord = getCoordinateString(bestUnitRow, bestUnitCol);

            Commands.selectedSquare = startCoord;
            Commands.selectedRow = bestUnitRow;
            Commands.selectedColumn = bestUnitCol;

            if (selectedActionIndex >=0 && selectedActionIndex <= 3) {
                int[][] dirs = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
                int targetRow = bestUnitRow + dirs[selectedActionIndex][0];
                int targetCol = bestUnitCol + dirs[selectedActionIndex][1];
                String targetCoord = getCoordinateString(targetRow, targetCol);
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
    }

    public static void discardCard() {
        List<Unit> hand = GameEngine.team2.hand.hand;
        if (hand.size() < 5) {
            return;
        }

        int maxWeight = Integer.MIN_VALUE;
        List<Integer> originalWeights = new ArrayList<>()
        for (Unit unit : hand) {
            int weight = unit.getAtk() + unit.getDef();
            originalWeights.add(weight);
            if (weight > maxWeight) {
                maxWeight = weight;
            }
        }

        List<Integer> invertedWeights = new ArrayList<>();
        int totalInvWeight = 0;
        for (int weight : originalWeights) {
            int invWeight = maxWeight - weight;
            invertedWeights.add(invWeight);
            totalInvWeight += invWeight;
        }

        int selectedDiscardIndex = 0;
        if (totalInvWeight > 0) {
            int randomWeight = RandomGenerator.randomIntegerPick(1, totalInvWeight + 1);
            int runningSum = 0;
            for (int i = 0; i < invertedWeights.size(); i++) {
                runningSum += invertedWeights.get(i);
                if (randomWeight <= runningSum) {
                    selectedDiscardIndex = i;
                    break;
                }
            }
        }

        Unit unitToDiscard = hand.get(selectedDiscardIndex);
        hand.remove(unitToDiscard);
        Output.printDiscard(GameEngine.team2.getName(), unitToDiscard);
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

    private static int[] getPlayerKingPosition() {
        for (int row = 0; row < GameBoard.DIMENSION; row++) {
            for (int col = 0; col < GameBoard.DIMENSION; col++) {
                Unit unit = GameBoard.getUnitAt(row, col);
                if (unit != null && unit.getTeam().equals(GameEngine.team1) && unit.getRole().equals("King")) {
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

    private static int getUnitRow(Unit unit) {
        for (int row = 0; row < GameBoard.DIMENSION; row++) {
            for (int col = 0; col < GameBoard.DIMENSION; col++) {
                if (GameBoard.getUnitAt(row, col) == unit) {
                    return row;
                }
            }
        }
        return -1;
    }

    //try to make these two methods into one, by passing key row and col hehe:)

    private static int getUnitCol(Unit unit) {
        for (int row = 0; row < GameBoard.DIMENSION; row++) {
            for (int col = 0; col < GameBoard.DIMENSION; col++) {
                if (GameBoard.getUnitAt(row, col) == unit) {
                    return col;
                }
            }
        }
        return -1;
    }

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
            int[][] ortho = {{-1,0},{0,1},{1,0},{0,-1}};
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

    private static class TargetSquare {
        int row;
        int col;

        public TargetSquare(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}

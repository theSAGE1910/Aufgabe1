package edu.kit.kastel.ai;

import edu.kit.kastel.*;

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

        TargetSquare targetSquare = validTargets.get(0);
        if (validTargets.size() > 1) {
            int draw = RandomGenerator.randomIntegerPick(1, validTargets.size() + 1);
            targetSquare = validTargets.get(draw - 1);
        }

        List<Unit> hand = GameEngine.team2.getHand().getHand();
        if (hand.isEmpty()) {
            return;
        }

        Unit unitToPlace = selectCardToPlace(hand);
        String coord = AIMovement.getCoordinateString(targetSquare.getRow(), targetSquare.getCol());

        Output.printPlacement(GameEngine.team2.getName(), unitToPlace, coord);

        unitToPlace.setTeam(GameEngine.team2);
        unitToPlace.setHasMovedThisTurn(false);
        hand.remove(unitToPlace);

        int boardCount = getBoardCount(GameEngine.team2);
        if (boardCount >= 5) {
            GameBoard.setUnitAt(targetSquare.getRow(), targetSquare.getCol(), null);
            Output.printElimination(unitToPlace.getUnitName());
        } else {
            GameBoard.setUnitAt(targetSquare.getRow(), targetSquare.getCol(), unitToPlace);
        }

        GameState.selectedSquare = coord;
        GameState.selectedRow = targetSquare.getRow();
        GameState.selectedColumn = targetSquare.getCol();
        GameUI.updateDisplay();
    }

    private static Unit selectCardToPlace(List<Unit> hand) {
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
                if (randomWeight <= runningSum) {
                    selectedCardIndex = i;
                    break;
                }
            }
        }

        return hand.get(selectedCardIndex);
    }

    private static List<TargetSquare> getBestPlacementSquares(int[] aiKingPos, int[] playerKingPos) {
        int[][] clockDirs = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};

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

            int[] counts = countAdjacentUnits(targetRow, targetCol);
            int enemies = counts[0];
            int fellows = counts[1];

            int score = -steps + (2 * enemies) - fellows;

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
        int enemies = 0;
        int fellows = 0;

        int[][] orthDirs = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        for (int[] dir : orthDirs) {
            int adjRow = row + dir[0];
            int adjCol = col + dir[1];

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
        return new int[]{enemies, fellows};
    }
}

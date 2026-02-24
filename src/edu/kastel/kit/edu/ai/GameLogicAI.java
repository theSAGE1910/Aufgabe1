package edu.kastel.kit.edu.ai;

import edu.kastel.kit.edu.*;

public class GameLogicAI {
    static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}, {0, 0}};

    public static void executeTurn() {
        if (!Commands.isRunning) {
            return;
        }
        AIMovement.moveFarmerKing();

        if (!Commands.isRunning) {
            return;
        }
        AIPlacement.placeUnit();

        if (!Commands.isRunning) {
            return;
        }
        AIMovement.moveUnits();

        if (!Commands.isRunning) {
            return;
        }
        AIDiscard.discardCard();
    }

    static String getCoordinateString(int row, int col) {
        char colChar = (char) ('A' + col);
        int rowNum = 7 - row;

        return "" + colChar + rowNum;
    }
}

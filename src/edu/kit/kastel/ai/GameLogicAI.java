package edu.kit.kastel.ai;

import edu.kit.kastel.Commands;

/**
 * A utility class that coordinates the overall sequence of actions for the AI's turn.
 * This class ensures that the AI follows the correct order of operations: moving the king,
 * placing a unit, moving other units, and discarding if necessary.
 * @author uxuwg
 * @version 0.7
 */
public final class GameLogicAI {

    /**
     * An array representing the possible movement directions for the AI.
     * The directions are ordered as: Up (-1, 0), Right (0, 1), Down (1, 0),
     * Left (0, -1), and en place( or no movement) (0, 0).
     */
    static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}, {0, 0}};

    private GameLogicAI() {
    }

    /**
     * Executes the complete turn sequence for the AI player.
     * The sequence halts immediately if the game ends during any of the steps.
     * Order of operations:
     * 1. Move the Farmer King.
     * 2. Place a unit from the hand onto the board.
     * 3. Move all eligible units on the board.
     * 4. Discard a card if the hand is full at the end of the turn.
     */
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
}
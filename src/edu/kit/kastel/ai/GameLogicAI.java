package edu.kit.kastel.ai;

import edu.kit.kastel.GameState;
import edu.kit.kastel.Team;

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
     * Executes the AI's turn by following a structured sequence of actions.
     * @param aiTeam the AI's team object, used to access the hand and update the board state after placement and discarding.
     * @param playerTeam the player's team object, used to evaluate the board state and make informed decisions during the AI's turn.
     */
    public static void executeTurn(Team aiTeam, Team playerTeam) {
        if (!GameState.isIsRunning()) {
            return;
        }
        AIMovement.moveFarmerKing(aiTeam, playerTeam);

        if (!GameState.isIsRunning()) {
            return;
        }
        AIPlacement.placeUnit(aiTeam);

        if (!GameState.isIsRunning()) {
            return;
        }
        AIMovement.moveUnits(aiTeam);

        if (!GameState.isIsRunning()) {
            return;
        }
        AIDiscard.discardCard(aiTeam);
    }
}
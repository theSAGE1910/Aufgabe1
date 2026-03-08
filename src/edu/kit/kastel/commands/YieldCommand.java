package edu.kit.kastel.commands;

import edu.kit.kastel.CommandProcessor;
import edu.kit.kastel.GameEngine;
import edu.kit.kastel.GameState;
import edu.kit.kastel.Hand;
import edu.kit.kastel.Unit;
import edu.kit.kastel.Output;
import edu.kit.kastel.ai.GameLogicAI;

/**
 * Command to end the current team's turn and switch to the other team.
 * @author uxuwg
 * @version 0.9
 */
public class YieldCommand implements Command {
    @Override
    public void execute(String argument) {
        Hand currentHand = GameEngine.activeTeam.getHand();

        if (currentHand.getHand().size() == 5 && argument == null) {
            System.err.println("ERROR: Hand is full. You must specify a card to discard.");
            return;
        } else if (currentHand.getHand().size() < 5 && argument != null) {
            System.err.println("ERROR: Hand is not full. You cannot discard a card.");
            return;
        }

        int discardIndex = 0;
        if (argument != null)  {
            discardIndex = CommandProcessor.parseHandIndex(argument, currentHand);
            if (discardIndex == -1) {
                return;
            }

            Unit unitToDiscard = currentHand.getHand().get(discardIndex);
            currentHand.removeUnitFromHand(unitToDiscard);
            Output.printDiscard(GameEngine.activeTeam.getName(), unitToDiscard);
        }

        GameEngine.resetTeamMovement(GameEngine.activeTeam);
        GameEngine.switchTurn();
        //GameEngine.resetTeamBlocks(GameEngine.activeTeam);

        GameState.hasPlacedThisTurn = false;
        GameState.selectedSquare = null;

        if (GameEngine.activeTeam.equals(GameEngine.team1)) {
            Output.printPlayerTurn();
        } else {
            Output.printEnemyTurn();
        }

        if (!GameEngine.tryDrawCard(GameEngine.activeTeam)) {
            return;
        }
        if (GameEngine.activeTeam.equals(GameEngine.team2)) {
            GameLogicAI.executeTurn();

            if (!GameState.isRunning) {
                return;
            }

            GameEngine.resetTeamMovement(GameEngine.team2);
            GameEngine.switchTurn();
            //GameEngine.resetTeamBlocks(GameEngine.activeTeam);

            GameState.hasPlacedThisTurn = false;
            Output.printPlayerTurn();
            GameState.selectedSquare = null;

            if (!GameEngine.tryDrawCard(GameEngine.team1)) {
                return;
            }
        }
    }
}

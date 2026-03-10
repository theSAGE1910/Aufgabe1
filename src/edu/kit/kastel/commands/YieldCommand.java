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

    private static final String ERROR_HAND_IS_FULL_YOU_MUST_SPECIFY_A_CARD_TO_DISCARD
            = "ERROR: Hand is full. You must specify a card to discard.";
    private static final String ERROR_HAND_IS_NOT_FULL_YOU_CANNOT_DISCARD_A_CARD
            = "ERROR: Hand is not full. You cannot discard a card.";

    @Override
    public void execute(String argument) {
        Hand currentHand = GameEngine.getActiveTeam().getHand();
        
        boolean validToProceed = validateDiscardState(argument, currentHand);

        if (validToProceed && argument != null)  {
            validToProceed = processDiscard(argument, currentHand);
        }

        if (validToProceed) {
            executeTurn();
        }
    }

    private static boolean validateDiscardState(String argument, Hand currentHand) {
        if (currentHand.getHand().size() == 5 && argument == null) {
            System.err.println(ERROR_HAND_IS_FULL_YOU_MUST_SPECIFY_A_CARD_TO_DISCARD);
            return false;
        } else if (currentHand.getHand().size() < 5 && argument != null) {
            System.err.println(ERROR_HAND_IS_NOT_FULL_YOU_CANNOT_DISCARD_A_CARD);
            return false;
        }
        return true;
    }

    private static boolean processDiscard(String argument, Hand currentHand) {
        int discardIndex = CommandProcessor.parseHandIndex(argument, currentHand);
        if (discardIndex == -1) {
            return false;
        }
        Unit unitToDiscard = currentHand.getHand().get(discardIndex);
        currentHand.removeUnitFromHand(unitToDiscard);
        Output.printDiscard(GameEngine.getActiveTeam().getName(), unitToDiscard);
        return true;
    }

    private static void executeTurn() {
        GameEngine.resetTeamMovement(GameEngine.getActiveTeam());
        GameEngine.switchTurn();

        GameState.setHasPlacedThisTurn(false);
        GameState.setSelectedSquare(null);

        if (GameEngine.getActiveTeam().equals(GameEngine.getTeam1())) {
            Output.printTurn(GameEngine.getTeam1().getName());
        } else {
            Output.printTurn(GameEngine.getTeam2().getName());
        }

        if (GameEngine.tryDrawCard(GameEngine.getActiveTeam())) {
            if (GameEngine.getActiveTeam().equals(GameEngine.getTeam2())) {
                executeAITurn();
            }
        }
    }

    private static void executeAITurn() {
        GameLogicAI.executeTurn();

        if (GameState.isIsRunning()) {
            GameEngine.resetTeamMovement(GameEngine.getTeam2());
            GameEngine.switchTurn();

            GameState.setHasPlacedThisTurn(false);
            Output.printTurn(GameEngine.getTeam1().getName());
            GameState.setSelectedSquare(null);

            GameEngine.tryDrawCard(GameEngine.getTeam1());
        }
    }
}

package edu.kit.kastel.ai;

import edu.kit.kastel.GameEngine;
import edu.kit.kastel.Output;
import edu.kit.kastel.RandomGenerator;
import edu.kit.kastel.Unit;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class responsible for handling the AI's card discard phase.
 * Evaluates the AI's hand at the end of its turn and discards a card if the hand is full,
 * using an inverted weighted random selection that favors discarding weaker units.
 * @author uxuwg
 * @version 0.7
 */
public final class AIDiscard {
    private AIDiscard() {
    }

    /**
     * Executes the discard logic for the AI team.
     * If the AI has exactly 5 cards in hand, it calculates an inverted weight for each card
     * (Max Weight in hand - Card Weight, where Weight = ATK + DEF).
     * A card is then selected randomly based on these inverted weights.
     * If all cards have the exact same weight, the first card in the hand is discarded.
     */
    public static void discardCard() {
        List<Unit> hand = GameEngine.getTeam2().getHand().getHand();
        if (hand.size() < 5) {
            return;
        }

        int maxWeight = Integer.MIN_VALUE;
        List<Integer> originalWeights = new ArrayList<>();
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
        Output.printDiscard(GameEngine.getTeam2().getName(), unitToDiscard);
    }
}

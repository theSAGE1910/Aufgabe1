package edu.kit.kastel.ai;

import edu.kit.kastel.Output;
import edu.kit.kastel.RandomGenerator;
import edu.kit.kastel.Team;
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

    private static final int MAX_HAND_SIZE = 5;
    private static final int INITIAL_VALUE = 0;
    private static final int RANDOM_MIN_BOUND = 1;
    private static final int RANDOM_OFFSET = 1;
    private static final int START_INDEX = 0;

    private AIDiscard() {
    }

    /**
     * Executes the discard logic for the AI team.
     * @param aiTeam the AI team executing the discard logic
     */
    public static void discardCard(Team aiTeam) {
        List<Unit> handList = aiTeam.getHand().getHand();
        if (handList.size() < MAX_HAND_SIZE) {
            return;
        }

        int maxWeight = Integer.MIN_VALUE;
        List<Integer> originalWeights = new ArrayList<>();
        for (Unit unit : handList) {
            int weight = unit.getAtk() + unit.getDef();
            originalWeights.add(weight);
            if (weight > maxWeight) {
                maxWeight = weight;
            }
        }

        List<Integer> invertedWeights = new ArrayList<>();
        int totalInvWeight = INITIAL_VALUE;
        for (int weight : originalWeights) {
            int invWeight = maxWeight - weight;
            invertedWeights.add(invWeight);
            totalInvWeight += invWeight;
        }

        int selectedDiscardIndex = INITIAL_VALUE;
        if (totalInvWeight > INITIAL_VALUE) {
            int randomWeight = RandomGenerator.randomIntegerPick(RANDOM_MIN_BOUND, totalInvWeight + RANDOM_OFFSET);
            int runningSum = INITIAL_VALUE;
            for (int i = START_INDEX; i < invertedWeights.size(); i++) {
                runningSum += invertedWeights.get(i);
                if (randomWeight <= runningSum) {
                    selectedDiscardIndex = i;
                    break;
                }
            }
        }

        Unit unitToDiscard = handList.get(selectedDiscardIndex);

        aiTeam.getHand().removeUnitFromHand(unitToDiscard);
        Output.printDiscard(aiTeam.getName(), unitToDiscard);
    }
}
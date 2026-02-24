package edu.kastel.kit.edu.ai;

import edu.kastel.kit.edu.GameEngine;
import edu.kastel.kit.edu.Output;
import edu.kastel.kit.edu.RandomGenerator;
import edu.kastel.kit.edu.Unit;

import java.util.ArrayList;
import java.util.List;

public class AIDiscard {
    public static void discardCard() {
        List<Unit> hand = GameEngine.team2.hand.hand;
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
        Output.printDiscard(GameEngine.team2.getName(), unitToDiscard);
    }
}

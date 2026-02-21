package edu.kastel.kit.edu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Deck {

    private static final String ERROR_NUMERIC_EXCEPTION = "ERROR: File contains non-numeric data.";

    private Map<Unit, Integer> deckInfo;
    private int[] allUnitCount;

    public Deck() {
        this.deckInfo = new HashMap<>();
    }

//    public void setUnitList(List<Unit> unitList) {
//        this.unitList = unitList;
//    }

    public int[] extractDeckSize(List<String> deckData) {
        this.allUnitCount = new int[deckData.size()];
        int size = 0;
        try {
            for (String line : deckData) {
                this.allUnitCount[size] = Integer.parseInt(line.trim());
                size++;
            }
        } catch (NumberFormatException e) {
            System.err.println(ERROR_NUMERIC_EXCEPTION);
        }
        return allUnitCount;
    }

    public void assignDeck() {
        if (this.allUnitCount == null) {
            System.err.println("You must extract deck sizes before assigning.");
            return;
        }

        if (Unit.unitList.size() == this.allUnitCount.length) {
            for (int i = 0; i < Unit.unitList.size(); i++) {
                this.deckInfo.put(Unit.unitList.get(i), this.allUnitCount[i]);
            }
        } else {
            System.err.println("ERROR: The number of units and deck counts do not match.");
        }
    }

    public List<Unit> generatePlayableDeck() {
        List<Unit> playableDeck = new ArrayList<>();

        for (Map.Entry<Unit, Integer> entry : this.deckInfo.entrySet()) {
            Unit currentUnit = entry.getKey();
            int amountInDeck = entry.getValue();

            for (int i = 0; i < amountInDeck; i++) {
                playableDeck.add(currentUnit);
            }
        }

        return playableDeck;
    }

    public Map<Unit, Integer> getDeckInfo() {
        return this.deckInfo;
    }
}

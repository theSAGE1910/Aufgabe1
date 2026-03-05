package edu.kit.kastel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a deck of units in the game.
 * This class handles the parsing of deck configuration data, maps the defined
 * counts to their respective unit types, and generates the flat list of cards
 * that the player will actually draw from during the game.
 * @author uxuwg
 * @version 0.9
 */
public class Deck {

    private static final String ERROR_NUMERIC_EXCEPTION = "ERROR: File contains non-numeric data.";

    private final Map<Unit, Integer> deckInfo;
    private int[] allUnitCount;

    /**
     * Constructs a new Deck instance and initializes the internal map.
     */
    public Deck() {
        this.deckInfo = new LinkedHashMap<>();
    }

    /**
     * Parses the raw string lines from the deck configuration file into an array of integers.
     * @param deckData the list of string lines read directly from the deck file
     */
    public void extractDeckSize(List<String> deckData) {
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
    }

    /**
     * Maps the extracted unit counts to the globally loaded unit types.
     * Validates that the number of counts matches the number of defined units
     * before assigning them to the deck map.
     */
    public void assignDeck() {
        if (this.allUnitCount == null) {
            System.err.println("ERROR: You must extract deck sizes before assigning.");
            return;
        }

        if (Unit.getUnitList().size() == this.allUnitCount.length) {
            for (int i = 0; i < Unit.getUnitList().size(); i++) {
                this.deckInfo.put(Unit.getUnitList().get(i), this.allUnitCount[i]);
            }
        } else {
            System.err.println("ERROR: The number of units and deck counts do not match.");
        }
    }

    /**
     * Generates a flat list of Unit objects based on the mapped deck configuration.
     * If a unit has a count of 3, three separate instances/references of that unit
     * are added to the list.
     * @return the fully populated list of units representing the playable deck
     */
    public List<Unit> generatePlayableDeck() {
        List<Unit> playableDeck = new ArrayList<>();

        for (Entry<Unit, Integer> entry : this.deckInfo.entrySet()) {
            Unit currentUnit = entry.getKey();
            int amountInDeck = entry.getValue();

            for (int i = 0; i < amountInDeck; i++) {
                playableDeck.add(currentUnit);
            }
        }

        return playableDeck;
    }
}

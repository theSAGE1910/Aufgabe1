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
     * @return true if parsing was successful, false if there was a numeric format error
     */
    public boolean extractDeckSize(List<String> deckData) {
        this.allUnitCount = new int[deckData.size()];
        int size = 0;
        int sum = 0;
        try {
            for (String line : deckData) {
                int count = Integer.parseInt(line.trim());
                this.allUnitCount[size] = count;
                sum += count;
                size++;
            }
        } catch (NumberFormatException e) {
            System.err.println(ERROR_NUMERIC_EXCEPTION);
            return false;
        }

        if (sum != 40) {
            System.err.println("ERROR: Deck must contain exactly 40 cards.");
            return false;
        }

        return true;
    }

    /**
     * Maps the extracted unit counts to the globally loaded unit types.
     * Validates that the number of counts matches the number of defined units
     * before assigning them to the deck map.
     * @return true if the assignment was successful, false if there was a mismatch or if sizes were not extracted
     */
    public boolean assignDeck() {
        if (this.allUnitCount == null) {
            System.err.println("ERROR: You must extract deck sizes before assigning.");
            return false;
        }

        if (Unit.getUnitList().size() == this.allUnitCount.length) {
            for (int i = 0; i < Unit.getUnitList().size(); i++) {
                this.deckInfo.put(Unit.getUnitList().get(i), this.allUnitCount[i]);
            }
            return true;
        } else {
            System.err.println("ERROR: The number of units and deck counts do not match.");
            return false;
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
                Unit newCard = new Unit(currentUnit.getQualifier(),
                        currentUnit.getRole(),
                        currentUnit.getAtk(),
                        currentUnit.getDef());
                playableDeck.add(newCard);
            }
        }

        return playableDeck;
    }
}

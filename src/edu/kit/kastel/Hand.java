package edu.kit.kastel;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player's hand of unit cards.
 * Manages drawing cards from the deck, storing the current units held by the player,
 * and removing units when they are placed on the board or discarded.
 * @author uxuwg
 * @version 0.9
 */
public class Hand {

    /**
     * The maximum number of cards a player can hold in their hand.
     */
    public static final int MAX_HAND_SIZE = 5;

    private final List<Unit> hand;

    /**
     * Constructs a new empty Hand with an initial capacity set to the maximum hand size.
     */
    public Hand() {
        this.hand = new ArrayList<>(MAX_HAND_SIZE);
    }

    /**
     * Attempts to draw a card from the deck and add it to the hand.
     * The hand will only draw a card if it currently contains fewer than the maximum allowed cards.
     * @param team the team from which to draw the card, used to access the deck
     * @return boolean value of the success of the draw action; returns false if the hand is full or the deck is empty, true otherwise
     */
    public boolean handLoader(Team team) {
        if (this.hand.size() < MAX_HAND_SIZE) {
            Unit drawnCard = team.drawTopCard();
            if (drawnCard == null) {
                return false;
            } else {
                this.hand.add(drawnCard);
            }
        }
        return true;
    }

    /**
     * Removes a specific unit from the hand.
     * @param unit the unit to remove
     */
    public void removeUnitFromHand(Unit unit) {
        this.hand.remove(unit);
    }

    /**
     * Retrieves the list of units currently in the hand.
     * @return the list of units
     */
    public List<Unit> getHand() {
        return new ArrayList<>(this.hand);
    }
}
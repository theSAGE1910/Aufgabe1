package edu.kit.kastel;

import java.util.List;

/**
 * Represents a playing faction in the game (e.g., the Player or the Enemy).
 * This class tracks the team's current life points, their assigned deck,
 * their active hand of units, and their remaining drawable cards.
 * @author uxuwg
 * @version 0.9
 */
public class Team {

    /**
     * The starting life points for every team at the beginning of the game.
     */
    public static final int INITIAL_HP = 8000;

    private final String name;
    private final Deck deck;
    private final Hand hand;
    private final List<Unit> shuffledDeck;
    private final int initialDeckSize;

    private int teamHP;

    /**
     * Constructs a new Team with the specified name and deck configurations.
     * @param name the display name of the team
     * @param deck the full deck configuration assigned to the team
     * @param hand the team's active hand of drawn cards
     * @param shuffledDeck the list of units representing the team's drawable pile
     */
    public Team(String name, Deck deck, Hand hand, List<Unit> shuffledDeck) {
        this.name = name;
        this.teamHP = INITIAL_HP;
        this.deck = deck;
        this.hand = hand;
        this.shuffledDeck = shuffledDeck;
        this.initialDeckSize = shuffledDeck.size();
    }

    /**
     * Reduces the team's life points by the specified amount.
     * The life points cannot drop below zero.
     * @param amount the integer amount of damage to take
     */
    public void takeDamage(int amount) {
        this.teamHP = Math.max(0, this.teamHP - amount);
    }

    /**
     * Retrieves the team's current life points.
     * @return the current integer HP
     */
    public int getTeamHP() {
        return this.teamHP;
    }

    /**
     * Retrieves the display name of the team.
     * @return the string name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retrieves the total number of cards the team had in their deck at the start of the game.
     * @return the initial integer deck size
     */
    public int getInitialDeckSize() {
        return this.initialDeckSize;
    }

    /**
     * Retrieves the team's active hand of drawn units.
     * @return the Hand object
     */
    public Hand getHand() {
        return this.hand;
    }

    /**
     * Retrieves the remaining list of units waiting to be drawn from the deck.
     * @return the list of playable units in the draw pile
     */
    public List<Unit> getShuffledDeck() {
        return this.shuffledDeck;
    }

}
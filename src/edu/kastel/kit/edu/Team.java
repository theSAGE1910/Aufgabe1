package edu.kastel.kit.edu;

import java.util.List;

public class Team {
    public static final int INITIAL_HP = 8000;

    public Hand hand;
    public List<Unit> shuffledDeck;
    private int initialDeckSize;
    private String name;
    private int teamHP;
    private Deck deck;

    public Team(String name, Deck deck, Hand hand, List<Unit> shuffledDeck) {
        this.name = name;
        this.teamHP = INITIAL_HP;
        this.deck = deck;
        this.hand = hand;
        this.shuffledDeck = shuffledDeck;
        this.initialDeckSize = shuffledDeck.size();
    }

    public void takeDamage(int amount) {
        this.teamHP = Math.max(0, this.teamHP - amount);
    }

    public int getTeamHP() {
        return this.teamHP;
    }

    public String getName() {
        return this.name;
    }

    public int getInitialDeckSize() {
        return this.initialDeckSize;
    }

}
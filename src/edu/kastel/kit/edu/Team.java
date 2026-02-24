package edu.kastel.kit.edu;

import java.util.List;

public class Team {
    public static final int INITIAL_HP = 8000;

    private int initialDeckSize;
    private String name;
    private int teamHP;
    private Deck deck;
    public Hand hand;
    List<Unit> shuffledDeck;

    public Team(String name, Deck deck, Hand hand, List<Unit> shuffledDeck) {
        this.name = name;
        this.teamHP = INITIAL_HP;
        this.deck = deck;
        this.hand = hand;
        this.shuffledDeck = shuffledDeck;
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
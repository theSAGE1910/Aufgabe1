package edu.kastel.kit.edu;

import java.util.List;

public class Team {
    public static final int INITIAL_HP = 8000;

    String name;
    int teamHP;
    Deck deck;
    Hand hand;
    List<Unit> shuffledDeck;

    public Team(String name, Deck deck, Hand hand, List<Unit> shuffledDeck) {
        this.name = name;
        this.teamHP = INITIAL_HP;
        this.deck = deck;
        this.hand = hand;
        this.shuffledDeck = shuffledDeck;
    }

}
package edu.kastel.kit.edu;

public class Team {
    public static final int INITIAL_HP = 8000;

    int teamHP;
    Deck deck;
    Hand hand;

    public Team(Deck deck, Hand hand) {
        this.teamHP = INITIAL_HP;
        this.deck = deck;
        this.hand = hand;
    }

}
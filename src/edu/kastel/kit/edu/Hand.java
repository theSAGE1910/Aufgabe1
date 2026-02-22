package edu.kastel.kit.edu;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    List<Unit> hand;
    List<Unit> deck;

    public Hand() {
        this.hand = new ArrayList<>(5);
    }

    public void handLoader(List<Unit> deck) {
        while (this.hand.size() < 5 && !deck.isEmpty()) {
            Unit drawnCard = deck.removeFirst();
            this.hand.add(drawnCard);
        }
    }

    public void removeUnitFromHand(Unit unit) {
        this.hand.remove(unit);
    }
}

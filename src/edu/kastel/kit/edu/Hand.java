package edu.kastel.kit.edu;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    public List<Unit> hand;

    public Hand() {
        this.hand = new ArrayList<>(5);
    }

//    public void handLoader(List<Unit> deck) {
//        while (this.hand.size() < 5 && !deck.isEmpty()) {
//            Unit drawnCard = deck.removeFirst();
//            this.hand.add(drawnCard);
//        }
//    }
    public boolean handLoader(List<Unit> shuffledDeck) {
        if (this.hand.size() < 5) {
            if (shuffledDeck.isEmpty()) {
                return false;
            } else {
                Unit drawnCard = shuffledDeck.removeFirst();
                this.hand.add(drawnCard);
            }
        }
        return true;
    }

    public void removeUnitFromHand(Unit unit) {
        this.hand.remove(unit);
    }
}

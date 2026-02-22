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
        for (Unit unit : deck) {
            if (this.hand.size() == 5) {
                break;
            }
            this.hand.add(unit);
            deck.remove(unit);
        }
    }

    public void removeUnitFromHand(Unit unit) {
        this.hand.remove(unit);
    }
}

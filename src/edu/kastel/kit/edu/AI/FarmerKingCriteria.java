package edu.kastel.kit.edu.AI;

public class FarmerKingCriteria {
    int distance;
    int enemies;
    int fellows;
    int fellowsPresent;
    int score;
    int steps;

    public int decisionScore() {
        int moveKingScore = fellows - 2 * enemies - distance - 3 * fellowsPresent;
        int placeUnitScore = 2 * enemies - steps - fellows;
        //int moveUnitScore = 1;
        return 0;
    }
}

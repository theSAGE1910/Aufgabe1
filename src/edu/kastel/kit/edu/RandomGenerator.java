package edu.kastel.kit.edu;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomGenerator {
    static Random random;

    public static void initialise(int seed) {
        random = new Random(seed);
    }

    public static void shuffleDeck(List<Unit> playableDeck) {
        Collections.shuffle(playableDeck, random);
    }

    public static int randomIntegerPick(int origin, int bound) {
        return random.nextInt(origin, bound);
    }
}

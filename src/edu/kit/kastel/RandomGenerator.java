package edu.kit.kastel;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Utility class that provides centralized random number generation for the game.
 * By initializing this generator with a specific seed, the game ensures
 * deterministic behavior for deck shuffling and AI decision-making.
 * @author uxuwg
 * @version 0.9
 */
public final class RandomGenerator {
    private static Random random = null;

    private RandomGenerator() {
    }

    /**
     * Initializes the random number generator with a specific seed.
     * @param seed the integer seed used to initialize the generator
     */
    public static void initialise(int seed) {
        random = new Random(seed);
    }

    /**
     * Shuffles the provided deck of units using the seeded random generator.
     * @param playableDeck the list of units representing a player's deck
     */
    public static void shuffleDeck(List<Unit> playableDeck) {
        Collections.shuffle(playableDeck, random);
    }

    /**
     * Generates a random integer between the specified origin and bound.
     * @param origin the lowest possible integer value
     * @param bound  the highest possible integer value
     * @return a randomly generated integer within the specified range
     */
    public static int randomIntegerPick(int origin, int bound) {
        return random.nextInt(origin, bound);
    }
}

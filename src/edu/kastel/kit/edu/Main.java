package edu.kastel.kit.edu;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        //String[] info = {"seed=-4022738", "deck=default_deck.txt", "verbosity=compact", "units=default_units.txt"};
        GameEngine.run(args);
    }
}

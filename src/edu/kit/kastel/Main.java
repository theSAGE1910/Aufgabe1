package edu.kit.kastel;

/**
 * The main entry point for the application.
 * This class simply captures the command line arguments and passes them
 * directly to the GameEngine to bootstrap and start the game.
 * @author uxuwg
 * @version 0.9
 */
public final class Main {
    private Main() {
    }

    /**
     * The main method that starts the application.
     * @param args the command line arguments used to configure the game data
     */
    public static void main(String[] args) {
        GameEngine.run(args);
    }
}

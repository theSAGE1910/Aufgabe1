package edu.kit.kastel;

/**
 * The core engine of the game, responsible for managing the main game flow,
 * initializing the game state, and tracking which team's turn is currently active.
 * @author uxuwg
 * @version 0.9
 */
public final class GameEngine {

    /**
     * The first team participating in the game (typically the Player).
     */
    public static Team team1;

    /**
     * The second team participating in the game (typically the Enemy AI).
     */
    public static Team team2;

    /**
     * The team whose turn is currently taking place.
     */
    public static Team activeTeam;

    private static final String HELPING_TEXT
            = "Use one of the following commands: select, board, move, flip, block, hand, place, show, yield, state, quit.";


    private GameEngine() {
    }

    /**
     * Bootstraps and starts the game engine.
     * This method parses the command line arguments, triggers the initialization
     * phase if the arguments are valid, and then hands control over to the GameUI
     * to begin processing user input.
     * @param args the command line arguments provided to the application
     */
    public static void run(String[] args) {
        if (GameData.extractArgumentInfo(args)) {
            if (Initialiser.initialise()) {
                System.out.println(HELPING_TEXT);
                GameUI.getInput();
            }
        }
    }

    /**
     * Switches the active turn between the two teams.
     * If Team 1 is currently active, it hands the turn over to Team 2, and vice versa.
     */
    public static void switchTurn() {
        if (activeTeam.equals(team1)) {
            activeTeam = team2;
        } else {
            activeTeam = team1;
        }
    }
}

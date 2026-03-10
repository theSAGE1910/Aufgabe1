package edu.kit.kastel;

/**
 * The core engine of the game, responsible for managing the main game flow,
 * initializing the game state, and tracking which team's turn is currently active.
 * @author uxuwg
 * @version 0.9
 */
public final class GameEngine {

    private static final String NO_MORE_CARDS_LEFT_IN_THE_DECK = "ERROR: %s has no more cards left in the deck!";
    private static Team team1 = null;
    private static Team team2 = null;
    private static Team activeTeam = null;

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

                CommandProcessor.initialise();
                GameUI.getInput();
            }
        }
    }

    /**
     * Switches the active turn between the two teams.
     * If Team 1 is currently active, it hands the turn over to Team 2, and vice versa.
     */
    public static void switchTurn() {
        if (getActiveTeam().equals(getTeam1())) {
            setActiveTeam(getTeam2());
        } else {
            setActiveTeam(getTeam1());
        }
    }

    /**
     * Resets the movement status of all units belonging to the specified team at the end of a turn.
     * @param team the team for which to reset the movement status of all units
     */
    public static void resetTeamMovement(Team team) {
        for (int row = 0; row < GameBoard.DIMENSION; row++) {
            for (int col = 0; col < GameBoard.DIMENSION; col++) {
                Unit boardUnit = GameBoard.getUnitAt(row, col);
                if (boardUnit != null && boardUnit.getTeam().equals(team)) {
                    boardUnit.setHasMovedThisTurn(false);
                }
            }
        }
    }

    /**
     * Resets the block status of all units belonging to the specified team at the START of their turn.
     * @param team the team for which to reset the block status
     */
    public static void resetTeamBlocks(Team team) {
        for (int row = 0; row < GameBoard.DIMENSION; row++) {
            for (int col = 0; col < GameBoard.DIMENSION; col++) {
                Unit boardUnit = GameBoard.getUnitAt(row, col);
                if (boardUnit != null && boardUnit.getTeam().equals(team)) {
                    boardUnit.setBlocking(false);
                }
            }
        }
    }

    /**
     * Attempts to draw a card from the specified team's deck and add it to their hand.
     * @param team the team for which to attempt drawing a card from the deck
     * @return true if the card was successfully drawn and added to the team's hand, false if the deck is empty and the game has ended
     */
    public static boolean tryDrawCard(Team team) {
        boolean success = team.getHand().handLoader(team.getShuffledDeck());
        if (!success) {
            System.err.printf(NO_MORE_CARDS_LEFT_IN_THE_DECK, team.getName());
            Team winner = team.equals(getTeam1()) ? getTeam2() : getTeam1();
            System.out.println(winner.getName() + " wins!");
            GameState.setIsRunning(false);
        }
        return success;
    }

    /**
     * The team whose turn is currently taking place.
     * @return the active team whose turn it currently is
     */
    public static Team getActiveTeam() {
        return activeTeam;
    }

    /**
     * Sets the active team to the specified team, indicating that it is now that team's turn.
     * @param activeTeam the team to set as the active team for the current turn
     */
    public static void setActiveTeam(Team activeTeam) {
        GameEngine.activeTeam = activeTeam;
    }

    /**
     * The second team participating in the game (typically the Enemy AI).
     * @return the second team participating in the game
     */
    public static Team getTeam2() {
        return team2;
    }

    /**
     * Sets the second team participating in the game to the specified team.
     * @param team2 the team to set as the second team participating in the game
     */
    public static void setTeam2(Team team2) {
        GameEngine.team2 = team2;
    }

    /**
     * The first team participating in the game (typically the Player).
     * @return the first team participating in the game
     */
    public static Team getTeam1() {
        return team1;
    }

    /**
     * Sets the first team participating in the game to the specified team.
     * @param team1 the team to set as the first team participating in the game
     */
    public static void setTeam1(Team team1) {
        GameEngine.team1 = team1;
    }
}

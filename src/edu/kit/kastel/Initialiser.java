package edu.kit.kastel;

import java.util.List;

/**
 * Utility class responsible for bootstrapping and setting up the initial state of the game.
 * It handles the creation of the board, parsing units, building and shuffling decks,
 * assigning teams, drawing initial hands, and placing the Farmer Kings.
 * @author uxuwg
 * @version 0.9
 */
public final class Initialiser {
    private static Deck deck1 = null;
    private static Deck deck2 = null;
    private static List<Unit> player1DrawPile = null;
    private static List<Unit> player2DrawPile = null;

    private Initialiser() {
    }

    /**
     * Executes the complete initialization sequence for the game.
     * Sets the RNG seed, prepares the board, loads configurations, builds teams,
     * and prepares the first turn.
     * @return true if initialization was successful, false if there were errors in loading decks or configurations
     */
    public static boolean initialise() {
        RandomGenerator.initialise(GameData.getSeed());
        initialiseGameBoard();
        BoardTheme.initialiseTheme();
        initialiseUnits();

        if (!initialiseDecks()) {
            return false;
        }

        initialiseTeams();
        initialiseHands(GameEngine.getTeam1());
        initialiseHands(GameEngine.getTeam2());

        GameEngine.setActiveTeam(GameEngine.getTeam1());

        initialiseKings();
        return true;
    }

    private static void initialiseGameBoard() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                GameBoard.setUnitAt(i, j, null);
            }
        }
    }

    private static void initialiseUnits() {
        Unit.extractUnits(GameData.getUnitData());
    }

    private static boolean initialiseDecks() {
        deck1 = new Deck();
        deck2 = new Deck();

        if (!deck1.extractDeckSize(GameData.getDeck1Data())) {
            return false;
        }
        if (!deck2.extractDeckSize(GameData.getDeck2Data())) {
            return false;
        }
        if (!deck1.assignDeck()) {
            return false;
        }
        if (!deck2.assignDeck()) {
            return false;
        }

        player1DrawPile = deck1.generatePlayableDeck();
        player2DrawPile = deck2.generatePlayableDeck();

        RandomGenerator.shuffleDeck(player1DrawPile);
        RandomGenerator.shuffleDeck(player2DrawPile);

        return true;
    }

    private static void initialiseTeams() {
        GameEngine.setTeam1(new Team(GameData.getTeam1Name(), deck1, new Hand(), player1DrawPile));
        GameEngine.setTeam2(new Team(GameData.getTeam2Name(), deck2, new Hand(), player2DrawPile));
    }

    private static void initialiseHands(Team team) {
        for (int i = 0; i < Hand.MAX_HAND_SIZE; i++) {
            team.getHand().handLoader(team.getShuffledDeck());
        }
    }

    private static void initialiseKings() {
        Unit team1King = new Unit(GameMessages.FARMER, GameMessages.KING, 0, 0, GameEngine.getTeam1());
        Unit team2King = new Unit(GameMessages.FARMER, GameMessages.KING, 0, 0, GameEngine.getTeam2());

        GameBoard.setUnitAt(GameBoard.DIMENSION - 1, GameBoard.DIMENSION / 2, team1King);
        GameBoard.setUnitAt(0, GameBoard.DIMENSION / 2, team2King);
    }
}
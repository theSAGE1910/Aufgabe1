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

    private static final int START_INDEX = 0;
    private static final int MAX_DIMENSION = 7;
    private static final int OFFSET_ONE = 1;
    private static final int OFFSET_TWO = 2;
    private static final int INITIAL_STAT = 0;

    private Initialiser() {
    }

    /**
     * Executes the complete initialization sequence for the game.
     * @return true if initialization was successful, false if there were errors
     */
    public static boolean initialise() {
        RandomGenerator.initialise(GameData.getSeed());
        initialiseGameBoard();
        BoardTheme.initialiseTheme();
        initialiseUnits();

        if (!initialiseDecksAndTeams()) {
            return false;
        }

        initialiseHands(GameEngine.getTeam1());
        initialiseHands(GameEngine.getTeam2());

        GameEngine.setActiveTeam(GameEngine.getTeam1());

        initialiseKings();
        return true;
    }

    private static void initialiseGameBoard() {
        for (int i = START_INDEX; i < MAX_DIMENSION; i++) {
            for (int j = START_INDEX; j < MAX_DIMENSION; j++) {
                GameBoard.setUnitAt(i, j, null);
            }
        }
    }

    private static void initialiseUnits() {
        Unit.extractUnits(GameData.getUnitData());
    }

    private static boolean initialiseDecksAndTeams() {
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();

        if (deck1.extractDeckSize(GameData.getDeck1Data())) {
            return false;
        }
        if (deck2.extractDeckSize(GameData.getDeck2Data())) {
            return false;
        }
        if (deck1.assignDeck()) {
            return false;
        }
        if (deck2.assignDeck()) {
            return false;
        }

        List<Unit> player1DrawPile = deck1.generatePlayableDeck();
        List<Unit> player2DrawPile = deck2.generatePlayableDeck();

        RandomGenerator.shuffleDeck(player1DrawPile);
        RandomGenerator.shuffleDeck(player2DrawPile);

        GameEngine.setTeam1(new Team(GameData.getTeam1Name(), new Hand(), player1DrawPile));
        GameEngine.setTeam2(new Team(GameData.getTeam2Name(), new Hand(), player2DrawPile));

        return true;
    }

    private static void initialiseHands(Team team) {
        for (int i = START_INDEX; i < Hand.MAX_HAND_SIZE; i++) {
            team.getHand().handLoader(team);
        }
    }

    private static void initialiseKings() {
        Unit team1King = new Unit(GameMessages.FARMER, GameMessages.KING, INITIAL_STAT, INITIAL_STAT, GameEngine.getTeam1());
        Unit team2King = new Unit(GameMessages.FARMER, GameMessages.KING, INITIAL_STAT, INITIAL_STAT, GameEngine.getTeam2());

        GameBoard.setUnitAt(GameBoard.DIMENSION - OFFSET_ONE, GameBoard.DIMENSION / OFFSET_TWO, team1King);
        GameBoard.setUnitAt(START_INDEX, GameBoard.DIMENSION / OFFSET_TWO, team2King);
    }
}
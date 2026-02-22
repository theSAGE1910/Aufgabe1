package edu.kastel.kit.edu;

import java.util.List;

public class Initialiser {

    public static Deck deck1;
    public static Deck deck2;

    public static List<Unit> player1DrawPile;
    public static List<Unit> player2DrawPile;

    static Unit team1King;
    static Unit team2King;

    public static void initialise() {
        initialiseGameBoard();
        BoardTheme.initialiseTheme();
        initialiseUnits();
        initialiseDecks();
        initialiseTeams();
        initialiseHands(GameEngine.team1);
        initialiseHands(GameEngine.team2);

        GameEngine.activeTeam = GameEngine.team1;

        initialiseKings();
    }

    private static void initialiseGameBoard() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                GameBoard.gameBoard[i][j] = null;
            }
        }
    }

    private static void initialiseUnits() {
        Unit.extractUnits(GameData.unitData);
    }

    private static void initialiseDecks() {
        deck1 = new Deck();
        deck2 = new Deck();
        deck1.extractDeckSize(GameData.deck1Data);
        deck2.extractDeckSize(GameData.deck2Data);
        deck1.assignDeck();
        deck2.assignDeck();

        player1DrawPile = deck1.generatePlayableDeck();
        player2DrawPile = deck2.generatePlayableDeck();

        RandomGenerator.shuffleDeck(player1DrawPile);
        RandomGenerator.shuffleDeck(player2DrawPile);
    }

    public static void initialiseTeams() {
        GameEngine.team1 = new Team(GameData.team1Name, deck1, new Hand(), player1DrawPile);
        GameEngine.team2 = new Team(GameData.team2Name, deck2, new Hand(), player2DrawPile);
    }

    public static void initialiseHands(Team team) {
        team.hand.handLoader(team.shuffledDeck);
    }

    public static void initialiseKings() {
        team1King = new Unit("Farmer", "King", 0, 0, GameEngine.team1);
        team2King = new Unit("Farmer", "King", 0, 0, GameEngine.team2);

        GameBoard.gameBoard[6][3] = team1King;
        GameBoard.gameBoard[0][3] = team2King;
    }
}

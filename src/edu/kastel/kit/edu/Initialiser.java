package edu.kastel.kit.edu;

import java.util.List;

public class Initialiser {

    public static Deck deck1;
    public static Deck deck2;
    public static Team team1;
    public static Team team2;

    public static List<Unit> player1DrawPile;
    public static List<Unit> player2DrawPile;

    public static void initialise() {
        initialiseGameBoard();
        initialiseUnits();
        initialiseDecks();
        initialiseHands(team1);
        initialiseHands(team2);
        initialiseKings();
    }

    private static void initialiseGameBoard() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                GameBoard.gameBoard[i][j] = "   ";
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
        team1 = new Team(GameData.team1Name, deck1, new Hand(), player1DrawPile);
        team2 = new Team(GameData.team2Name, deck2, new Hand(), player2DrawPile);
    }

    public static void initialiseHands(Team team) {
        team.hand.handLoader(team.shuffledDeck);
    }

    public static void initialiseKings() {
        GameBoard.gameBoard[6][3] = GameData.enemyKingSymbol;
        GameBoard.gameBoard[0][3] = GameData.playerKingSymbol;
    }
}

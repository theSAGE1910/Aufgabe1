package edu.kastel.kit.edu;

public class Initialiser {

    public static Deck deck1;
    public static Deck deck2;

    public static void initialise() {
        initialiseGameBoard();
        initialiseUnits();
        initialiseDecks();
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
        RandomGenerator.shuffleDeck(deck1.generatePlayableDeck());
        RandomGenerator.shuffleDeck(deck2.generatePlayableDeck());
    }

    public static void initialiseKings() {
        GameBoard.gameBoard[6][3] = GameData.enemyKingSymbol;
        GameBoard.gameBoard[0][3] = GameData.playerKingSymbol;
    }
}

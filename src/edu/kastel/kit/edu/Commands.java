package edu.kastel.kit.edu;

import edu.kastel.kit.edu.AI.GameLogicAI;

public class Commands {
    public static final String REGEX_SPACE = " ";
    public static boolean isRunning = true;
    static String currentSquare;
    public static String selectedSquare = null;
    static int row;
    static int column;
    public static int selectedRow;
    public static int selectedColumn;

    public static void processCommands(String input) {
        String key = input.toUpperCase();
        String argument = null;
        String[] words = input.split(REGEX_SPACE);

        if (words.length == 2) {
            key = words[0];
            argument = words[1];
        }

        switch (key.toLowerCase()) {
            case "select":
                handleSelect(argument);
                break;
            case "board":
                handleBoard();
                break;
            case "move":
                MovementController.handleMove(argument);
                break;
            case "flip":
                handleFlip();
                break;
            case "block":
                handleBlock();
                break;
            case "hand":
                handleHand();
                break;
            case "place":
                handlePlace(argument);
                break;
            case "show":
                handleShow();
                break;
            case "yield":
                handleYield(argument);
                break;
            case "state":
                handleState();
                break;
            case "quit":
                isRunning = false;
                break;
            default:
                System.err.println("ERROR: Invalid command.");
                break;
        }
    }

    private static void handleSelect(String argument) {
        if (argument != null && argument.length() == 2) {
            selectedSquare = argument;
            currentSquare = argument;

            selectedRow = getCoordinates(argument)[0];
            selectedColumn = getCoordinates(argument)[1];
            updateDisplay();
        } else {
            System.err.println("ERROR: Invalid square selected.");
        }
    }

    private static void handleBoard() {
        if (currentSquare != null && currentSquare.length() == 2) {
            GameBoard.showGameBoard(currentSquare.charAt(0),
                    Character.getNumericValue(currentSquare.charAt(1)));
        }
    }

    private static void handleFlip() {
        Unit unitToFlip = getValidatedActiveUnit();
        if (unitToFlip == null) {
            return;
        }

        if (!unitToFlip.getTeam().equals(GameEngine.activeTeam)) {
            System.err.println("ERROR: You can only flip your own units.");
            return;
        }

        if (unitToFlip.isFaceUp()) {
            System.err.println("ERROR: Unit is already face up.");
            return;
        }

        unitToFlip.setFaceUp(true);
        unitToFlip.setHasMovedThisTurn(true);
        Output.printFlip(unitToFlip.getUnitName(), unitToFlip.getAtk(), unitToFlip.getDef(), selectedSquare);
        updateDisplay();
    }

    private static void handleBlock() {
        Unit unitToBlock = getValidatedActiveUnit();
        if (unitToBlock == null) {
            return;
        }

        if (!unitToBlock.getTeam().equals(GameEngine.activeTeam)) {
            System.err.println("ERROR: You can only block your own units.");
            return;
        }

        if (unitToBlock.isBlocking()) {
            System.err.println("ERROR: Unit is already blocking.");
            return;
        }

        unitToBlock.setBlocking(true);
        unitToBlock.setHasMovedThisTurn(true);
        Output.printBlock(unitToBlock.getUnitName(), selectedSquare);
        updateDisplay();
    }

    private static void handleHand() {
        Output.printHand(GameEngine.activeTeam.hand);
    }

    private static void handlePlace(String argument) {
        if (selectedSquare == null) {
            System.err.println("ERROR: No square selected.");
            return;
        }

        if (GameBoard.getUnitAt(selectedRow, selectedColumn) != null) {
            System.err.println("ERROR: Square already occupied.");
            return;
        }

        Hand currentHand = GameEngine.activeTeam.hand;

        int handIndex = parseHandIndex(argument, currentHand);
        if (handIndex == -1) {
            return;
        }

        Unit unitToPlace = currentHand.hand.get(handIndex);

        unitToPlace.setTeam(GameEngine.activeTeam);
        unitToPlace.setHasMovedThisTurn(true);
        GameBoard.setUnitAt(selectedRow, selectedColumn, unitToPlace);
        currentHand.removeUnitFromHand(unitToPlace);
        updateDisplay();
    }

    private static void handleShow() {
        if (currentSquare == null) {
            System.out.println("ERROR: No square selected.");
            return;
        }

        Unit unitToShow = GameBoard.getUnitAt(selectedRow, selectedColumn);

        if (unitToShow == null) {
            System.out.println();
        } else {
            if (isKing(unitToShow)) {
                Output.printFarmerKing(unitToShow);
            } else if (!unitToShow.isFaceUp() && !unitToShow.getTeam().equals(GameEngine.activeTeam)) {
                Output.printHiddenUnit(unitToShow);
            } else {
                Output.printVisibleUnit(unitToShow);
            }
        }
    }

    private static void handleYield(String argument) {
        Hand currentHand = GameEngine.activeTeam.hand;

        int discardIndex = 0;
        if (argument != null) {
            discardIndex = parseHandIndex(argument, currentHand);
            if (discardIndex == -1) {
                return;
            }
        }

        Unit unitToDiscard = currentHand.hand.get(discardIndex);

        currentHand.removeUnitFromHand(unitToDiscard);
        Output.printDiscard(GameEngine.activeTeam.getName(), unitToDiscard);

        GameEngine.activeTeam.hand.handLoader(GameEngine.activeTeam.shuffledDeck);

        for (int row = 0; row < GameBoard.DIMENSION; row++) {
            for (int col = 0; col < GameBoard.DIMENSION; col++) {
                Unit boardUnit = GameBoard.getUnitAt(row, col);
                if (boardUnit != null && boardUnit.getTeam().equals(GameEngine.activeTeam)) {
                    boardUnit.setHasMovedThisTurn(false);
                }
            }
        }

        GameEngine.switchTurn();

        if (GameEngine.activeTeam.equals(GameEngine.team1)) {
            Output.printPlayerTurn();
        } else {
            Output.printEnemyTurn();
            GameLogicAI.executeTurn();
            GameEngine.activeTeam.hand.handLoader(GameEngine.activeTeam.shuffledDeck);
            GameEngine.switchTurn();
            Output.printPlayerTurn();
        }

        updateDisplay();
    }

    private static void handleState() {
        Output.printState(GameEngine.team1, GameEngine.team2);
        updateDisplay();
    }

    public static void updateDisplay() {
        processCommands("board");
        processCommands("show");
    }

    public static Unit getValidatedActiveUnit() {
        if (selectedSquare == null) {
            System.err.println("ERROR: No square selected.");
            return null;
        }

        Unit unit = GameBoard.getUnitAt(selectedRow, selectedColumn);
        if (unit == null) {
            System.err.println("ERROR: No unit on the selected square.");
            return null;
        }

        if (unit.hasMovedThisTurn()) {
            System.err.println("ERROR: Unit has already moved this turn.");
            return null;
        }

        return unit;
    }

    private static int parseHandIndex(String argument, Hand currentHand) {
        if (argument == null) {
            System.err.println("ERROR: No card index provided.");
            return -1;
        }

        try {
            int index = Integer.parseInt(argument);
            if (index < 1 || index > currentHand.hand.size()) {
                System.err.println("ERROR: Invalid card index.");
                return -1;
            }
            return index - 1;
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid card index.");
            return -1;
        }
    }

    public static boolean isKing(Unit unitToShow) {
        return unitToShow.getQualifier().equals("Farmer") && unitToShow.getRole().equals("King");
    }

    public static int[] getCoordinates(String coordinate) {
        int[] coords = new int[2];

        coords[0] = 7 - Character.getNumericValue(coordinate.charAt(1));
        coords[1] = Character.getNumericValue(coordinate.toUpperCase().charAt(0)) - 10;

        return coords;
    }
}

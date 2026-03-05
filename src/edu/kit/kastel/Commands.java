package edu.kit.kastel;

import edu.kit.kastel.ai.GameLogicAI;

/**
 * Handles the parsing and execution of all user input commands.
 * This class acts as the central controller for the game, routing commands
 * to their respective logic methods and updating the game state.
 * @author uxuwg
 * @version 0.9
 */
public final class Commands {

    /** Flag indicating whether the main game loop is currently running. */
    public static boolean isRunning = true;

    /** The coordinate string of the currently selected square. */
    public static String selectedSquare = null;

    /** The row index of the currently selected square. */
    public static int selectedRow = 0;

    /** The column index of the currently selected square. */
    public static int selectedColumn = 0;

    private static String currentSquare = null;
    private static final String REGEX_SPACE = " ";

    private Commands() {
    }

    /**
     * Parses the user input and delegates it to the appropriate command handler.
     * @param input the raw string input from the user
     */
    public static void processCommands(String input) {
        String key = input.toUpperCase();
        String argument = null;
        String[] words = input.split(REGEX_SPACE);

        if (words.length == 2) {
            key = words[0];
            argument = words[1].toUpperCase();
        }

        switch (key.toLowerCase()) {
            case "select":
                handleSelect(argument);
                break;
            case "board":
                if (currentSquare != null && currentSquare.length() == 2) {
                    GameBoard.showGameBoard(currentSquare.charAt(0),
                            Character.getNumericValue(currentSquare.charAt(1)));
                }
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
                Output.printHand(GameEngine.activeTeam.getHand());
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
                Output.printState(GameEngine.team1, GameEngine.team2);
                updateDisplay();
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

    private static void handlePlace(String argument) {
        if (selectedSquare == null) {
            System.err.println("ERROR: No square selected.");
            return;
        }

        if (GameBoard.getUnitAt(selectedRow, selectedColumn) != null) {
            System.err.println("ERROR: Square already occupied.");
            return;
        }

        int[] kingPosition = GameEngine.activeTeam.equals(GameEngine.team1)
                ? GameBoard.getPlayerKingPosition()
                : GameBoard.getEnemyKingPosition();
        if (kingPosition != null) {
            int rowDiff = Math.abs(selectedRow - kingPosition[0]);
            int colDiff = Math.abs(selectedColumn - kingPosition[1]);
            if (rowDiff > 1 || colDiff > 1) {
                System.err.println("ERROR: Target square must be adjacent to the Farmer King.");
                return;
            }
        }

        Hand currentHand = GameEngine.activeTeam.getHand();
        int handIndex = parseHandIndex(argument, currentHand);
        if (handIndex == -1) {
            return;
        }

        Unit unitToPlace = currentHand.getHand().get(handIndex);
        Output.printPlacement(GameEngine.activeTeam.getName(), unitToPlace, selectedSquare);

        unitToPlace.setTeam(GameEngine.activeTeam);
        unitToPlace.setHasMovedThisTurn(false);
        currentHand.removeUnitFromHand(unitToPlace);

        int boardCount = Output.getBoardCount(GameEngine.activeTeam);
        if (boardCount >= 5) {
            GameBoard.setUnitAt(selectedRow, selectedColumn, null);
            Output.printElimination(unitToPlace.getUnitName());
        } else {
            GameBoard.setUnitAt(selectedRow, selectedColumn, unitToPlace);
        }
        updateDisplay();
    }

    private static void handleShow() {
        if (currentSquare == null) {
            System.out.println("ERROR: No square selected.");
            return;
        }

        Unit unitToShow = GameBoard.getUnitAt(selectedRow, selectedColumn);

        if (unitToShow == null) {
            System.out.println("<no unit>");
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
        Hand currentHand = GameEngine.activeTeam.getHand();

        if (currentHand.getHand().size() == 5 && argument == null) {
            System.err.println("ERROR: Hand is full. You must specify a card to discard.");
        } else if (currentHand.getHand().size() < 5 && argument != null) {
            System.err.println("ERROR: Hand is not full. You cannot discard a card.");
        }

        int discardIndex = 0;
        if (argument != null) {
            discardIndex = parseHandIndex(argument, currentHand);
            if (discardIndex == -1) {
                return;
            }

            Unit unitToDiscard = currentHand.getHand().get(discardIndex);
            currentHand.removeUnitFromHand(unitToDiscard);
            Output.printDiscard(GameEngine.activeTeam.getName(), unitToDiscard);
        }

        resetTeamMovement(GameEngine.activeTeam);
        GameEngine.switchTurn();

        if (GameEngine.activeTeam.equals(GameEngine.team1)) {
            Output.printPlayerTurn();
        } else {
            Output.printEnemyTurn();
        }

        if (!tryDrawCard(GameEngine.activeTeam)) {
            return;
        }

        if (GameEngine.activeTeam.equals(GameEngine.team2)) {
            GameLogicAI.executeTurn();

            if (!isRunning) {
                return;
            }

            resetTeamMovement(GameEngine.team2);
            GameEngine.switchTurn();
            Output.printPlayerTurn();

            if (!tryDrawCard(GameEngine.team1)) {
                return;
            }
        }

        updateDisplay();
    }

    /**
     * Updates the terminal display by implicitly calling the board and show commands.
     */
    public static void updateDisplay() {
        processCommands("board");
        processCommands("show");
    }

    /**
     * Validates that a square is selected, holds a unit, and the unit hasn't moved yet.
     * @return the valid Unit, or null if validation fails
     */
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
            if (index < 1 || index > currentHand.getHand().size()) {
                System.err.println("ERROR: Invalid card index.");
                return -1;
            }
            return index - 1;
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid card index.");
            return -1;
        }
    }

    /**
     * Checks if a specified unit is the Farmer King.
     * @param unitToShow the unit to evaluate
     * @return true if the unit is the Farmer King, false otherwise
     */
    public static boolean isKing(Unit unitToShow) {
        return unitToShow.getQualifier().equals("Farmer") && unitToShow.getRole().equals("King");
    }

    /**
     * Converts an alphanumeric coordinate string into board array indices.
     * @param coordinate the coordinate string
     * @return an integer array containing the row at index 0 and column at index 1
     */
    public static int[] getCoordinates(String coordinate) {
        int[] coords = new int[2];

        coords[0] = 7 - Character.getNumericValue(coordinate.charAt(1));
        coords[1] = Character.getNumericValue(coordinate.toUpperCase().charAt(0)) - 10;

        return coords;
    }

    private static void resetTeamMovement(Team team) {
        for (int row = 0; row < GameBoard.DIMENSION; row++) {
            for (int col = 0; col < GameBoard.DIMENSION; col++) {
                Unit boardUnit = GameBoard.getUnitAt(row, col);
                if (boardUnit != null && boardUnit.getTeam().equals(team)) {
                    boardUnit.setHasMovedThisTurn(false);
                }
            }
        }
    }

    private static boolean tryDrawCard(Team team) {
        boolean success = team.getHand().handLoader(team.getShuffledDeck());
        if (!success) {
            System.err.println("ERROR: " + team.getName() + " has no more cards left in the deck!");
            Team winner = team.equals(GameEngine.team1) ? GameEngine.team2 : GameEngine.team1;
            System.out.println(winner.getName() + " wins!");
            isRunning = false;
        }
        return success;
    }
}

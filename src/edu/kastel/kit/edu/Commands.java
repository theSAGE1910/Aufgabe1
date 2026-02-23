package edu.kastel.kit.edu;

public class Commands {
    public static final String REGEX_SPACE = " ";
    public static boolean isRunning = true;
    static String currentSquare;
    static String selectedSquare = null;
    static int row;
    static int column;
    static int selectedRow;
    static int selectedColumn;

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
                handleMove(argument);
                break;
            case "flip":
                handleFlip();

                break;
            case "block":
                handleBlock();
                break;
            case "hand":
                Output.printHand(GameEngine.activeTeam.hand);
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
                break;
        }
    }

    private static void handleState() {
        Output.printState(GameEngine.team1, GameEngine.team2);
        processCommands("board");
        processCommands("show");
    }

    private static void handleYield(String argument) {
        if (argument != null) {
            int discardIndex;
            try {
                discardIndex = Integer.parseInt(argument);
            } catch (NumberFormatException e) {
                System.err.println("ERROR: Invalid card index.");
                return;
            }

            Hand currentHand = GameEngine.activeTeam.hand;

            if (discardIndex < 1 || discardIndex > currentHand.hand.size()) {
                System.err.println("ERROR: Invalid card index.");
                return;
            }

            Unit unitToDiscard = currentHand.hand.get(discardIndex - 1);

            currentHand.removeUnitFromHand(unitToDiscard);
            Output.printDiscard(GameEngine.activeTeam.getName(), unitToDiscard);
        }

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
        }

        processCommands("board");
        processCommands("show");
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
            if (unitToShow.getQualifier().equals("Farmer") && unitToShow.getRole().equals("King")) {
                Output.printFarmerKing(unitToShow);
            } else if (!unitToShow.isFaceUp() && !unitToShow.getTeam().equals(GameEngine.activeTeam)) {
                Output.printHiddenUnit(unitToShow);
            } else {
                Output.printVisibleUnit(unitToShow);
            }
        }
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

        if (argument == null) {
            System.err.println("ERROR: No card index provided.");
        }

        int handIndex;
        try {
            handIndex = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid card index.");
            return;
        }

        Hand currentHand = GameEngine.activeTeam.hand;

        if (handIndex < 1 || handIndex > currentHand.hand.size()) {
            System.err.println("ERROR: Invalid card index.");
            return;
        }

        Unit unitToPlace = currentHand.hand.get(handIndex - 1);

        unitToPlace.setTeam(GameEngine.activeTeam);
        unitToPlace.setHasMovedThisTurn(true);
        GameBoard.setUnitAt(selectedRow, selectedColumn, unitToPlace);
        currentHand.removeUnitFromHand(unitToPlace);

        processCommands("board");
        processCommands("show");
    }

    private static void handleBlock() {
        if (selectedSquare == null) {
            System.err.println("ERROR: No square selected.");
            return;
        }

        Unit unitToBlock = GameBoard.getUnitAt(selectedRow, selectedColumn);

        if (unitToBlock == null) {
            System.err.println("ERROR: No unit on the selected square.");
            return;
        }

        if (!unitToBlock.getTeam().equals(GameEngine.activeTeam)) {
            System.err.println("ERROR: You can only block your own units.");
            return;
        }

        if (unitToBlock.hasMovedThisTurn()) {
            System.err.println("ERROR: Unit has already moved this turn.");
            return;
        }

        if (unitToBlock.isBlocking()) {
            System.err.println("ERROR: Unit is already blocking.");
            return;
        }

        unitToBlock.setBlocking(true);
        unitToBlock.setHasMovedThisTurn(true);
        Output.printBlock(unitToBlock.getUnitName(), selectedSquare);

        processCommands("board");
        processCommands("show");
    }

    private static void handleFlip() {
        if (selectedSquare == null) {
            System.err.println("ERROR: No square selected.");
            return;
        }

        Unit unitToFlip = GameBoard.getUnitAt(selectedRow, selectedColumn);

        if (unitToFlip == null) {
            System.err.println("ERROR: No unit on the selected square.");
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

        if (unitToFlip.hasMovedThisTurn()) {
            System.err.println("ERROR: Unit has already moved this turn.");
            return;
        }

        unitToFlip.setFaceUp(true);
        unitToFlip.setHasMovedThisTurn(true);
        Output.printFlip(unitToFlip.getUnitName(), unitToFlip.getAtk(), unitToFlip.getDef(), selectedSquare);
        processCommands("board");
        processCommands("show");
    }

    private static void handleMove(String argument) {
        if (selectedSquare == null) {
            System.err.println("ERROR: No square selected.");
            return;
        }
        if (argument == null || argument.length() != 2) {
            System.err.println("ERROR: Invalid target square.");
            return;
        }

        int targetRow = getCoordinates(argument)[0];
        int targetCol = getCoordinates(argument)[1];

        Unit movingUnit = GameBoard.getUnitAt(selectedRow, selectedColumn);

        if (movingUnit == null) {
            System.err.println("ERROR: No unit on the selected square.");
            return;
        }

        if (movingUnit.hasMovedThisTurn()) {
            System.err.println("ERROR: Unit has already moved this turn.");
            return;
        }

        int rowDiff = Math.abs(targetRow - selectedRow);
        int colDiff = Math.abs(targetCol - selectedColumn);

        if (rowDiff + colDiff > 1) {
            System.err.println("ERROR: Move must be exactly 1 square orthogonally or en place.");
            return;
        }

        Unit targetUnit = GameBoard.getUnitAt(targetRow, targetCol);
        boolean isMoverKing = movingUnit.getQualifier().equals("Farmer") && movingUnit.getRole().equals("King");

        if (targetUnit != null) {
            boolean isTargetKing = targetUnit.getQualifier().equals("Farmer") && targetUnit.getRole().equals("King");
            boolean isSameTeam = movingUnit.getTeam().equals(targetUnit.getTeam());

            if (isMoverKing && !isSameTeam) {
                System.err.println("ERROR: Farmer King cannot move onto an enemy unit.");
            }

            if (!isMoverKing && isTargetKing && isSameTeam) {
                System.err.println("ERROR: Unit cannot move onto its own Farmer King.");
                return;
            }
        }

        if (targetUnit == null) {
            GameBoard.setUnitAt(selectedRow, selectedColumn, null);
            GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
            movingUnit.setHasMovedThisTurn(true);
            Output.printMovement(movingUnit.getUnitName(), argument);

            selectedSquare = argument;
            selectedRow = targetRow;
            selectedColumn = targetCol;
            processCommands("board");
            processCommands("show");

        } else if (!movingUnit.getTeam().equals(targetUnit.getTeam())) {

            if (movingUnit.isBlocking()) {
                movingUnit.setBlocking(false);
                Output.printNoBlock(movingUnit.getUnitName());
            }

            Output.printAtkMove(movingUnit.getUnitName(), movingUnit.getAtk(), movingUnit.getDef(),
                    targetUnit.getUnitName(), targetUnit.getAtk(), targetUnit.getDef(), argument);

            if (!movingUnit.isFaceUp()) {
                movingUnit.setFaceUp(true);
                Output.printFlip(movingUnit.getUnitName(), movingUnit.getAtk(), movingUnit.getDef(), selectedSquare);
            }
            if (!targetUnit.isFaceUp()) {
                targetUnit.setFaceUp(true);
                Output.printFlip(targetUnit.getUnitName(), targetUnit.getAtk(), targetUnit.getDef(), argument);
            }

            boolean attackerMovesToTargetSquare = false;

            if (targetUnit.getQualifier().equals("Farmer") && targetUnit.getRole().equals("King")) {
                targetUnit.getTeam().takeDamage(movingUnit.getAtk());
                Output.printDamage(targetUnit.getTeam().getName(), movingUnit.getAtk());
            } else if (targetUnit.isBlocking()) {
                if (movingUnit.getAtk() > targetUnit.getDef()) {
                    GameBoard.setUnitAt(targetRow, targetCol, null);
                    Output.printElimination(targetUnit.getUnitName());
                    attackerMovesToTargetSquare = true;
                } else if (movingUnit.getAtk() < targetUnit.getDef()) {
                    int damage = targetUnit.getDef() - movingUnit.getAtk();
                    movingUnit.getTeam().takeDamage(damage);
                    Output.printDamage(movingUnit.getTeam().getName(), damage);
                }
            } else {
                if (movingUnit.getAtk() > targetUnit.getAtk()) {
                    int damage = movingUnit.getAtk() - targetUnit.getAtk();
                    targetUnit.getTeam().takeDamage(damage);
                    GameBoard.setUnitAt(targetRow, targetCol, null);
                    Output.printElimination(targetUnit.getUnitName());
                    Output.printDamage(targetUnit.getTeam().getName(), damage);
                    attackerMovesToTargetSquare = true;
                } else if (movingUnit.getAtk() < targetUnit.getAtk()) {
                    int damage = targetUnit.getAtk() - movingUnit.getAtk();
                    movingUnit.getTeam().takeDamage(damage);
                    Output.printElimination(movingUnit.getUnitName());
                    Output.printDamage(movingUnit.getTeam().getName(), damage);
                } else {
                    GameBoard.setUnitAt(selectedRow, selectedColumn, null);
                    GameBoard.setUnitAt(targetRow, targetCol, null);
                    Output.printElimination(targetUnit.getUnitName());
                    Output.printElimination(movingUnit.getUnitName());
                }
            }

            if (attackerMovesToTargetSquare) {
                GameBoard.setUnitAt(selectedRow, selectedColumn, null);
                GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
                Output.printMovement(movingUnit.getUnitName(), argument);

                selectedSquare = argument;
                selectedRow = targetRow;
                selectedColumn = targetCol;
            }

            movingUnit.setHasMovedThisTurn(true);

            if (GameEngine.team1.getTeamHP() <= 0) {
                Output.printZeroPoints(GameEngine.team1.getName());
                Output.printWin(GameEngine.team2.getName());
                isRunning = false;
            } else if (GameEngine.team2.getTeamHP() <= 0) {
                Output.printZeroPoints(GameEngine.team2.getName());
                Output.printWin(GameEngine.team1.getName());
                isRunning = false;
            }

            if (isRunning) {
                processCommands("board");
                processCommands("show");
            }

        } else {
            Output.printMovement(movingUnit.getUnitName(), argument);
            Output.printMerge(movingUnit.getUnitName(), targetUnit.getUnitName(), argument);

            Unit mergedUnit = movingUnit.mergeUnits(movingUnit, targetUnit);

            if (mergedUnit != null) {
                GameBoard.setUnitAt(selectedRow, selectedColumn, null);
                GameBoard.setUnitAt(targetRow, targetCol, mergedUnit);
                mergedUnit.setHasMovedThisTurn(true);
            } else {
                GameBoard.setUnitAt(selectedRow, selectedColumn, null);
                GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
                movingUnit.setHasMovedThisTurn(true);
                Output.printMergeFail(movingUnit.getUnitName());
            }

            selectedSquare = argument;
            selectedRow = targetRow;
            selectedColumn = targetCol;
            processCommands("board");
            processCommands("show");
        }
    }

    private static void handleBoard() {
        if (currentSquare != null && currentSquare.length() == 2) {
            GameBoard.showGameBoard(currentSquare.charAt(0),
                    Character.getNumericValue(currentSquare.charAt(1)));
        }
    }

    private static void handleSelect(String argument) {
        if (argument != null && argument.length() == 2) {
            selectedSquare = argument;
            currentSquare = argument;

            selectedRow = getCoordinates(argument)[0];
            selectedColumn = getCoordinates(argument)[1];

            processCommands("board");
            processCommands("show");
        } else {
            System.err.println("ERROR: Invalid square selected.");
        }
    }

    private static int[] getCoordinates(String coordinate) {
        int[] coords = new int[2];

        coords[0] = 7 - Character.getNumericValue(coordinate.charAt(1));
        coords[1] = Character.getNumericValue(coordinate.toUpperCase().charAt(0)) - 10;

        return coords;
    }


}

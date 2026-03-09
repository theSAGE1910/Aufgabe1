package edu.kit.kastel;

/**
 * Handles all movement and combat interactions for units on the game board.
 * This class validates move commands, manages empty square movement, executes
 * friendly unit merges, and resolves combat when moving onto an enemy unit.
 * @author uxuwg
 * @version 0.9
 */
public final class MovementController {

    private MovementController() {
    }

    /**
     * Validates that the move interaction does not violate any special rules regarding the Farmer King unit.
     * @param movingUnit the unit that is attempting to move
     * @param targetUnit the unit that is currently on the target square (can be null if the square is empty)
     * @return true if the move is valid with respect to Farmer King interactions, false if it violates any rules
     */
    public static boolean isValidKingInteraction(Unit movingUnit, Unit targetUnit) {
        if (targetUnit == null) {
            return true;
        }

        boolean isMoverKing = Unit.isKing(movingUnit);
        boolean isTargetKing = Unit.isKing(targetUnit);
        boolean isSameTeam = movingUnit.getTeam().equals(targetUnit.getTeam());

        if (isMoverKing && !isSameTeam) {
            System.err.println("ERROR: Farmer King cannot move onto an enemy unit.");
            return false;
        }

        if (!isMoverKing && isTargetKing && isSameTeam) {
            System.err.println("ERROR: Unit cannot move onto its own Farmer King.");
            return false;
        }

        return true;
    }

    /**
     * Validates that the move command has a properly formatted target square argument.
     * @param argument the target square argument from the move command
     * @return true if the argument is valid, false if it is null or does not have exactly 2 characters
     */
    public static boolean isArgumentValid(String argument) {
        if (argument == null || argument.length() != 2) {
            System.err.println("ERROR: Invalid target square.");
            return false;
        }
        return true;
    }

    /**
     * Validates that the target square is within the allowed movement distance of 1 square orthogonally or en place.
     * @param targetRow the row index of the target square
     * @param targetCol the column index of the target square
     * @return true if the target square is within the allowed movement distance, false if it is too far away
     */
    public static boolean isDistanceValid(int targetRow, int targetCol) {
        int rowDiff = Math.abs(targetRow - GameState.selectedRow);
        int colDiff = Math.abs(targetCol - GameState.selectedColumn);

        if (rowDiff + colDiff > 1) {
            System.err.println("ERROR: Move must be exactly 1 square orthogonally or en place.");
            return false;
        }
        return true;
    }

    /**
     * Executes the move command based on the type of interaction between the moving unit and the target square.
     * @param argument the target square argument from the move command
     * @param targetUnit the unit that is currently on the target square
     * @param targetRow the row index of the target square
     * @param targetCol the column index of the target square
     * @param movingUnit the unit that is attempting to move
     */
    public static void executeMove(String argument, Unit targetUnit, int targetRow, int targetCol, Unit movingUnit) {
        if (movingUnit.isBlocking()) {
            movingUnit.setBlocking(false);
            Output.printNoBlock(movingUnit.getUnitName());
        }

        if (movingUnit == targetUnit) {
            movingUnit.setHasMovedThisTurn(true);
            Output.printMovement(movingUnit.getUnitName(), argument);
            GameUI.updateDisplay();
        } else if (targetUnit == null) {
            moveToEmptySquare(argument, targetRow, targetCol, movingUnit);
        } else if (!movingUnit.getTeam().equals(targetUnit.getTeam())) {
            initiateCombat(argument, movingUnit, targetUnit, targetRow, targetCol);
        } else {
            mergeFriendlyUnits(argument, movingUnit, targetUnit, targetRow, targetCol);
        }
    }

    private static void moveToEmptySquare(String argument, int targetRow, int targetCol, Unit movingUnit) {
        GameBoard.setUnitAt(GameState.selectedRow, GameState.selectedColumn, null);
        GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
        movingUnit.setHasMovedThisTurn(true);
        Output.printMovement(movingUnit.getUnitName(), argument);

        GameState.selectedSquare = argument;
        GameState.selectedRow = targetRow;
        GameState.selectedColumn = targetCol;
        GameUI.updateDisplay();
    }

    private static void initiateCombat(String argument, Unit movingUnit, Unit targetUnit, int targetRow, int targetCol) {
        String targetDisplay;
        if (Unit.isKing(targetUnit)) {
            targetDisplay = targetUnit.getUnitName();
        } else if (!targetUnit.isFaceUp() && !targetUnit.getTeam().equals(GameEngine.activeTeam)) {
            targetDisplay = "???";
        } else {
            targetDisplay = targetUnit.getUnitName() + " (" + targetUnit.getAtk() + "/" + targetUnit.getDef() + ")";
        }
        Output.printAtkMove(movingUnit.getUnitName(), movingUnit.getAtk(), movingUnit.getDef(),
                targetDisplay, argument);

        revealCombatUnits(argument, movingUnit, targetUnit);

        boolean attackerMovesToTargetSquare = resolveCombat(movingUnit, targetUnit, targetRow, targetCol);

        if (attackerMovesToTargetSquare) {
            GameBoard.setUnitAt(GameState.selectedRow, GameState.selectedColumn, null);
            GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
            Output.printMovement(movingUnit.getUnitName(), argument);

            GameState.selectedSquare = argument;
            GameState.selectedRow = targetRow;
            GameState.selectedColumn = targetCol;
        }

        movingUnit.setHasMovedThisTurn(true);
        hpStatusCheck();

        GameUI.updateDisplay();
    }

    private static void mergeFriendlyUnits(String argument, Unit movingUnit, Unit targetUnit, int targetRow, int targetCol) {
        Output.printMovement(movingUnit.getUnitName(), argument);
        Output.printMerge(movingUnit.getUnitName(), targetUnit.getUnitName(), argument);

        Unit mergedUnit = movingUnit.mergeUnits(movingUnit, targetUnit);

        GameBoard.setUnitAt(GameState.selectedRow, GameState.selectedColumn, null);
        if (mergedUnit != null) {
            GameBoard.setUnitAt(targetRow, targetCol, mergedUnit);
            mergedUnit.setHasMovedThisTurn(false);
            System.out.println("Success!");
        } else {
            GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
            movingUnit.setHasMovedThisTurn(true);
            Output.printMergeFail(targetUnit.getUnitName());
        }

        GameState.selectedSquare = argument;
        GameState.selectedRow = targetRow;
        GameState.selectedColumn = targetCol;
        GameUI.updateDisplay();
    }

    private static void revealCombatUnits(String argument, Unit movingUnit, Unit targetUnit) {
        if (!movingUnit.isFaceUp() && !Unit.isKing(movingUnit)) {
            movingUnit.setFaceUp(true);
            Output.printFlip(movingUnit.getUnitName(), movingUnit.getAtk(), movingUnit.getDef(), GameState.selectedSquare);
        }
        if (!targetUnit.isFaceUp() && !Unit.isKing(targetUnit)) {
            targetUnit.setFaceUp(true);
            Output.printFlip(targetUnit.getUnitName(), targetUnit.getAtk(), targetUnit.getDef(), argument);
        }
    }

    private static boolean resolveCombat(Unit movingUnit, Unit targetUnit, int targetRow, int targetCol) {
        if (Unit.isKing(targetUnit)) {
            targetUnit.getTeam().takeDamage(movingUnit.getAtk());
            Output.printDamage(targetUnit.getTeam().getName(), movingUnit.getAtk());
            return false;
        }

        if (targetUnit.isBlocking()) {
            return resolveBlockCombat(movingUnit, targetUnit, targetRow, targetCol);
        }

        return resolveStandardCombat(movingUnit, targetUnit, targetRow, targetCol);
    }

    private static boolean resolveBlockCombat(Unit movingUnit, Unit targetUnit, int targetRow, int targetCol) {
        if (movingUnit.getAtk() > targetUnit.getDef()) {
            GameBoard.setUnitAt(targetRow, targetCol, null);
            Output.printElimination(targetUnit.getUnitName());
            return true;
        } else if (movingUnit.getAtk() < targetUnit.getDef()) {
            int damage = targetUnit.getDef() - movingUnit.getAtk();
            movingUnit.getTeam().takeDamage(damage);
            Output.printDamage(movingUnit.getTeam().getName(), damage);
            return false;
        }
        return false;
    }

    private static boolean resolveStandardCombat(Unit movingUnit, Unit targetUnit, int targetRow, int targetCol) {
        if (movingUnit.getAtk() > targetUnit.getAtk()) {
            int damage = movingUnit.getAtk() - targetUnit.getAtk();
            targetUnit.getTeam().takeDamage(damage);
            GameBoard.setUnitAt(targetRow, targetCol, null);
            Output.printElimination(targetUnit.getUnitName());
            Output.printDamage(targetUnit.getTeam().getName(), damage);
            return true;
        } else if (movingUnit.getAtk() < targetUnit.getAtk()) {
            int damage = targetUnit.getAtk() - movingUnit.getAtk();
            movingUnit.getTeam().takeDamage(damage);
            GameBoard.setUnitAt(GameState.selectedRow, GameState.selectedColumn, null);
            Output.printElimination(movingUnit.getUnitName());
            Output.printDamage(movingUnit.getTeam().getName(), damage);
            return false;
        } else {
            GameBoard.setUnitAt(GameState.selectedRow, GameState.selectedColumn, null);
            GameBoard.setUnitAt(targetRow, targetCol, null);
            Output.printElimination(targetUnit.getUnitName());
            Output.printElimination(movingUnit.getUnitName());
            return false;
        }
    }

    private static void hpStatusCheck() {
        if (GameEngine.team1.getTeamHP() <= 0) {
            Output.printZeroPoints(GameEngine.team1.getName());
            Output.printWin(GameEngine.team2.getName());
            GameState.isRunning = false;
        } else if (GameEngine.team2.getTeamHP() <= 0) {
            Output.printZeroPoints(GameEngine.team2.getName());
            Output.printWin(GameEngine.team1.getName());
            GameState.isRunning = false;
        }
    }
}

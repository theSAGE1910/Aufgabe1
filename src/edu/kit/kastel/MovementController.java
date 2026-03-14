package edu.kit.kastel;

/**
 * Handles all movement and combat interactions for units on the game board.
 * This class validates move commands, manages empty square movement, executes
 * friendly unit merges, and resolves combat when moving onto an enemy unit.
 * @author uxuwg
 * @version 0.9
 */
public final class MovementController {

    private static final String ERROR_FARMER_KING_CANNOT_MOVE_ONTO_AN_ENEMY_UNIT = "ERROR: Farmer King cannot move onto an enemy unit.";
    private static final String ERROR_UNIT_CANNOT_MOVE_ONTO_ITS_OWN_FARMER_KING = "ERROR: Unit cannot move onto its own Farmer King.";
    private static final String ERROR_INVALID_TARGET_SQUARE = "ERROR: Invalid target square.";
    private static final String ERROR_MOVE_MUST_BE_EXACTLY_1_SQUARE_ORTHOGONALLY_OR_EN_PLACE
            = "ERROR: Move must be exactly 1 square orthogonally or en place.";
    private static final String HIDDEN_TARGET = "???";

    private static final int DISTANCE_ONE = 1;
    private static final int DISTANCE_TWO = 2;
    private static final String FORMAT_PAREN_OPEN = " (";
    private static final String FORMAT_SLASH = "/";
    private static final String FORMAT_PAREN_CLOSE = ")";
    private static final int ZERO_HP = 0;

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
            System.err.println(ERROR_FARMER_KING_CANNOT_MOVE_ONTO_AN_ENEMY_UNIT);
            return false;
        }

        if (!isMoverKing && isTargetKing && isSameTeam) {
            System.err.println(ERROR_UNIT_CANNOT_MOVE_ONTO_ITS_OWN_FARMER_KING);
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
        if (argument == null || argument.length() != DISTANCE_TWO) {
            System.err.println(ERROR_INVALID_TARGET_SQUARE);
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
        int rowDiff = Math.abs(targetRow - GameState.getSelectedRow());
        int colDiff = Math.abs(targetCol - GameState.getSelectedColumn());

        if (rowDiff + colDiff > DISTANCE_ONE) {
            System.err.println(ERROR_MOVE_MUST_BE_EXACTLY_1_SQUARE_ORTHOGONALLY_OR_EN_PLACE);
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
            Output.printBlockStatus(movingUnit.getUnitName(), null, false);
        }

        if (movingUnit == targetUnit) {
            movingUnit.setMovedThisTurn(true);
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
        GameBoard.setUnitAt(GameState.getSelectedRow(), GameState.getSelectedColumn(), null);
        GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
        movingUnit.setMovedThisTurn(true);
        Output.printMovement(movingUnit.getUnitName(), argument);

        GameState.setSelectedSquare(argument);
        GameState.setSelectedRow(targetRow);
        GameState.setSelectedColumn(targetCol);
        GameUI.updateDisplay();
    }

    private static void initiateCombat(String argument, Unit movingUnit, Unit targetUnit, int targetRow, int targetCol) {
        String targetDisplay;
        if (Unit.isKing(targetUnit)) {
            targetDisplay = targetUnit.getUnitName();
        } else if (!targetUnit.isFaceUp() && !targetUnit.getTeam().equals(GameEngine.getActiveTeam())) {
            targetDisplay = HIDDEN_TARGET;
        } else {
            targetDisplay = targetUnit.getUnitName() + FORMAT_PAREN_OPEN + targetUnit.getAtk()
                    + FORMAT_SLASH + targetUnit.getDef() + FORMAT_PAREN_CLOSE;
        }
        Output.printAtkMove(movingUnit.getUnitName(), movingUnit.getAtk(), movingUnit.getDef(),
                targetDisplay, argument);

        revealCombatUnits(argument, movingUnit, targetUnit);

        boolean attackerMovesToTargetSquare = resolveCombat(movingUnit, targetUnit, targetRow, targetCol);

        if (attackerMovesToTargetSquare) {
            GameBoard.setUnitAt(GameState.getSelectedRow(), GameState.getSelectedColumn(), null);
            GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
            Output.printMovement(movingUnit.getUnitName(), argument);

            GameState.setSelectedSquare(argument);
            GameState.setSelectedRow(targetRow);
            GameState.setSelectedColumn(targetCol);
        }

        movingUnit.setMovedThisTurn(true);
        hpStatusCheck();

        GameUI.updateDisplay();
    }

    private static void mergeFriendlyUnits(String argument, Unit movingUnit, Unit targetUnit, int targetRow, int targetCol) {
        Output.printMovement(movingUnit.getUnitName(), argument);
        Output.printMerge(movingUnit.getUnitName(), targetUnit.getUnitName(), argument);

        Unit mergedUnit = movingUnit.mergeUnits(movingUnit, targetUnit);

        GameBoard.setUnitAt(GameState.getSelectedRow(), GameState.getSelectedColumn(), null);
        if (mergedUnit != null) {
            GameBoard.setUnitAt(targetRow, targetCol, mergedUnit);
            mergedUnit.setMovedThisTurn(false);
            System.out.println(GameMessages.SUCCESS_MESSAGE);
        } else {
            GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
            movingUnit.setMovedThisTurn(true);
            Output.printMergeFail(targetUnit.getUnitName());
        }

        GameState.setSelectedSquare(argument);
        GameState.setSelectedRow(targetRow);
        GameState.setSelectedColumn(targetCol);
        GameUI.updateDisplay();
    }

    private static void revealCombatUnits(String argument, Unit movingUnit, Unit targetUnit) {
        if (!movingUnit.isFaceUp() && !Unit.isKing(movingUnit)) {
            movingUnit.setFaceUp(true);
            Output.printFlip(movingUnit.getUnitName(), movingUnit.getAtk(), movingUnit.getDef(), GameState.getSelectedSquare());
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
            GameBoard.setUnitAt(GameState.getSelectedRow(), GameState.getSelectedColumn(), null);
            Output.printElimination(movingUnit.getUnitName());
            Output.printDamage(movingUnit.getTeam().getName(), damage);
            return false;
        } else {
            GameBoard.setUnitAt(GameState.getSelectedRow(), GameState.getSelectedColumn(), null);
            GameBoard.setUnitAt(targetRow, targetCol, null);
            Output.printElimination(targetUnit.getUnitName());
            Output.printElimination(movingUnit.getUnitName());
            return false;
        }
    }

    private static void hpStatusCheck() {
        if (GameEngine.getTeam1().getTeamHP() <= ZERO_HP) {
            Output.printGameOver(GameEngine.getTeam1().getName(), GameEngine.getTeam2().getName());
            GameState.setIsRunning(false);
        } else if (GameEngine.getTeam2().getTeamHP() <= ZERO_HP) {
            Output.printGameOver(GameEngine.getTeam2().getName(), GameEngine.getTeam1().getName());
            GameState.setIsRunning(false);
        }
    }
}
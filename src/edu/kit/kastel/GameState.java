package edu.kit.kastel;

/**
 * Centralized class for managing the state of the game, including selected square, active unit, and turn status.
 * @author uxuwg
 * @version 0.9
 */
public final class GameState {
    private static final String ERR_NO_UNIT = "ERROR: No unit on the selected square.";
    private static final String ERR_ALREADY_MOVED = "ERROR: Unit has already moved this turn.";
    private static final int DEFAULT_COORDINATE = 0;

    private static boolean isRunning = true;

    private static String selectedSquare = null;

    private static int selectedRow = DEFAULT_COORDINATE;

    private static int selectedColumn = DEFAULT_COORDINATE;

    private static boolean hasPlacedThisTurn = false;

    private GameState() {
    }

    /**
     * Validates that a square is selected, holds a unit, and the unit hasn't moved yet.
     * @return the valid Unit, or null if validation fails
     */
    public static Unit getValidatedActiveUnit() {
        if (getSelectedSquare() == null) {
            System.err.println(GameMessages.ERR_NO_SQUARE);
            return null;
        }

        Unit unit = GameBoard.getUnitAt(getSelectedRow(), getSelectedColumn());
        if (unit == null) {
            System.err.println(ERR_NO_UNIT);
            return null;
        }
        if (unit.hasMovedThisTurn()) {
            System.err.println(ERR_ALREADY_MOVED);
            return null;
        }
        return unit;
    }

    /**
     * Flag indicating whether the main game loop is currently running.
     * @return true if the game is running, false if it has been terminated.
     */
    public static boolean isIsRunning() {
        return isRunning;
    }

    /**
     * Sets the flag for whether the game is running. Should be set to false to terminate the main game loop and end the application.
     * @param isRunning the boolean value to set for whether the game is running.
     */
    public static void setIsRunning(boolean isRunning) {
        GameState.isRunning = isRunning;
    }

    /**
     * The coordinate string of the currently selected square.
     * @return the coordinate string of the selected square, or null if no square is currently selected.
     */
    public static String getSelectedSquare() {
        return selectedSquare;
    }

    /**
     * Sets the coordinate string for the currently selected square.
     * @param selectedSquare the coordinate string to set for the currently selected square.
     */
    public static void setSelectedSquare(String selectedSquare) {
        GameState.selectedSquare = selectedSquare;
    }

    /**
     * The row index of the currently selected square.
     * @return the row index of the selected square, or 0 if no square is selected.
     */
    public static int getSelectedRow() {
        return selectedRow;
    }

    /**
     * Sets the row index for the currently selected square. Should be a valid index on the game board, or 0 if no square is selected.
     * @param selectedRow the row index to set for the currently selected square.
     */
    public static void setSelectedRow(int selectedRow) {
        GameState.selectedRow = selectedRow;
    }

    /**
     * The column index of the currently selected square.
     * @return the column index of the selected square, or 0 if no square is selected.
     */
    public static int getSelectedColumn() {
        return selectedColumn;
    }

    /**
     * Sets the column index for the currently selected square. Should be a valid index on the game board, or 0 if no square is selected.
     * @param selectedColumn the column index to set for the currently selected square.
     */
    public static void setSelectedColumn(int selectedColumn) {
        GameState.selectedColumn = selectedColumn;
    }

    /**
     * Flag indicating whether the player has placed a unit during the current turn.
     * @return true if the player has placed a unit this turn, false otherwise.
     */
    public static boolean isHasPlacedThisTurn() {
        return hasPlacedThisTurn;
    }

    /**
     * Sets the flag for whether the player has placed a unit this turn.
     * Should be set to true after a successful placement, and reset to false at the start of each new turn.
     * @param hasPlacedThisTurn the boolean value to set for whether the player has placed a unit this turn.
     */
    public static void setHasPlacedThisTurn(boolean hasPlacedThisTurn) {
        GameState.hasPlacedThisTurn = hasPlacedThisTurn;
    }
}
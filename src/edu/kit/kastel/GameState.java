package edu.kit.kastel;

/**
 *
 * @author uxuwg
 */
public final class GameState {
    /** Flag indicating whether the main game loop is currently running. */
    public static boolean isRunning = true;

    /** The coordinate string of the currently selected square. */
    public static String selectedSquare = null;

    /** The row index of the currently selected square. */
    public static int selectedRow = 0;

    /** The column index of the currently selected square. */
    public static int selectedColumn = 0;

    /** Flag indicating whether the player has placed a unit during the current turn. */
    public static boolean hasPlacedThisTurn = false;

    private GameState() {
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
}

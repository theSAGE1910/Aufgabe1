package edu.kit.kastel.ai;

/**
 * Represents a specific target square on the game board.
 * This class is primarily used by the AI to store and evaluate
 * target coordinates for unit movement and placement.
 * @author uxuwg
 * @version 0.7
 */
public class TargetSquare {

    private final int row;
    private final int col;

    /**
     * Constructs a new TargetSquare with the specified coordinates.
     * @param row the row index of the target square
     * @param col the column index of the target square
     */
    public TargetSquare(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the row index of the target square.
     * @return the row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column index of the target square.
     * @return the column index
     */
    public int getCol() {
        return col;
    }
}

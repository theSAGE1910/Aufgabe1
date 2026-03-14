package edu.kit.kastel;

/**
 * Represents the main game board and handles all positional logic and rendering.
 * This class stores the current layout of units and provides utility methods
 * for retrieving units, checking valid spaces, and printing the board to the console.
 * @author uxuwg
 * @version 0.9
 */
public final class GameBoard {

    /**
     * The standard width and height of the game board.
     */
    public static final int DIMENSION = 7;

    /**
     * The 2D array storing the positions of all units currently on the board.
     */
    private static final Unit[][] GAME_BOARD = new Unit[DIMENSION][DIMENSION];

    private static final int START_INDEX = 0;
    private static final int ROW_INDEX = 0;
    private static final int COL_INDEX = 1;
    private static final int COORD_LENGTH = 2;
    private static final int RADIX_TEN = 10;
    private static final int NOT_FOUND = -1;
    private static final int START_COUNT = 0;

    private GameBoard() {
    }

    /**
     * Retrieves the unit currently located at the specified coordinates.
     * @param row the row index
     * @param col the column index
     * @return the Unit at the coordinates, or null if the space is empty
     */
    public static Unit getUnitAt(int row, int col) {
        return GAME_BOARD[row][col];
    }

    /**
     * Places a unit at the specified coordinates on the board.
     * @param row the row index
     * @param col the column index
     * @param unit the Unit to place (or null to clear the space)
     */
    public static void setUnitAt(int row, int col, Unit unit) {
        GAME_BOARD[row][col] = unit;
    }

    /**
     * Finds the current position of the Enemy team's Farmer King on the board.
     * @return an integer array with the row at index 0 and column at index 1, or null if not found
     */
    public static int[] getEnemyKingPosition() {
        return getKingPosition(GameEngine.getTeam2());
    }

    /**
     * Finds the current position of the Player team's Farmer King on the board.
     * @return an integer array with the row at index 0 and column at index 1, or null if not found
     */
    public static int[] getPlayerKingPosition() {
        return getKingPosition(GameEngine.getTeam1());
    }

    private static int[] getKingPosition(Team team) {
        for (int row = START_INDEX; row < DIMENSION; row++) {
            for (int col = START_INDEX; col < DIMENSION; col++) {
                Unit unit = getUnitAt(row, col);
                if (unit != null && unit.getTeam().equals(team) && unit.getRole().equals(GameMessages.KING)) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the current row index of a specific unit on the board.
     * @param unit the unit to find
     * @return the integer row index, or -1 if the unit is not on the board
     */
    public static int getUnitRow(Unit unit) {
        return getUnitPosition(unit)[ROW_INDEX];
    }

    /**
     * Retrieves the current column index of a specific unit on the board.
     * @param unit the unit to find
     * @return the integer column index, or -1 if the unit is not on the board
     */
    public static int getUnitCol(Unit unit) {
        return getUnitPosition(unit)[COL_INDEX];
    }

    private static int[] getUnitPosition(Unit unit) {
        for (int row = START_INDEX; row < DIMENSION; row++) {
            for (int col = START_INDEX; col < DIMENSION; col++) {
                if (getUnitAt(row, col) == unit) {
                    return new int[]{row, col};
                }
            }
        }
        return new int[]{NOT_FOUND, NOT_FOUND};
    }

    /**
     * Converts an alphanumeric coordinate string into board array indices.
     * @param coordinate the coordinate string
     * @return an integer array containing the row at index 0 and column at index 1
     */
    public static int[] getCoordinates(String coordinate) {
        int[] coords = new int[COORD_LENGTH];

        coords[ROW_INDEX] = DIMENSION - Character.getNumericValue(coordinate.charAt(COL_INDEX));
        coords[COL_INDEX] = Character.getNumericValue(coordinate.toUpperCase().charAt(ROW_INDEX)) - RADIX_TEN;

        return coords;
    }

    /**
     * Counts the total number of standard units (excluding the Farmer King) a team has on the board.
     * @param team the team whose units are being counted
     * @return the number of units the team currently has on the board
     */
    public static int getBoardCount(Team team) {
        int count = START_COUNT;
        for (int row = START_INDEX; row < DIMENSION; row++) {
            for (int col = START_INDEX; col < DIMENSION; col++) {
                Unit boardUnit = getUnitAt(row, col);
                if (boardUnit != null && boardUnit.getTeam().equals(team) && !boardUnit.getRole().equals(GameMessages.KING)) {
                    count++;
                }
            }
        }
        return count;
    }
}
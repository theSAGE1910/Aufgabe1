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
    public static final Unit[][] GAME_BOARD = new Unit[DIMENSION][DIMENSION];

    private static final String EMPTY = "   ";

    private GameBoard() {
    }

    /**
     * Checks if there is at least one orthogonally adjacent empty space around a coordinate.
     * @param row the row index to check around
     * @param column the column index to check around
     * @return true if an adjacent empty space exists, false otherwise or if out of bounds
     */
    public static boolean checkEmptySpace(int row, int column) {
        if (row < 0 || row >= DIMENSION || column < 0 || column >= DIMENSION) {
            return false;
        }

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : directions) {
            int adjRow = row + dir[0];
            int adjCol = column + dir[1];

            if (adjRow >= 0 && adjRow < DIMENSION && adjCol >= 0 && adjCol < DIMENSION) {
                if (GAME_BOARD[adjRow][adjCol] == null) {
                    return true;
                }
            }
        }
        return false;
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
     * Prints the current state of the game board to the console, optionally
     * highlighting a specific selected square.
     * @param selCol the character column to highlight
     * @param selRow the integer row to highlight
     */
    public static void showGameBoard(char selCol, int selRow) {
        int selectedRow = DIMENSION - selRow;
        int selectedCol = selCol - 'A';

        for (int row = 0; row <= DIMENSION; row++) {
            if (GameData.verbosity.equalsIgnoreCase("all")) {
                System.out.print("  ");
                for (int col = 0; col <= DIMENSION; col++) {
                    System.out.print(getIntersectionChar(row, col, selectedRow, selectedCol));
                    if (col < DIMENSION) {
                        char hLine = getHorizontalChar(row, col, selectedRow, selectedCol);
                        System.out.print("" + hLine + hLine + hLine);
                    }
                }
                System.out.println();
            }
            if (row < DIMENSION) {
                System.out.print((DIMENSION - row) + " ");
                for (int col = 0; col <= DIMENSION; col++) {
                    System.out.print(getVerticalChar(row, col, selectedRow, selectedCol));
                    if (col < DIMENSION) {
                        if (GAME_BOARD[row][col] == null) {
                            System.out.print(EMPTY);
                        } else {
                            System.out.print(GAME_BOARD[row][col].toString());
                        }
                    }
                }
                System.out.println();
            }
        }
        System.out.print("    ");

        for (char ch = 'A'; ch < 'A' + DIMENSION; ch++) {
            System.out.print(ch);
            if (ch < 'A' + DIMENSION - 1) {
                System.out.print("   ");
            }
        }
        System.out.println();
    }

    private static char getIntersectionChar(int row, int col, int selectedRow, int selectedCol) {
        boolean isTopLeft = (row == selectedRow && col == selectedCol);
        boolean isTopRight = (row == selectedRow && col == selectedCol + 1);
        boolean isBottomLeft = (row == selectedRow + 1 && col == selectedCol);
        boolean isBottomRight = (row == selectedRow + 1 && col == selectedCol + 1);

        if (row == 0) {
            return getTopEdgeChar(col, isTopLeft, isTopRight);
        }
        if (row == DIMENSION) {
            return getBottomEdgeChar(col, isBottomLeft, isBottomRight);
        }
        if (col == 0) {
            return getLeftEdgeChar(isTopLeft, isBottomLeft);
        }
        if (col == DIMENSION) {
            return getRightEdgeChar(isTopRight, isBottomRight);
        }

        return getInnerChar(isTopLeft, isTopRight, isBottomLeft, isBottomRight);
    }

    private static char getInnerChar(boolean isTopLeft, boolean isTopRight, boolean isBottomLeft, boolean isBottomRight) {
        if (isTopLeft) {
            return BoardTheme.get(BoardTheme.SEL_INNER_TOP_LEFT);
        }
        if (isTopRight) {
            return BoardTheme.get(BoardTheme.SEL_INNER_TOP_RIGHT);
        }
        if (isBottomLeft) {
            return BoardTheme.get(BoardTheme.SEL_INNER_BOTTOM_LEFT);
        }
        if (isBottomRight) {
            return BoardTheme.get(BoardTheme.SEL_INNER_BOTTOM_RIGHT);
        }

        return BoardTheme.get(BoardTheme.CROSS);
    }

    private static char getTopEdgeChar(int col, boolean isTopLeft, boolean isTopRight) {
        if (col == 0) {
            return isTopLeft ? BoardTheme.get(BoardTheme.SEL_CORNER_TOP_LEFT) : BoardTheme.get(BoardTheme.CORNER_TOP_LEFT);
        }
        if (col == DIMENSION) {
            return isTopRight ? BoardTheme.get(BoardTheme.SEL_CORNER_TOP_RIGHT) : BoardTheme.get(BoardTheme.CORNER_TOP_RIGHT);
        }
        if (isTopLeft) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_TOP_LEFT);
        }
        if (isTopRight) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_TOP_RIGHT);
        }
        return BoardTheme.get(BoardTheme.EDGE_TOP);
    }

    private static char getBottomEdgeChar(int col, boolean isBottomLeft, boolean isBottomRight) {
        if (col == 0) {
            return isBottomLeft ? BoardTheme.get(BoardTheme.SEL_CORNER_BOTTOM_LEFT) : BoardTheme.get(BoardTheme.CORNER_BOTTOM_LEFT);
        }
        if (col == DIMENSION) {
            return isBottomRight ? BoardTheme.get(BoardTheme.SEL_CORNER_BOTTOM_RIGHT) : BoardTheme.get(BoardTheme.CORNER_BOTTOM_RIGHT);
        }
        if (isBottomLeft) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_BOTTOM_LEFT);
        }
        if (isBottomRight) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_BOTTOM_RIGHT);
        }
        return BoardTheme.get(BoardTheme.EDGE_BOTTOM);
    }

    private static char getLeftEdgeChar(boolean isTopLeft, boolean isBottomLeft) {
        if (isTopLeft) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_LEFT_TOP);
        }
        if (isBottomLeft) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_LEFT_BOTTOM);
        }
        return BoardTheme.get(BoardTheme.EDGE_LEFT);
    }

    private static char getRightEdgeChar(boolean isTopRight, boolean isBottomRight) {
        if (isTopRight) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_RIGHT_TOP);
        }
        if (isBottomRight) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_RIGHT_BOTTOM);
        }
        return BoardTheme.get(BoardTheme.EDGE_RIGHT);
    }

    private static char getHorizontalChar(int row, int col, int selectedRow, int selectedCol) {
        boolean isHighlighted = (col == selectedCol) && (row == selectedRow || row == selectedRow + 1);
        return (isHighlighted ? BoardTheme.get(BoardTheme.SEL_HORIZONTAL) : BoardTheme.get(BoardTheme.HORIZONTAL));
    }

    private static char getVerticalChar(int row, int col, int selectedRow, int selectedCol) {
        boolean isHighlighted = (row == selectedRow) && (col == selectedCol || col == selectedCol + 1);
        return (isHighlighted ? BoardTheme.get(BoardTheme.SEL_VERTICAL) : BoardTheme.get(BoardTheme.VERTICAL));
    }

    /**
     * Finds the current position of the Enemy team's Farmer King on the board.
     * @return an integer array with the row at index 0 and column at index 1, or null if not found
     */
    public static int[] getEnemyKingPosition() {
        return getKingPosition(GameEngine.team2);
    }

    /**
     * Finds the current position of the Player team's Farmer King on the board.
     * @return an integer array with the row at index 0 and column at index 1, or null if not found
     */
    public static int[] getPlayerKingPosition() {
        return getKingPosition(GameEngine.team1);
    }

    private static int[] getKingPosition(Team team) {
        for (int row = 0; row < DIMENSION; row++) {
            for (int col = 0; col < DIMENSION; col++) {
                Unit unit = getUnitAt(row, col);
                if (unit != null && unit.getTeam().equals(team) && unit.getRole().equals("King")) {
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
        return getUnitPosition(unit)[0];
    }

    //if I have time, make these two methods into one, by passing key row and col hehe:)

    /**
     * Retrieves the current column index of a specific unit on the board.
     * @param unit the unit to find
     * @return the integer column index, or -1 if the unit is not on the board
     */
    public static int getUnitCol(Unit unit) {
        return getUnitPosition(unit)[1];
    }

    private static int[] getUnitPosition(Unit unit) {
        for (int row = 0; row < DIMENSION; row++) {
            for (int col = 0; col < DIMENSION; col++) {
                if (getUnitAt(row, col) == unit) {
                    return new int[]{row, col};
                }
            }
        }
        return new int[]{-1, -1};
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
}

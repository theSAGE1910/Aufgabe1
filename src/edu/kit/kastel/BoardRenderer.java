package edu.kit.kastel;

/**
 * The BoardRenderer class is responsible for rendering the game board to the console.
 * It provides methods to display the current state of the game board.
 * @author uxuwg
 * @version 0.9
 */
public final class BoardRenderer {

    private static final int START_INDEX = 0;
    private static final int OFFSET_ONE = 1;
    private static final String SPACE = " ";
    private static final String TWO_SPACES = "  ";
    private static final String THREE_SPACES = "   ";
    private static final String FOUR_SPACES = "    ";

    private static final char OUT_OF_BOUND_COL = 'Z';

    private BoardRenderer() {
    }

    /**
     * Prints the current state of the game board to the console, optionally
     * highlighting a specific selected square.
     * @param selCol the character column to highlight
     * @param selRow the integer row to highlight
     */
    public static void showGameBoard(char selCol, int selRow) {
        int selectedRow = GameBoard.DIMENSION - selRow;
        int selectedCol = selCol - GameMessages.CHAR_BASE;

        for (int row = START_INDEX; row <= GameBoard.DIMENSION; row++) {
            if (GameData.getVerbosity().equalsIgnoreCase(GameMessages.ALL)) {
                System.out.print(TWO_SPACES);
                for (int col = START_INDEX; col <= GameBoard.DIMENSION; col++) {
                    System.out.print(getIntersectionChar(row, col, selectedRow, selectedCol));
                    if (col < GameBoard.DIMENSION) {
                        char hLine = getHorizontalChar(row, col, selectedRow, selectedCol);
                        System.out.print("" + hLine + hLine + hLine);
                    }
                }
                System.out.println();
            }
            if (row < GameBoard.DIMENSION) {
                System.out.print((GameBoard.DIMENSION - row) + SPACE);
                for (int col = START_INDEX; col <= GameBoard.DIMENSION; col++) {
                    System.out.print(getVerticalChar(row, col, selectedRow, selectedCol));
                    if (col < GameBoard.DIMENSION) {
                        if (GameBoard.getUnitAt(row, col) == null) {
                            System.out.print(THREE_SPACES);
                        } else {
                            System.out.print(GameBoard.getUnitAt(row, col).toString());
                        }
                    }
                }
                System.out.println();
            }
        }
        System.out.print(FOUR_SPACES);

        for (char ch = GameMessages.CHAR_BASE; ch < GameMessages.CHAR_BASE + GameBoard.DIMENSION; ch++) {
            System.out.print(ch);
            if (ch < GameMessages.CHAR_BASE + GameBoard.DIMENSION - OFFSET_ONE) {
                System.out.print(THREE_SPACES);
            }
        }
        System.out.println();
    }

    /**
     * Overloaded method to show the game board without any highlighted square.
     */
    public static void showGameBoard() {
        showGameBoard(OUT_OF_BOUND_COL, START_INDEX);
    }

    private static char getIntersectionChar(int row, int col, int selectedRow, int selectedCol) {
        boolean isTopLeft = (row == selectedRow && col == selectedCol);
        boolean isTopRight = (row == selectedRow && col == selectedCol + OFFSET_ONE);
        boolean isBottomLeft = (row == selectedRow + OFFSET_ONE && col == selectedCol);
        boolean isBottomRight = (row == selectedRow + OFFSET_ONE && col == selectedCol + OFFSET_ONE);

        if (row == START_INDEX) {
            return getTopEdgeChar(col, isTopLeft, isTopRight);
        }
        if (row == GameBoard.DIMENSION) {
            return getBottomEdgeChar(col, isBottomLeft, isBottomRight);
        }
        if (col == START_INDEX) {
            return getLeftEdgeChar(isTopLeft, isBottomLeft);
        }
        if (col == GameBoard.DIMENSION) {
            return getRightEdgeChar(isTopRight, isBottomRight);
        }

        return getInnerChar(isTopLeft, isTopRight, isBottomLeft, isBottomRight);
    }

    private static char getInnerChar(boolean isTopLeft, boolean isTopRight, boolean isBottomLeft, boolean isBottomRight) {
        if (isTopLeft) {
            return BoardTheme.get(BoardTheme.SEL_INNER_BOTTOM_RIGHT);
        }
        if (isTopRight) {
            return BoardTheme.get(BoardTheme.SEL_INNER_BOTTOM_LEFT);
        }
        if (isBottomLeft) {
            return BoardTheme.get(BoardTheme.SEL_INNER_TOP_RIGHT);
        }
        if (isBottomRight) {
            return BoardTheme.get(BoardTheme.SEL_INNER_TOP_LEFT);
        }

        return BoardTheme.get(BoardTheme.CROSS);
    }

    private static char getTopEdgeChar(int col, boolean isTopLeft, boolean isTopRight) {
        if (col == START_INDEX) {
            return isTopLeft ? BoardTheme.get(BoardTheme.SEL_CORNER_TOP_LEFT) : BoardTheme.get(BoardTheme.CORNER_TOP_LEFT);
        }
        if (col == GameBoard.DIMENSION) {
            return isTopRight ? BoardTheme.get(BoardTheme.SEL_CORNER_TOP_RIGHT) : BoardTheme.get(BoardTheme.CORNER_TOP_RIGHT);
        }
        if (isTopLeft) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_TOP_RIGHT);
        }
        if (isTopRight) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_TOP_LEFT);
        }
        return BoardTheme.get(BoardTheme.EDGE_TOP);
    }

    private static char getBottomEdgeChar(int col, boolean isBottomLeft, boolean isBottomRight) {
        if (col == START_INDEX) {
            return isBottomLeft ? BoardTheme.get(BoardTheme.SEL_CORNER_BOTTOM_LEFT) : BoardTheme.get(BoardTheme.CORNER_BOTTOM_LEFT);
        }
        if (col == GameBoard.DIMENSION) {
            return isBottomRight ? BoardTheme.get(BoardTheme.SEL_CORNER_BOTTOM_RIGHT) : BoardTheme.get(BoardTheme.CORNER_BOTTOM_RIGHT);
        }
        if (isBottomLeft) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_BOTTOM_RIGHT);
        }
        if (isBottomRight) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_BOTTOM_LEFT);
        }
        return BoardTheme.get(BoardTheme.EDGE_BOTTOM);
    }

    private static char getLeftEdgeChar(boolean isTopLeft, boolean isBottomLeft) {
        if (isTopLeft) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_LEFT_BOTTOM);
        }
        if (isBottomLeft) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_LEFT_TOP);
        }
        return BoardTheme.get(BoardTheme.EDGE_LEFT);
    }

    private static char getRightEdgeChar(boolean isTopRight, boolean isBottomRight) {
        if (isTopRight) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_RIGHT_BOTTOM);
        }
        if (isBottomRight) {
            return BoardTheme.get(BoardTheme.SEL_EDGE_RIGHT_TOP);
        }
        return BoardTheme.get(BoardTheme.EDGE_RIGHT);
    }

    private static char getHorizontalChar(int row, int col, int selectedRow, int selectedCol) {
        boolean isHighlighted = (col == selectedCol) && (row == selectedRow || row == selectedRow + OFFSET_ONE);
        return (isHighlighted ? BoardTheme.get(BoardTheme.SEL_HORIZONTAL) : BoardTheme.get(BoardTheme.HORIZONTAL));
    }

    private static char getVerticalChar(int row, int col, int selectedRow, int selectedCol) {
        boolean isHighlighted = (row == selectedRow) && (col == selectedCol || col == selectedCol + OFFSET_ONE);
        return (isHighlighted ? BoardTheme.get(BoardTheme.SEL_VERTICAL) : BoardTheme.get(BoardTheme.VERTICAL));
    }
}
package edu.kastel.kit.edu;

public final class GameBoard {
    public static final int DIMENSION = 7;
    private static final String EMPTY = "   ";
    public static final Unit[][] GAME_BOARD = new Unit[DIMENSION][DIMENSION];

    private GameBoard() {
    }

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

    //public static boolean checkEnemy

    public static Unit getUnitAt(int row, int col) {
        return GAME_BOARD[row][col];
    }

    public static void setUnitAt(int row, int col, Unit unit) {
        GAME_BOARD[row][col] = unit;
    }

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

    public static int[] getEnemyKingPosition() {
        return getKingPosition(GameEngine.team2);
    }

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

    public static int getUnitRow(Unit unit) {
        return getUnitPosition(unit)[0];
    }

    //if I have time, make these two methods into one, by passing key row and col hehe:)

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
}

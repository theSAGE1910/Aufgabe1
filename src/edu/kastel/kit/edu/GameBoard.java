package edu.kastel.kit.edu;

public class GameBoard {
    public static final int DIMENSION = 7;
    public static final String EMPTY = "   ";
    public static Unit[][] gameBoard = new Unit[DIMENSION][DIMENSION];

    public static boolean checkEmptySpace(int row, int column) {
        return gameBoard[row + 1][column] == null
                || gameBoard[row - 1][column] == null
                || gameBoard[row][column + 1] == null
                || gameBoard[row][column - 1] == null;
    }

    //public static boolean checkEnemy

    public static Unit getUnitAt(int row, int col) {
        return gameBoard[row][col];
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
                        if (gameBoard[row][col] == null) {
                            System.out.print(EMPTY);
                        } else {
                            System.out.print(gameBoard[row][col].toString());
                        }
                    }
                }
                System.out.println();
            }
        }
        System.out.print("    ");

        for (char ch = 'A'; ch < 'A' + DIMENSION; ch++) {
            System.out.print(ch + "   ");
        }
        System.out.println();
    }

    private static char getIntersectionChar(int row, int col, int selectedRow, int selectedCol) {
        boolean isTopLeft = (row == selectedRow && col == selectedCol);
        boolean isTopRight = (row == selectedRow && col == selectedCol + 1);
        boolean isBottomLeft = (row == selectedRow + 1 && col == selectedCol);
        boolean isBottomRight = (row == selectedRow + 1 && col == selectedCol + 1);
        if (row == 0 && col == 0) {
            return (isTopLeft ? BoardTheme.selCornerTopLeft : BoardTheme.cornerTopLeft);
        } else if (row == 0 && col == DIMENSION) {
            return (isTopRight ? BoardTheme.selCornerTopRight : BoardTheme.cornerTopRight);
        } else if (row == DIMENSION && col == 0) {
            return (isBottomLeft ? BoardTheme.selCornerBottomLeft : BoardTheme.cornerBottomLeft);
        } else if (row == DIMENSION && col == DIMENSION) {
            return (isBottomRight ? BoardTheme.selCornerBottomRight : BoardTheme.cornerBottomRight);
        }
        if (row == 0) {
            if (isTopLeft) {
                return (BoardTheme.selEdgeTopLeft);
            } else if (isTopRight) {
                return (BoardTheme.selEdgeTopRight);
            } else {
                return (BoardTheme.edgeTop);
            }
        } else if (col == DIMENSION) {
            if (isTopRight) {
                return (BoardTheme.selEdgeRightTop);
            } else if (isBottomRight) {
                return (BoardTheme.selEdgeRightBottom);
            } else {
                return (BoardTheme.edgeRight);
            }
        } else if (row == DIMENSION) {
            if (isBottomLeft) {
                return (BoardTheme.selEdgeBottomLeft);
            } else if (isBottomRight) {
                return (BoardTheme.selEdgeBottomRight);
            } else {
                return (BoardTheme.edgeBottom);
            }
        } else if (col == 0) {
            if (isTopLeft) {
                return (BoardTheme.selEdgeLeftTop);
            } else if (isBottomLeft) {
                return (BoardTheme.selEdgeLeftBottom);
            } else {
                return (BoardTheme.edgeLeft);
            }
        }
        if (isTopLeft) {
            return (BoardTheme.selInnerTopLeft);
        } else if (isTopRight) {
            return (BoardTheme.selInnerTopRight);
        } else if (isBottomLeft) {
            return (BoardTheme.selInnerBottomLeft);
        } else if (isBottomRight) {
            return (BoardTheme.selInnerBottomRight);
        }
        return (BoardTheme.cross);
    }

    private static char getHorizontalChar(int row, int col, int selectedRow, int selectedCol) {
        boolean isHighlighted = (col == selectedCol) && (row == selectedRow || row == selectedRow + 1);
        return (isHighlighted ? BoardTheme.selHorizontal : BoardTheme.horizontal);
    }

    private static char getVerticalChar(int row, int col, int selectedRow, int selectedCol) {
        boolean isHighlighted = (row == selectedRow) && (col == selectedCol || col == selectedCol + 1);
        return (isHighlighted ? BoardTheme.selVertical : BoardTheme.vertical);
    }

    public static void setUnitAt(int row, int col, Unit unit) {
        gameBoard[row][col] = unit;
    }

    public static int[] getEnemyKingPosition() {
        for (int row = 0; row < GameBoard.DIMENSION; row++) {
            for (int col = 0; col < GameBoard.DIMENSION; col++) {
                Unit unit = GameBoard.getUnitAt(row, col);
                if (unit != null && unit.getTeam().equals(GameEngine.team2) && unit.getRole().equals("King")) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    public static int[] getPlayerKingPosition() {
        for (int row = 0; row < GameBoard.DIMENSION; row++) {
            for (int col = 0; col < GameBoard.DIMENSION; col++) {
                Unit unit = GameBoard.getUnitAt(row, col);
                if (unit != null && unit.getTeam().equals(GameEngine.team1) && unit.getRole().equals("King")) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    public static int getUnitRow(Unit unit) {
        for (int row = 0; row < GameBoard.DIMENSION; row++) {
            for (int col = 0; col < GameBoard.DIMENSION; col++) {
                if (GameBoard.getUnitAt(row, col) == unit) {
                    return row;
                }
            }
        }
        return -1;
    }

    //try to make these two methods into one, by passing key row and col hehe:)

    public static int getUnitCol(Unit unit) {
        for (int row = 0; row < GameBoard.DIMENSION; row++) {
            for (int col = 0; col < GameBoard.DIMENSION; col++) {
                if (GameBoard.getUnitAt(row, col) == unit) {
                    return col;
                }
            }
        }
        return -1;
    }
}

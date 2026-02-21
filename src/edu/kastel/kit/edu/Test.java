package edu.kastel.kit.edu;

public class Test {
    public static final int DIMENSION = 7;

    static String[][] gameBoard = new String[DIMENSION][DIMENSION];

    // Exactly 29 characters matching the 0-28 index mapping
    static String standardKeySet = "++++++++-|+############=N####";
    static String debugKeySet = "abcdefghijklmnopqrstuvwxyzäöü";

    public static void initialiseGameBoard() {
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                gameBoard[i][j] = "   ";
            }
        }
    }

    public static void showAllGameBoardFinal(int selRow, char selCol, String charSet) {
        // Map visual coordinates (Row 7-1, Col A-G) to array coordinates (0-6)
        int sr = DIMENSION - selRow;
        int sc = selCol - 'A';

        // r loops 0 to 7 (8 horizontal intersection lines)
        for (int r = 0; r <= DIMENSION; r++) {

            System.out.print("  "); // Indent to align with row numbers below

            // 1. Print Intersections and Horizontal Lines
            for (int c = 0; c <= DIMENSION; c++) {
                // Print the corner / junction / cross
                System.out.print(getIntersectionChar(r, c, sr, sc, charSet));

                // Print the horizontal line bridging to the next intersection
                if (c < DIMENSION) {
                    char hLine = getHorizontalLineChar(r, c, sr, sc, charSet);
                    // Print it 3 times to span the width of the " x " cell
                    System.out.print("" + hLine + hLine + hLine);
                }
            }
            System.out.println();

            // 2. Print Row Numbers, Vertical Lines, and Cell Contents
            if (r < DIMENSION) {
                int displayRow = DIMENSION - r;
                System.out.print(displayRow + " "); // Print row number

                for (int c = 0; c <= DIMENSION; c++) {
                    // Print vertical boundary
                    System.out.print(getVerticalLineChar(r, c, sr, sc, charSet));

                    // Print actual cell content
                    if (c < DIMENSION) {
                        System.out.print(gameBoard[r][c]);
                    }
                }
                System.out.println();
            }
        }

        // 3. Print bottom letters A–G
        System.out.print("    ");
        for (char ch = 'A'; ch < 'A' + DIMENSION; ch++) {
            System.out.print(ch + "   ");
        }
        System.out.println();
    }

    // --- HELPER METHODS FOR THE 29-CHARACTER MAPPING ---

    private static char getIntersectionChar(int r, int c, int sr, int sc, String chars) {
        // Determine relationship to the selected cell (sr, sc)
        boolean isTopLeft = (r == sr && c == sc);
        boolean isTopRight = (r == sr && c == sc + 1);
        boolean isBottomLeft = (r == sr + 1 && c == sc);
        boolean isBottomRight = (r == sr + 1 && c == sc + 1);

        // 1. Outer Corners (0-3, 11-14)
        if (r == 0 && c == 0) {
            return chars.charAt(isTopLeft ? 11 : 0);
        }
        if (r == 0 && c == DIMENSION) {
            return chars.charAt(isTopRight ? 12 : 1);
        }
        if (r == DIMENSION && c == 0) {
            return chars.charAt(isBottomLeft ? 13 : 2);
        }
        if (r == DIMENSION && c == DIMENSION) {
            return chars.charAt(isBottomRight ? 14 : 3);
        }

        // 2. T-Junctions (4-7, 15-22)
        if (r == 0) { // Top T-junction
            if (isTopRight) return chars.charAt(15); // Left neighbor selected
            if (isTopLeft) return chars.charAt(16);  // Right neighbor selected
            return chars.charAt(4);
        }
        if (r == DIMENSION) { // Bottom T-junction
            if (isBottomRight) return chars.charAt(19); // Left neighbor selected
            if (isBottomLeft) return chars.charAt(20);  // Right neighbor selected
            return chars.charAt(6);
        }
        if (c == 0) { // Left T-junction
            if (isBottomLeft) return chars.charAt(21); // Top neighbor selected
            if (isTopLeft) return chars.charAt(22);    // Bottom neighbor selected
            return chars.charAt(7);
        }
        if (c == DIMENSION) { // Right T-junction
            if (isBottomRight) return chars.charAt(17); // Top neighbor selected
            if (isTopRight) return chars.charAt(18);    // Bottom neighbor selected
            return chars.charAt(5);
        }

        // 3. Central Crosses (10, 25-28)
        if (isBottomRight) {
            return chars.charAt(25); // Top-left neighbor selected
        }
        if (isBottomLeft) {
            return chars.charAt(26);  // Top-right neighbor selected
        }
        if (isTopRight) {
            return chars.charAt(27);    // Bottom-left neighbor selected
        }
        if (isTopLeft) {
            return chars.charAt(28);     // Bottom-right neighbor selected
        }

        return chars.charAt(10); // Standard cross
    }

    private static char getHorizontalLineChar(int r, int c, int sr, int sc, String chars) {
        boolean isHighlighted = (c == sc) && (r == sr || r == sr + 1);
        return chars.charAt(isHighlighted ? 23 : 8);
    }

    private static char getVerticalLineChar(int r, int c, int sr, int sc, String chars) {
        boolean isHighlighted = (r == sr) && (c == sc || c == sc + 1);
        return chars.charAt(isHighlighted ? 24 : 9);
    }

    public static void main(String[] args) {
        initialiseGameBoard();
        System.out.println("--- STANDARD KEYSET ---");
        showAllGameBoardFinal(2, 'G', standardKeySet);

        System.out.println("\n--- DEBUG KEYSET ---");
        showAllGameBoardFinal(2, 'G', debugKeySet);
    }
}
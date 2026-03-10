package edu.kit.kastel;

/**
 * Utility class that manages the characters used to draw the game board.
 * It stores the indices for each specific board piece and retrieves the
 * corresponding character from the loaded theme string.
 * @author uxuwg
 * @version 0.9
 */
public final class BoardTheme {
    /**
     * Index for the top-left corner character.
     */
    public static final int CORNER_TOP_LEFT = 0;
    /**
     * Index for the top-right corner character.
     */
    public static final int CORNER_TOP_RIGHT = 1;
    /**
     * Index for the bottom-left corner character.
     */
    public static final int CORNER_BOTTOM_LEFT = 2;
    /**
     * Index for the bottom-right corner character.
     */
    public static final int CORNER_BOTTOM_RIGHT = 3;

    /**
     * Index for the top edge character.
     */
    public static final int EDGE_TOP = 4;
    /**
     * Index for the right edge character.
     */
    public static final int EDGE_RIGHT = 5;
    /**
     * Index for the bottom edge character.
     */
    public static final int EDGE_BOTTOM = 6;
    /**
     * Index for the left edge character.
     */
    public static final int EDGE_LEFT = 7;

    /**
     * Index for the horizontal line character.
     */
    public static final int HORIZONTAL = 8;
    /**
     * Index for the vertical line character.
     */
    public static final int VERTICAL = 9;
    /**
     * Index for the crossing intersection character.
     */
    public static final int CROSS = 10;

    /**
     * Index for the selected top-left corner character.
     */
    public static final int SEL_CORNER_TOP_LEFT = 11;
    /**
     * Index for the selected top-right corner character.
     */
    public static final int SEL_CORNER_TOP_RIGHT = 12;
    /**
     * Index for the selected bottom-left corner character.
     */
    public static final int SEL_CORNER_BOTTOM_LEFT = 13;
    /**
     * Index for the selected bottom-right corner character.
     */
    public static final int SEL_CORNER_BOTTOM_RIGHT = 14;

    /**
     * Index for the selected top-left edge character.
     */
    public static final int SEL_EDGE_TOP_LEFT = 15;
    /**
     * Index for the selected top-right edge character.
     */
    public static final int SEL_EDGE_TOP_RIGHT = 16;
    /**
     * Index for the selected right-top edge character.
     */
    public static final int SEL_EDGE_RIGHT_TOP = 17;
    /**
     * Index for the selected right-bottom edge character.
     */
    public static final int SEL_EDGE_RIGHT_BOTTOM = 18;
    /**
     * Index for the selected bottom-left edge character.
     */
    public static final int SEL_EDGE_BOTTOM_LEFT = 19;
    /**
     * Index for the selected bottom-right edge character.
     */
    public static final int SEL_EDGE_BOTTOM_RIGHT = 20;
    /**
     * Index for the selected left-top edge character.
     */
    public static final int SEL_EDGE_LEFT_TOP = 21;
    /**
     * Index for the selected left-bottom edge character.
     */
    public static final int SEL_EDGE_LEFT_BOTTOM = 22;

    /**
     * Index for the selected horizontal line character.
     */
    public static final int SEL_HORIZONTAL = 23;
    /**
     * Index for the selected vertical line character.
     */
    public static final int SEL_VERTICAL = 24;

    /**
     * Index for the selected inner top-left intersection character.
     */
    public static final int SEL_INNER_TOP_LEFT = 25;
    /**
     * Index for the selected inner top-right intersection character.
     */
    public static final int SEL_INNER_TOP_RIGHT = 26;
    /**
     * Index for the selected inner bottom-left intersection character.
     */
    public static final int SEL_INNER_BOTTOM_LEFT = 27;
    /**
     * Index for the selected inner bottom-right intersection character.
     */
    public static final int SEL_INNER_BOTTOM_RIGHT = 28;

    /**
     * The minimum length of the theme string required to properly initialize the board theme.
     */
    public static final int MIN_KEYSET_LENGTH = 29;

    private static final String STANDARD_CHARSET = "++++++++-|+############=N####";

    private static String currentTheme = null;

    private BoardTheme() {
    }

    /**
     * Initializes the board theme using the loaded game data.
     * If the provided data is invalid or shorter than the required 29 characters,
     * it falls back to the standard default character set.
     */
    public static void initialiseTheme() {
        String keySet = GameData.getBoardData();
        if (keySet == null || keySet.length() < MIN_KEYSET_LENGTH) {
            currentTheme = STANDARD_CHARSET;
        } else {
            currentTheme = keySet;
        }
    }

    /**
     * Retrieves a specific character from the current board theme based on its index.
     *
     * @param symbolIndex the index of the symbol to retrieve
     * @return the character at the specified index in the theme string
     */
    public static char get(int symbolIndex) {
        return currentTheme.charAt(symbolIndex);
    }
}

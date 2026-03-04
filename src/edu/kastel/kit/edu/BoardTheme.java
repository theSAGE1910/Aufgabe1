package edu.kastel.kit.edu;

public final class BoardTheme {
    public static final int CORNER_TOP_LEFT = 0;
    public static final int CORNER_TOP_RIGHT = 1;
    public static final int CORNER_BOTTOM_LEFT = 2;
    public static final int CORNER_BOTTOM_RIGHT = 3;

    public static final int EDGE_TOP = 4;
    public static final int EDGE_RIGHT = 5;
    public static final int EDGE_BOTTOM = 6;
    public static final int EDGE_LEFT = 7;

    public static final int HORIZONTAL = 8;
    public static final int VERTICAL = 9;
    public static final int CROSS = 10;

    public static final int SEL_CORNER_TOP_LEFT = 11;
    public static final int SEL_CORNER_TOP_RIGHT = 12;
    public static final int SEL_CORNER_BOTTOM_LEFT = 13;
    public static final int SEL_CORNER_BOTTOM_RIGHT = 14;

    public static final int SEL_EDGE_TOP_LEFT = 15;
    public static final int SEL_EDGE_TOP_RIGHT = 16;
    public static final int SEL_EDGE_RIGHT_TOP = 17;
    public static final int SEL_EDGE_RIGHT_BOTTOM = 18;
    public static final int SEL_EDGE_BOTTOM_LEFT = 19;
    public static final int SEL_EDGE_BOTTOM_RIGHT = 20;
    public static final int SEL_EDGE_LEFT_TOP = 21;
    public static final int SEL_EDGE_LEFT_BOTTOM = 22;

    public static final int SEL_HORIZONTAL = 23;
    public static final int SEL_VERTICAL = 24;

    public static final int SEL_INNER_TOP_LEFT = 25;
    public static final int SEL_INNER_TOP_RIGHT = 26;
    public static final int SEL_INNER_BOTTOM_LEFT = 27;
    public static final int SEL_INNER_BOTTOM_RIGHT = 28;

    private static final String STANDARD_CHARSET = "++++++++-|+############=N####";
    private static String currentTheme = STANDARD_CHARSET;

    private BoardTheme() {
    }

    public static void initialiseTheme() {
        String keySet = GameData.boardData;
        if (keySet == null || keySet.length() < 29) {
            currentTheme = STANDARD_CHARSET;
        } else {
            currentTheme = keySet;
        }
    }

    public static char get(int symbolIndex) {
        return currentTheme.charAt(symbolIndex);
    }
}

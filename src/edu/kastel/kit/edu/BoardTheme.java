package edu.kastel.kit.edu;

public class BoardTheme {
    private static final String STANDARD_CHARSET = "++++++++-|+############=N####";

    public static char cornerTopLeft;
    public static char cornerTopRight;
    public static char cornerBottomLeft;
    public static char cornerBottomRight;

    public static char edgeTop;
    public static char edgeRight;
    public static char edgeBottom;
    public static char edgeLeft;

    public static char horizontal;
    public static char vertical;
    public static char cross;

    public static char selCornerTopLeft;
    public static char selCornerTopRight;
    public static char selCornerBottomLeft;
    public static char selCornerBottomRight;

    public static char selEdgeTopLeft;
    public static char selEdgeTopRight;
    public static char selEdgeRightTop;
    public static char selEdgeRightBottom;
    public static char selEdgeBottomLeft;
    public static char selEdgeBottomRight;
    public static char selEdgeLeftTop;
    public static char selEdgeLeftBottom;
    public static char selHorizontal;
    public static char selVertical;

    public static char selInnerTopLeft;
    public static char selInnerTopRight;
    public static char selInnerBottomLeft;
    public static char selInnerBottomRight;

    public static void initialiseTheme() {
        String keySet = GameData.boardData;
        if (keySet == null || keySet.length() < 29) {
            keySet = STANDARD_CHARSET;
        }
        cornerTopLeft = keySet.charAt(0);
        cornerTopRight = keySet.charAt(1);
        cornerBottomLeft = keySet.charAt(2);
        cornerBottomRight = keySet.charAt(3);

        edgeTop = keySet.charAt(4);
        edgeRight = keySet.charAt(5);
        edgeBottom = keySet.charAt(6);
        edgeLeft = keySet.charAt(7);

        horizontal = keySet.charAt(8);
        vertical = keySet.charAt(9);
        cross = keySet.charAt(10);

        selCornerTopLeft = keySet.charAt(11);
        selCornerTopRight = keySet.charAt(12);
        selCornerBottomLeft = keySet.charAt(13);
        selCornerBottomRight = keySet.charAt(14);

        selEdgeTopLeft = keySet.charAt(15);
        selEdgeTopRight = keySet.charAt(16);
        selEdgeRightTop = keySet.charAt(17);
        selEdgeRightBottom = keySet.charAt(18);
        selEdgeBottomLeft = keySet.charAt(19);
        selEdgeBottomRight = keySet.charAt(20);
        selEdgeLeftTop = keySet.charAt(21);
        selEdgeLeftBottom = keySet.charAt(22);

        selHorizontal = keySet.charAt(23);
        selVertical = keySet.charAt(24);

        selInnerTopLeft = keySet.charAt(25);
        selInnerTopRight = keySet.charAt(26);
        selInnerBottomLeft = keySet.charAt(27);
        selInnerBottomRight = keySet.charAt(28);
    }
}

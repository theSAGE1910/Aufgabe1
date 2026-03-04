package edu.kastel.kit.edu;

public class BoardTheme {
    private static final String STANDARD_CHARSET = "++++++++-|+############=N####";

    private static char cornerTopLeft;
    private static char cornerTopRight;
    private static char cornerBottomLeft;
    private static char cornerBottomRight;

    private static char edgeTop;
    private static char edgeRight;
    private static char edgeBottom;
    private static char edgeLeft;

    private static char horizontal;
    private static char vertical;
    private static char cross;

    private static char selCornerTopLeft;
    private static char selCornerTopRight;
    private static char selCornerBottomLeft;
    private static char selCornerBottomRight;

    private static char selEdgeTopLeft;
    private static char selEdgeTopRight;
    private static char selEdgeRightTop;
    private static char selEdgeRightBottom;
    private static char selEdgeBottomLeft;
    private static char selEdgeBottomRight;
    private static char selEdgeLeftTop;
    private static char selEdgeLeftBottom;
    private static char selHorizontal;
    private static char selVertical;

    private static char selInnerTopLeft;
    private static char selInnerTopRight;
    private static char selInnerBottomLeft;
    private static char selInnerBottomRight;

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

    public static char getCornerTopLeft() {
        return cornerTopLeft;
    }
    public static char getCornerTopRight() {
        return cornerTopRight;
    }
    public static char getCornerBottomLeft() {
        return cornerBottomLeft;
    }
    public static char getCornerBottomRight() {
        return cornerBottomRight;
    }

    public static char getEdgeTop() {
        return edgeTop;
    }
    public static char getEdgeRight() {
        return edgeRight;
    }
    public static char getEdgeBottom() {
        return edgeBottom;
    }
    public static char getEdgeLeft() {
        return edgeLeft;
    }

    public static char getHorizontal() {
        return horizontal;
    }
    public static char getVertical() {
        return vertical;
    }
    public static char getCross() {
        return cross;
    }

    public static char getSelCornerTopLeft() {
        return selCornerTopLeft;
    }
    public static char getSelCornerTopRight() {
        return selCornerTopRight;
    }
    public static char getSelCornerBottomLeft() {
        return selCornerBottomLeft;
    }
    public static char getSelCornerBottomRight() {
        return selCornerBottomRight;
    }

    public static char getSelEdgeTopLeft() {
        return selEdgeTopLeft;
    }
    public static char getSelEdgeTopRight() {
        return selEdgeTopRight;
    }
    public static char getSelEdgeRightTop() {
        return selEdgeRightTop;
    }
    public static char getSelEdgeRightBottom() {
        return selEdgeRightBottom;
    }
    public static char getSelEdgeBottomLeft() {
        return selEdgeBottomLeft;
    }
    public static char getSelEdgeBottomRight() {
        return selEdgeBottomRight;
    }
    public static char getSelEdgeLeftTop() {
        return selEdgeLeftTop;
    }
    public static char getSelEdgeLeftBottom() {
        return selEdgeLeftBottom;
    }

    public static char getSelHorizontal() {
        return selHorizontal;
    }
    public static char getSelVertical() {
        return selVertical;
    }

    public static char getSelInnerTopLeft() {
        return selInnerTopLeft;
    }
    public static char getSelInnerTopRight() {
        return selInnerTopRight;
    }
    public static char getSelInnerBottomLeft() {
        return selInnerBottomLeft;
    }
    public static char getSelInnerBottomRight() {
        return selInnerBottomRight;
    }
}

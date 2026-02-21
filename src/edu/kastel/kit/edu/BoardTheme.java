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

    public BoardTheme() {
        String keySet = GameData.boardData;
        if (keySet == null || keySet.length() < 29) {
            keySet = STANDARD_CHARSET;
        }
        this.cornerTopLeft = keySet.charAt(0);
        this.cornerTopRight = keySet.charAt(1);
        this.cornerBottomLeft = keySet.charAt(2);
        this.cornerBottomRight = keySet.charAt(3);

        this.edgeTop = keySet.charAt(4);
        this.edgeRight = keySet.charAt(5);
        this.edgeBottom = keySet.charAt(6);
        this.edgeLeft = keySet.charAt(7);

        this.horizontal = keySet.charAt(8);
        this.vertical = keySet.charAt(9);
        this.cross = keySet.charAt(10);

        this.selCornerTopLeft = keySet.charAt(11);
        this.selCornerTopRight = keySet.charAt(12);
        this.selCornerBottomLeft = keySet.charAt(13);
        this.selCornerBottomRight = keySet.charAt(14);

        this.selEdgeTopLeft = keySet.charAt(15);
        this.selEdgeTopRight = keySet.charAt(16);
        this.selEdgeRightTop = keySet.charAt(17);
        this.selEdgeRightBottom = keySet.charAt(18);
        this.selEdgeBottomLeft = keySet.charAt(19);
        this.selEdgeBottomRight = keySet.charAt(20);
        this.selEdgeLeftTop = keySet.charAt(21);
        this.selEdgeLeftBottom = keySet.charAt(22);

        this.selHorizontal = keySet.charAt(23);
        this.selVertical = keySet.charAt(24);

        this.selInnerTopLeft = keySet.charAt(25);
        this.selInnerTopRight = keySet.charAt(26);
        this.selInnerBottomLeft = keySet.charAt(27);
        this.selInnerBottomRight = keySet.charAt(28);
    }
}

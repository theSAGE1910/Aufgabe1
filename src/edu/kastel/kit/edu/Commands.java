package edu.kastel.kit.edu;

public class Commands {
    public static final String REGEX_SPACE = " ";
    public static boolean isRunning = true;
    static String currentSquare;
    static int row;
    static int column;

    public static void processCommands(String input) {
        String key = input;
        String[] words = input.split(REGEX_SPACE);
        if (words.length == 2) {
            key = words[0];
            currentSquare = words[1];
            if (words[1].length() == 2) {
                row = Character.getNumericValue(currentSquare.charAt(1)) - 7;
                column = Character.getNumericValue(currentSquare.toUpperCase().charAt(0)) - 10;
            }
        }

        switch (key.toLowerCase()) {
            case "select":
                processCommands("board");
                processCommands("show");
                //display details of the unit on the selected field.
                break;
            case "board":
                if (words[1].length() == 2) {
                    GameBoard.showGameBoard(currentSquare.charAt(0),
                            Character.getNumericValue(currentSquare.charAt(1)));
                }
                break;
            case "move":

                break;
            case "flip":
                break;
            case "block":
                break;
            case "hand":
                break;
            case "place":
                break;
            case "show":
                for (Unit unit : Unit.unitList) {
                    if (unit.row == row && unit.column == column) {

                    }
                }
                break;
            case "yield":
                break;
            case "state":
                break;
            case "quit":
                isRunning = false;
                break;
            default:
                break;
        }
    }


}

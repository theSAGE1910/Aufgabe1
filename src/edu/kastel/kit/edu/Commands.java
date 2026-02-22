package edu.kastel.kit.edu;

public class Commands {
    public static final String REGEX_SPACE = " ";
    public static boolean isRunning = true;
    static String currentSquare;
    static String prevSquare;
    static int row;
    static int column;

    public static void processCommands(String input) {
        String key = input;
        String[] words = input.split(REGEX_SPACE);
        if (words.length == 2) {
            key = words[0];
            currentSquare = words[1];
            if (words[1].length() == 2) {
                row = getCoordinates(currentSquare)[0];
                column = getCoordinates(currentSquare)[1];
            }
        }

        switch (key.toLowerCase()) {
            case "select":
                processCommands("board");
                processCommands("show");
                prevSquare = currentSquare;
                //display details of the unit on the selected field.
                break;
            case "board":
                if (words[1].length() == 2) {
                    GameBoard.showGameBoard(currentSquare.charAt(0),
                            Character.getNumericValue(currentSquare.charAt(1)));
                }
                break;
            case "move":
                if (GameBoard.checkEmptySpace(row, column)) {

                }
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
                if (currentSquare == null) {
                    System.out.println("ERROR: No square selected.");
                    break;
                }

                Unit unit = GameBoard.getUnitAt(row, column);

                if (unit == null) {
                    System.out.println();
                } else {
                    if (unit.getQualifier().equals("Farmer") && unit.getRole().equals("King")) {
                        Output.printFarmerKing(unit);
                    } else if (!unit.isFaceUp() && !unit.getTeam().equals(GameEngine.activeTeam)) {
                        Output.printHiddenUnit(unit);
                    } else {
                        Output.printVisibleUnit(unit);
                    }
//                    Output.printUnitName(unit);
//                    System.out.print(" ");
//                    Output.printTeamName(unit.getTeamName());
//                    System.out.println();
//                    Output.printPlayerUnitStat(unit);
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

    private static int[] getCoordinates(String coordinate) {
        int[] coords = new int[2];

        coords[0] = 7 - Character.getNumericValue(currentSquare.charAt(1));
        coords[1] = Character.getNumericValue(currentSquare.toUpperCase().charAt(0)) - 10;

        return coords;
    }


}

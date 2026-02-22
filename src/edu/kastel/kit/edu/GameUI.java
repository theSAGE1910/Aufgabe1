package edu.kastel.kit.edu;

import java.util.Scanner;

public class GameUI {
    private static Scanner scanner = null;

    private static final String HELPING_TEXT = "Use one of the following commands: select, board, move, flip, block, hand, place, show, yield, state, quit.";

    public static String getInput() {
        String input = "";
        System.out.println(HELPING_TEXT);
        try {
            scanner = new Scanner(System.in);
            while (Commands.isRunning) {
                System.out.print("> ");
                input = scanner.nextLine();
                Commands.processCommands(input);
            }
        } finally {
            scanner.close();
        }
        return input;
    }



}

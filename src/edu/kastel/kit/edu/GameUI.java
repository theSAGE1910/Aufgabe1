package edu.kastel.kit.edu;

import java.util.Scanner;

public class GameUI {
    private static Scanner scanner = null;

    private static final String HELPING_TEXT = "Use one of the following commands: select, board, move, flip, block, hand, place, show, yield, state, quit.";

    public static void processInput() {
        while (Commands.isRunning) {
            System.out.println(HELPING_TEXT);
            Commands.processCommands(getInput());
        }
    }

    private static String getInput() {
        String input;
        try {
            scanner = new Scanner(System.in);
            System.out.print("> ");
            input = scanner.nextLine();
        } finally {
            scanner.close();
        }
        return input;
    }



}

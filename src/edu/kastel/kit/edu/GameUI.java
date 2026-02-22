package edu.kastel.kit.edu;

import java.util.Scanner;

public class GameUI {
    private static Scanner scanner = null;

    private static final String HELPING_TEXT = "Use one of the following commands: select, board, move, flip, block, hand, place, show, yield, state, quit.";

    public static String getInput() {
        String input;
        try {
            System.out.println(HELPING_TEXT);
            scanner = new Scanner(System.in);
            while (Commands.isRunning) {
                System.out.print("> ");
                input = scanner.nextLine();
            }
        } finally {
            scanner.close();
            Commands.processCommands(scanner.nextLine());
        }
        return input;
    }



}

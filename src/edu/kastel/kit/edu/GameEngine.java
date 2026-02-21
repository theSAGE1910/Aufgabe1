package edu.kastel.kit.edu;

public class GameEngine {

    public static void run(String[] args) {
        GameData.extractArgumentInfo(args);
        Initialiser.initialise();
        GameUI.processInput();
    }
}

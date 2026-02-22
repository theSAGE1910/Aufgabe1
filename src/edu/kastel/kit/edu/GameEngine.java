package edu.kastel.kit.edu;

public class GameEngine {

    public static Team team1;
    public static Team team2;
    public static Team activeTeam;

    public static void run(String[] args) {
        if (GameData.extractArgumentInfo(args)) {
            //System.out.println(GameData.boardData);
            Initialiser.initialise();
            GameUI.getInput();
        }
    }

    public static void switchTurn() {
        if (activeTeam.equals(team1)) {
            activeTeam = team2;
        } else {
            activeTeam = team1;
        }
    }
}

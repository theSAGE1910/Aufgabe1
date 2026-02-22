package edu.kastel.kit.edu;

public class Output {
    public static void printNoBlock(String name) {
        System.out.println(name + " no longer blocks.");
    }

    public static void printMovement(String name, String field) {
        System.out.println(name + " moves to " + field);
    }

    public static void printAtkMove(String mover, int atkMov, int defMov, String target, int atkTar, int defTar, String field) {
        System.out.println(mover + " (" + atkMov + "/" + defMov + ") attacks " + target + "(" + atkTar + "/" + defTar + ") on " + field + "!");
    }

    public static void printFlip(String name, int atk, int def, String field) {
        System.out.println(name + " (" + atk + "/" + def + ") was flipped on " + field + "!");
    }

    public static void printElimination(String name) {
        System.out.println(name + " was eliminated!");
    }

    public static void printDamage(char team, int damage) {
        System.out.println(team + " takes " + damage + "!");
    }

    public static void printMerge(String unit1, String unit2, String field) {
        System.out.println(unit1 + " and " + unit2 + " on " + field + " join forces!");
    }

    public static void printMergeFail(String name) {
        System.out.println("Union failed. " + name + " was eliminated.");
    }

    public static void printZeroPoints(char team) {
        System.out.println(team + "'s life points dropped to 0!");
    }

    public static void printWin(char team) {
        System.out.println(team + " wins!");
    }

    public static void printPlayerUnitStat(Unit unit) {
        System.out.println("ATK: " + unit.atk);
        System.out.println("DEF: " + unit.def);
    }

    public static void printPlayerTurn() {
        System.out.println("It is Player's turn!");
    }

    public static void printEnemyTurn() {
        System.out.println("It is Enemy's turn!");
    }

    public static void printUnitName(Unit unit) {
        System.out.print(unit.role + " " + unit.qualifier);
    }

    public static void printTeamName(String name) {
        System.out.print("(" + name + ")");
    }

    public static void printHand(Hand team) {
        int numbering = 1;
        for (Unit unit : team.hand) {
            System.out.print("[" + numbering + "]" + " " + unit.getUnitName() + " ");
            printStat(unit.atk, unit.def);
        }
    }

    public static void printStat(int atk, int def) {
        System.out.println("(" + atk + "/" + def + ")");
    }

    // Scenario B: Farmer King output
    public static void printFarmerKing(Unit unit) {
        System.out.println(unit.getTeam().name + "'s Farmer King");
    }

    // Scenario C: Hidden enemy unit output
    public static void printHiddenUnit(Unit unit) {
        System.out.println("??? (Team " + unit.getTeam().name + ")");
        System.out.println("ATK: ???");
        System.out.println("DEF: ???");
    }

    // Scenario D: Standard visible unit output
    public static void printVisibleUnit(Unit unit) {
        // Print the combined name and team
        System.out.println(unit.getQualifier() + " " + unit.getRole() + " (Team " + unit.getTeam().name + ")");
        // Print the stats
        System.out.println("ATK: " + unit.getAtk());
        System.out.println("DEF: " + unit.getDef());
    }
}

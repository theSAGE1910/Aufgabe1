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

    public static void printPlayerUnitStat(Unit unit, int atk, int def) {
        System.out.println("ATK: " + atk);
        System.out.println("DEF: " + def);
    }

    public static void printPlayerTurn() {
        System.out.println("It is Player's turn!");
    }

    public static void printEnemyTurn() {
        System.out.println("It is Enemy's turn!");
    }

}

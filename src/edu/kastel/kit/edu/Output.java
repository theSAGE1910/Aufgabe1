package edu.kastel.kit.edu;

public class Output {
    public static void printNoBlock(String name) {
        System.out.println(name + " no longer blocks.");
    }

    public static void printBlock(String name, String field) {
        System.out.println(name + " (" + field + ") blocks!");
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

    public static void printDamage(String team, int damage) {
        System.out.println(team + " takes " + damage + "!");
    }

    public static void printMerge(String unit1, String unit2, String field) {
        System.out.println(unit1 + " and " + unit2 + " on " + field + " join forces!");
    }

    public static void printMergeFail(String name) {
        System.out.println("Union failed. " + name + " was eliminated.");
    }

    public static void printZeroPoints(String team) {
        System.out.println(team + "'s life points dropped to 0!");
    }

    public static void printWin(String team) {
        System.out.println(team + " wins!");
    }

    public static void printPlayerUnitStat(Unit unit) {
        System.out.println("ATK: " + unit.getAtk());
        System.out.println("DEF: " + unit.getDef());
    }

    public static void printPlayerTurn() {
        System.out.println("It is Player's turn!");
    }

    public static void printEnemyTurn() {
        System.out.println("It is Enemy's turn!");
    }

    public static void printUnitName(Unit unit) {
        System.out.print(unit.getRole() + " " + unit.getQualifier());
    }

    public static void printTeamName(String name) {
        System.out.print("(" + name + ")");
    }

    public static void printHand(Hand team) {
        int numbering = 1;
        for (Unit unit : team.hand) {
            System.out.print("[" + numbering + "]" + " " + unit.getUnitName() + " ");
            printStat(unit.getAtk(), unit.getDef());
            numbering++;
        }
    }

    public static void printDiscard(String team, Unit unit) {
        System.out.print(team + " discarded " + unit.getUnitName() + " ");
        printStat(unit.getAtk(), unit.getDef());
    }

    public static void printStat(int atk, int def) {
        System.out.println("(" + atk + "/" + def + ").");
    }

    public static void printFarmerKing(Unit unit) {
        System.out.println(unit.getTeam().getName() + "'s Farmer King");
    }

    public static void printHiddenUnit(Unit unit) {
        System.out.println("??? (Team " + unit.getTeam().getName() + ")");
        System.out.println("ATK: ???");
        System.out.println("DEF: ???");
    }

    public static void printVisibleUnit(Unit unit) {
        System.out.println(unit.getQualifier() + " " + unit.getRole() + " (Team " + unit.getTeam().getName() + ")");
        System.out.println("ATK: " + unit.getAtk());
        System.out.println("DEF: " + unit.getDef());
    }

    public static void printState(Team team1, Team team2) {
        System.out.printf(" %-14s%15s%n", team1.getName(), team2.getName());
        System.out.printf(" %-14s%15s%n", team1.getTeamHP() + "/" + Team.INITIAL_HP + " LP", team2.getTeamHP() + "/" + Team.INITIAL_HP + " LP");
        System.out.printf(" %-14s%15s%n", "DC: " + team1.shuffledDeck.size() + "/" + team1.getInitialDeckSize(),
                team2.shuffledDeck.size() + "/" + team2.getInitialDeckSize());
        System.out.printf(" %-14s%15s%n", "BC: " + getBoardCount(team1) + "/5", "BC:" + getBoardCount(team2) + "/5");
    }

    private static int getBoardCount(Team team) {
        int count = 0;
        for (int row = 0; row < GameBoard.DIMENSION; row++) {
            for (int col = 0; col < GameBoard.DIMENSION; col++) {
                Unit boardUnit = GameBoard.getUnitAt(row, col);
                if (boardUnit != null && boardUnit.getTeam().equals(team) && !boardUnit.getRole().equals("King")) {
                    count++;
                }
            }
        }
        return count;
    }
}

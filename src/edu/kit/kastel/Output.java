package edu.kit.kastel;

/**
 * Utility class handling all terminal output messages for the game.
 * It ensures consistent phrasing, formatting, and spacing for all game events,
 * including combat, movement, state tracking, and error messaging.
 * @author uxuwg
 * @version 0.9
 */
public final class Output {

    private static final String MOVEMENT_FORMAT = "%s moves to %s.";
    private static final String DAMAGE_FORMAT = "%s takes %d damage!";
    private static final String ELIMINATION_FORMAT = "%s was eliminated!";
    private static final String BLOCK_FORMAT = "%s (%s) blocks!";
    private static final String TURN_FORMAT = "It is %s's turn!";
    private static final String NO_LONGER_BLOCKS_FORMAT = "%s no longer blocks.";

    private static final int MAX_STATE_LINE_LENGTH = 31;

    private Output() {
    }

    /**
     * Prints the message for when a unit stops blocking.
     * @param name the name of the unit
     */
    public static void printNoBlock(String name) {
        System.out.printf(NO_LONGER_BLOCKS_FORMAT, name);
    }

    /**
     * Prints the message for when a unit assumes a blocking stance.
     * @param name the name of the unit
     * @param field the coordinate square the unit is on
     */
    public static void printBlock(String name, String field) {
        System.out.printf(BLOCK_FORMAT, name, field);
    }

    /**
     * Prints the message for when a unit moves to a new square.
     * @param name the name of the moving unit
     * @param field the destination coordinate square
     */
    public static void printMovement(String name, String field) {
        System.out.printf(MOVEMENT_FORMAT, name, field);
    }

    /**
     * Prints the attack initiation message.
     * @param mover the name of the attacking unit
     * @param atkMov the attack value of the attacking unit
     * @param defMov the defense value of the attacking unit
     * @param target the formatted display string of the defending unit
     * @param field the coordinate square where combat occurs
     */
    public static void printAtkMove(String mover, int atkMov, int defMov, String target, String field) {
        System.out.println(mover + " (" + atkMov + "/" + defMov + ") attacks "
                + target + " on " + field + "!");
    }

    /**
     * Prints the message for when a face-down unit is revealed.
     * @param name the name of the flipped unit
     * @param atk the attack value of the unit
     * @param def the defense value of the unit
     * @param field the coordinate square where the flip occurred
     */
    public static void printFlip(String name, int atk, int def, String field) {
        System.out.println(name + " (" + atk + "/" + def + ") was flipped on " + field + "!");
    }

    /**
     * Prints the message for when a unit is removed from the board.
     * @param name the name of the eliminated unit
     */
    public static void printElimination(String name) {
        System.out.printf(ELIMINATION_FORMAT, name);
    }

    /**
     * Prints the damage sustained by a team's life points.
     * @param team the name of the team taking damage
     * @param damage the amount of damage taken
     */
    public static void printDamage(String team, int damage) {
        System.out.printf(DAMAGE_FORMAT, team, damage);
    }

    /**
     * Prints the message for when two friendly units successfully merge.
     * @param unit1 the moving unit
     * @param unit2 the stationary unit being merged into
     * @param field the coordinate square of the merge
     */
    public static void printMerge(String unit1, String unit2, String field) {
        System.out.println(unit1 + " and " + unit2 + " on " + field + " join forces!");
    }

    /**
     * Prints the message for when a unit merge fails due to incompatible properties.
     * @param name the name of the stationary unit that is eliminated
     */
    public static void printMergeFail(String name) {
        System.out.println("Union failed. " + name + " was eliminated.");
    }

    /**
     * Prints the critical message indicating a team's life points have been depleted.
     * @param team the name of the team whose points reached zero
     */
    public static void printZeroPoints(String team) {
        System.out.println(team + "'s life points dropped to 0!");
    }

    /**
     * Prints the final victory message.
     * @param team the name of the winning team
     */
    public static void printWin(String team) {
        System.out.println(team + " wins!");
    }

    /**
     * Prints the message indicating the start of a team's turn.
     * @param team the name of the team whose turn is starting
     */
    public static void printTurn(String team) {
        System.out.printf(TURN_FORMAT, team);
    }

    /**
     * Prints the numbered list of all units currently held in a team's hand.
     * @param teamHand the hand object containing the list of units
     */
    public static void printHand(Hand teamHand) {
        int numbering = 1;
        for (Unit unit : teamHand.getHand()) {
            System.out.print("[" + numbering + "]" + " " + unit.getUnitName() + " ");
            printStat(unit.getAtk(), unit.getDef());
            System.out.println();
            numbering++;
        }
    }

    /**
     * Prints the message for when a unit is discarded from a hand.
     * @param team the name of the team discarding the card
     * @param unit the unit being discarded
     */
    public static void printDiscard(String team, Unit unit) {
        System.out.print(team + " discarded " + unit.getUnitName() + " ");
        printStat(unit.getAtk(), unit.getDef());
        System.out.println(".");
    }

    /**
     * Prints the message for placing a unit onto the game board from a hand.
     * @param team the name of the team placing the unit
     * @param unit the unit being placed
     * @param field the target coordinate square
     */
    public static void printPlacement(String team, Unit unit, String field) {
        System.out.println(team + " places " + unit.getUnitName() + " on " + field.toUpperCase() + ".");
    }

    /**
     * Prints the identifier for the Farmer King unit.
     * @param unit the Farmer King unit object
     */
    public static void printFarmerKing(Unit unit) {
        System.out.println(unit.getTeam().getName() + "'s Farmer King");
    }

    /**
     * Prints the concealed information format for a face-down enemy unit.
     * @param unit the face-down unit object
     */
    public static void printHiddenUnit(Unit unit) {
        System.out.println("??? (Team " + unit.getTeam().getName() + ")");
        System.out.println("ATK: ???");
        System.out.println("DEF: ???");
    }

    /**
     * Prints the full identification and stats of a face-up unit on the board.
     * @param unit the face-up unit object
     */
    public static void printVisibleUnit(Unit unit) {
        System.out.println(unit.getQualifier() + " " + unit.getRole() + " (Team " + unit.getTeam().getName() + ")");
        printPlayerUnitStat(unit);
    }

    /**
     * Prints the rigidly formatted global state of the game (Life Points, Deck Count, Board Count).
     * @param team1 the first team object
     * @param team2 the second team object
     */
    public static void printState(Team team1, Team team2) {
        printStateLine(team1.getName(), team2.getName());
        printStateLine(team1.getTeamHP() + "/" + Team.INITIAL_HP + " LP",
                team2.getTeamHP() + "/" + Team.INITIAL_HP + " LP");
        printStateLine("DC: " + team1.getShuffledDeck().size() + "/" + team1.getInitialDeckSize(),
                "DC: " + team2.getShuffledDeck().size() + "/" + team2.getInitialDeckSize());
        printStateLine("BC: " + getBoardCount(team1) + "/5",
                "BC: " + getBoardCount(team2) + "/5");
    }

    /**
     * Counts the total number of standard units (excluding the Farmer King) a team has on the board.
     * @param team the team whose units are being counted
     * @return the number of units the team currently has on the board
     */
    public static int getBoardCount(Team team) {
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

    private static void printPlayerUnitStat(Unit unit) {
        System.out.println("ATK: " + unit.getAtk());
        System.out.println("DEF: " + unit.getDef());
    }

    private static void printStat(int atk, int def) {
        System.out.print("(" + atk + "/" + def + ")");
    }

    private static void printStateLine(String left, String right) {
        String base = "  " + left; // Exactly 2 spaces indentation
        int spacesNeeded = MAX_STATE_LINE_LENGTH - base.length() - right.length();
        System.out.println(base + " ".repeat(Math.max(0, spacesNeeded)) + right);
    }
}

package edu.kit.kastel;

/**
 * Utility class handling all terminal output messages for the game.
 * It ensures consistent phrasing, formatting, and spacing for all game events,
 * including combat, movement, state tracking, and error messaging.
 * @author uxuwg
 * @version 0.9
 */
public final class Output {

    private static final String MOVEMENT_FORMAT = "%s moves to %s.%n";
    private static final String DAMAGE_FORMAT = "%s takes %d damage!%n";
    private static final String ELIMINATION_FORMAT = "%s was eliminated!%n";
    private static final String BLOCK_FORMAT = "%s (%s) blocks!%n";
    private static final String TURN_FORMAT = "It is %s's turn!%n";
    private static final String NO_LONGER_BLOCKS_FORMAT = "%s no longer blocks.%n";
    private static final String ATK_MOVE_FORMAT = "%s (%d/%d) attacks %s on %s!%n";
    private static final String FLIP_FORMAT = "%s (%d/%d) was flipped on %s!%n";
    private static final String MERGE_FORMAT = "%s and %s on %s join forces!%n";
    private static final String MERGE_FAIL_FORMAT = "Union failed. %s was eliminated.%n";
    private static final String ZERO_POINTS_FORMAT = "%s's life points dropped to 0!%n";
    private static final String WIN_FORMAT = "%s wins!%n";
    private static final String HAND_FORMAT = "[%d] %s (%d/%d)%n";
    private static final String DISCARD_FORMAT = "%s discarded %s (%d/%d).%n";
    private static final String PLACEMENT_FORMAT = "%s places %s on %s.%n";
    private static final String FARMER_KING_FORMAT = "%s's Farmer King%n";
    private static final String HIDDEN_UNIT_FORMAT = "??? (Team %s)%nATK: ???%nDEF: ???%n";
    private static final String VISIBLE_UNIT_FORMAT = "%s %s (Team %s)%nATK: %d%nDEF: %d%n";

    private static final String LP_FORMAT = "%d/%d LP";
    private static final String DC_FORMAT = "DC: %d/%d";
    private static final String BC_FORMAT = "BC: %d/%d";

    private static final int MAX_STATE_LINE_LENGTH = 31;
    private static final int MAX_BOARD_UNITS = 5;

    private static final int START_INDEX = 0;
    private static final int START_NUMBERING = 1;
    private static final String SPACE = " ";
    private static final String INDENT = "  "; // Add this line!

    private Output() {
    }

    /**
     * Prints the message for when a unit starts blocking or stops blocking.
     * @param name the name of the unit that is blocking or no longer blocking
     * @param field the coordinate square where the block or unblock action is occurring
     * @param isBlocking true if the unit is blocking, false if the unit is no longer blocking
     */
    public static void printBlockStatus(String name, String field, boolean isBlocking) {
        if (isBlocking) {
            System.out.printf(BLOCK_FORMAT, name, field);
        } else {
            System.out.printf(NO_LONGER_BLOCKS_FORMAT, name);
        }
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
        System.out.printf(ATK_MOVE_FORMAT, mover, atkMov, defMov, target, field);
    }

    /**
     * Prints the message for when a face-down unit is revealed.
     * @param name the name of the flipped unit
     * @param atk the attack value of the unit
     * @param def the defense value of the unit
     * @param field the coordinate square where the flip occurred
     */
    public static void printFlip(String name, int atk, int def, String field) {
        System.out.printf(FLIP_FORMAT, name, atk, def, field);
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
        System.out.printf(MERGE_FORMAT, unit1, unit2, field);
    }

    /**
     * Prints the message for when a unit merge fails due to incompatible properties.
     * @param name the name of the stationary unit that is eliminated
     */
    public static void printMergeFail(String name) {
        System.out.printf(MERGE_FAIL_FORMAT, name);
    }

    /**
     * Prints the message for when a team's life points drop to zero, indicating their defeat and the opponent's victory.
     * @param loser the name of the team whose life points have dropped to zero
     * @param winner the name of the team that wins as a result of the opponent's life points dropping to zero
     */
    public static void printGameOver(String loser, String winner) {
        System.out.printf(ZERO_POINTS_FORMAT, loser);
        System.out.printf(WIN_FORMAT, winner);
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
        int numbering = START_NUMBERING;
        for (Unit unit : teamHand.getHand()) {
            System.out.printf(HAND_FORMAT, numbering, unit.getUnitName(), unit.getAtk(), unit.getDef());
            numbering++;
        }
    }

    /**
     * Prints the message for when a unit is discarded from a hand.
     * @param team the name of the team discarding the card
     * @param unit the unit being discarded
     */
    public static void printDiscard(String team, Unit unit) {
        System.out.printf(DISCARD_FORMAT, team, unit.getUnitName(), unit.getAtk(), unit.getDef());
    }

    /**
     * Prints the message for placing a unit onto the game board from a hand.
     * @param team the name of the team placing the unit
     * @param unit the unit being placed
     * @param field the target coordinate square
     */
    public static void printPlacement(String team, Unit unit, String field) {
        System.out.printf(PLACEMENT_FORMAT, team, unit.getUnitName(), field.toUpperCase());
    }

    /**
     * Prints the details of a unit on the board, based on whether the unit is a Farmer King, face-down, or face-up.
     * @param unit the unit whose details are being printed
     * @param activeTeam the team currently taking their turn, used to determine whether to reveal details of face-down units
     */
    public static void printUnitDetails(Unit unit, Team activeTeam) {
        if (unit.getRole().equals(GameMessages.KING)) {
            System.out.printf(FARMER_KING_FORMAT, unit.getTeam().getName());
        } else if (!unit.isFaceUp() && !unit.getTeam().equals(activeTeam)) {
            System.out.printf(HIDDEN_UNIT_FORMAT, unit.getTeam().getName());
        } else {
            System.out.printf(VISIBLE_UNIT_FORMAT,
                    unit.getQualifier(), unit.getRole(),
                    unit.getTeam().getName(),
                    unit.getAtk(), unit.getDef());
        }
    }

    /**
     * Prints the rigidly formatted global state of the game (Life Points, Deck Count, Board Count).
     * @param team1 the first team object
     * @param team2 the second team object
     */
    public static void printState(Team team1, Team team2) {
        String[] lefts = {
                team1.getName(),
                String.format(LP_FORMAT, team1.getTeamHP(), Team.INITIAL_HP),
                String.format(DC_FORMAT, team1.getShuffledDeck().size(), team1.getInitialDeckSize()),
                String.format(BC_FORMAT, GameBoard.getBoardCount(team1), MAX_BOARD_UNITS)
        };
        String[] rights = {
                team2.getName(),
                String.format(LP_FORMAT, team2.getTeamHP(), Team.INITIAL_HP),
                String.format(DC_FORMAT, team2.getShuffledDeck().size(), team2.getInitialDeckSize()),
                String.format(BC_FORMAT, GameBoard.getBoardCount(team2), MAX_BOARD_UNITS)
        };
        for (int i = START_INDEX; i < lefts.length; i++) {
            String base = INDENT + lefts[i];
            int spacesNeeded = MAX_STATE_LINE_LENGTH - base.length() - rights[i].length();
            System.out.print(base + SPACE.repeat(Math.max(START_INDEX, spacesNeeded)) + rights[i]);
            System.out.println();
        }
    }
}
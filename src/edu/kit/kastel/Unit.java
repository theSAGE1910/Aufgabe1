package edu.kit.kastel;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a single playable unit (card or board piece) in the game.
 * Stores the unit's stats, role, and current board state. Contains the logic
 * for parsing units from files and evaluating the complex math required to merge them.
 * @author uxuwg
 * @version 0.9
 */
public class Unit {

    private static final String REGEX = "^([^;]+);([^;]+);([0-9]+);([0-9]+)$";

    private static final String PREFIX_SYMBOL = "*";
    private static final String BLOCK_SYMBOL = "b";
    private static final String SPACE = " ";

    private static final int GROUP_QUALIFIER = 1;
    private static final int GROUP_ROLE = 2;
    private static final int GROUP_ATK = 3;
    private static final int GROUP_DEF = 4;

    private static final int STAT_ARRAY_SIZE = 2;
    private static final int ATK_INDEX = 0;
    private static final int DEF_INDEX = 1;

    private static final int MERGE_THRESHOLD = 100;
    private static final int STAT_DIVISOR = 100;

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;

    private static Unit[] unitArray = null;

    private final String qualifier;
    private final String role;
    private final int atk;
    private final int def;

    private Team team;
    private boolean faceUp;
    private boolean movedThisTurn = false;
    private boolean blocking = false;

    /**
     * Constructs a basic unit without an assigned team (used during initial file parsing).
     * @param qualifier the prefix name of the unit
     * @param role the suffix name of the unit
     * @param atk the base attack value
     * @param def the base defense value
     */
    public Unit(String qualifier, String role, int atk, int def) {
        this.qualifier = qualifier;
        this.role = role;
        this.atk = atk;
        this.def = def;
    }

    /**
     * Constructs a fully qualified unit assigned to a specific team.
     * @param qualifier the prefix name of the unit (e.g., "Farmer")
     * @param role the suffix name of the unit (e.g., "King")
     * @param atk the base attack value
     * @param def the base defense value
     * @param team the team this unit belongs to
     */
    public Unit(String qualifier, String role, int atk, int def, Team team) {
        this.qualifier = qualifier;
        this.role = role;
        this.atk = atk;
        this.def = def;
        this.team = team;
    }

    /**
     * Retrieves the list of available unit templates that were parsed from the configuration file.
     * @return the list of available unit templates
     */
    public static List<Unit> getUnitList() {
        if (unitArray == null) {
            return new ArrayList<>();
        }
        // Converts the array back into a List for your other classes to use!
        return new ArrayList<>(Arrays.asList(unitArray));
    }

    /**
     * Parses the raw string data from the units file.
     * @param unitData the list of raw string lines from the configuration file
     */
    public static void extractUnits(List<String> unitData) {
        List<Unit> tempUnits = new ArrayList<>();
        Pattern pattern = Pattern.compile(REGEX);
        for (String unit : unitData) {
            Matcher matcher = pattern.matcher(unit);
            if (matcher.find()) {
                tempUnits.add(new Unit(matcher.group(GROUP_QUALIFIER),
                        matcher.group(GROUP_ROLE),
                        Integer.parseInt(matcher.group(GROUP_ATK)),
                        Integer.parseInt(matcher.group(GROUP_DEF)), null));
            }
        }
        unitArray = tempUnits.toArray(new Unit[0]);
    }

    /**
     * Attempts to merge two friendly units based on their stats and compatibility rules.
     * @param moverUnit the unit moving into the target square
     * @param targetUnit the stationary unit currently on the target square
     * @return a newly merged Unit object, or null if the units are incompatible
     */
    public Unit mergeUnits(Unit moverUnit, Unit targetUnit) {
        int[] mergedAtkDef = compatibilityCheck(moverUnit, targetUnit);

        if (mergedAtkDef == null) {
            return null;
        }

        int newAtk = mergedAtkDef[ATK_INDEX];
        int newDef = mergedAtkDef[DEF_INDEX];

        String newQualifier = targetUnit.getQualifier() + SPACE + moverUnit.getQualifier();
        String newRole = targetUnit.getRole();

        Unit mergedUnit = new Unit(newQualifier, newRole, newAtk, newDef, moverUnit.getTeam());

        mergedUnit.setFaceUp(moverUnit.faceUp && targetUnit.faceUp);

        return mergedUnit;
    }

    private int[] compatibilityCheck(Unit moverUnit, Unit targetUnit) {
        int[] mergedAtkDef = new int[STAT_ARRAY_SIZE];

        if (moverUnit.getUnitName().equals(targetUnit.getUnitName())) {
            return null;
        }

        if (checkSymbioticCondition(moverUnit, targetUnit)) {
            mergedAtkDef[ATK_INDEX] = moverUnit.atk;
            mergedAtkDef[DEF_INDEX] = targetUnit.def;
        } else {
            int g3t = calculateG3t(moverUnit, targetUnit);
            if (g3t > MERGE_THRESHOLD) {
                mergedAtkDef[ATK_INDEX] = moverUnit.atk + targetUnit.atk - g3t;
                mergedAtkDef[DEF_INDEX] = moverUnit.def + targetUnit.def - g3t;
            } else if (g3t == MERGE_THRESHOLD && checkPrimeCondition(moverUnit, targetUnit)) {
                mergedAtkDef[ATK_INDEX] = moverUnit.atk + targetUnit.atk;
                mergedAtkDef[DEF_INDEX] = moverUnit.def + targetUnit.def;
            }  else {
                return null;
            }
        }
        return mergedAtkDef;
    }

    private boolean checkSymbioticCondition(Unit moverUnit, Unit targetUnit) {
        return moverUnit.atk > targetUnit.atk
                && moverUnit.atk == targetUnit.def
                && targetUnit.atk == moverUnit.def;
    }

    private int calculateG3t(Unit moverUnit, Unit targetUnit) {
        int gcdAtk = calculateGcd(moverUnit.atk, targetUnit.atk);
        int gcdDef = calculateGcd(moverUnit.def, targetUnit.def);

        return Math.max(gcdAtk, gcdDef);
    }

    private int calculateGcd(int moverStat, int targetStat) {
        int int1 = moverStat;
        int int2 = targetStat;
        while (int2 != ZERO) {
            int remainder = int2;
            int2 = int1 % int2;
            int1 = remainder;
        }
        return Math.abs(int1);
    }

    private boolean checkPrimeCondition(Unit moverUnit, Unit targetUnit) {
        int moverAtk = moverUnit.atk / STAT_DIVISOR;
        int targetAtk = targetUnit.atk / STAT_DIVISOR;
        int moverDef = moverUnit.def / STAT_DIVISOR;
        int targetDef = targetUnit.def / STAT_DIVISOR;

        boolean atkPrime = isPrime(moverAtk) && isPrime(targetAtk);
        boolean defPrime = isPrime(moverDef) && isPrime(targetDef);

        return atkPrime || defPrime;
    }

    private boolean isPrime(int stat) {
        if (stat == ONE) {
            return false;
        } else {
            for (int i = TWO; i < stat / TWO; i++) {
                if (stat % i == ZERO) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public String toString() {
        boolean isActiveTeam = this.getTeam() != null && this.getTeam().equals(GameEngine.getActiveTeam());
        String prefix = (isActiveTeam && !this.hasMovedThisTurn()) ? PREFIX_SYMBOL : SPACE;

        char symbol;
        if (this.getTeam() != null && this.getTeam().equals(GameEngine.getTeam1())) {
            symbol = this.getRole().equals(GameMessages.KING) ? GameMessages.PLAYER_KING_SYMBOL : GameMessages.PLAYER_UNIT_SYMBOL;
        } else {
            symbol = this.getRole().equals(GameMessages.KING) ? GameMessages.ENEMY_KING_SYMBOL : GameMessages.ENEMY_UNIT_SYMBOL;
        }

        String suffix = this.isBlocking() ? BLOCK_SYMBOL : SPACE;

        return prefix + symbol + suffix;
    }

    /**
     * Gets the full combined name of the unit, including both qualifier and role.
     * @return the full combined name of the unit
     */
    public String getUnitName() {
        return this.qualifier + SPACE + this.role;
    }

    /**
     * Gets the prefix name of the unit, which typically indicates its base type or class.
     * @return the prefix name of the unit
     */
    public String getQualifier() {
        return qualifier;
    }

    /**
     * Gets the suffix name of the unit, which typically indicates its specific role.
     * @return the suffix name of the unit
     */
    public String getRole() {
        return role;
    }

    /**
     * Gets the current attack value of the unit.
     * @return the current attack value
     */
    public int getAtk() {
        return atk;
    }

    /**
     * Gets the current defense value of the unit.
     * @return the current defense value
     */
    public int getDef() {
        return def;
    }

    /**
     * Sets the team affiliation of the unit, which determines its ownership and interactions on the board.
     * @param team the team to assign this unit to
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Gets the team affiliation of the unit, which determines its ownership and interactions on the board.
     * @return the team this unit currently belongs to
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Checks whether the unit's stats are currently visible to the opponent.
     * @return true if the unit's stats are visible to the opponent, false if hidden
     */
    public boolean isFaceUp() {
        return faceUp;
    }

    /**
     * Sets the visibility state of the unit, determining whether its stats are visible to the opponent.
     * @param faceUp the new visibility state of the unit
     */
    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    /**
     * Checks whether the unit has already performed its action for the current turn.
     * @return true if the unit has already acted this turn, false otherwise
     */
    public boolean hasMovedThisTurn() {
        return movedThisTurn;
    }

    /**
     * Sets the movement state of the unit.
     * @param movedThisTurn the new movement state flag for the unit
     */
    public void setMovedThisTurn(boolean movedThisTurn) {
        this.movedThisTurn = movedThisTurn;
    }

    /**
     * Checks whether the unit is currently in a defensive blocking stance.
     * @return true if the unit is currently in a defensive blocking stance
     */
    public boolean isBlocking() {
        return blocking;
    }

    /**
     * Sets the blocking state of the unit.
     * @param isBlocking the new blocking stance state for the unit
     */
    public void setBlocking(boolean isBlocking) {
        this.blocking = isBlocking;
    }

    /**
     * Checks if a specified unit is the Farmer King.
     * @param unitToShow the unit to evaluate
     * @return true if the unit is the Farmer King, false otherwise
     */
    public static boolean isKing(Unit unitToShow) {
        return unitToShow.getQualifier().equals(GameMessages.FARMER) && unitToShow.getRole().equals(GameMessages.KING);
    }
}

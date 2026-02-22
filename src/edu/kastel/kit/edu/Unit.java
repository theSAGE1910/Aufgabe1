package edu.kastel.kit.edu;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Unit {

    private static final String REGEX = "^([^;]+);([^;]+);([0-9]+);([0-9]+)$";
    private static final String ERROR_IO_EXCEPTION = "ERROR: Something went wrong in IO!";
    private static final String ERROR_INCOMPATIBLE = "ERROR: Incompatible to merge!";

    private final String qualifier;
    private final String role;
    private final int atk;
    private final int def;
    private final int weight;
    public int row;
    public int column;
    private Team team;
    private boolean isFaceUp;
    private boolean hasMovedThisTurn = false;
    private boolean isBlocking = false;

    public static List<Unit> unitList = new ArrayList<>();

    public Unit(String qualifier, String role, int atk, int def) {
        this.qualifier = qualifier;
        this.role = role;
        this.atk = atk;
        this.def = def;
        this.weight = atk + def;
    }

    public Unit(String qualifier, String role, int atk, int def, Team team) {
        this.qualifier = qualifier;
        this.role = role;
        this.atk = atk;
        this.def = def;
        this.weight = atk + def;
        this.team = team;
    }

    public void displayUnit(Unit unit) {
        System.out.print(unit.qualifier + " " + unit.role);
    }

    public static List<Unit> getUnitList() {
        return unitList;
    }

    public static List<Unit> extractUnits(List<String> unitData) {

        Pattern pattern = Pattern.compile(REGEX);
        for (String unit : unitData) {
            Matcher matcher = pattern.matcher(unit);
            if (matcher.find()) {
                unitList.add(new Unit(matcher.group(1),
                        matcher.group(2),
                        Integer.parseInt(matcher.group(3)),
                        Integer.parseInt(matcher.group(4)), null));
            }
        }
        return unitList;
    }

    public Unit mergeUnits(Unit moverUnit, Unit targetUnit) {
        int[] mergedAtkDef = compatibilityCheck(moverUnit, targetUnit);

        if (mergedAtkDef == null) {
            return null;
        }

        int newAtk = mergedAtkDef[0];
        int newDef = mergedAtkDef[1];

        String newQualifier = targetUnit.getQualifier() + " " + moverUnit.getQualifier();
        String newRole = targetUnit.getRole() + " " + moverUnit.getRole();

        Unit mergedUnit = new Unit(newQualifier, newRole, newAtk, newDef, moverUnit.getTeam());

        mergedUnit.setFaceUp(moverUnit.isFaceUp && targetUnit.isFaceUp);

        return mergedUnit;
    }

    private int[] compatibilityCheck(Unit moverUnit, Unit targetUnit) {
        int[] mergedAtkDef = new int[2];

        if (moverUnit.getUnitName().equals(targetUnit.getUnitName())) {
            System.err.println(ERROR_INCOMPATIBLE);
            return null;
        }

        if (checkSymbioticCondition(moverUnit, targetUnit)) {
            mergedAtkDef[0] = moverUnit.atk;
            mergedAtkDef[1] = targetUnit.def;
        } else {
            int g3t = calculateG3t(moverUnit, targetUnit);
            if (g3t > 100) {
                mergedAtkDef[0] = moverUnit.atk + targetUnit.atk - g3t;
                mergedAtkDef[1] = moverUnit.def + targetUnit.def - g3t;
            } else if (g3t == 100 && checkPrimeCondition(moverUnit, targetUnit)) {
                mergedAtkDef[0] = moverUnit.atk + targetUnit.atk;
                mergedAtkDef[1] = moverUnit.def + targetUnit.def;
            }  else {
                System.err.println(ERROR_INCOMPATIBLE);
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
        BigInteger moverAtk = BigInteger.valueOf(moverUnit.atk);
        BigInteger targetAtk = BigInteger.valueOf(targetUnit.atk);
        BigInteger moverDef = BigInteger.valueOf(moverUnit.def);
        BigInteger targetDef = BigInteger.valueOf(targetUnit.def);

        BigInteger gcdAtk = moverAtk.gcd(targetAtk);
        BigInteger gcdDef = moverDef.gcd(targetDef);

        BigInteger g3t = gcdAtk.max(gcdDef);

        return g3t.intValue();
    }

    private boolean checkPrimeCondition(Unit moverUnit, Unit targetUnit) {
        int moverAtk = moverUnit.atk / 100;
        int targetAtk = targetUnit.atk / 100;
        int moverDef = moverUnit.def / 100;
        int targetDef = targetUnit.def / 100;

        boolean atkPrime = isPrime(moverAtk) && isPrime(targetAtk);
        boolean defPrime = isPrime(moverDef) && isPrime(targetDef);

        return atkPrime || defPrime;
    }

    private boolean isPrime(int stat) {
        if (stat == 1) {
            return false;
        } else {
            for (int i = 2; i < stat / 2; i++) {
                if (stat % i == 0) {
                    return false;
                }
            }
            return true;
        }
    }

    public void assignCoordinate(Unit unit, int row, int column) {
        unit.row = row;
        unit.column = column;
    }

    @Override
    public String toString() {
        if (this.role.equals("King")) {
            if (this.team.getName().equals(GameData.team1Name)) {
                return GameData.playerKingSymbol;
            } else {
                return GameData.enemyKingSymbol;
            }
        }

        if (this.isFaceUp) {
            if (this.team.getName().equals(GameData.team1Name)) {
                return GameData.playerUnitSymbol;
            } else {
                return GameData.enemyUnitSymbol;
            }
        }

        return " ? ";
    }

    public String getUnitName() {
        return this.qualifier + " " + this.role;
    }

    public String getQualifier() {
        return qualifier;
    }

    public String getRole() {
        return role;
    }

    public int getAtk() {
        return atk;
    }

    public int getDef() {
        return def;
    }

    public Team getTeam() {
        return team;
    }

    public boolean isFaceUp() {
        return isFaceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.isFaceUp = faceUp;
    }

    public boolean hasMovedThisTurn() {
        return hasMovedThisTurn;
    }

    public void setHasMovedThisTurn(boolean hasMovedThisTurn) {
        this.hasMovedThisTurn = hasMovedThisTurn;
    }

    public boolean isBlocking() {
        return isBlocking;
    }

    public void setBlocking(boolean isBlocking) {
        this.isBlocking = isBlocking;
    }

}

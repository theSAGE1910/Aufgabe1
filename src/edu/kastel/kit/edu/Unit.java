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

    public static List<Unit> unitList;

    public Unit(String qualifier, String role, int atk, int def) {
        this.qualifier = qualifier;
        this.role = role;
        this.atk = atk;
        this.def = def;
        this.weight = atk + def;
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
//                String[] infoParts = line.split(REGEX);
//                if (infoParts.length == 4) {
//                    unitList.add(new Unit(infoParts[0], infoParts[1], Integer.parseInt(infoParts[2]), Integer.parseInt(infoParts[3])));
//                }
            if (matcher.find()) {
                unitList.add(new Unit(matcher.group(1),
                        matcher.group(2),
                        Integer.parseInt(matcher.group(3)),
                        Integer.parseInt(matcher.group(4))));
            }
        }
        return unitList;
    }

    public Unit mergeUnits(Unit moverUnit, Unit targetUnit) {
        int[] mergedAtkDef = compatibilityCheck(moverUnit, targetUnit);
        int newAtk = mergedAtkDef[0];
        int newDef = mergedAtkDef[1];
        return new Unit(targetUnit.qualifier + moverUnit.qualifier,
                moverUnit.role + targetUnit.role, newAtk, newDef);
    }

    private int[] compatibilityCheck(Unit moverUnit, Unit targetUnit) {
        int[] mergedAtkDef = new int[2];
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
}

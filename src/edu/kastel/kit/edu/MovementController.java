package edu.kastel.kit.edu;


public class MovementController {

    public static void handleMove(String argument) {
        if (!isArgumentValid(argument)) {
            return;
        }

        Unit movingUnit = Commands.getValidatedActiveUnit();
        if (movingUnit == null) {
            return;
        }

        int targetRow = Commands.getCoordinates(argument)[0];
        int targetCol = Commands.getCoordinates(argument)[1];

        if (!isDistanceValid(targetRow, targetCol)) {
            return;
        }

        Unit targetUnit = GameBoard.getUnitAt(targetRow, targetCol);

        if (!isValidKingInteraction(movingUnit, targetUnit)) {
            return;
        }

        executeMove(argument, targetUnit, targetRow, targetCol, movingUnit);
    }

    private static boolean isValidKingInteraction(Unit movingUnit, Unit targetUnit) {
        if (targetUnit == null) {
            return true;
        }

        boolean isMoverKing = Commands.isKing(movingUnit);
        boolean isTargetKing = Commands.isKing(targetUnit);
        boolean isSameTeam = movingUnit.getTeam().equals(targetUnit.getTeam());

        if (isMoverKing && !isSameTeam) {
            System.err.println("ERROR: Farmer King cannot move onto an enemy unit.");
            return false;
        }

        if (!isMoverKing && isTargetKing && isSameTeam) {
            System.err.println("ERROR: Unit cannot move onto its own Farmer King.");
            return false;
        }

        return true;
    }

    private static boolean isArgumentValid(String argument) {
        if (argument == null || argument.length() != 2) {
            System.err.println("ERROR: Invalid target square.");
            return false;
        }
        return true;
    }

    private static boolean isDistanceValid(int targetRow, int targetCol) {
        int rowDiff = Math.abs(targetRow - Commands.selectedRow);
        int colDiff = Math.abs(targetCol - Commands.selectedColumn);

        if (rowDiff + colDiff > 1) {
            System.err.println("ERROR: Move must be exactly 1 square orthogonally or en place.");
            return false;
        }
        return true;
    }

    private static void executeMove(String argument, Unit targetUnit, int targetRow, int targetCol, Unit movingUnit) {
        if (movingUnit == targetUnit) {
            movingUnit.setHasMovedThisTurn(true);
            Output.printMovement(movingUnit.getUnitName(), argument);
            Commands.updateDisplay();
        } else if (targetUnit == null) {
            moveToEmptySquare(argument, targetRow, targetCol, movingUnit);
        } else if (!movingUnit.getTeam().equals(targetUnit.getTeam())) {
            initiateCombat(argument, movingUnit, targetUnit, targetRow, targetCol);
        } else {
            mergeFriendlyUnits(argument, movingUnit, targetUnit, targetRow, targetCol);
        }
    }

    private static void moveToEmptySquare(String argument, int targetRow, int targetCol, Unit movingUnit) {
        GameBoard.setUnitAt(Commands.selectedRow, Commands.selectedColumn, null);
        GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
        movingUnit.setHasMovedThisTurn(true);
        Output.printMovement(movingUnit.getUnitName(), argument);

        Commands.selectedSquare = argument;
        Commands.selectedRow = targetRow;
        Commands.selectedColumn = targetCol;
        Commands.updateDisplay();
    }

    private static void initiateCombat(String argument, Unit movingUnit, Unit targetUnit, int targetRow, int targetCol) {
        if (movingUnit.isBlocking()) {
            movingUnit.setBlocking(false);
            Output.printNoBlock(movingUnit.getUnitName());
        }

        Output.printAtkMove(movingUnit.getUnitName(), movingUnit.getAtk(), movingUnit.getDef(),
                targetUnit.getUnitName(), targetUnit.getAtk(), targetUnit.getDef(), argument);

        revealCombatUnits(argument, movingUnit, targetUnit);

        boolean attackerMovesToTargetSquare = resolveCombat(movingUnit, targetUnit, targetRow, targetCol);

        if (attackerMovesToTargetSquare) {
            GameBoard.setUnitAt(Commands.selectedRow, Commands.selectedColumn, null);
            GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
            Output.printMovement(movingUnit.getUnitName(), argument);

            Commands.selectedSquare = argument;
            Commands.selectedRow = targetRow;
            Commands.selectedColumn = targetCol;
        }

        movingUnit.setHasMovedThisTurn(true);
        checkHpStatus();

        if (Commands.isRunning) {
            Commands.updateDisplay();
        }
    }

    private static void mergeFriendlyUnits(String argument, Unit movingUnit, Unit targetUnit, int targetRow, int targetCol) {
        Output.printMovement(movingUnit.getUnitName(), argument);
        Output.printMerge(movingUnit.getUnitName(), targetUnit.getUnitName(), argument);

        Unit mergedUnit = movingUnit.mergeUnits(movingUnit, targetUnit);

        GameBoard.setUnitAt(Commands.selectedRow, Commands.selectedColumn, null);
        if (mergedUnit != null) {
            GameBoard.setUnitAt(targetRow, targetCol, mergedUnit);
            mergedUnit.setHasMovedThisTurn(true);
        } else {
            GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
            movingUnit.setHasMovedThisTurn(true);
            Output.printMergeFail(movingUnit.getUnitName());
        }

        Commands.selectedSquare = argument;
        Commands.selectedRow = targetRow;
        Commands.selectedColumn = targetCol;
        Commands.updateDisplay();
    }

    private static void revealCombatUnits(String argument, Unit movingUnit, Unit targetUnit) {
        if (!movingUnit.isFaceUp()) {
            movingUnit.setFaceUp(true);
            Output.printFlip(movingUnit.getUnitName(), movingUnit.getAtk(), movingUnit.getDef(), Commands.selectedSquare);
        }
        if (!targetUnit.isFaceUp()) {
            targetUnit.setFaceUp(true);
            Output.printFlip(targetUnit.getUnitName(), targetUnit.getAtk(), targetUnit.getDef(), argument);
        }
    }

    private static boolean resolveCombat(Unit movingUnit, Unit targetUnit, int targetRow, int targetCol) {
        if (Commands.isKing(targetUnit)) {
            targetUnit.getTeam().takeDamage(movingUnit.getAtk());
            Output.printDamage(targetUnit.getTeam().getName(), movingUnit.getAtk());
            return false;
        }

        if (targetUnit.isBlocking()) {
            return resolveBlockCombat(movingUnit, targetUnit, targetRow, targetCol);
        }

        return resolveStandardCombat(movingUnit, targetUnit, targetRow, targetCol);
    }

    private static boolean resolveBlockCombat(Unit movingUnit, Unit targetUnit, int targetRow, int targetCol) {
        if (movingUnit.getAtk() > targetUnit.getDef()) {
            GameBoard.setUnitAt(targetRow, targetCol, null);
            Output.printElimination(targetUnit.getUnitName());
            return true;
        } else if (movingUnit.getAtk() < targetUnit.getDef()) {
            int damage = targetUnit.getDef() - movingUnit.getAtk();
            movingUnit.getTeam().takeDamage(damage);
            Output.printDamage(movingUnit.getTeam().getName(), damage);
            return false;
        }
        return false;
    }

    private static boolean resolveStandardCombat(Unit movingUnit, Unit targetUnit, int targetRow, int targetCol) {
        if (movingUnit.getAtk() > targetUnit.getAtk()) {
            int damage = movingUnit.getAtk() - targetUnit.getAtk();
            targetUnit.getTeam().takeDamage(damage);
            GameBoard.setUnitAt(targetRow, targetCol, null);
            Output.printElimination(targetUnit.getUnitName());
            Output.printDamage(targetUnit.getTeam().getName(), damage);
            return true;
        } else if (movingUnit.getAtk() < targetUnit.getAtk()) {
            int damage = targetUnit.getAtk() - movingUnit.getAtk();
            movingUnit.getTeam().takeDamage(damage);
            Output.printElimination(movingUnit.getUnitName());
            Output.printDamage(movingUnit.getTeam().getName(), damage);
            return false;
        } else {
            GameBoard.setUnitAt(Commands.selectedRow, Commands.selectedColumn, null);
            GameBoard.setUnitAt(targetRow, targetCol, null);
            Output.printElimination(targetUnit.getUnitName());
            Output.printElimination(movingUnit.getUnitName());
            return false;
        }
    }

    private static void checkHpStatus() {
        if (GameEngine.team1.getTeamHP() <= 0) {
            Output.printZeroPoints(GameEngine.team1.getName());
            Output.printWin(GameEngine.team2.getName());
            Commands.isRunning = false;
        } else if (GameEngine.team2.getTeamHP() <= 0) {
            Output.printZeroPoints(GameEngine.team2.getName());
            Output.printWin(GameEngine.team1.getName());
            Commands.isRunning = false;
        }
    }
}

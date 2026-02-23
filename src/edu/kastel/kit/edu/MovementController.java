package edu.kastel.kit.edu;


public class MovementController {

    public static void handleMove(String argument) {
        if (argument == null || argument.length() != 2) {
            System.err.println("ERROR: Invalid target square.");
            return;
        }

        Unit movingUnit = Commands.getValidatedActiveUnit();
        if (movingUnit == null) {
            return;
        }

        int targetRow = Commands.getCoordinates(argument)[0];
        int targetCol = Commands.getCoordinates(argument)[1];

        int rowDiff = Math.abs(targetRow - Commands.selectedRow);
        int colDiff = Math.abs(targetCol - Commands.selectedColumn);

        if (rowDiff + colDiff > 1) {
            System.err.println("ERROR: Move must be exactly 1 square orthogonally or en place.");
            return;
        }

        Unit targetUnit = GameBoard.getUnitAt(targetRow, targetCol);
        boolean isMoverKing = Commands.isKing(movingUnit);

        if (targetUnit != null) {
            boolean isTargetKing = Commands.isKing(targetUnit);
            boolean isSameTeam = movingUnit.getTeam().equals(targetUnit.getTeam());

            if (isMoverKing && !isSameTeam) {
                System.err.println("ERROR: Farmer King cannot move onto an enemy unit.");
            }

            if (!isMoverKing && isTargetKing && isSameTeam) {
                System.err.println("ERROR: Unit cannot move onto its own Farmer King.");
                return;
            }
        }

        if (targetUnit == null) {
            GameBoard.setUnitAt(Commands.selectedRow, Commands.selectedColumn, null);
            GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
            movingUnit.setHasMovedThisTurn(true);
            Output.printMovement(movingUnit.getUnitName(), argument);

            Commands.selectedSquare = argument;
            Commands.selectedRow = targetRow;
            Commands.selectedColumn = targetCol;
            Commands.updateDisplay();

        } else if (!movingUnit.getTeam().equals(targetUnit.getTeam())) {

            if (movingUnit.isBlocking()) {
                movingUnit.setBlocking(false);
                Output.printNoBlock(movingUnit.getUnitName());
            }

            Output.printAtkMove(movingUnit.getUnitName(), movingUnit.getAtk(), movingUnit.getDef(),
                    targetUnit.getUnitName(), targetUnit.getAtk(), targetUnit.getDef(), argument);

            if (!movingUnit.isFaceUp()) {
                movingUnit.setFaceUp(true);
                Output.printFlip(movingUnit.getUnitName(), movingUnit.getAtk(), movingUnit.getDef(), Commands.selectedSquare);
            }
            if (!targetUnit.isFaceUp()) {
                targetUnit.setFaceUp(true);
                Output.printFlip(targetUnit.getUnitName(), targetUnit.getAtk(), targetUnit.getDef(), argument);
            }

            boolean attackerMovesToTargetSquare = false;

            if (Commands.isKing(targetUnit)) {
                targetUnit.getTeam().takeDamage(movingUnit.getAtk());
                Output.printDamage(targetUnit.getTeam().getName(), movingUnit.getAtk());
            } else if (targetUnit.isBlocking()) {
                if (movingUnit.getAtk() > targetUnit.getDef()) {
                    GameBoard.setUnitAt(targetRow, targetCol, null);
                    Output.printElimination(targetUnit.getUnitName());
                    attackerMovesToTargetSquare = true;
                } else if (movingUnit.getAtk() < targetUnit.getDef()) {
                    int damage = targetUnit.getDef() - movingUnit.getAtk();
                    movingUnit.getTeam().takeDamage(damage);
                    Output.printDamage(movingUnit.getTeam().getName(), damage);
                }
            } else {
                if (movingUnit.getAtk() > targetUnit.getAtk()) {
                    int damage = movingUnit.getAtk() - targetUnit.getAtk();
                    targetUnit.getTeam().takeDamage(damage);
                    GameBoard.setUnitAt(targetRow, targetCol, null);
                    Output.printElimination(targetUnit.getUnitName());
                    Output.printDamage(targetUnit.getTeam().getName(), damage);
                    attackerMovesToTargetSquare = true;
                } else if (movingUnit.getAtk() < targetUnit.getAtk()) {
                    int damage = targetUnit.getAtk() - movingUnit.getAtk();
                    movingUnit.getTeam().takeDamage(damage);
                    Output.printElimination(movingUnit.getUnitName());
                    Output.printDamage(movingUnit.getTeam().getName(), damage);
                } else {
                    GameBoard.setUnitAt(Commands.selectedRow, Commands.selectedColumn, null);
                    GameBoard.setUnitAt(targetRow, targetCol, null);
                    Output.printElimination(targetUnit.getUnitName());
                    Output.printElimination(movingUnit.getUnitName());
                }
            }

            if (attackerMovesToTargetSquare) {
                GameBoard.setUnitAt(Commands.selectedRow, Commands.selectedColumn, null);
                GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
                Output.printMovement(movingUnit.getUnitName(), argument);

                Commands.selectedSquare = argument;
                Commands.selectedRow = targetRow;
                Commands.selectedColumn = targetCol;
            }

            movingUnit.setHasMovedThisTurn(true);

            if (GameEngine.team1.getTeamHP() <= 0) {
                Output.printZeroPoints(GameEngine.team1.getName());
                Output.printWin(GameEngine.team2.getName());
                Commands.isRunning = false;
            } else if (GameEngine.team2.getTeamHP() <= 0) {
                Output.printZeroPoints(GameEngine.team2.getName());
                Output.printWin(GameEngine.team1.getName());
                Commands.isRunning = false;
            }

            if (Commands.isRunning) {
                Commands.updateDisplay();
            }

        } else {
            Output.printMovement(movingUnit.getUnitName(), argument);
            Output.printMerge(movingUnit.getUnitName(), targetUnit.getUnitName(), argument);

            Unit mergedUnit = movingUnit.mergeUnits(movingUnit, targetUnit);

            if (mergedUnit != null) {
                GameBoard.setUnitAt(Commands.selectedRow, Commands.selectedColumn, null);
                GameBoard.setUnitAt(targetRow, targetCol, mergedUnit);
                mergedUnit.setHasMovedThisTurn(true);
            } else {
                GameBoard.setUnitAt(Commands.selectedRow, Commands.selectedColumn, null);
                GameBoard.setUnitAt(targetRow, targetCol, movingUnit);
                movingUnit.setHasMovedThisTurn(true);
                Output.printMergeFail(movingUnit.getUnitName());
            }

            Commands.selectedSquare = argument;
            Commands.selectedRow = targetRow;
            Commands.selectedColumn = targetCol;
            Commands.updateDisplay();
        }
    }
}

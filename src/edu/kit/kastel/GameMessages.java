package edu.kit.kastel;

/**
 * This class contains all the messages that are used in the game, such as error messages and success messages.
 * @author uxuwg
 * @version 0.9
 */
public final class GameMessages {
    /**
     * The label for the King role, used for display purposes.
     */
    public static final String KING = "King";

    /**
     * The label for the King qualifier, used for display purposes.
     */
    public static final String FARMER = "Farmer";

    /**
     * The symbol representing the player's Farmer King on the board.
     */
    public static final char PLAYER_KING_SYMBOL = 'X';

    /**
     * The symbol representing the enemy's Farmer King on the board.
     */
    public static final char ENEMY_KING_SYMBOL = 'Y';

    /**
     * The symbol representing a standard player unit on the board.
     */
    public static final char PLAYER_UNIT_SYMBOL = 'x';

    /**
     * The symbol representing a standard enemy unit on the board.
     */
    public static final char ENEMY_UNIT_SYMBOL = 'y';

    /**
     * The minimum integer value used for comparison purposes in the game, such as when evaluating move scores or determining the best move.
     */
    public static final int MIN_INT = -9999999;

    /**
     * The base character used for labeling rows and columns on the board, 'A' for columns and '1' for rows.
     */
    public static final char CHAR_BASE = 'A';

    /**
     * The label for the board, used for display purposes when showing the game state.
     */
    public static final String BOARD = "board";


    /**
     * The label for the player's hand, used for display purposes when showing the player's current cards.
     */
    public static final String SHOW = "show";


    /**
     * The label for the command to select a unit or square, used for processing player input.
     */
    public static final String ALL = "all";

    /**
     * The standard success message used for confirming valid player input or successful actions.
     */
    public static final String SUCCESS_MESSAGE = "Success!";

    /**
     * The error message used when the player provides an invalid command or input that cannot be processed.
     */
    public static final String ERROR_INVALID_CARD_INDEX = "ERROR: Invalid card index.";
    /**
     * The error message used when the player attempts to select a square that does not exist on the board.
     */
    public static final String ERR_NO_SQUARE = "ERROR: No square selected.";

    /**
     * The error message used when the player attempts to select a square that does not contain a unit.
     */
    public static final String ERROR_COMMAND_DOES_NOT_TAKE_ANY_ARGUMENTS = "ERROR: Command does not take any arguments.";

    private GameMessages() {
    }
}
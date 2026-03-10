package edu.kit.kastel;

/**
 * Enum representing the role of a player in the game. Each role has a label that can be used for display purposes.
 * @author uxuwg
 * @version 0.9
 */
public enum Role {
    KING("King"),
    FARMER("Farmer");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    /**
     * Returns the label associated with this role.
     * @return the label of the role
     */
    public String getLabel() {
        return label;
    }
}
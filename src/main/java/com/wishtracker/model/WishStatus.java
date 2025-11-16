package com.wishtracker.model;

/**
 * Enum representing the lifecycle status of a wish.
 * Wishes follow a structured workflow: WISH → IN_PROGRESS → ACHIEVED
 *
 * Status transitions are enforced by business logic:
 * - WISH can only transition to IN_PROGRESS
 * - IN_PROGRESS can only transition to ACHIEVED
 * - No backwards transitions allowed
 */
public enum WishStatus {
    /**
     * Initial status - wish has been created but not started
     * Displayed in the first column of the Kanban board
     */
    WISH("Wish"),

    /**
     * Wish is actively being worked on
     * Displayed in the second column of the Kanban board
     */
    IN_PROGRESS("In Progress"),

    /**
     * Wish has been completed/achieved
     * Displayed in the third column of the Kanban board
     * When set, achievedDate is automatically populated
     */
    ACHIEVED("Achieved");

    private final String displayName;

    /**
     * Constructor for enum values
     *
     * @param displayName the human-readable name for UI display
     */
    WishStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the display name for this status
     *
     * @return the human-readable display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this status can transition to the target status
     *
     * @param targetStatus the status to transition to
     * @return true if transition is valid, false otherwise
     */
    public boolean canTransitionTo(WishStatus targetStatus) {
        return switch (this) {
            case WISH -> targetStatus == IN_PROGRESS;
            case IN_PROGRESS -> targetStatus == ACHIEVED;
            case ACHIEVED -> false; // No transitions from ACHIEVED
        };
    }

    /**
     * Get the next valid status in the workflow
     *
     * @return the next status, or null if no valid transition exists
     */
    public WishStatus getNextStatus() {
        return switch (this) {
            case WISH -> IN_PROGRESS;
            case IN_PROGRESS -> ACHIEVED;
            case ACHIEVED -> null;
        };
    }
}

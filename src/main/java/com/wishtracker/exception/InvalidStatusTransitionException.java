package com.wishtracker.exception;

import com.wishtracker.model.WishStatus;

/**
 * Exception thrown when attempting an invalid status transition.
 * Valid transitions: WISH → IN_PROGRESS → ACHIEVED (no backwards transitions)
 */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }

    public InvalidStatusTransitionException(WishStatus currentStatus, WishStatus targetStatus) {
        super("Invalid status transition from " + currentStatus + " to " + targetStatus +
              ". Valid transitions: WISH → IN_PROGRESS → ACHIEVED");
    }
}

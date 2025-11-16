package com.wishtracker.exception;

/**
 * Exception thrown when a wish is not found or doesn't belong to the user.
 */
public class WishNotFoundException extends RuntimeException {

    public WishNotFoundException(String message) {
        super(message);
    }

    public WishNotFoundException(Long id) {
        super("Wish not found with id: " + id);
    }
}

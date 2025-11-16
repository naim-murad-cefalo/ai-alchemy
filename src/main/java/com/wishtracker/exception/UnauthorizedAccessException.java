package com.wishtracker.exception;

/**
 * Exception thrown when a user attempts to access or modify data they don't own.
 * This is a critical security exception for enforcing user data isolation.
 */
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String resourceType, Long resourceId) {
        super("Unauthorized access to " + resourceType + " with id: " + resourceId);
    }
}

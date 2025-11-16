package com.wishtracker.exception;

/**
 * Exception thrown when a user is not found.
 * Typically occurs when trying to get the current authenticated user
 * but no user record exists in the database.
 */
public class UserNotFoundException extends RuntimeException {


    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}

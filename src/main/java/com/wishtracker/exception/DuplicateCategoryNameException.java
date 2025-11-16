package com.wishtracker.exception;

/**
 * Exception thrown when attempting to create a category with a name that already exists for the user.
 * Category names must be unique per user.
 */
public class DuplicateCategoryNameException extends RuntimeException {


    public DuplicateCategoryNameException(String categoryName) {
        super("A category with the name '" + categoryName + "' already exists. Please choose a different name.");
    }
}

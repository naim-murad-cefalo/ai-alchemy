package com.wishtracker.exception;

/**
 * Exception thrown when a category is not found or doesn't belong to the user.
 */
public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(Long id) {
        super("Category not found with id: " + id);
    }
}

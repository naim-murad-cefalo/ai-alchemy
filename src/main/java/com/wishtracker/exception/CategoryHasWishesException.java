package com.wishtracker.exception;

/**
 * Exception thrown when attempting to delete a category that still has wishes.
 * Categories must be empty before deletion.
 */
public class CategoryHasWishesException extends RuntimeException {

    public CategoryHasWishesException(String message) {
        super(message);
    }

    public CategoryHasWishesException(String categoryName, long wishCount) {
        super("Cannot delete category '" + categoryName + "' because it has " + wishCount + " wish(es). Please move or delete the wishes first.");
    }
}

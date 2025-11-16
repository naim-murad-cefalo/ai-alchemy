package com.wishtracker.repository;

import com.wishtracker.model.Category;
import com.wishtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Category entity operations.
 *
 * CRITICAL: ALL queries MUST filter by User to ensure data isolation.
 * Users can ONLY access their own categories.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find all categories belonging to a specific user.
     *
     * @param user the user who owns the categories
     * @return list of categories for the user
     */
    List<Category> findByUser(User user);

    /**
     * Find a category by ID AND verify it belongs to the specified user.
     * This is CRITICAL for authorization - prevents users from accessing other users' categories.
     *
     * @param id   the category ID
     * @param user the user who should own the category
     * @return Optional containing the category if found and owned by user, empty otherwise
     */
    Optional<Category> findByIdAndUser(Long id, User user);

    /**
     * Find all categories for a user, ordered by name ascending.
     * Useful for displaying categories in alphabetical order.
     *
     * @param user the user who owns the categories
     * @return list of categories sorted by name
     */
    List<Category> findByUserOrderByNameAsc(User user);

    /**
     * Check if a category with the given name exists for a specific user.
     * Used to enforce the unique constraint (name, user) before creating/updating.
     *
     * @param name the category name to check
     * @param user the user to check for
     * @return true if category with this name exists for this user, false otherwise
     */
    boolean existsByNameAndUser(String name, User user);
}

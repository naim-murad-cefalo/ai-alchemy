package com.wishtracker.repository;

import com.wishtracker.model.Category;
import com.wishtracker.model.User;
import com.wishtracker.model.Wish;
import com.wishtracker.model.WishStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Wish entity operations.
 *
 * CRITICAL: ALL queries MUST filter by User to ensure data isolation.
 * Users can ONLY access their own wishes.
 */
@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

    /**
     * Find all wishes belonging to a specific user.
     *
     * @param user the user who owns the wishes
     * @return list of wishes for the user
     */
    List<Wish> findByUser(User user);

    /**
     * Find a wish by ID AND verify it belongs to the specified user.
     * This is CRITICAL for authorization - prevents users from accessing other users' wishes.
     *
     * @param id   the wish ID
     * @param user the user who should own the wish
     * @return Optional containing the wish if found and owned by user, empty otherwise
     */
    Optional<Wish> findByIdAndUser(Long id, User user);

    /**
     * Find all wishes for a user, ordered by creation date descending (newest first).
     * Useful for displaying recent wishes.
     *
     * @param user the user who owns the wishes
     * @return list of wishes sorted by creation date (newest first)
     */
    List<Wish> findByUserOrderByCreatedDateDesc(User user);

    /**
     * Find all wishes for a user with a specific status.
     * Used for Kanban board columns (WISH, IN_PROGRESS, ACHIEVED).
     *
     * @param user   the user who owns the wishes
     * @param status the status to filter by
     * @return list of wishes with the specified status
     */
    List<Wish> findByUserAndStatus(User user, WishStatus status);

    /**
     * Find all wishes for a user in a specific category.
     * Used for category filtering in the UI.
     *
     * @param user     the user who owns the wishes
     * @param category the category to filter by
     * @return list of wishes in the specified category
     */
    List<Wish> findByUserAndCategory(User user, Category category);

    /**
     * Find all wishes for a user in a specific category with a specific status.
     * Used for filtered Kanban board views.
     *
     * @param user     the user who owns the wishes
     * @param category the category to filter by
     * @param status   the status to filter by
     * @return list of wishes matching both category and status
     */
    List<Wish> findByUserAndCategoryAndStatus(User user, Category category, WishStatus status);

    /**
     * Count the number of wishes in a category.
     * Used to prevent deletion of categories with wishes.
     * Also used for displaying wish count per category.
     *
     * @param category the category to count wishes for
     * @return number of wishes in the category
     */
    long countByCategory(Category category);
}

package com.wishtracker.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User entity representing authenticated users in the Wish Tracker application.
 * Users are automatically created on first Google SSO login.
 *
 * Each user has complete data isolation - they can only access their own wishes and categories.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_email", columnNames = "email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Primary key - auto-generated
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's email from Google OAuth - unique identifier
     * This is the principal name used in Spring Security
     */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * User's display name from Google OAuth
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * User's profile picture URL from Google OAuth (optional)
     */
    @Column(length = 500)
    private String pictureUrl;

    /**
     * Timestamp when the user account was created (first login)
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * Timestamp of the user's last login
     * Updated on each successful authentication
     */
    @Column
    private LocalDateTime lastLoginDate;

    /**
     * Categories owned by this user
     * Cascade ALL: When user is deleted, all categories are deleted
     * orphanRemoval: When category is removed from list, it's deleted from DB
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Category> categories = new ArrayList<>();

    /**
     * Wishes owned by this user
     * Cascade ALL: When user is deleted, all wishes are deleted
     * orphanRemoval: When wish is removed from list, it's deleted from DB
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Wish> wishes = new ArrayList<>();

    /**
     * Automatically set createdDate when entity is first persisted
     */
    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (lastLoginDate == null) {
            lastLoginDate = LocalDateTime.now();
        }
    }

    /**
     * Helper method to add a category to this user
     * Maintains bidirectional relationship
     *
     * @param category the category to add
     */
    public void addCategory(Category category) {
        categories.add(category);
        category.setUser(this);
    }

    /**
     * Helper method to remove a category from this user
     * Maintains bidirectional relationship
     *
     * @param category the category to remove
     */
    public void removeCategory(Category category) {
        categories.remove(category);
        category.setUser(null);
    }

    /**
     * Helper method to add a wish to this user
     * Maintains bidirectional relationship
     *
     * @param wish the wish to add
     */
    public void addWish(Wish wish) {
        wishes.add(wish);
        wish.setUser(this);
    }

    /**
     * Helper method to remove a wish from this user
     * Maintains bidirectional relationship
     *
     * @param wish the wish to remove
     */
    public void removeWish(Wish wish) {
        wishes.remove(wish);
        wish.setUser(null);
    }
}

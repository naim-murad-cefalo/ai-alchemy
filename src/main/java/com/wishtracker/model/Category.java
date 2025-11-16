package com.wishtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Category entity for organizing wishes.
 * Each category belongs to a specific user and can have multiple wishes.
 *
 * Categories are user-scoped - each user can have their own set of categories
 * with custom names and colors for visual organization.
 *
 * IMPORTANT: Category names must be unique per user (enforced by unique constraint)
 */
@Entity
@Table(name = "categories", uniqueConstraints = {
    @UniqueConstraint(name = "uk_category_name_user", columnNames = {"name", "user_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    /**
     * Primary key - auto-generated
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Category name - must be unique per user
     * Examples: "Travel", "Books", "Fitness", "Tech"
     */
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Optional description for the category
     */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(length = 500)
    private String description;

    /**
     * Color for visual identification in UI (hex color code)
     * Default: #6B7280 (gray)
     * Example: "#3B82F6" (blue), "#10B981" (green)
     */
    @NotBlank(message = "Color is required")
    @Column(nullable = false, length = 100)
    @Builder.Default
    private String color = "#6B7280";

    /**
     * The user who owns this category
     * LAZY fetch to avoid loading user unnecessarily
     * IMPORTANT: Every category MUST belong to a user (data isolation)
     */
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_category_user"))
    @ToString.Exclude
    private User user;

    /**
     * Wishes associated with this category
     * Cascade ALL: When category is deleted, all wishes are deleted
     * mappedBy: Category is the non-owning side of the relationship
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Wish> wishes = new ArrayList<>();

    /**
     * Timestamp when the category was created
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * Automatically set createdDate when entity is first persisted
     */
    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }

    /**
     * Helper method to add a wish to this category
     * Maintains bidirectional relationship
     *
     * @param wish the wish to add
     */
    public void addWish(Wish wish) {
        wishes.add(wish);
        wish.setCategory(this);
    }

    /**
     * Helper method to remove a wish from this category
     * Maintains bidirectional relationship
     *
     * @param wish the wish to remove
     */
    public void removeWish(Wish wish) {
        wishes.remove(wish);
        wish.setCategory(null);
    }

    /**
     * Get the count of wishes in this category
     *
     * @return number of wishes
     */
    public int getWishCount() {
        return wishes.size();
    }
}

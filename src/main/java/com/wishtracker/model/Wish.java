package com.wishtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Wish entity representing a user's wish/goal.
 * Each wish belongs to a specific user and is categorized.
 *
 * Wishes follow a workflow: WISH → IN_PROGRESS → ACHIEVED
 * Status transitions are enforced by business logic in the service layer.
 *
 * IMPORTANT: Wishes are user-scoped - users can only access their own wishes
 */
@Entity
@Table(name = "wishes", indexes = {
    @Index(name = "idx_wish_user", columnList = "user_id"),
    @Index(name = "idx_wish_category", columnList = "category_id"),
    @Index(name = "idx_wish_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wish {

    /**
     * Primary key - auto-generated
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Wish title - brief description of the wish
     * Example: "Visit Japan", "Read 50 books", "Learn Spring Boot"
     */
    @NotBlank(message = "Wish title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * Detailed description of the wish (optional)
     * Can include goals, steps, motivation, etc.
     */
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    /**
     * Current status of the wish in the workflow
     * Default: WISH (initial status)
     */
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private WishStatus status = WishStatus.WISH;

    /**
     * Optional remarks/notes about the wish
     * Can include progress notes, obstacles, reflections, etc.
     */
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    @Column(length = 500)
    private String remarks;

    /**
     * The category this wish belongs to
     * LAZY fetch to avoid loading category unnecessarily
     * IMPORTANT: Every wish MUST belong to a category
     */
    @NotNull(message = "Category is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_wish_category"))
    @ToString.Exclude
    private Category category;

    /**
     * The user who owns this wish
     * LAZY fetch to avoid loading user unnecessarily
     * IMPORTANT: Every wish MUST belong to a user (data isolation)
     */
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_wish_user"))
    @ToString.Exclude
    private User user;

    /**
     * Timestamp when the wish was created
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * Timestamp when the wish was last updated
     * Automatically updated on any modification
     */
    @Column(nullable = false)
    private LocalDateTime updatedDate;

    /**
     * Timestamp when the wish was achieved (status = ACHIEVED)
     * Null for wishes that haven't been achieved yet
     * Automatically set when status transitions to ACHIEVED
     */
    @Column
    private LocalDateTime achievedDate;

    /**
     * Automatically set createdDate and updatedDate when entity is first persisted
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdDate == null) {
            createdDate = now;
        }
        if (updatedDate == null) {
            updatedDate = now;
        }
    }

    /**
     * Automatically update updatedDate when entity is modified
     */
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    /**
     * Update the status of this wish
     * If transitioning to ACHIEVED, automatically set achievedDate
     *
     * @param newStatus the new status
     */
    public void updateStatus(WishStatus newStatus) {
        this.status = newStatus;
        if (newStatus == WishStatus.ACHIEVED && this.achievedDate == null) {
            this.achievedDate = LocalDateTime.now();
        }
    }

    /**
     * Check if this wish has been achieved
     *
     * @return true if status is ACHIEVED
     */
    public boolean isAchieved() {
        return status == WishStatus.ACHIEVED;
    }

    /**
     * Check if this wish is in progress
     *
     * @return true if status is IN_PROGRESS
     */
    public boolean isInProgress() {
        return status == WishStatus.IN_PROGRESS;
    }

    /**
     * Check if this wish is still in WISH status
     *
     * @return true if status is WISH
     */
    public boolean isWish() {
        return status == WishStatus.WISH;
    }
}

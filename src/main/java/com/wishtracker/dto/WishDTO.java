package com.wishtracker.dto;

import com.wishtracker.model.Category;
import com.wishtracker.model.User;
import com.wishtracker.model.Wish;
import com.wishtracker.model.WishStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data Transfer Object for Wish entity.
 * Used to transfer wish data between layers without exposing entity internals.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishDTO {

    private Long id;

    @NotBlank(message = "Wish title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Status is required")
    private WishStatus status;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;

    @NotNull(message = "Category is required")
    private Long categoryId;

    /**
     * Category name for display (denormalized for UI convenience)
     */
    private String categoryName;

    /**
     * Category color for display (denormalized for UI convenience)
     */
    private String categoryColor;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime achievedDate;

    /**
     * Convert Wish entity to DTO
     *
     * @param wish the wish entity
     * @return WishDTO
     */
    public static WishDTO fromEntity(Wish wish) {
        if (wish == null) {
            return null;
        }

        return WishDTO.builder()
                .id(wish.getId())
                .title(wish.getTitle())
                .description(wish.getDescription())
                .status(wish.getStatus())
                .remarks(wish.getRemarks())
                .categoryId(wish.getCategory().getId())
                .categoryName(wish.getCategory().getName())
                .categoryColor(wish.getCategory().getColor())
                .createdDate(wish.getCreatedDate())
                .updatedDate(wish.getUpdatedDate())
                .achievedDate(wish.getAchievedDate())
                .build();
    }

    /**
     * Convert DTO to Wish entity
     * Note: User and Category must be set separately for data isolation
     *
     * @param user     the user who owns this wish
     * @param category the category for this wish
     * @return Wish entity
     */
    public Wish toEntity(User user, Category category) {
        return Wish.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .status(this.status != null ? this.status : WishStatus.WISH)
                .remarks(this.remarks)
                .category(category)
                .user(user)
                .build();
    }

    /**
     * Get formatted created date for display
     *
     * @return formatted date string
     */
    public String getFormattedCreatedDate() {
        if (createdDate == null) {
            return "";
        }
        return createdDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    /**
     * Get formatted achieved date for display
     *
     * @return formatted date string or empty if not achieved
     */
    public String getFormattedAchievedDate() {
        if (achievedDate == null) {
            return "";
        }
        return achievedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    /**
     * Get truncated description for card preview
     *
     * @param maxLength maximum length
     * @return truncated description
     */
    public String getTruncatedDescription(int maxLength) {
        if (description == null || description.length() <= maxLength) {
            return description;
        }
        return description.substring(0, maxLength) + "...";
    }
}

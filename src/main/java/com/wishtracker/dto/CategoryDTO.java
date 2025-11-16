package com.wishtracker.dto;

import com.wishtracker.model.Category;
import com.wishtracker.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Category entity.
 * Used to transfer category data between layers without exposing entity internals.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotBlank(message = "Color is required")
    private String color;

    /**
     * Number of wishes in this category (for display purposes)
     */
    private int wishCount;

    /**
     * Convert Category entity to DTO
     *
     * @param category the category entity
     * @return CategoryDTO
     */
    public static CategoryDTO fromEntity(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .color(category.getColor())
                .wishCount(category.getWishCount())
                .build();
    }

    /**
     * Convert DTO to Category entity
     * Note: User must be set separately for data isolation
     *
     * @param user the user who owns this category
     * @return Category entity
     */
    public Category toEntity(User user) {
        return Category.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .color(this.color != null ? this.color : "#6B7280")
                .user(user)
                .build();
    }
}

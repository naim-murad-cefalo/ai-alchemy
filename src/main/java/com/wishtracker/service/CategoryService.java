package com.wishtracker.service;

import com.wishtracker.dto.CategoryDTO;
import com.wishtracker.exception.CategoryHasWishesException;
import com.wishtracker.exception.CategoryNotFoundException;
import com.wishtracker.exception.DuplicateCategoryNameException;
import com.wishtracker.exception.UnauthorizedAccessException;
import com.wishtracker.model.Category;
import com.wishtracker.model.User;
import com.wishtracker.repository.CategoryRepository;
import com.wishtracker.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing categories with USER CONTEXT.
 * ALL operations are user-scoped - users can only access their own categories.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final WishRepository wishRepository;

    /**
     * Find all categories for a user, ordered by name.
     *
     * @param user the user who owns the categories
     * @return list of category DTOs
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> findAllByUser(User user) {
        log.debug("Finding all categories for user: {}", user.getEmail());
        return categoryRepository.findByUserOrderByNameAsc(user).stream()
                .map(CategoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Find a category by ID and verify ownership.
     *
     * @param id   the category ID
     * @param user the user who should own the category
     * @return category DTO
     * @throws CategoryNotFoundException if category not found or doesn't belong to user
     */
    @Transactional(readOnly = true)
    public CategoryDTO findByIdAndUser(Long id, User user) {
        log.debug("Finding category {} for user: {}", id, user.getEmail());
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return CategoryDTO.fromEntity(category);
    }

    /**
     * Create a new category for a user.
     * Validates that category name is unique for this user.
     *
     * @param dto  the category data
     * @param user the user who will own the category
     * @return created category DTO
     * @throws DuplicateCategoryNameException if category name already exists for user
     */
    @Transactional
    public CategoryDTO create(CategoryDTO dto, User user) {
        log.info("Creating category '{}' for user: {}", dto.getName(), user.getEmail());

        // Check for duplicate name
        if (categoryRepository.existsByNameAndUser(dto.getName(), user)) {
            throw new DuplicateCategoryNameException(dto.getName());
        }

        Category category = dto.toEntity(user);
        Category saved = categoryRepository.save(category);
        log.info("Created category {} with name '{}'", saved.getId(), saved.getName());

        return CategoryDTO.fromEntity(saved);
    }

    /**
     * Update an existing category.
     * Verifies ownership and validates name uniqueness.
     *
     * @param id   the category ID to update
     * @param dto  the updated category data
     * @param user the user who owns the category
     * @return updated category DTO
     * @throws CategoryNotFoundException      if category not found or doesn't belong to user
     * @throws DuplicateCategoryNameException if new name already exists for user
     */
    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto, User user) {
        log.info("Updating category {} for user: {}", id, user.getEmail());

        // Verify ownership
        Category existingCategory = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        // Check for duplicate name (excluding current category)
        if (!existingCategory.getName().equals(dto.getName()) &&
            categoryRepository.existsByNameAndUser(dto.getName(), user)) {
            throw new DuplicateCategoryNameException(dto.getName());
        }

        // Update fields
        existingCategory.setName(dto.getName());
        existingCategory.setDescription(dto.getDescription());
        existingCategory.setColor(dto.getColor());

        Category updated = categoryRepository.save(existingCategory);
        log.info("Updated category {} with name '{}'", updated.getId(), updated.getName());

        return CategoryDTO.fromEntity(updated);
    }

    /**
     * Delete a category.
     * Verifies ownership and ensures category has no wishes.
     *
     * @param id   the category ID to delete
     * @param user the user who owns the category
     * @throws CategoryNotFoundException  if category not found or doesn't belong to user
     * @throws CategoryHasWishesException if category still has wishes
     */
    @Transactional
    public void delete(Long id, User user) {
        log.info("Deleting category {} for user: {}", id, user.getEmail());

        // Verify ownership
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        // Check if category has wishes
        long wishCount = wishRepository.countByCategory(category);
        if (wishCount > 0) {
            throw new CategoryHasWishesException(category.getName(), wishCount);
        }

        categoryRepository.delete(category);
        log.info("Deleted category {} with name '{}'", id, category.getName());
    }

    /**
     * Get a category entity by ID and user (for internal use by other services).
     *
     * @param id   the category ID
     * @param user the user who should own the category
     * @return category entity
     * @throws CategoryNotFoundException if category not found or doesn't belong to user
     */
    @Transactional(readOnly = true)
    public Category getCategoryEntity(Long id, User user) {
        return categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }
}

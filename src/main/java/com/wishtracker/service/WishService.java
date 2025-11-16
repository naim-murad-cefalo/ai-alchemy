package com.wishtracker.service;

import com.wishtracker.dto.WishDTO;
import com.wishtracker.exception.InvalidStatusTransitionException;
import com.wishtracker.exception.WishNotFoundException;
import com.wishtracker.model.Category;
import com.wishtracker.model.User;
import com.wishtracker.model.Wish;
import com.wishtracker.model.WishStatus;
import com.wishtracker.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing wishes with USER CONTEXT.
 * ALL operations are user-scoped - users can only access their own wishes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WishService {

    private final WishRepository wishRepository;
    private final CategoryService categoryService;

    /**
     * Find all wishes for a user, ordered by creation date (newest first).
     *
     * @param user the user who owns the wishes
     * @return list of wish DTOs
     */
    @Transactional(readOnly = true)
    public List<WishDTO> findAllByUser(User user) {
        log.debug("Finding all wishes for user: {}", user.getEmail());
        return wishRepository.findByUserOrderByCreatedDateDesc(user).stream()
                .map(WishDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Find a wish by ID and verify ownership.
     *
     * @param id   the wish ID
     * @param user the user who should own the wish
     * @return wish DTO
     * @throws WishNotFoundException if wish not found or doesn't belong to user
     */
    @Transactional(readOnly = true)
    public WishDTO findByIdAndUser(Long id, User user) {
        log.debug("Finding wish {} for user: {}", id, user.getEmail());
        Wish wish = wishRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new WishNotFoundException(id));
        return WishDTO.fromEntity(wish);
    }

    /**
     * Find all wishes for a user with a specific status.
     *
     * @param user   the user who owns the wishes
     * @param status the status to filter by
     * @return list of wish DTOs
     */
    @Transactional(readOnly = true)
    public List<WishDTO> findByUserAndStatus(User user, WishStatus status) {
        log.debug("Finding wishes with status {} for user: {}", status, user.getEmail());
        return wishRepository.findByUserAndStatus(user, status).stream()
                .map(WishDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Find all wishes for a user in a specific category.
     *
     * @param user       the user who owns the wishes
     * @param categoryId the category ID to filter by
     * @return list of wish DTOs
     */
    @Transactional(readOnly = true)
    public List<WishDTO> findByUserAndCategory(User user, Long categoryId) {
        log.debug("Finding wishes in category {} for user: {}", categoryId, user.getEmail());

        // Verify category belongs to user
        Category category = categoryService.getCategoryEntity(categoryId, user);

        return wishRepository.findByUserAndCategory(user, category).stream()
                .map(WishDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Create a new wish for a user.
     * Verifies that the category belongs to the user.
     *
     * @param dto  the wish data
     * @param user the user who will own the wish
     * @return created wish DTO
     */
    @Transactional
    public WishDTO create(WishDTO dto, User user) {
        log.info("Creating wish '{}' for user: {}", dto.getTitle(), user.getEmail());

        // Verify category belongs to user
        Category category = categoryService.getCategoryEntity(dto.getCategoryId(), user);

        Wish wish = dto.toEntity(user, category);
        Wish saved = wishRepository.save(wish);
        log.info("Created wish {} with title '{}'", saved.getId(), saved.getTitle());

        return WishDTO.fromEntity(saved);
    }

    /**
     * Update an existing wish.
     * Verifies ownership and that the new category belongs to the user.
     *
     * @param id   the wish ID to update
     * @param dto  the updated wish data
     * @param user the user who owns the wish
     * @return updated wish DTO
     * @throws WishNotFoundException if wish not found or doesn't belong to user
     */
    @Transactional
    public WishDTO update(Long id, WishDTO dto, User user) {
        log.info("Updating wish {} for user: {}", id, user.getEmail());

        // Verify ownership
        Wish existingWish = wishRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new WishNotFoundException(id));

        // Verify category belongs to user
        Category category = categoryService.getCategoryEntity(dto.getCategoryId(), user);

        // Update fields
        existingWish.setTitle(dto.getTitle());
        existingWish.setDescription(dto.getDescription());
        existingWish.setRemarks(dto.getRemarks());
        existingWish.setCategory(category);

        // Only update status if it's a valid transition
        if (dto.getStatus() != null && !dto.getStatus().equals(existingWish.getStatus())) {
            if (existingWish.getStatus().canTransitionTo(dto.getStatus())) {
                existingWish.updateStatus(dto.getStatus());
            } else {
                throw new InvalidStatusTransitionException(existingWish.getStatus(), dto.getStatus());
            }
        }

        Wish updated = wishRepository.save(existingWish);
        log.info("Updated wish {} with title '{}'", updated.getId(), updated.getTitle());

        return WishDTO.fromEntity(updated);
    }

    /**
     * Delete a wish.
     * Verifies ownership before deletion.
     *
     * @param id   the wish ID to delete
     * @param user the user who owns the wish
     * @throws WishNotFoundException if wish not found or doesn't belong to user
     */
    @Transactional
    public void delete(Long id, User user) {
        log.info("Deleting wish {} for user: {}", id, user.getEmail());

        // Verify ownership
        Wish wish = wishRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new WishNotFoundException(id));

        wishRepository.delete(wish);
        log.info("Deleted wish {} with title '{}'", id, wish.getTitle());
    }

    /**
     * Change the status of a wish.
     * Validates status transitions: WISH → IN_PROGRESS → ACHIEVED
     * Automatically sets achievedDate when transitioning to ACHIEVED.
     *
     * @param id        the wish ID
     * @param newStatus the new status
     * @param user      the user who owns the wish
     * @return updated wish DTO
     * @throws WishNotFoundException             if wish not found or doesn't belong to user
     * @throws InvalidStatusTransitionException  if transition is not valid
     */
    @Transactional
    public WishDTO changeStatus(Long id, WishStatus newStatus, User user) {
        log.info("Changing status of wish {} to {} for user: {}", id, newStatus, user.getEmail());

        // Verify ownership
        Wish wish = wishRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new WishNotFoundException(id));

        WishStatus currentStatus = wish.getStatus();

        // Validate status transition
        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }

        // Update status (this will automatically set achievedDate if status is ACHIEVED)
        wish.updateStatus(newStatus);

        Wish updated = wishRepository.save(wish);
        log.info("Changed status of wish {} from {} to {}", id, currentStatus, newStatus);

        return WishDTO.fromEntity(updated);
    }
}

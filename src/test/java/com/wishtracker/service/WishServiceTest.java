package com.wishtracker.service;

import com.wishtracker.dto.WishDTO;
import com.wishtracker.exception.InvalidStatusTransitionException;
import com.wishtracker.exception.WishNotFoundException;
import com.wishtracker.model.Category;
import com.wishtracker.model.User;
import com.wishtracker.model.Wish;
import com.wishtracker.model.WishStatus;
import com.wishtracker.repository.WishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WishService.
 * Tests all CRUD operations and status transitions with user context.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WishService Tests")
class WishServiceTest {

    @Mock
    private WishRepository wishRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private WishService wishService;

    private User testUser;
    private Category testCategory;
    private Wish testWish;
    private WishDTO testWishDTO;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .build();

        // Create test category
        testCategory = Category.builder()
                .id(1L)
                .name("Travel")
                .color("#FF5733")
                .user(testUser)
                .build();

        // Create test wish
        testWish = Wish.builder()
                .id(1L)
                .title("Visit Japan")
                .description("Experience Tokyo and Kyoto")
                .status(WishStatus.WISH)
                .remarks("Need to save money")
                .category(testCategory)
                .user(testUser)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        // Create test DTO
        testWishDTO = WishDTO.builder()
                .title("Visit Japan")
                .description("Experience Tokyo and Kyoto")
                .status(WishStatus.WISH)
                .remarks("Need to save money")
                .categoryId(1L)
                .build();
    }

    @Test
    @DisplayName("Should find all wishes for user ordered by creation date")
    void testFindAllByUser() {
        // Given
        Wish wish2 = Wish.builder()
                .id(2L)
                .title("Learn Spring Boot")
                .status(WishStatus.IN_PROGRESS)
                .category(testCategory)
                .user(testUser)
                .createdDate(LocalDateTime.now().minusDays(1))
                .updatedDate(LocalDateTime.now())
                .build();

        List<Wish> wishes = Arrays.asList(testWish, wish2);
        when(wishRepository.findByUserOrderByCreatedDateDesc(testUser)).thenReturn(wishes);

        // When
        List<WishDTO> result = wishService.findAllByUser(testUser);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Visit Japan");
        assertThat(result.get(1).getTitle()).isEqualTo("Learn Spring Boot");
        verify(wishRepository, times(1)).findByUserOrderByCreatedDateDesc(testUser);
    }

    @Test
    @DisplayName("Should find wish by ID and user")
    void testFindByIdAndUser() {
        // Given
        when(wishRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testWish));

        // When
        WishDTO result = wishService.findByIdAndUser(1L, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Visit Japan");
        assertThat(result.getDescription()).isEqualTo("Experience Tokyo and Kyoto");
        verify(wishRepository, times(1)).findByIdAndUser(1L, testUser);
    }

    @Test
    @DisplayName("Should throw WishNotFoundException when wish not found")
    void testFindByIdAndUser_NotFound() {
        // Given
        when(wishRepository.findByIdAndUser(999L, testUser)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> wishService.findByIdAndUser(999L, testUser))
                .isInstanceOf(WishNotFoundException.class);
        verify(wishRepository, times(1)).findByIdAndUser(999L, testUser);
    }

    @Test
    @DisplayName("Should find wishes by user and status")
    void testFindByUserAndStatus() {
        // Given
        List<Wish> wishStatusList = Arrays.asList(testWish);
        when(wishRepository.findByUserAndStatus(testUser, WishStatus.WISH)).thenReturn(wishStatusList);

        // When
        List<WishDTO> result = wishService.findByUserAndStatus(testUser, WishStatus.WISH);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(WishStatus.WISH);
        verify(wishRepository, times(1)).findByUserAndStatus(testUser, WishStatus.WISH);
    }

    @Test
    @DisplayName("Should find wishes by user and category")
    void testFindByUserAndCategory() {
        // Given
        List<Wish> categoryWishes = Arrays.asList(testWish);
        when(categoryService.getCategoryEntity(1L, testUser)).thenReturn(testCategory);
        when(wishRepository.findByUserAndCategory(testUser, testCategory)).thenReturn(categoryWishes);

        // When
        List<WishDTO> result = wishService.findByUserAndCategory(testUser, 1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryName()).isEqualTo("Travel");
        verify(categoryService, times(1)).getCategoryEntity(1L, testUser);
        verify(wishRepository, times(1)).findByUserAndCategory(testUser, testCategory);
    }

    @Test
    @DisplayName("Should create new wish successfully")
    void testCreate() {
        // Given
        when(categoryService.getCategoryEntity(1L, testUser)).thenReturn(testCategory);
        when(wishRepository.save(any(Wish.class))).thenReturn(testWish);

        // When
        WishDTO result = wishService.create(testWishDTO, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Visit Japan");
        assertThat(result.getStatus()).isEqualTo(WishStatus.WISH);
        verify(categoryService, times(1)).getCategoryEntity(1L, testUser);
        verify(wishRepository, times(1)).save(any(Wish.class));
    }

    @Test
    @DisplayName("Should update wish successfully")
    void testUpdate() {
        // Given
        WishDTO updateDTO = WishDTO.builder()
                .title("Visit Japan and South Korea")
                .description("Extended trip")
                .remarks("Updated plan")
                .categoryId(1L)
                .status(WishStatus.WISH)
                .build();

        when(wishRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testWish));
        when(categoryService.getCategoryEntity(1L, testUser)).thenReturn(testCategory);
        when(wishRepository.save(any(Wish.class))).thenReturn(testWish);

        // When
        WishDTO result = wishService.update(1L, updateDTO, testUser);

        // Then
        assertThat(result).isNotNull();
        verify(wishRepository, times(1)).findByIdAndUser(1L, testUser);
        verify(categoryService, times(1)).getCategoryEntity(1L, testUser);
        verify(wishRepository, times(1)).save(any(Wish.class));
    }

    @Test
    @DisplayName("Should delete wish successfully")
    void testDelete() {
        // Given
        when(wishRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testWish));
        doNothing().when(wishRepository).delete(testWish);

        // When
        wishService.delete(1L, testUser);

        // Then
        verify(wishRepository, times(1)).findByIdAndUser(1L, testUser);
        verify(wishRepository, times(1)).delete(testWish);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent wish")
    void testDelete_NotFound() {
        // Given
        when(wishRepository.findByIdAndUser(999L, testUser)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> wishService.delete(999L, testUser))
                .isInstanceOf(WishNotFoundException.class);
        verify(wishRepository, times(1)).findByIdAndUser(999L, testUser);
        verify(wishRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should change status from WISH to IN_PROGRESS")
    void testChangeStatus_WishToInProgress() {
        // Given
        when(wishRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testWish));
        when(wishRepository.save(any(Wish.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        WishDTO result = wishService.changeStatus(1L, WishStatus.IN_PROGRESS, testUser);

        // Then
        assertThat(result).isNotNull();
        verify(wishRepository, times(1)).findByIdAndUser(1L, testUser);
        verify(wishRepository, times(1)).save(any(Wish.class));
    }

    @Test
    @DisplayName("Should change status from IN_PROGRESS to ACHIEVED and set achievedDate")
    void testChangeStatus_InProgressToAchieved() {
        // Given
        testWish.setStatus(WishStatus.IN_PROGRESS);
        when(wishRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testWish));
        when(wishRepository.save(any(Wish.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        WishDTO result = wishService.changeStatus(1L, WishStatus.ACHIEVED, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(testWish.getAchievedDate()).isNotNull();
        verify(wishRepository, times(1)).findByIdAndUser(1L, testUser);
        verify(wishRepository, times(1)).save(any(Wish.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid status transition")
    void testChangeStatus_InvalidTransition() {
        // Given
        when(wishRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testWish));

        // When & Then - trying to go from WISH to ACHIEVED (skipping IN_PROGRESS)
        assertThatThrownBy(() -> wishService.changeStatus(1L, WishStatus.ACHIEVED, testUser))
                .isInstanceOf(InvalidStatusTransitionException.class);
        verify(wishRepository, times(1)).findByIdAndUser(1L, testUser);
        verify(wishRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not allow backwards transition from ACHIEVED")
    void testChangeStatus_NoBackwardsTransition() {
        // Given
        testWish.setStatus(WishStatus.ACHIEVED);
        testWish.setAchievedDate(LocalDateTime.now());
        when(wishRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testWish));

        // When & Then - trying to go from ACHIEVED to IN_PROGRESS
        assertThatThrownBy(() -> wishService.changeStatus(1L, WishStatus.IN_PROGRESS, testUser))
                .isInstanceOf(InvalidStatusTransitionException.class);
        verify(wishRepository, times(1)).findByIdAndUser(1L, testUser);
        verify(wishRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return empty list when user has no wishes")
    void testFindAllByUser_Empty() {
        // Given
        when(wishRepository.findByUserOrderByCreatedDateDesc(testUser)).thenReturn(Arrays.asList());

        // When
        List<WishDTO> result = wishService.findAllByUser(testUser);

        // Then
        assertThat(result).isEmpty();
        verify(wishRepository, times(1)).findByUserOrderByCreatedDateDesc(testUser);
    }
}

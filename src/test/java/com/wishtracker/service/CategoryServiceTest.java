package com.wishtracker.service;

import com.wishtracker.dto.CategoryDTO;
import com.wishtracker.exception.CategoryHasWishesException;
import com.wishtracker.exception.CategoryNotFoundException;
import com.wishtracker.exception.DuplicateCategoryNameException;
import com.wishtracker.model.Category;
import com.wishtracker.model.User;
import com.wishtracker.repository.CategoryRepository;
import com.wishtracker.repository.WishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryService.
 * Tests all CRUD operations with user context.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private WishRepository wishRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User testUser;
    private Category testCategory;
    private CategoryDTO testCategoryDTO;

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
                .description("Travel plans and destinations")
                .color("#FF5733")
                .user(testUser)
                .build();

        // Create test DTO
        testCategoryDTO = CategoryDTO.builder()
                .name("Travel")
                .description("Travel plans and destinations")
                .color("#FF5733")
                .build();
    }

    @Test
    @DisplayName("Should find all categories for user ordered by name")
    void testFindAllByUser() {
        // Given
        Category category2 = Category.builder()
                .id(2L)
                .name("Books")
                .color("#3498db")
                .user(testUser)
                .build();

        List<Category> categories = Arrays.asList(testCategory, category2);
        when(categoryRepository.findByUserOrderByNameAsc(testUser)).thenReturn(categories);

        // When
        List<CategoryDTO> result = categoryService.findAllByUser(testUser);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Travel");
        assertThat(result.get(1).getName()).isEqualTo("Books");
        verify(categoryRepository, times(1)).findByUserOrderByNameAsc(testUser);
    }

    @Test
    @DisplayName("Should find category by ID and user")
    void testFindByIdAndUser() {
        // Given
        when(categoryRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testCategory));

        // When
        CategoryDTO result = categoryService.findByIdAndUser(1L, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Travel");
        assertThat(result.getDescription()).isEqualTo("Travel plans and destinations");
        assertThat(result.getColor()).isEqualTo("#FF5733");
        verify(categoryRepository, times(1)).findByIdAndUser(1L, testUser);
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when category not found")
    void testFindByIdAndUser_NotFound() {
        // Given
        when(categoryRepository.findByIdAndUser(999L, testUser)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.findByIdAndUser(999L, testUser))
                .isInstanceOf(CategoryNotFoundException.class);
        verify(categoryRepository, times(1)).findByIdAndUser(999L, testUser);
    }

    @Test
    @DisplayName("Should create new category successfully")
    void testCreate() {
        // Given
        when(categoryRepository.existsByNameAndUser("Travel", testUser)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        CategoryDTO result = categoryService.create(testCategoryDTO, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Travel");
        assertThat(result.getColor()).isEqualTo("#FF5733");
        verify(categoryRepository, times(1)).existsByNameAndUser("Travel", testUser);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw DuplicateCategoryNameException when name exists")
    void testCreate_DuplicateName() {
        // Given
        when(categoryRepository.existsByNameAndUser("Travel", testUser)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.create(testCategoryDTO, testUser))
                .isInstanceOf(DuplicateCategoryNameException.class);
        verify(categoryRepository, times(1)).existsByNameAndUser("Travel", testUser);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update category successfully")
    void testUpdate() {
        // Given
        CategoryDTO updateDTO = CategoryDTO.builder()
                .name("Travel Updated")
                .description("Updated description")
                .color("#00FF00")
                .build();

        when(categoryRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByNameAndUser("Travel Updated", testUser)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        CategoryDTO result = categoryService.update(1L, updateDTO, testUser);

        // Then
        assertThat(result).isNotNull();
        verify(categoryRepository, times(1)).findByIdAndUser(1L, testUser);
        verify(categoryRepository, times(1)).existsByNameAndUser("Travel Updated", testUser);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should update category with same name")
    void testUpdate_SameName() {
        // Given
        CategoryDTO updateDTO = CategoryDTO.builder()
                .name("Travel")  // Same name
                .description("Updated description")
                .color("#00FF00")
                .build();

        when(categoryRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        CategoryDTO result = categoryService.update(1L, updateDTO, testUser);

        // Then
        assertThat(result).isNotNull();
        verify(categoryRepository, times(1)).findByIdAndUser(1L, testUser);
        verify(categoryRepository, never()).existsByNameAndUser(anyString(), any());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw exception when updating to duplicate name")
    void testUpdate_DuplicateName() {
        // Given
        CategoryDTO updateDTO = CategoryDTO.builder()
                .name("Books")
                .description("Updated description")
                .color("#00FF00")
                .build();

        when(categoryRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByNameAndUser("Books", testUser)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.update(1L, updateDTO, testUser))
                .isInstanceOf(DuplicateCategoryNameException.class);
        verify(categoryRepository, times(1)).findByIdAndUser(1L, testUser);
        verify(categoryRepository, times(1)).existsByNameAndUser("Books", testUser);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete category successfully when no wishes")
    void testDelete() {
        // Given
        when(categoryRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testCategory));
        when(wishRepository.countByCategory(testCategory)).thenReturn(0L);
        doNothing().when(categoryRepository).delete(testCategory);

        // When
        categoryService.delete(1L, testUser);

        // Then
        verify(categoryRepository, times(1)).findByIdAndUser(1L, testUser);
        verify(wishRepository, times(1)).countByCategory(testCategory);
        verify(categoryRepository, times(1)).delete(testCategory);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent category")
    void testDelete_NotFound() {
        // Given
        when(categoryRepository.findByIdAndUser(999L, testUser)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.delete(999L, testUser))
                .isInstanceOf(CategoryNotFoundException.class);
        verify(categoryRepository, times(1)).findByIdAndUser(999L, testUser);
        verify(wishRepository, never()).countByCategory(any());
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw exception when deleting category with wishes")
    void testDelete_HasWishes() {
        // Given
        when(categoryRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testCategory));
        when(wishRepository.countByCategory(testCategory)).thenReturn(5L);

        // When & Then
        assertThatThrownBy(() -> categoryService.delete(1L, testUser))
                .isInstanceOf(CategoryHasWishesException.class)
                .hasMessageContaining("5");
        verify(categoryRepository, times(1)).findByIdAndUser(1L, testUser);
        verify(wishRepository, times(1)).countByCategory(testCategory);
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should get category entity by ID and user")
    void testGetCategoryEntity() {
        // Given
        when(categoryRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testCategory));

        // When
        Category result = categoryService.getCategoryEntity(1L, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testCategory);
        assertThat(result.getName()).isEqualTo("Travel");
        verify(categoryRepository, times(1)).findByIdAndUser(1L, testUser);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent category entity")
    void testGetCategoryEntity_NotFound() {
        // Given
        when(categoryRepository.findByIdAndUser(999L, testUser)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.getCategoryEntity(999L, testUser))
                .isInstanceOf(CategoryNotFoundException.class);
        verify(categoryRepository, times(1)).findByIdAndUser(999L, testUser);
    }

    @Test
    @DisplayName("Should return empty list when user has no categories")
    void testFindAllByUser_Empty() {
        // Given
        when(categoryRepository.findByUserOrderByNameAsc(testUser)).thenReturn(Arrays.asList());

        // When
        List<CategoryDTO> result = categoryService.findAllByUser(testUser);

        // Then
        assertThat(result).isEmpty();
        verify(categoryRepository, times(1)).findByUserOrderByNameAsc(testUser);
    }

    @Test
    @DisplayName("Should handle category with null description")
    void testCreate_NullDescription() {
        // Given
        CategoryDTO dtoWithNullDescription = CategoryDTO.builder()
                .name("Test")
                .description(null)
                .color("#FF0000")
                .build();

        Category savedCategory = Category.builder()
                .id(1L)
                .name("Test")
                .description(null)
                .color("#FF0000")
                .user(testUser)
                .build();

        when(categoryRepository.existsByNameAndUser("Test", testUser)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // When
        CategoryDTO result = categoryService.create(dtoWithNullDescription, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isNull();
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
}

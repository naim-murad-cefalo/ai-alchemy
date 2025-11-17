package com.wishtracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Wish entity.
 * Tests business logic and lifecycle methods.
 */
@DisplayName("Wish Entity Tests")
class WishTest {

    private User testUser;
    private Category testCategory;
    private Wish wish;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .build();

        testCategory = Category.builder()
                .id(1L)
                .name("Travel")
                .color("#FF5733")
                .user(testUser)
                .build();

        wish = Wish.builder()
                .title("Visit Japan")
                .description("Experience Tokyo and Kyoto")
                .status(WishStatus.WISH)
                .remarks("Need to save money")
                .category(testCategory)
                .user(testUser)
                .build();
    }

    @Test
    @DisplayName("Should create wish with all fields")
    void testCreateWish() {
        assertThat(wish.getTitle()).isEqualTo("Visit Japan");
        assertThat(wish.getDescription()).isEqualTo("Experience Tokyo and Kyoto");
        assertThat(wish.getStatus()).isEqualTo(WishStatus.WISH);
        assertThat(wish.getRemarks()).isEqualTo("Need to save money");
        assertThat(wish.getCategory()).isEqualTo(testCategory);
        assertThat(wish.getUser()).isEqualTo(testUser);
    }

    @Test
    @DisplayName("Should have default status as WISH")
    void testDefaultStatus() {
        Wish newWish = Wish.builder()
                .title("Test")
                .category(testCategory)
                .user(testUser)
                .build();

        assertThat(newWish.getStatus()).isEqualTo(WishStatus.WISH);
    }

    @Test
    @DisplayName("onCreate should set createdDate and updatedDate")
    void testOnCreate() {
        wish.onCreate();

        assertThat(wish.getCreatedDate()).isNotNull();
        assertThat(wish.getUpdatedDate()).isNotNull();
        assertThat(wish.getCreatedDate()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(wish.getUpdatedDate()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("onCreate should not override existing createdDate")
    void testOnCreateWithExistingDate() {
        LocalDateTime existingDate = LocalDateTime.now().minusDays(5);
        wish.setCreatedDate(existingDate);
        wish.onCreate();

        assertThat(wish.getCreatedDate()).isEqualTo(existingDate);
    }

    @Test
    @DisplayName("onUpdate should update updatedDate")
    void testOnUpdate() throws InterruptedException {
        wish.onCreate();
        LocalDateTime originalUpdatedDate = wish.getUpdatedDate();

        // Wait a bit to ensure different timestamp
        Thread.sleep(10);

        wish.onUpdate();

        assertThat(wish.getUpdatedDate()).isAfter(originalUpdatedDate);
    }

    @Test
    @DisplayName("updateStatus should change status")
    void testUpdateStatus() {
        wish.updateStatus(WishStatus.IN_PROGRESS);

        assertThat(wish.getStatus()).isEqualTo(WishStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("updateStatus to ACHIEVED should set achievedDate")
    void testUpdateStatusToAchievedSetsDate() {
        wish.updateStatus(WishStatus.ACHIEVED);

        assertThat(wish.getStatus()).isEqualTo(WishStatus.ACHIEVED);
        assertThat(wish.getAchievedDate()).isNotNull();
        assertThat(wish.getAchievedDate()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("updateStatus to ACHIEVED should not override existing achievedDate")
    void testUpdateStatusPreservesAchievedDate() {
        LocalDateTime existingAchievedDate = LocalDateTime.now().minusDays(3);
        wish.setAchievedDate(existingAchievedDate);
        wish.updateStatus(WishStatus.ACHIEVED);

        assertThat(wish.getAchievedDate()).isEqualTo(existingAchievedDate);
    }

    @Test
    @DisplayName("updateStatus to non-ACHIEVED should not set achievedDate")
    void testUpdateStatusToInProgressDoesNotSetAchievedDate() {
        wish.updateStatus(WishStatus.IN_PROGRESS);

        assertThat(wish.getStatus()).isEqualTo(WishStatus.IN_PROGRESS);
        assertThat(wish.getAchievedDate()).isNull();
    }

    @Test
    @DisplayName("isWish should return true for WISH status")
    void testIsWish() {
        wish.setStatus(WishStatus.WISH);
        assertThat(wish.isWish()).isTrue();
        assertThat(wish.isInProgress()).isFalse();
        assertThat(wish.isAchieved()).isFalse();
    }

    @Test
    @DisplayName("isInProgress should return true for IN_PROGRESS status")
    void testIsInProgress() {
        wish.setStatus(WishStatus.IN_PROGRESS);
        assertThat(wish.isWish()).isFalse();
        assertThat(wish.isInProgress()).isTrue();
        assertThat(wish.isAchieved()).isFalse();
    }

    @Test
    @DisplayName("isAchieved should return true for ACHIEVED status")
    void testIsAchieved() {
        wish.setStatus(WishStatus.ACHIEVED);
        assertThat(wish.isWish()).isFalse();
        assertThat(wish.isInProgress()).isFalse();
        assertThat(wish.isAchieved()).isTrue();
    }

    @Test
    @DisplayName("Should allow null description")
    void testNullDescription() {
        wish.setDescription(null);
        assertThat(wish.getDescription()).isNull();
    }

    @Test
    @DisplayName("Should allow null remarks")
    void testNullRemarks() {
        wish.setRemarks(null);
        assertThat(wish.getRemarks()).isNull();
    }

    @Test
    @DisplayName("Should allow empty description")
    void testEmptyDescription() {
        wish.setDescription("");
        assertThat(wish.getDescription()).isEmpty();
    }

    @Test
    @DisplayName("Should allow empty remarks")
    void testEmptyRemarks() {
        wish.setRemarks("");
        assertThat(wish.getRemarks()).isEmpty();
    }

    @Test
    @DisplayName("Builder should create wish correctly")
    void testBuilder() {
        Wish builtWish = Wish.builder()
                .id(100L)
                .title("Test Wish")
                .description("Test Description")
                .status(WishStatus.IN_PROGRESS)
                .remarks("Test Remarks")
                .category(testCategory)
                .user(testUser)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        assertThat(builtWish.getId()).isEqualTo(100L);
        assertThat(builtWish.getTitle()).isEqualTo("Test Wish");
        assertThat(builtWish.getDescription()).isEqualTo("Test Description");
        assertThat(builtWish.getStatus()).isEqualTo(WishStatus.IN_PROGRESS);
        assertThat(builtWish.getRemarks()).isEqualTo("Test Remarks");
        assertThat(builtWish.getCategory()).isEqualTo(testCategory);
        assertThat(builtWish.getUser()).isEqualTo(testUser);
    }

    @Test
    @DisplayName("Should update wish fields")
    void testUpdateFields() {
        wish.setTitle("Updated Title");
        wish.setDescription("Updated Description");
        wish.setRemarks("Updated Remarks");

        assertThat(wish.getTitle()).isEqualTo("Updated Title");
        assertThat(wish.getDescription()).isEqualTo("Updated Description");
        assertThat(wish.getRemarks()).isEqualTo("Updated Remarks");
    }

    @Test
    @DisplayName("Should handle status workflow correctly")
    void testStatusWorkflow() {
        // Start as WISH
        assertThat(wish.getStatus()).isEqualTo(WishStatus.WISH);
        assertThat(wish.getAchievedDate()).isNull();

        // Move to IN_PROGRESS
        wish.updateStatus(WishStatus.IN_PROGRESS);
        assertThat(wish.getStatus()).isEqualTo(WishStatus.IN_PROGRESS);
        assertThat(wish.getAchievedDate()).isNull();

        // Move to ACHIEVED
        wish.updateStatus(WishStatus.ACHIEVED);
        assertThat(wish.getStatus()).isEqualTo(WishStatus.ACHIEVED);
        assertThat(wish.getAchievedDate()).isNotNull();
    }
}

package com.wishtracker.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for WishStatus enum.
 * Tests status transitions and workflow rules.
 */
@DisplayName("WishStatus Tests")
class WishStatusTest {

    @Test
    @DisplayName("Should have correct display names")
    void testDisplayNames() {
        assertThat(WishStatus.WISH.getDisplayName()).isEqualTo("Wish");
        assertThat(WishStatus.IN_PROGRESS.getDisplayName()).isEqualTo("In Progress");
        assertThat(WishStatus.ACHIEVED.getDisplayName()).isEqualTo("Achieved");
    }

    @Test
    @DisplayName("WISH can transition to IN_PROGRESS")
    void testWishToInProgress() {
        assertThat(WishStatus.WISH.canTransitionTo(WishStatus.IN_PROGRESS)).isTrue();
    }

    @Test
    @DisplayName("WISH cannot transition to ACHIEVED")
    void testWishCannotTransitionToAchieved() {
        assertThat(WishStatus.WISH.canTransitionTo(WishStatus.ACHIEVED)).isFalse();
    }

    @Test
    @DisplayName("WISH cannot stay in WISH status")
    void testWishCannotStayWish() {
        assertThat(WishStatus.WISH.canTransitionTo(WishStatus.WISH)).isFalse();
    }

    @Test
    @DisplayName("IN_PROGRESS can transition to ACHIEVED")
    void testInProgressToAchieved() {
        assertThat(WishStatus.IN_PROGRESS.canTransitionTo(WishStatus.ACHIEVED)).isTrue();
    }

    @Test
    @DisplayName("IN_PROGRESS cannot go back to WISH")
    void testInProgressCannotGoBackToWish() {
        assertThat(WishStatus.IN_PROGRESS.canTransitionTo(WishStatus.WISH)).isFalse();
    }

    @Test
    @DisplayName("IN_PROGRESS cannot stay in IN_PROGRESS status")
    void testInProgressCannotStayInProgress() {
        assertThat(WishStatus.IN_PROGRESS.canTransitionTo(WishStatus.IN_PROGRESS)).isFalse();
    }

    @Test
    @DisplayName("ACHIEVED cannot transition to any status")
    void testAchievedCannotTransition() {
        assertThat(WishStatus.ACHIEVED.canTransitionTo(WishStatus.WISH)).isFalse();
        assertThat(WishStatus.ACHIEVED.canTransitionTo(WishStatus.IN_PROGRESS)).isFalse();
        assertThat(WishStatus.ACHIEVED.canTransitionTo(WishStatus.ACHIEVED)).isFalse();
    }

    @Test
    @DisplayName("WISH next status should be IN_PROGRESS")
    void testWishNextStatus() {
        assertThat(WishStatus.WISH.getNextStatus()).isEqualTo(WishStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("IN_PROGRESS next status should be ACHIEVED")
    void testInProgressNextStatus() {
        assertThat(WishStatus.IN_PROGRESS.getNextStatus()).isEqualTo(WishStatus.ACHIEVED);
    }

    @Test
    @DisplayName("ACHIEVED next status should be null")
    void testAchievedNextStatus() {
        assertThat(WishStatus.ACHIEVED.getNextStatus()).isNull();
    }

    @ParameterizedTest
    @CsvSource({
        "WISH, IN_PROGRESS, true",
        "WISH, ACHIEVED, false",
        "WISH, WISH, false",
        "IN_PROGRESS, ACHIEVED, true",
        "IN_PROGRESS, WISH, false",
        "IN_PROGRESS, IN_PROGRESS, false",
        "ACHIEVED, WISH, false",
        "ACHIEVED, IN_PROGRESS, false",
        "ACHIEVED, ACHIEVED, false"
    })
    @DisplayName("Should validate all status transitions")
    void testAllStatusTransitions(WishStatus from, WishStatus to, boolean expected) {
        assertThat(from.canTransitionTo(to)).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should have exactly 3 status values")
    void testStatusCount() {
        assertThat(WishStatus.values()).hasSize(3);
    }

    @Test
    @DisplayName("Should be able to convert from string")
    void testValueOf() {
        assertThat(WishStatus.valueOf("WISH")).isEqualTo(WishStatus.WISH);
        assertThat(WishStatus.valueOf("IN_PROGRESS")).isEqualTo(WishStatus.IN_PROGRESS);
        assertThat(WishStatus.valueOf("ACHIEVED")).isEqualTo(WishStatus.ACHIEVED);
    }

    @Test
    @DisplayName("Should throw exception for invalid status string")
    void testInvalidValueOf() {
        assertThatThrownBy(() -> WishStatus.valueOf("INVALID"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

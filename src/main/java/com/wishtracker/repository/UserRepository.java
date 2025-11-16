package com.wishtracker.repository;

import com.wishtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity operations.
 *
 * Users are identified by their email (from Google OAuth).
 * Email is the principal name used throughout the application for authentication.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their email address.
     * Email is the unique identifier from Google OAuth.
     *
     * @param email the user's email address
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given email address.
     * Used during OAuth login to determine if this is a new or existing user.
     *
     * @param email the user's email address
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
}

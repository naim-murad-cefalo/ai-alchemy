package com.wishtracker.service;

import com.wishtracker.exception.UserNotFoundException;
import com.wishtracker.model.User;
import com.wishtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for managing User entities.
 * Handles user creation during OAuth login and retrieval of current authenticated user.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Find a user by email address.
     *
     * @param email the user's email
     * @return Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find or create a user based on OAuth2 login information.
     * Called during Google OAuth2 authentication flow.
     * If user exists: updates lastLoginDate
     * If user doesn't exist: creates new user with OAuth data
     *
     * @param email      user's email from Google OAuth
     * @param name       user's name from Google OAuth
     * @param pictureUrl user's profile picture URL from Google OAuth
     * @return the found or created user
     */
    @Transactional
    public User findOrCreateUser(String email, String name, String pictureUrl) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            log.debug("User found with email: {}, updating last login date", email);
            User user = existingUser.get();
            updateLastLogin(user);
            return user;
        } else {
            log.info("Creating new user with email: {}", email);
            User newUser = User.builder()
                    .email(email)
                    .name(name)
                    .pictureUrl(pictureUrl)
                    .createdDate(LocalDateTime.now())
                    .lastLoginDate(LocalDateTime.now())
                    .build();
            return userRepository.save(newUser);
        }
    }

    /**
     * Get the currently authenticated user from the security context.
     * This is the primary method for services and controllers to get the current user.
     *
     * @return the current authenticated user
     * @throws UserNotFoundException if user is not authenticated or not found in database
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotFoundException("No authenticated user found");
        }

        String email;
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User oauth2User) {
            email = oauth2User.getAttribute("email");
        } else if (principal instanceof String) {
            email = (String) principal;
        } else {
            throw new UserNotFoundException("Unable to extract email from authentication principal");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * Update the last login date for a user.
     *
     * @param user the user to update
     */
    @Transactional
    public void updateLastLogin(User user) {
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Update the last login date for a user by email.
     *
     * @param email the user's email
     */
    @Transactional
    public void updateLastLogin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        updateLastLogin(user);
    }
}

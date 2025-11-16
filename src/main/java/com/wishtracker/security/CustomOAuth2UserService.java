package com.wishtracker.security;

import com.wishtracker.model.User;
import com.wishtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Custom OAuth2 User Service that handles automatic user creation on first login.
 *
 * This service intercepts the OAuth2 authentication flow and:
 * 1. Extracts user information from Google OAuth2 response
 * 2. Creates a new User entity if this is the first login
 * 3. Updates lastLoginDate if user already exists
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    /**
     * Load OAuth2 user and create/update User entity.
     *
     * @param userRequest the OAuth2 user request
     * @return the OAuth2 user
     * @throws OAuth2AuthenticationException if authentication fails
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Call parent to get OAuth2User with attributes from Google
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            // Extract user information from OAuth2 attributes
            Map<String, Object> attributes = oauth2User.getAttributes();
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String pictureUrl = (String) attributes.get("picture");

            log.info("OAuth2 login attempt for email: {}", email);

            // Validate required attributes
            if (email == null || email.isBlank()) {
                log.error("Email not found in OAuth2 attributes");
                throw new OAuth2AuthenticationException("Email not found in OAuth2 user info");
            }

            if (name == null || name.isBlank()) {
                name = email.split("@")[0]; // Use email prefix as fallback
            }

            // Find or create user
            User user = userService.findOrCreateUser(email, name, pictureUrl);
            log.info("User authenticated: {} (ID: {})", user.getEmail(), user.getId());

            return oauth2User;

        } catch (Exception e) {
            log.error("Error processing OAuth2 user", e);
            throw new OAuth2AuthenticationException(e.getMessage());
        }
    }
}

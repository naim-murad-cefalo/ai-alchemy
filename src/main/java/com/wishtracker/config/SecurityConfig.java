package com.wishtracker.config;

import com.wishtracker.security.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Wish Tracker application.
 *
 * Configures:
 * - Google OAuth2 authentication (ONLY authentication method)
 * - URL authorization (public vs authenticated)
 * - H2 console access (development only)
 * - Logout behavior
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    /**
     * Configure security filter chain.
     *
     * @param http the HttpSecurity to configure
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Authorization rules
            .authorizeHttpRequests(authorize -> authorize
                // Public endpoints (no authentication required)
                .requestMatchers(
                    "/",
                    "/login",
                    "/error",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/h2-console/**"  // H2 console for development
                ).permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )

            // OAuth2 login configuration
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/wishes", true)
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
            )

            // Logout configuration
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )

            // H2 Console configuration (development only)
            // Disable CSRF and frame options for H2 console
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions
                    .sameOrigin()  // Allow H2 console in frames
                )
            );

        return http.build();
    }
}

package com.wishtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Wish Tracker.
 * A wish tracking application with Kanban board visualization and Google SSO authentication.
 *
 * Features:
 * - User-scoped wish and category management
 * - Google OAuth2 authentication
 * - Kanban board for wish status tracking (Wish → In Progress → Achieved)
 * - Category-based organization with custom colors
 */
@SpringBootApplication
public class WishTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WishTrackerApplication.class, args);
    }
}

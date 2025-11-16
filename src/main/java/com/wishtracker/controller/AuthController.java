package com.wishtracker.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for authentication-related pages.
 * Handles login page display and home redirect.
 */
@Controller
public class AuthController {

    /**
     * Root path handler.
     * Redirects to /wishes if authenticated, otherwise to /login.
     *
     * @param authentication the current authentication (null if not authenticated)
     * @return redirect path
     */
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/wishes";
        }
        return "redirect:/login";
    }

    /**
     * Display login page.
     * Shows Google sign-in button.
     *
     * @return login template name
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

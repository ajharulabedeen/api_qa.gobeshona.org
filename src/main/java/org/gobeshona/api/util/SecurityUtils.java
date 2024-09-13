package org.gobeshona.api.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    public static String getCurrentUsername() {
        // Get the authentication object from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authentication is not null and if it has a valid principal
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                // If principal is an instance of UserDetails, get the username
                return ((UserDetails) principal).getUsername();
            } else {
                // In case the principal is a String (like when using JWTs), it's the username
                return principal.toString();
            }
        }

        // Return null or throw an exception if the username is not found
        return null;
    }
}

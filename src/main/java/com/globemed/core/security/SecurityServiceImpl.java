package com.globemed.core.security;

import com.globemed.core.util.Logger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SecurityServiceImpl implements SecurityService {
    private static final Logger log = Logger.getLogger(SecurityServiceImpl.class);
    private String currentUser;

    // Thread-safe maps for storing user credentials and roles
    private final Map<String, String> userCredentials;
    private final Map<String, Set<Role>> userRoles;

    public SecurityServiceImpl() {
        this.userCredentials = new ConcurrentHashMap<>();
        this.userRoles = new ConcurrentHashMap<>();
        initializeUsers();
    }

    private void initializeUsers() {
        // Admin user
        userCredentials.put("admin", "admin123");
        userRoles.put("admin", new HashSet<>(Collections.singletonList(Role.ADMIN)));

        // Doctor users
        userCredentials.put("dr.smith", "doctor123");
        userRoles.put("dr.smith", new HashSet<>(Collections.singletonList(Role.DOCTOR)));

        userCredentials.put("dr.jones", "doctor456");
        userRoles.put("dr.jones", new HashSet<>(Collections.singletonList(Role.DOCTOR)));

        // Nurse users
        userCredentials.put("nurse1", "nurse123");
        userRoles.put("nurse1", new HashSet<>(Collections.singletonList(Role.NURSE)));

        userCredentials.put("nurse2", "nurse456");
        userRoles.put("nurse2", new HashSet<>(Collections.singletonList(Role.NURSE)));

        // Front desk users
        userCredentials.put("desk1", "desk123");
        userRoles.put("desk1", new HashSet<>(Collections.singletonList(Role.RECEPTIONIST)));

        log.info("Initialized {} users in the system", userCredentials.size());
    }

    @Override
    public boolean hasAccess(String userId, String resourceId, Set<Permission> requiredPermissions) {
        log.info("Checking access for user {} on resource {}", userId, resourceId);
        try {
            return hasRole(Role.ADMIN) || hasRole(Role.DOCTOR); // Simplified for now
        } catch (Exception e) {
            log.error("Error checking access for user {} on resource {}", userId, resourceId);
            return false;
        }
    }

    @Override
    public void checkAccess(String userId, String resourceId, Set<Permission> requiredPermissions) throws SecurityException {
        log.info("Enforcing access check for user {} on resource {}", userId, resourceId);
        if (!hasAccess(userId, resourceId, requiredPermissions)) {
            log.error("Access denied for user {} on resource {}", userId, resourceId);
            throw new SecurityException("Access denied");
        }
    }

    @Override
    public boolean authenticate(String username, String password) {
        log.info("Authentication attempt for user: {}", username);
        try {
            if (!userCredentials.containsKey(username)) {
                log.error("Authentication failed: User {} not found", username);
                return false;
            }

            if (!userCredentials.get(username).equals(password)) {
                log.error("Authentication failed: Invalid password for user {}", username);
                return false;
            }

            currentUser = username;
            log.info("Authentication successful for user: {}", username);
            return true;
        } catch (Exception e) {
            log.error("Authentication error for user: {}", username, e);
            return false;
        }
    }

    @Override
    public String getCurrentUser() {
        return currentUser != null ? currentUser : "anonymous";
    }

    @Override
    public boolean hasRole(Role role) {
        if (role == null || getCurrentUser().equals("anonymous")) {
            return false;
        }

        try {
            Set<Role> roles = userRoles.get(getCurrentUser());
            if (roles == null) {
                return false;
            }

            // Admin has all roles
            return roles.contains(Role.ADMIN) || roles.contains(role);
        } catch (Exception e) {
            log.error("Error checking role {} for user {}", role, getCurrentUser(), e);
            return false;
        }
    }
}

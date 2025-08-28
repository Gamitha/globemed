package com.globemed.core.security;

import com.globemed.core.util.Logger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SecurityServiceImpl implements SecurityService {
    private static final Logger log = Logger.getLogger(SecurityServiceImpl.class);
    private static final String SYSTEM_USER = "system";
    private String currentUser;
    private final Map<String, String> userCredentials = new ConcurrentHashMap<>();
    private final Map<String, Set<Role>> userRoles = new ConcurrentHashMap<>();
    private final Map<String, Set<Permission>> userPermissions = new ConcurrentHashMap<>();

    // Store original permissions for restoration
    private final Map<String, Set<Permission>> originalPermissions = new ConcurrentHashMap<>();

    public SecurityServiceImpl() {
        // Initialize system user first
        initializeSystemUser();
        // Set system context for initialization
        currentUser = SYSTEM_USER;
        // Initialize other users
        initializeUsers();
    }

    private void initializeSystemUser() {
        userCredentials.put(SYSTEM_USER, UUID.randomUUID().toString());
        userRoles.put(SYSTEM_USER, new HashSet<>(Collections.singleton(Role.ADMIN)));
        userPermissions.put(SYSTEM_USER, new HashSet<>(Arrays.asList(Permission.values())));
    }

    private void initializeUsers() {
        // Initialize admin user
        userCredentials.put("admin", "admin123");
        userRoles.put("admin", new HashSet<>(Collections.singleton(Role.ADMIN)));
        userPermissions.put("admin", new HashSet<>(Arrays.asList(Permission.values())));

        // Initialize doctor users
        initializeDoctor("dr.smith", "doctor123");
        initializeDoctor("dr.jones", "doctor456");

        // Initialize nurse users
        initializeNurse("nurse1", "nurse123");
        initializeNurse("nurse2", "nurse456");

        // Initialize receptionist users
        initializeReceptionist("desk1", "desk123");

        log.info("Initialized {} users in the system", userCredentials.size());
    }

    private void initializeDoctor(String username, String password) {
        userCredentials.put(username, password);
        userRoles.put(username, new HashSet<>(Collections.singleton(Role.DOCTOR)));
        userPermissions.put(username, new HashSet<>(Arrays.asList(
            Permission.READ, Permission.WRITE,
            Permission.READ_PATIENT, Permission.WRITE_PATIENT,
            Permission.READ_APPOINTMENT, Permission.WRITE_APPOINTMENT,
            Permission.READ_BILLING
        )));
    }

    private void initializeNurse(String username, String password) {
        userCredentials.put(username, password);
        userRoles.put(username, new HashSet<>(Collections.singleton(Role.NURSE)));
        userPermissions.put(username, new HashSet<>(Arrays.asList(
            Permission.READ, Permission.WRITE,
            Permission.READ_PATIENT, Permission.WRITE_PATIENT,
            Permission.READ_APPOINTMENT, Permission.WRITE_APPOINTMENT
        )));
    }

    private void initializeReceptionist(String username, String password) {
        userCredentials.put(username, password);
        userRoles.put(username, new HashSet<>(Collections.singleton(Role.RECEPTIONIST)));
        userPermissions.put(username, new HashSet<>(Arrays.asList(
            Permission.READ,
            Permission.READ_APPOINTMENT, Permission.WRITE_APPOINTMENT
        )));
    }

    @Override
    public boolean hasAccess(String userId, String resourceId, Set<Permission> requiredPermissions) {
        if (currentUser == null || !currentUser.equals(userId)) {
            return false;
        }

        Set<Permission> userPerms = userPermissions.getOrDefault(userId, Collections.emptySet());
        return userPerms.containsAll(requiredPermissions);
    }

    @Override
    public void checkAccess(String userId, String resourceId, Set<Permission> requiredPermissions) {
        if (!hasAccess(userId, resourceId, requiredPermissions)) {
            throw new SecurityException("Access denied for user: " + userId);
        }
    }

    @Override
    public boolean hasPermission(Permission permission) {
        if (currentUser == null) {
            return false;
        }
        Set<Permission> userPerms = userPermissions.getOrDefault(currentUser, Collections.emptySet());
        return userPerms.contains(permission) || hasRole(Role.ADMIN);
    }

    @Override
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        String storedPassword = userCredentials.get(username);
        if (storedPassword != null && storedPassword.equals(password)) {
            currentUser = username;
            log.info("User authenticated successfully: {}", username);
            return true;
        }

        log.warn("Authentication failed for user: {}", username);
        return false;
    }

    @Override
    public String getCurrentUser() {
        return currentUser != null ? currentUser : SYSTEM_USER;
    }

    @Override
    public boolean hasRole(Role role) {
        if (role == null || currentUser == null) {
            return false;
        }

        Set<Role> roles = userRoles.get(currentUser);
        return roles != null && (roles.contains(role) || roles.contains(Role.ADMIN));
    }

    @Override
    public void elevatePermissions(Permission permission) {
        String user = getCurrentUser();
        if (user == null) {
            throw new SecurityException("No user logged in");
        }

        // Store original permissions if not already stored
        if (!originalPermissions.containsKey(user)) {
            Set<Permission> current = userPermissions.getOrDefault(user, new HashSet<>());
            originalPermissions.put(user, new HashSet<>(current));
        }

        // Add the new permission
        Set<Permission> elevated = new HashSet<>(userPermissions.getOrDefault(user, new HashSet<>()));
        elevated.add(permission);
        userPermissions.put(user, elevated);

        log.info("Temporarily elevated permissions for user: {} with permission: {}", user, permission);
    }

    @Override
    public void resetPermissions() {
        String user = getCurrentUser();
        if (user == null) {
            throw new SecurityException("No user logged in");
        }

        // Restore original permissions if they exist
        Set<Permission> original = originalPermissions.remove(user);
        if (original != null) {
            userPermissions.put(user, original);
            log.info("Reset permissions for user: {}", user);
        }
    }

    public void setSystemContext() {
        this.currentUser = SYSTEM_USER;
    }

    public void clearSystemContext() {
        if (SYSTEM_USER.equals(currentUser)) {
            this.currentUser = null;
        }
    }
}

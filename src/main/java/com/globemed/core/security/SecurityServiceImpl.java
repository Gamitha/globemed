package com.globemed.core.security;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SecurityServiceImpl implements SecurityService {
    private Map<String, Set<Role>> userRoles;
    private Map<String, String> credentials;
    private String currentUser;
    
    private static final Map<Role, Set<Permission>> ROLE_PERMISSIONS = new HashMap<>();
    
    static {
        // Initialize role permissions
        ROLE_PERMISSIONS.put(Role.DOCTOR, EnumSet.of(
            Permission.READ, Permission.WRITE
        ));
        ROLE_PERMISSIONS.put(Role.NURSE, EnumSet.of(
            Permission.READ, Permission.WRITE
        ));
        ROLE_PERMISSIONS.put(Role.ADMIN, EnumSet.of(
            Permission.READ, Permission.WRITE, Permission.DELETE, Permission.ADMIN
        ));
        ROLE_PERMISSIONS.put(Role.RECEPTIONIST, EnumSet.of(
            Permission.READ
        ));
        ROLE_PERMISSIONS.put(Role.PHARMACIST, EnumSet.of(
            Permission.READ
        ));
    }
    
    public SecurityServiceImpl() {
        this.userRoles = new ConcurrentHashMap<>();
        this.credentials = new ConcurrentHashMap<>();
        
        // Add demo users with roles
        addUser("admin", "admin123", EnumSet.of(Role.ADMIN));
        addUser("doctor", "doctor123", EnumSet.of(Role.DOCTOR));
        addUser("nurse", "nurse123", EnumSet.of(Role.NURSE));
    }
    
    private void addUser(String username, String password, Set<Role> roles) {
        credentials.put(username, password);
        userRoles.put(username, roles);
    }

    @Override
    public boolean authenticate(String username, String password) {
        if (credentials.containsKey(username) && 
            credentials.get(username).equals(password)) {
            currentUser = username;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasAccess(String userId, String resourceId, Set<Permission> requiredPermissions) {
        if (!userRoles.containsKey(userId)) {
            return false;
        }

        Set<Permission> userPermissions = new HashSet<>();
        for (Role role : userRoles.get(userId)) {
            userPermissions.addAll(ROLE_PERMISSIONS.get(role));
        }

        return userPermissions.containsAll(requiredPermissions);
    }

    @Override
    public void checkAccess(String userId, String resourceId, Set<Permission> requiredPermissions) 
            throws SecurityException {
        if (!hasAccess(userId, resourceId, requiredPermissions)) {
            throw new SecurityException("Access denied for user: " + userId);
        }
    }

    @Override
    public String getCurrentUser() {
        return currentUser;
    }
}

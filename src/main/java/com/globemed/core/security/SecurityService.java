package com.globemed.core.security;

import java.util.Set;

public interface SecurityService {
    boolean hasAccess(String userId, String resourceId, Set<Permission> requiredPermissions);
    void checkAccess(String userId, String resourceId, Set<Permission> requiredPermissions) throws SecurityException;
    boolean authenticate(String username, String password);
    String getCurrentUser();
    boolean hasRole(Role role);  // Added method to check user roles
}

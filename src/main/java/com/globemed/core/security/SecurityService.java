package com.globemed.core.security;

import java.util.Set;

public interface SecurityService {
    boolean hasAccess(String userId, String resourceId, Set<Permission> requiredPermissions);
    void checkAccess(String userId, String resourceId, Set<Permission> requiredPermissions) throws SecurityException;
    boolean hasPermission(Permission permission);
    boolean authenticate(String username, String password);
    String getCurrentUser();
    boolean hasRole(Role role);

    /**
     * Temporarily elevates the current user's permissions.
     * Should only be used for system initialization tasks.
     * @param permission The permission to temporarily grant
     */
    void elevatePermissions(Permission permission);

    /**
     * Resets permissions to their original state after elevation.
     */
    void resetPermissions();

    /**
     * Sets the security context to system user for initialization tasks
     */
    void setSystemContext();

    /**
     * Clears the system context and reverts to normal user context
     */
    void clearSystemContext();
}

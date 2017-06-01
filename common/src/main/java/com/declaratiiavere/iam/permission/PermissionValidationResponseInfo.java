package com.declaratiiavere.iam.permission;

/**
 * Indicates if a permission is valid.
 *
 * @author Razvan Dani
 */
public class PermissionValidationResponseInfo {
    private Boolean isPermissionValid;

    public Boolean getIsPermissionValid() {
        return isPermissionValid;
    }

    public void setIsPermissionValid(Boolean permissionValid) {
        isPermissionValid = permissionValid;
    }
}

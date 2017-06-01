package com.declaratiiavere.iam.permission;

/**
 * @author Viorel Vesa.
 */
public class RolePermissionInfo {

    private Integer roleId;
    private Integer permissionId;

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public Integer getPermissionId() {
        return permissionId;
    }
}

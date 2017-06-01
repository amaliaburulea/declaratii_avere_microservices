package com.declaratiiavere.iam.user;

/**
 * Encapsulates role information.
 *
 * @author Razvan Dani
 */
public class RoleInfo {
    private Integer roleId;
    private String roleName;
    private String roleDesc;
    private Boolean isDeleted;
    private Boolean isPredefinedRole; // should not set when saving

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getIsPredefinedRole() {
        return isPredefinedRole;
    }

    public void setIsPredefinedRole(Boolean predefinedRole) {
        isPredefinedRole = predefinedRole;
    }
}

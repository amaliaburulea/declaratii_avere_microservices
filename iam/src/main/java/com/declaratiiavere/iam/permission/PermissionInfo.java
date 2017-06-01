package com.declaratiiavere.iam.permission;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

/**
 * @author Viorel Vesa.
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PermissionInfo {

    private Integer permissionId;
    private String permissionCode;
    private String permissionName;
    private String permissionDesc;
    private Boolean isDeleted;
    private Set<PermissionRestMethodInfo> permissionRestMethods;
    private Set<Integer> roleIds;

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionDesc(String permissionDesc) {
        this.permissionDesc = permissionDesc;
    }

    public String getPermissionDesc() {
        return permissionDesc;
    }

    public Set<PermissionRestMethodInfo> getPermissionRestMethods() {
        return permissionRestMethods;
    }

    public void setPermissionRestMethods(Set<PermissionRestMethodInfo> permissionRestMethods) {
        this.permissionRestMethods = permissionRestMethods;
    }

    public Set<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Integer> roleIds) {
        this.roleIds = roleIds;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}

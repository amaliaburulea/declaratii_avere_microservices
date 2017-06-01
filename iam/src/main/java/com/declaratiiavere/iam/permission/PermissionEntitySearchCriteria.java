package com.declaratiiavere.iam.permission;

import com.declaratiiavere.jpaframework.EntitySearchCriteria;

/**
 * Entity search criteria for permissions.
 *
 * @author Razan Dani
 */
public class PermissionEntitySearchCriteria extends EntitySearchCriteria {
    private Integer roleId;
    private String restRequestPath;
    private String restRequestMethod;
    private String permissionName;
    private String permissionCode;
    private Boolean searchOnlyNotDeleted = true;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRestRequestPath() {
        return restRequestPath;
    }

    public void setRestRequestPath(String restRequestPath) {
        this.restRequestPath = restRequestPath;
    }

    public String getRestRequestMethod() {
        return restRequestMethod;
    }

    public void setRestRequestMethod(String restRequestMethod) {
        this.restRequestMethod = restRequestMethod;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public Boolean getSearchOnlyNotDeleted() {
        return searchOnlyNotDeleted;
    }

    public void setSearchOnlyNotDeleted(Boolean searchOnlyNotDeleted) {
        this.searchOnlyNotDeleted = searchOnlyNotDeleted;
    }
}

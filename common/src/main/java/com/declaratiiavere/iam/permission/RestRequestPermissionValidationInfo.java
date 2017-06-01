package com.declaratiiavere.iam.permission;

/**
 * Contains information needed in order to validate permissions for a REST request.
 */
public class RestRequestPermissionValidationInfo {
    private Integer roleId;
    private String restRequestPath;
    private String restRequestMethod;

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
}

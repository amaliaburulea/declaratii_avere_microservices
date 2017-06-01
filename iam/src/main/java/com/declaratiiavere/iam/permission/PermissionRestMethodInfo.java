package com.declaratiiavere.iam.permission;

/**
 * @author Viorel Vesa.
 */
public class PermissionRestMethodInfo {

    private String restRequestPath;
    private String restRequestMethod;

    public PermissionRestMethodInfo() {
    }

    public PermissionRestMethodInfo(String restRequestPath, String restRequestMethod) {
        this.restRequestPath = restRequestPath;
        this.restRequestMethod = restRequestMethod;
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

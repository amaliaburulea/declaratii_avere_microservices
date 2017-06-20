package com.declaratiiavere.iam.user;

import java.util.Date;

/**
 * Encapsulates user information.
 *
 * @author Razvan Dani
 */
public class UserInfo {
    private static final Integer ROLE_ID_SUPER_USER = 1;
    private static final Integer ROLE_ID_ORGANIZER = 2;
    private static final Integer ROLE_ID_VOLUNTEER = 3;

    private Integer userId;
    private String username;
    private String password;
    private String tempPassword;
    private Integer roleId;
    private Boolean isActive;
    private String email;
    private Date createdDttm;
    private Date lastLoginDttm;
    private boolean mustChangeTemporaryPassword;
    private String errorMessage;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedDttm() {
        return createdDttm;
    }

    public void setCreatedDttm(Date createdDttm) {
        this.createdDttm = createdDttm;
    }

    public Date getLastLoginDttm() {
        return lastLoginDttm;
    }

    public void setLastLoginDttm(Date lastLoginDttm) {
        this.lastLoginDttm = lastLoginDttm;
    }

    public Boolean getMustChangeTemporaryPassword() {
        return mustChangeTemporaryPassword;
    }

    public void setMustChangeTemporaryPassword(Boolean mustChangeTemporaryPassword) {
        this.mustChangeTemporaryPassword = mustChangeTemporaryPassword;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuperUser() {
        return roleId.equals(ROLE_ID_SUPER_USER);
    }

    public boolean isOrganizer() {
        return roleId.equals(ROLE_ID_ORGANIZER);
    }

    public boolean isVolunteer() {
        return roleId.equals(ROLE_ID_VOLUNTEER);
    }
}

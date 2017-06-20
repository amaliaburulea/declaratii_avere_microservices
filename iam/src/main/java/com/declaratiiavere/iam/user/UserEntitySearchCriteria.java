package com.declaratiiavere.iam.user;

import com.declaratiiavere.jpaframework.EntitySearchCriteria;

import java.util.List;

/**
 * Entity search criteria for users.
 *
 * @author Razvan Dani
 */
public class UserEntitySearchCriteria extends EntitySearchCriteria {
    private String userName;
    private String email;
    private List<Integer> userIdList;
    private Boolean searchOnlyActive = true;
    private Integer roleId;

    public Boolean getSearchOnlyActive() {
        return searchOnlyActive;
    }

    public void setSearchOnlyActive(Boolean searchOnlyActive) {
        this.searchOnlyActive = searchOnlyActive;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Integer> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Integer> userIdList) {
        this.userIdList = userIdList;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}

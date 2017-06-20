package com.declaratiiavere.iam.user;

import java.util.List;

/**
 * Search criteria class for users.
 *
 * @author Razvan Dani
 */
public class SearchUserCriteria {
    private Boolean searchOnlyNotDeleted = true;
    private List<Integer> userIdList;
    private Integer roleId;

    public Boolean getSearchOnlyNotDeleted() {
        return searchOnlyNotDeleted;
    }

    public void setSearchOnlyNotDeleted(Boolean searchOnlyNotDeleted) {
        this.searchOnlyNotDeleted = searchOnlyNotDeleted;
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

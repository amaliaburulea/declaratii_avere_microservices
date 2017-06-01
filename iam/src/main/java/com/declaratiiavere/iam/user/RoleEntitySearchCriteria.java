package com.declaratiiavere.iam.user;

import com.declaratiiavere.jpaframework.EntitySearchCriteria;

import java.util.Set;

/**
 * Entity search criteria for roles.
 *
 * @author Razvan Dani
 */
public class RoleEntitySearchCriteria extends EntitySearchCriteria {
    private boolean searchOnlyNotDeleted;

    private Set<Integer> roleIdSet;

    public boolean isSearchOnlyNotDeleted() {
        return searchOnlyNotDeleted;
    }

    public void setSearchOnlyNotDeleted(boolean searchOnlyNotDeleted) {
        this.searchOnlyNotDeleted = searchOnlyNotDeleted;
    }

    public Set<Integer> getRoleIdSet() {
        return roleIdSet;
    }

    public void setRoleIdSet(Set<Integer> roleIdSet) {
        this.roleIdSet = roleIdSet;
    }
}

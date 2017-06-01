package com.declaratiiavere.iam.user;

/**
 * Criteria class for searching roles.
 *
 * @author Razvan Dani
 */
public class SearchRoleCriteria {
    private boolean searchOnlyNotDeleted;

    public boolean isSearchOnlyNotDeleted() {
        return searchOnlyNotDeleted;
    }

    public void setSearchOnlyNotDeleted(boolean searchOnlyNotDeleted) {
        this.searchOnlyNotDeleted = searchOnlyNotDeleted;
    }
}

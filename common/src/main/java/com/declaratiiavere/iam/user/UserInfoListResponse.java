package com.declaratiiavere.iam.user;

import java.util.List;

/**
 * Response object containing user info list.
 *
 * @author Razvan Dani
 */
public class UserInfoListResponse {
    private List<UserInfo> userInfoList;

    public UserInfoListResponse() {
    }

    public UserInfoListResponse(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }

    public void setUserInfoList(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }

    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }
}

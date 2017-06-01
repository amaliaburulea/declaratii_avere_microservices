package com.declaratiiavere.iam.user;

/**
 * Contains a thread local with UserInfo associated with login users.
 *
 * @author Razvan Dani
 */
public class UserIdentity {
    private static ThreadLocal<UserInfo> userInfoThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<String> authorizationHeaderThreadLocal = new ThreadLocal<>();

    public static void setUserToThreadLocal(UserInfo userInfo) {
        userInfoThreadLocal.set(userInfo);
    }

    public static UserInfo getLoginUser() {
        return userInfoThreadLocal.get();
    }

    public static void setAuthorizationHeaderToThreadLocal(String authorizationHeader) {
        authorizationHeaderThreadLocal.set(authorizationHeader);
    }

    public static String getAuthorizationHeader() {
        return authorizationHeaderThreadLocal.get();
    }
}

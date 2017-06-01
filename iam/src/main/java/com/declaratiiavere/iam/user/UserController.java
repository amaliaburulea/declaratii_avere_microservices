package com.declaratiiavere.iam.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.List;

/**
 * User controller.
 *
 * @author Razvan Dani
 */
@RestController
@RequestMapping(value = "/iam")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{username}/", method = RequestMethod.GET)
    public UserInfo getUser(@PathVariable String username) {
        return userService.getUserByUserName(username);
    }

    @RequestMapping(method = RequestMethod.POST)
    public UserInfo createUser(@RequestBody UserInfo userInfo) throws MessagingException {
        return userService.saveUser(userInfo);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public UserInfo updateUser(@RequestBody UserInfo userInfo) throws MessagingException {
        return userService.saveUser(userInfo);
    }

    @RequestMapping(value = "/validateActivationTokenAndUnlockUser/{userName}/{activationToken}", method = RequestMethod.GET)
    public UserInfo validateActivationTokenAndUnlockUser(@PathVariable String userName, @PathVariable String activationToken) {
        return userService.validateActivationTokenAndUnlockUser(userName, activationToken);
    }

    @RequestMapping(value = "/checkUserAndPassword", method = RequestMethod.POST)
    public UserInfo checkUserAndPassword(@RequestBody UserInfo user) {
        return userService.checkUserAndPassword(user);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public UserInfo login(@RequestBody UserInfo user, HttpServletRequest request) {
        UserInfo userInfo = userService.login(user, getIpAddress(request));

        if (userInfo.getErrorMessage() != null) {
            throw new ValidationException(userInfo.getErrorMessage());
        }

        return userInfo;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");

        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }

    @RequestMapping(value = "/changeTemporaryPassword", method = RequestMethod.POST)
    public UserInfo changeTemporaryPassword(@RequestBody UserInfo userPwd) {
        return userService.changeTemporaryPassword(userPwd);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public UserInfo logout(@RequestBody UserInfo userPwd) {
        return userService.logout(userPwd);
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public UserInfo resetPassword(@RequestBody UserInfo userInfo) throws MessagingException {
        return userService.resetPassword(userInfo);
    }

    @RequestMapping(value = "/role/{roleId}/", method = RequestMethod.GET)
    public RoleInfo getRole(@PathVariable Integer roleId) {
        return userService.getRole(roleId);
    }

    @RequestMapping(value = "/role", method = RequestMethod.POST)
    public RoleInfo saveRole(@RequestBody RoleInfo userInfo) {
        return userService.saveRole(userInfo);
    }

    @RequestMapping(value = "/role", method = RequestMethod.PUT)
    public RoleInfo updateRole(@RequestBody RoleInfo userInfo) {
        return userService.saveRole(userInfo);
    }

    @RequestMapping(value = "/role/{roleId}/", method = RequestMethod.DELETE)
    public String deleteRole(@PathVariable Integer roleId) {
        userService.deleteRole(roleId);

        return "SUCCESS";
    }

    @RequestMapping(value = "/role/find", method = RequestMethod.POST)
    public List<RoleInfo> findRoles(@RequestBody SearchRoleCriteria searchRoleCrtieria) {
        return userService.findRoles(searchRoleCrtieria);
    }

    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public UserInfoListResponse findUsers(@RequestBody SearchUserCriteria searchUserCriteria) {
        return new UserInfoListResponse(userService.findUsers(searchUserCriteria));
    }
}

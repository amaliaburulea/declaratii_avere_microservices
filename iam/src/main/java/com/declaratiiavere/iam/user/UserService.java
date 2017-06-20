package com.declaratiiavere.iam.user;

import com.declaratiiavere.iam.permission.PermissionEAO;
import com.declaratiiavere.email.EmailSenderService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User service.
 *
 * @author Razvan Dani
 */
@SuppressWarnings("unused")
@Service
public class UserService {
    @Value("${maxFailedLoginAttempts}")
    private Integer maxFailedLoginAttempts;

    @Autowired
    private PasswordEncoder standardPasswordEncoder;

    @Autowired
    private PermissionEAO permissionEAO;

    @Autowired
    private UserEAO userEAO;

    @Autowired
    private EmailSenderService emailSenderService;

    /**
     * Gets the user info for the specified user name.
     *
     * @param username The user name
     * @return The UserInfo
     */
    @Transactional(readOnly = true)
    public UserInfo getUserByUserName(String username) {
        return getUserInfo(userEAO.getUser(username));
    }

    private UserInfo getUserInfo(UserEntity userEntity) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userEntity.getUserId());
        userInfo.setUsername(userEntity.getUsername());

        userInfo.setRoleId(userEntity.getRoleId());
        userInfo.setIsActive(userEntity.getIsActive());
        userInfo.setEmail(userEntity.getEmail());
        userInfo.setCreatedDttm(userEntity.getCreatedDttm());
        userInfo.setLastLoginDttm(userEntity.getLastLoginDttm());

        if (userEntity.getTempPassword() != null) {
            userInfo.setMustChangeTemporaryPassword(true);
        }

        return userInfo;
    }

    /**
     * Saves a user.
     *
     * @param userInfo The UserInfo
     * @return The saved UserInfo
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserInfo saveUser(UserInfo userInfo) throws MessagingException {
        boolean shouldSendActivationEmail = false;

        if (userInfo.getUserId() == null && userInfo.getPassword() == null && userInfo.getTempPassword() == null) {
            String tempPassword = generateTempPassword();
            userInfo.setTempPassword(tempPassword);

            shouldSendActivationEmail = true;
        }

        validateUser(userInfo);

        if (shouldSendActivationEmail) {
            emailSenderService.sendEmail(userInfo.getEmail(), "Cerere activare voluntar", "Salut " + userInfo.getUsername() + ",<br>" +
                    "<br>" +
                    "Un user de tip voluntar a fost creat pentru tine, cu parola temporara " + userInfo.getTempPassword() + "<br>" +
                    "<br>" +
                    "Da click pe acest link pentru a finaliza procesul de inregistrare:<br>" +
                    "<a href=\"http://google.com?userName=" + userInfo.getUsername() + "&tempPassword=" + userInfo.getTempPassword() + "\">Activare cont</a><br>" +
                    "<br>" +
                    "Cu respect,<br>" +
                    "    Echipa Transparenta GOV");
        }

        UserEntity userEntity = populateUserEntity(userInfo);

        return getUserInfo(userEAO.saveUser(userEntity));
    }

    private String generateTempPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_";
        return RandomStringUtils.random(15, characters);
    }

    private String generateActivationToken() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return RandomStringUtils.random(15, characters);
    }

    private void validateUser(UserInfo userInfo) {
        if (userInfo.getUsername() == null) {
            throw new ValidationException("username is required");
        }

        if (userInfo.getUsername().length() > 45) {
            throw new ValidationException("username max length is 45");
        }

        if (userInfo.getUserId() == null && userInfo.getPassword() == null && userInfo.getTempPassword() == null) {
            throw new ValidationException("password or tempPassword are required");
        }

        if (userInfo.getUserId() == null && userInfo.getPassword() != null && userInfo.getTempPassword() != null) {
            throw new ValidationException("password and tempPassword cannot be specified at the same time");
        }

        if (userInfo.getPassword() != null && (userInfo.getPassword().length() < 8 || userInfo.getPassword().length() > 50)) {
            throw new ValidationException("password has to be between 8 and 50 chars long");
        }

        if (userInfo.getTempPassword() != null && (userInfo.getTempPassword().length() < 8 || userInfo.getTempPassword().length() > 50)) {
            throw new ValidationException("tempPassword has to be between 8 and 50 chars long");
        }

        if (userInfo.getIsActive() == null) {
            throw new ValidationException("isActive is required");
        }

        if (userInfo.getEmail() == null) {
            throw new ValidationException("email is required");
        }

        if (userInfo.getEmail().length() > 45) {
            throw new ValidationException("email max length is 45");
        }

        if (userInfo.getRoleId() == null) {
            throw new ValidationException("roleId is required");
        }
    }

    private void validateUserPassword(UserInfo userInfo) {
        if (userInfo.getUsername() == null) {
            throw new ValidationException("userName is required");
        }

        if (userInfo.getPassword() == null) {
            throw new ValidationException("password is required");
        }
    }


    private UserEntity populateUserEntity(UserInfo userInfo) {
        UserEntity userEntity;
        UserInfo loginUserInfo = UserIdentity.getLoginUser();
        String activationToken = null;

        if (userInfo.getUserId() != null) {
            userEntity = userEAO.getUser(userInfo.getUserId());

            if (userEntity == null) {
                throw new ValidationException("User does not exist");
            }

            if (loginUserInfo == null) {
                throw new ValidationException("Anon users cannot edit existing users");
            }
        } else {
            if (userEAO.getUser(userInfo.getUsername()) != null) {
                throw new ValidationException("User name already exists");
            }

            if (loginUserInfo == null && !userInfo.isOrganizer()) {
                throw new ValidationException("Only organizer users can be created by anon users");
            }

            userEntity = new UserEntity();
            userEntity.setCreatedDttm(new Date());
        }

        if (userInfo.getPassword() != null) {
            userEntity.setPassword(standardPasswordEncoder.encode(userInfo.getPassword()));
        }

        if (userInfo.getTempPassword() != null) {
            userEntity.setTempPassword(standardPasswordEncoder.encode(userInfo.getTempPassword()));
        }

        userEntity.setUsername(userInfo.getUsername());

        userEntity.setRoleId(userInfo.getRoleId());

        if (permissionEAO.getRole(userInfo.getRoleId()) == null) {
            throw new ValidationException("roleId does not exist");
        }

        userEntity.setIsActive(userInfo.getIsActive());

        if (!userInfo.getEmail().equals(userEntity.getEmail())
                && userEAO.getUserByEmail(userInfo.getEmail()) != null) {
            throw new ValidationException("Email already exists");
        }

        userEntity.setEmail(userInfo.getEmail());
        userEntity.setCreatedDttm(userInfo.getCreatedDttm());
        userEntity.setLastLoginDttm(userInfo.getLastLoginDttm());

        return userEntity;
    }

    @Transactional(readOnly = true)
    public UserInfo checkUserAndPassword(UserInfo userPwd) {
        validateUserPassword(userPwd);

        UserEntity userEntity = userEAO.getUser(userPwd.getUsername());

        if (userEntity != null && !userEntity.getRoleEntity().getIsDeleted() && ((userEntity.getPassword() != null
                && standardPasswordEncoder.matches(userPwd.getPassword(), userEntity.getPassword())) ||
                (userEntity.getTempPassword() != null
                        && standardPasswordEncoder.matches(userPwd.getPassword(), userEntity.getTempPassword())))) {
            return getUserInfo(userEntity);
        } else {
            return null;
        }
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserInfo login(UserInfo userPwd, String ipAddress) {
        validateUserPassword(userPwd);

        UserEntity userEntity = userEAO.getUser(userPwd.getUsername());

        if (userEntity != null && (!((userEntity.getPassword() != null
                && standardPasswordEncoder.matches(userPwd.getPassword(), userEntity.getPassword())) ||
                (userEntity.getTempPassword() != null
                        && standardPasswordEncoder.matches(userPwd.getPassword(), userEntity.getTempPassword()))))) {
            userEAO.saveUser(userEntity);
        }

        if (!(userEntity != null && userEntity.getIsActive() && !userEntity.getRoleEntity().getIsDeleted()
                && ((userEntity.getPassword() != null
                && standardPasswordEncoder.matches(userPwd.getPassword(), userEntity.getPassword())) ||
                (userEntity.getTempPassword() != null
                        && standardPasswordEncoder.matches(userPwd.getPassword(), userEntity.getTempPassword()))))) {
            if (userEntity == null) {
                throw new ValidationException("Bad Credentials");
            } else {
                return getUserInfo(userEntity);
            }
        }

        Date lastLoginTimestamp = new Date();
        userEntity.setLastLoginDttm(lastLoginTimestamp);
        userEAO.saveUser(userEntity);

        return getUserInfo(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserInfo changeTemporaryPassword(UserInfo userPwd) {
        validateChangeTemporaryPassword(userPwd);

        UserEntity userEntity = userEAO.getUser(userPwd.getUsername());

        if (userEntity.getTempPassword() == null || !standardPasswordEncoder.matches(userPwd.getTempPassword(), userEntity.getTempPassword())) {
            throw new ValidationException("tempPassword is invalid");
        }

        userEntity.setPassword(standardPasswordEncoder.encode(userPwd.getPassword()));
        userEntity.setTempPassword(null);

        return getUserInfo(userEAO.saveUser(userEntity));
    }

    private void validateChangeTemporaryPassword(UserInfo userInfo) {
        if (userInfo.getUsername() == null) {
            throw new ValidationException("userName is required");
        }

        if (userInfo.getPassword() == null) {
            throw new ValidationException("password is required");
        }

        if (userInfo.getTempPassword() == null) {
            throw new ValidationException("tempPassword is required");
        }

        if (userInfo.getPassword().length() < 8 || userInfo.getPassword().length() > 50) {
            throw new ValidationException("password has to be between 8 and 50 chars long");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserInfo logout(UserInfo userPwd) {
        validateLogout(userPwd);

        UserEntity userEntity = userEAO.getUser(userPwd.getUsername());

        if (userEntity == null || userEntity.getPassword() == null
                || !standardPasswordEncoder.matches(userPwd.getPassword(), userEntity.getPassword())) {
            throw new ValidationException("Logout not allowed");
        }

        return getUserInfo(userEntity);
    }

    private void validateLogout(UserInfo userInfo) {
        if (userInfo.getUsername() == null) {
            throw new ValidationException("userName is required");
        }

        if (userInfo.getPassword() == null) {
            throw new ValidationException("password is required");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserInfo resetPassword(UserInfo userInfo) throws MessagingException {
        if (userInfo.getEmail() == null) {
            throw new ValidationException("email is required");
        }

        UserEntity userEntity = userEAO.getUserByEmail(userInfo.getEmail());

        if (userEntity == null) {
            throw new ValidationException("User email does not exist");
        }

        String tempPassword = generateTempPassword();
        userEntity.setTempPassword(standardPasswordEncoder.encode(tempPassword));
        userEntity.setPassword(null);
        userEAO.saveUser(userEntity);

        sendEmailForPasswordReset(userEntity, tempPassword);

        return getUserInfo(userEntity);
    }

    private void sendEmailForPasswordReset(UserEntity userEntity, String tempPassword) throws MessagingException {
        StringBuilder body = new StringBuilder();
        body.append("Hi ").append(userEntity.getUsername()).append(",\n").append("\n")
                .append("We received a request to reset your password.  You can complete the process by clicking the link below.\n")
                .append("\n").append("{ http://google.com }\n").append(tempPassword)
                .append("\n").append("If you did not make this request, you can ignore this email or let us know by responding.  Your password will not be changed until you create a new one.\n")
                .append("\n").append("Thanks.\n").append("The team\n").append(" ");

        emailSenderService.sendEmail(userEntity.getEmail(), "Password Reset",
                body.toString());
    }

    private RoleInfo getRoleInfo(RoleEntity roleEntity) {
        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setRoleId(roleEntity.getRoleId());
        roleInfo.setRoleName(roleEntity.getRoleName());
        roleInfo.setRoleDesc(roleEntity.getRoleDesc());
        roleInfo.setIsDeleted(roleEntity.getIsDeleted());
        roleInfo.setIsPredefinedRole(roleEntity.getIsPredefinedRole());

        return roleInfo;
    }

    /**
     * Saves a role.
     *
     * @param roleInfo The RoleInfo
     * @return The saved RoleInfo
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RoleInfo saveRole(RoleInfo roleInfo) {
        validateRole(roleInfo);

        RoleEntity roleEntity = populateRoleEntity(roleInfo);

        return getRoleInfo(permissionEAO.saveRole(roleEntity));
    }

    private void validateRole(RoleInfo roleInfo) {
        if (roleInfo.getRoleName() == null) {
            throw new ValidationException("roleName is required");
        }

        if (roleInfo.getRoleName().length() > 45) {
            throw new ValidationException("roleName max length is 45");
        }

        if (roleInfo.getRoleDesc() == null) {
            throw new ValidationException("roleDesc is required");
        }

        if (roleInfo.getRoleDesc().length() > 45) {
            throw new ValidationException("roleDesc max length is 45");
        }

        if (roleInfo.getIsPredefinedRole() != null) {
            throw new ValidationException("isPredefinedRole should not be set when saving");
        }
    }

    private RoleEntity populateRoleEntity(RoleInfo roleInfo) {
        RoleEntity roleEntity;

        if (roleInfo.getRoleId() != null) {
            roleEntity = permissionEAO.getRole(roleInfo.getRoleId());

            if (roleEntity == null) {
                throw new ValidationException("Role does not exist");
            }
        } else {
            roleEntity = new RoleEntity();
            roleEntity.setIsPredefinedRole(false);
        }

        roleEntity.setRoleName(roleInfo.getRoleName());
        roleEntity.setRoleDesc(roleInfo.getRoleDesc());

        roleEntity.setIsDeleted(false);

        return roleEntity;
    }

    /**
     * Deletes a role.
     *
     * @param roleId The role id
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteRole(Integer roleId) {
        RoleEntity roleEntity = permissionEAO.getRole(roleId);

        if (roleEntity == null) {
            throw new ValidationException("role does not exist");
        }

        if (roleEntity.getIsPredefinedRole()) {
            throw new ValidationException("predefined roles cannot be deleted");
        }

        roleEntity.setIsDeleted(true);
        permissionEAO.saveRole(roleEntity);
    }

    /**
     * Gets a role.
     *
     * @param roleId The role id
     * @return The RoleInfo
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class)
    public RoleInfo getRole(Integer roleId) {
        return getRoleInfo(permissionEAO.getRole(roleId));
    }

    /**
     * Finds roles by the specified search criteria.
     *
     * @param searchRoleCriteria The SearchRoleCriteria object
     * @return The List of RoleInfo objects
     */
    @Transactional(readOnly = true)
    public List<RoleInfo> findRoles(SearchRoleCriteria searchRoleCriteria) {
        List<RoleInfo> roleInfoList = new ArrayList<>();

        RoleEntitySearchCriteria roleEntitySearchCriteria = new RoleEntitySearchCriteria();
        roleEntitySearchCriteria.setSearchOnlyNotDeleted(searchRoleCriteria.isSearchOnlyNotDeleted());

        List<RoleEntity> roleEntityList = permissionEAO.findRoles(roleEntitySearchCriteria);

        for (RoleEntity roleEntity : roleEntityList) {
            roleInfoList.add(getRoleInfo(roleEntity));
        }

        return roleInfoList;
    }

    /**
     * Validates access token and unlocks user.
     *
     * @param userName        The userName
     * @param activationToken The access token
     * @return The UserInfo object
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserInfo validateActivationTokenAndUnlockUser(String userName, String activationToken) {
        if (userName == null) {
            throw new ValidationException("userName is required");
        }

        UserEntity userEntity = userEAO.getUser(userName);

        userEAO.saveUser(userEntity);

        return getUserInfo(userEntity);
    }

    /**
     * Finds users for specified search criteria.
     *
     * @param searchUserCriteria The SearchUsersCriteria object
     * @return The List of UserInfo objects
     */
    @Transactional(readOnly = true)
    public List<UserInfo> findUsers(SearchUserCriteria searchUserCriteria) {
        List<UserInfo> userInfoList = new ArrayList<>();
        UserEntitySearchCriteria userEntitySearchCriteria = new UserEntitySearchCriteria();
        userEntitySearchCriteria.setUserIdList(searchUserCriteria.getUserIdList());
        userEntitySearchCriteria.setRoleId(searchUserCriteria.getRoleId());

        List<UserEntity> userEntityList = userEAO.findUsers(userEntitySearchCriteria);

        for (UserEntity userEntity : userEntityList) {
            UserInfo userInfo = getUserInfo(userEntity);
            userInfoList.add(userInfo);
        }

        return userInfoList;
    }
}

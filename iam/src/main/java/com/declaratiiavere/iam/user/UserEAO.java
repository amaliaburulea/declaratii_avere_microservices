package com.declaratiiavere.iam.user;

import com.declaratiiavere.jpaframework.EntityAccessObjectBase;
import com.declaratiiavere.jpaframework.JpaQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Entity access object for RM_USER table.
 *
 * @author Razvan Dani
 */
@Component
public class UserEAO extends EntityAccessObjectBase {

    /**
     * Finds and returns the UserEntity for the specified username.
     *
     * @param userName          The username
     * @return                  The UserEntity
     */
    public UserEntity getUser(String userName) {
        UserEntity userEntity = null;

        UserEntitySearchCriteria userEntitySearchCriteria = new UserEntitySearchCriteria();
        userEntitySearchCriteria.setUserName(userName);
        userEntitySearchCriteria.setSearchOnlyActive(false);

        List<UserEntity> userEntityList = findUsers(userEntitySearchCriteria);

        if (userEntityList.size() == 1) {
            userEntity = userEntityList.get(0);
        }

        return userEntity;
    }

    /**
     * Finds and returns the UserEntity for the specified email.
     *
     * @param email             The email
     * @return                  The UserEntity
     */
    public UserEntity getUserByEmail(String email) {
        UserEntity userEntity = null;

        UserEntitySearchCriteria userEntitySearchCriteria = new UserEntitySearchCriteria();
        userEntitySearchCriteria.setEmail(email);
        userEntitySearchCriteria.setSearchOnlyActive(false);

        List<UserEntity> userEntityList = findUsers(userEntitySearchCriteria);

        if (userEntityList.size() == 1) {
            userEntity = userEntityList.get(0);
        }

        return userEntity;
    }

    /**
     * Finds the users for the specified search criteria.
     *
     * @param searchCriteria    The UserEntitySearchCriteria
     * @return                  The List of UserEntity objects
     */
    public List<UserEntity> findUsers(UserEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = constructFindUsersBuilder(searchCriteria);

        return findEntities(queryBuilder, searchCriteria);
    }

    private JpaQueryBuilder constructFindUsersBuilder(UserEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = new JpaQueryBuilder("UserEntity", "u");

        if (searchCriteria.getUserName() != null) {
            queryBuilder.addCondition("u.username = :userName");
        }

        if (searchCriteria.getEmail() != null) {
            queryBuilder.addCondition("u.email = :email");
        }

        if (searchCriteria.getRoleId() != null) {
            queryBuilder.addCondition("u.roleId = :roleId");
        }

        if (searchCriteria.getUserIdList() != null) {
            queryBuilder.addCondition("u.userId IN (:userIdList)");
        }

        if (searchCriteria.getSearchOnlyActive()) {
            queryBuilder.addCondition("u.isActive = true");
        }

        return queryBuilder;
    }

    /**
     * Saves the user entity.
     *
     * @param userEntity    The UserEntity
     * @return              The stored UserEntity
     */
    public UserEntity saveUser(UserEntity userEntity) {
        return storeEntity(userEntity);
    }

    /**
     * Gets the UserEntity for the specified user id.
     *
     * @param userId    The user id
     * @return          The UserEntity
     */
    public UserEntity getUser(Integer userId) {
        return getEntity(UserEntity.class, userId);
    }
}

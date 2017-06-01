package com.declaratiiavere.iam.permission;

import com.declaratiiavere.iam.user.RoleEntity;
import com.declaratiiavere.iam.user.RoleEntitySearchCriteria;
import com.declaratiiavere.jpaframework.EntityAccessObjectBase;
import com.declaratiiavere.jpaframework.JpaQueryBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Entity access object for permissions.
 * 
 * @author Razvan Dani
 */
@Component
public class PermissionEAO extends EntityAccessObjectBase {

    public PermissionEntity getPermission(Integer permissionId) {
        return getEntity(PermissionEntity.class, permissionId);
    }

    public PermissionEntity savePermission(PermissionEntity permissionEntity) {
        return storeEntity(permissionEntity);
    }
    /**
     * Finds the permissions for the specified search criteria.
     *
     * @param searchCriteria    The PermissionEntitySearchCriteria
     * @return                  The List of PermissionEntity objects
     */
    public List<PermissionEntity> findPermissions(PermissionEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = constructFindPermissionsBuilder(searchCriteria);

        return findEntities(queryBuilder, searchCriteria);
    }

    /**
     * Finds and returns the PermissionEntity for the specified criteria.
     *
     * @param roleId            The role id
     * @param restRequestPath   The REST request path
     * @param restRequestMethod The REST request method
     * @return                                  The PermissionEntity
     */
    public PermissionEntity getPermissionForRestRequest(Integer roleId, String restRequestPath, String restRequestMethod) {
        PermissionEntity permissionEntity = null;

        PermissionEntitySearchCriteria permissionEntitySearchCriteria = new PermissionEntitySearchCriteria();
        permissionEntitySearchCriteria.setRoleId(roleId);
        permissionEntitySearchCriteria.setRestRequestPath(restRequestPath);
        permissionEntitySearchCriteria.setRestRequestMethod(restRequestMethod);
        List<PermissionEntity> permissionEntityList = findPermissions(permissionEntitySearchCriteria);

        if (permissionEntityList.size() >= 1) {
            permissionEntity = permissionEntityList.get(0);
        }

        return permissionEntity;
    }

    private JpaQueryBuilder constructFindPermissionsBuilder(PermissionEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = new JpaQueryBuilder("PermissionEntity", "p");

        if (searchCriteria.getPermissionName() != null) {
            queryBuilder.addCondition("p.permissionName = :permissionName");
        }

        if (searchCriteria.getPermissionCode() != null) {
            queryBuilder.addCondition("p.permissionCode = :permissionCode");
        }

        if (searchCriteria.getSearchOnlyNotDeleted()) {
            queryBuilder.addCondition("p.isDeleted = false");
        }

        if (searchCriteria.getRoleId() != null) {
            queryBuilder.addInnerFetchJoin("p.rolePermissionEntitySet rp");
            queryBuilder.addCondition("rp.roleId = :roleId");
        }

        if (searchCriteria.getRestRequestPath() != null && searchCriteria.getRestRequestMethod() != null) {
            queryBuilder.addInnerFetchJoin("p.permissionRestMethodEntitySet prm");
            queryBuilder.addCondition("prm.restRequestPath = :restRequestPath");
            queryBuilder.addCondition("prm.restRequestMethod = :restRequestMethod");
        }

        return queryBuilder;
    }

    public RoleEntity saveRole(RoleEntity roleEntity) {
        return storeEntity(roleEntity);
    }

    public RoleEntity getRole(Integer roleId) {
        return getEntity(RoleEntity.class, roleId);
    }

    /**
     * Finds the roles for the specified search criteria.
     *
     * @param searchCriteria    The RoleEntitySearchCriteria
     * @return                  The List of RoleEntity objects
     */
    public List<RoleEntity> findRoles(RoleEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = constructFindRolesBuilder(searchCriteria);

        return findEntities(queryBuilder, searchCriteria);
    }

    private JpaQueryBuilder constructFindRolesBuilder(RoleEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = new JpaQueryBuilder("RoleEntity", "r");

        if (searchCriteria.isSearchOnlyNotDeleted()) {
            queryBuilder.addCondition("r.isDeleted = false");
        }

        if (!CollectionUtils.isEmpty(searchCriteria.getRoleIdSet())) {
            queryBuilder.addCondition("r.roleId IN (:roleIdSet)");
        }

        return queryBuilder;
    }
}

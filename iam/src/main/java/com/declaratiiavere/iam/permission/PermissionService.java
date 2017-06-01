package com.declaratiiavere.iam.permission;

import com.google.common.collect.Sets;
import com.declaratiiavere.iam.config.EntityNotFoundException;
import com.declaratiiavere.iam.user.RoleEntity;
import com.declaratiiavere.iam.user.RoleEntitySearchCriteria;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Permission Service.
 *
 * @author Viorel Vesa
 */
@Service
public class PermissionService {
    private static final Logger log = LoggerFactory.getLogger(PermissionService.class);

    @Autowired
    private PermissionEAO permissionEAO;

    @Transactional(readOnly = true)
    public PermissionInfo getPermission(Integer permissionId) {
        if (permissionId == null) {
            throw new ValidationException("Permission id is required");
        }
        PermissionInfo permissionInfo = getPermissionInfo(permissionEAO.getPermission(permissionId), true);

        if (permissionInfo == null) {
            throw new EntityNotFoundException("Permission with id " + permissionId + " not found");
        }
        return permissionInfo;
    }

    /**
     * Finds permissions by search criteria.
     * @param searchPermissionCriteria SearchPermissionCriteria
     * @return The list of found permissions
     */
    @Transactional(readOnly = true)
    public List<PermissionInfo> findPermissions(SearchPermissionCriteria searchPermissionCriteria) {
        PermissionEntitySearchCriteria searchCriteria = new PermissionEntitySearchCriteria();

        if (searchPermissionCriteria != null) {
            searchCriteria.setPermissionName(searchPermissionCriteria.getPermissionName());
        }
        List<PermissionEntity> permissionEntityList = permissionEAO.findPermissions(searchCriteria);

        if (permissionEntityList != null) {
            return permissionEntityList.stream().map(p -> getPermissionInfo(p, false)).collect(Collectors.toList());
        }

        return null;
    }

    /**
     * Saves Permission together with rest methods and user role associations - full state update
     *
     * @param permissionInfo PermissionInfo
     * @return saved PermissionInfo
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PermissionInfo savePermission(PermissionInfo permissionInfo) {
        validatePermissionInfo(permissionInfo);

        PermissionEntity permissionEntity = populatePermissionEntity(permissionInfo);

        return getPermissionInfo(permissionEAO.savePermission(permissionEntity), true);
    }

    /**
     * Deletes Permission together with rest methods and user role associations
     *
     * @param permissionId PermissionEntity id
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deletePermission(Integer permissionId) {
        if (permissionId == null) {
            throw new ValidationException("Permission id is required");
        }
        PermissionEntity existingPermission = permissionEAO.getPermission(permissionId);

        if (existingPermission == null || existingPermission.getIsDeleted()) {
            throw new EntityNotFoundException("Could not find Permission with id: " + permissionId);
        }
        existingPermission.setIsDeleted(true);
        permissionEAO.savePermission(existingPermission);
    }

    @Transactional(readOnly = true)
    public PermissionValidationResponseInfo checkPermissionForRequest(RestRequestPermissionValidationInfo restRequestPermissionValidationInfo) {
        String restRequestPath = restRequestPermissionValidationInfo.getRestRequestPath();

        if (restRequestPath.endsWith("/")) {
            restRequestPath = restRequestPath.substring(0, restRequestPath.length() - 1);
        }

        String[] restRequestPathElements = restRequestPath.split("/");
        String lastRequestPathElement = restRequestPathElements[restRequestPathElements.length-1];

        if (NumberUtils.isNumber(lastRequestPathElement)) {
            restRequestPath = restRequestPath.substring(0, restRequestPath.length() - 1 - lastRequestPathElement.length());
        }

        restRequestPermissionValidationInfo.setRestRequestPath(restRequestPath);

        PermissionValidationResponseInfo permissionValidInfo = new PermissionValidationResponseInfo();

        PermissionEntity permissionEntity = permissionEAO.getPermissionForRestRequest(restRequestPermissionValidationInfo.getRoleId(),
                restRequestPermissionValidationInfo.getRestRequestPath(), restRequestPermissionValidationInfo.getRestRequestMethod());
        permissionValidInfo.setIsPermissionValid(permissionEntity != null);

        return permissionValidInfo;
    }

    private void validatePermissionInfo(PermissionInfo permissionInfo) {
        if (permissionInfo == null) {
            throw new ValidationException("Permission object required");
        }

        if (permissionInfo.getPermissionName() == null) {
            throw new ValidationException("permissionName required");
        }

        if (permissionInfo.getPermissionName().length() > 45) {
            throw new ValidationException("permissionName max length is 45");
        }

        if (permissionInfo.getPermissionCode() == null) {
            throw new ValidationException("permissionCode required");
        }

        if (permissionInfo.getPermissionCode().length() > 45) {
            throw new ValidationException("permissionCode max length is 45");
        }

        if (permissionInfo.getPermissionDesc() != null && permissionInfo.getPermissionDesc().length() > 45) {
            throw new ValidationException("permissionDesc max length is 45");
        }
    }

    private void validateRolePermissionEntities(Set<Integer> roleIds) {
        if (!CollectionUtils.isEmpty(roleIds)) {
            RoleEntitySearchCriteria roleEntitySearchCriteria = new RoleEntitySearchCriteria();
            roleEntitySearchCriteria.setRoleIdSet(roleIds);
            List<RoleEntity> existingRoleEntities = permissionEAO.findRoles(roleEntitySearchCriteria);

            if (CollectionUtils.isEmpty(existingRoleEntities)) {
                throw new ValidationException("Invalid role ids: " + roleIds);
            }
            Set<Integer> foundRoleIds = existingRoleEntities.stream()
                    .map(RoleEntity::getRoleId)
                    .collect(Collectors.toSet());
            Sets.SetView<Integer> invalidRoleIds = Sets.difference(roleIds, foundRoleIds);

            if (!CollectionUtils.isEmpty(invalidRoleIds)) {
                throw new ValidationException("Invalid role ids: " + invalidRoleIds);
            }
        }
    }

    public PermissionEntity checkExistingPermissionEntity(PermissionInfo permissionInfo) {
        if (permissionInfo.getPermissionId() == null) {
            return null; //create
        }
        //update
        PermissionEntity permissionEntity = permissionEAO.getPermission(permissionInfo.getPermissionId());

        if (permissionEntity == null) {
            throw new ValidationException("Permission with id " + permissionInfo.getPermissionId() + " does not exist");
        }

        return permissionEntity;
    }

    private PermissionEntity populatePermissionEntity(PermissionInfo permissionInfo) {
        PermissionEntity permissionEntity = checkExistingPermissionEntity(permissionInfo);
        Set<Integer> roleIds = permissionInfo.getRoleIds();
        Set<PermissionRestMethodInfo> permissionRestMethodInfoSet = permissionInfo.getPermissionRestMethods();

        if (permissionEntity == null) { //create mode
            validateUniquePermissionName(permissionInfo.getPermissionName());
            validateUniquePermissionCode(permissionInfo.getPermissionCode());
            permissionEntity = new PermissionEntity();

            if (roleIds != null) {
                validateRolePermissionEntities(roleIds);
                permissionEntity.addRolePermissionEntities(roleIds.stream()
                        .map(this::createRolePermissionEntity)
                        .collect(Collectors.toSet()));
            }

            if (permissionRestMethodInfoSet != null) {
                validatePermissionRestMethods(permissionRestMethodInfoSet);
                permissionEntity.addPermissionRestMethodEntities(permissionRestMethodInfoSet.stream()
                        .map(m -> getPermissionRestMethodEntity(m.getRestRequestMethod(), m.getRestRequestPath()))
                        .collect(Collectors.toSet()));
            }
        } else { // update mode
            if (!Objects.equals(permissionInfo.getPermissionName(), permissionEntity.getPermissionName())) {
                validateUniquePermissionName(permissionInfo.getPermissionName());
            }
            if (!Objects.equals(permissionInfo.getPermissionCode(), permissionEntity.getPermissionCode())) {
                validateUniquePermissionCode(permissionInfo.getPermissionCode());
            }
            Set<RolePermissionEntity> existingRolePermissionEntitySet = permissionEntity.getRolePermissionEntitySet();

            if (existingRolePermissionEntitySet != null) {
                updateRolePermissionEntities(permissionInfo, permissionEntity, existingRolePermissionEntitySet);
            }
            Set<PermissionRestMethodEntity> existingPermissionRestMethodEntitySet =
                    permissionEntity.getPermissionRestMethodEntitySet();

            if (existingPermissionRestMethodEntitySet != null) {
                updatePermissionRestEntities(permissionInfo, permissionEntity, existingPermissionRestMethodEntitySet);
            }
        }

        permissionEntity.setPermissionName(permissionInfo.getPermissionName());
        permissionEntity.setPermissionCode(permissionInfo.getPermissionCode());
        permissionEntity.setPermissionDesc(permissionInfo.getPermissionDesc());
        permissionEntity.setIsDeleted(false);

        return permissionEntity;
    }

    private void validatePermissionRestMethods(Set<PermissionRestMethodInfo> permissionRestMethodInfoSet) {
        if (!CollectionUtils.isEmpty(permissionRestMethodInfoSet)) {
            for (PermissionRestMethodInfo permissionRestMethodInfo : permissionRestMethodInfoSet) {
                if (permissionRestMethodInfo.getRestRequestPath() == null) {
                    throw new ValidationException("Permission Rest Request Path required");
                }

                if (permissionRestMethodInfo.getRestRequestPath().length() > 200) {
                    throw new ValidationException("Permission Rest Request Path max length is 200");
                }

                if (permissionRestMethodInfo.getRestRequestMethod() == null) {
                    throw new ValidationException("Permission Rest Method required");
                }

                if (permissionRestMethodInfo.getRestRequestMethod().length() > 10) {
                    throw new ValidationException("Permission Rest Request Path max length is 10");
                }
            }
        }
    }

    private void validateUniquePermissionName(String permissionName) {
        PermissionEntitySearchCriteria searchCriteria = new PermissionEntitySearchCriteria();
        searchCriteria.setPermissionName(permissionName);

        if (!CollectionUtils.isEmpty(permissionEAO.findPermissions(searchCriteria))) {
            throw new ValidationException("Permission name already exists");
        }
    }

    private void validateUniquePermissionCode(String permissionCode) {
        PermissionEntitySearchCriteria searchCriteria = new PermissionEntitySearchCriteria();
        searchCriteria.setPermissionCode(permissionCode);

        if (!CollectionUtils.isEmpty(permissionEAO.findPermissions(searchCriteria))) {
            throw new ValidationException("Permission code already exists");
        }
    }

    private void updatePermissionRestEntities(PermissionInfo permissionInfo, PermissionEntity permissionEntity,
                                              Set<PermissionRestMethodEntity> existingPermissionRestMethodEntitySet) {
        Set<Pair<String, String>> newMethodKeys;
        Set<Pair<String, String>> existingMethodKeys;

        if (permissionInfo.getPermissionRestMethods() == null) {
            newMethodKeys = new HashSet<>();
        } else {
            newMethodKeys = permissionInfo.getPermissionRestMethods().stream()
                    .map(m -> buildRestMethodKey(m.getRestRequestMethod(), m.getRestRequestPath()))
                    .collect(Collectors.toSet());
        }

        existingMethodKeys = existingPermissionRestMethodEntitySet.stream()
                .map(m -> buildRestMethodKey(m.getRestRequestMethod(), m.getRestRequestPath()))
                .collect(Collectors.toSet());

        Sets.SetView<Pair<String, String>> toAdd = Sets.difference(newMethodKeys, existingMethodKeys);
        Set<PermissionRestMethodEntity> permissionRestMethodEntitySetToAdd = new HashSet<>();

        for (Pair<String, String> restMethodKey : toAdd) {
            PermissionRestMethodEntity permissionRestMethodEntity = new PermissionRestMethodEntity();
            permissionRestMethodEntity.setRestRequestMethod(restMethodKey.getLeft());
            permissionRestMethodEntity.setRestRequestPath(restMethodKey.getRight());
            permissionRestMethodEntitySetToAdd.add(permissionRestMethodEntity);
        }
        permissionEntity.addPermissionRestMethodEntities(permissionRestMethodEntitySetToAdd);

        Sets.SetView<Pair<String, String>> toDelete = Sets.difference(existingMethodKeys, newMethodKeys);
        Set<PermissionRestMethodEntity> permissionRestMethodEntitySetToRemove = new HashSet<>();

        for (PermissionRestMethodEntity permissionRestMethodEntity : existingPermissionRestMethodEntitySet) {
            ImmutablePair<String, String> restMethodKey = buildRestMethodKey(
                    permissionRestMethodEntity.getRestRequestMethod(), permissionRestMethodEntity.getRestRequestPath());
            if (toDelete.contains(restMethodKey)) {
                permissionRestMethodEntitySetToRemove.add(permissionRestMethodEntity);
            }
        }

        permissionEntity.removePermissionRestMethodEntities(permissionRestMethodEntitySetToRemove);
    }

    private ImmutablePair<String, String> buildRestMethodKey(String restRequestMethod, String restRequestPath) {
        return new ImmutablePair<>(restRequestMethod, restRequestPath);
    }

    private void updateRolePermissionEntities(PermissionInfo permissionInfo, PermissionEntity permissionEntity,
                                              Set<RolePermissionEntity> existingRolePermissionEntitySet) {
        Set<Integer> newRoleIds = permissionInfo.getRoleIds() == null ? new HashSet<>() :
                permissionInfo.getRoleIds();
        Set<RolePermissionEntity> toDelete = new HashSet<>();

        for (RolePermissionEntity rolePermissionEntity : existingRolePermissionEntitySet) {
            if (!newRoleIds.contains(rolePermissionEntity.getRoleId())) {
                toDelete.add(rolePermissionEntity);
            }
        }
        permissionEntity.removeRolePermissionEntities(toDelete);

        Set<Integer> existingRoleIds = existingRolePermissionEntitySet.stream()
                .map(RolePermissionEntity::getRoleId)
                .collect(Collectors.toSet());
        Sets.SetView<Integer> roleIdsToAdd = Sets.difference(newRoleIds, existingRoleIds);
        validateRolePermissionEntities(roleIdsToAdd);

        Set<RolePermissionEntity> toAdd = roleIdsToAdd.stream()
                .map(this::createRolePermissionEntity)
                .collect(Collectors.toSet());
        permissionEntity.addRolePermissionEntities(toAdd);
    }

    private PermissionRestMethodEntity getPermissionRestMethodEntity(String restRequestMethod, String restRequestPath) {
        PermissionRestMethodEntity permissionRestMethodEntity = new PermissionRestMethodEntity();
        permissionRestMethodEntity.setRestRequestMethod(restRequestMethod);
        permissionRestMethodEntity.setRestRequestPath(restRequestPath);
        return permissionRestMethodEntity;
    }

    private RolePermissionEntity createRolePermissionEntity(Integer roleId) {
        RolePermissionEntity rolePermissionEntity = new RolePermissionEntity();
        rolePermissionEntity.setRoleId(roleId);
        return rolePermissionEntity;
    }

    private PermissionInfo getPermissionInfo(PermissionEntity permissionEntity, boolean includeRestMethods) {
        if (permissionEntity == null) {
            return null;
        }
        PermissionInfo permissionInfo = new PermissionInfo();

        permissionInfo.setPermissionId(permissionEntity.getPermissionId());
        permissionInfo.setPermissionCode(permissionEntity.getPermissionCode());
        permissionInfo.setPermissionName(permissionEntity.getPermissionName());
        permissionInfo.setPermissionDesc(permissionEntity.getPermissionDesc());
        permissionInfo.setIsDeleted(permissionEntity.getIsDeleted());

        if (includeRestMethods && permissionEntity.getPermissionRestMethodEntitySet() != null) {
            permissionInfo.setPermissionRestMethods(permissionEntity.getPermissionRestMethodEntitySet().stream()
                    .map(this::getPermissionRestMethodInfo)
                    .collect(Collectors.toSet()));
        }

        if (permissionEntity.getRolePermissionEntitySet() != null) {
            permissionInfo.setRoleIds(permissionEntity.getRolePermissionEntitySet().stream()
                    .map(RolePermissionEntity::getRoleId)
                    .collect(Collectors.toSet()));
        }

        return permissionInfo;
    }

    private PermissionRestMethodInfo getPermissionRestMethodInfo(PermissionRestMethodEntity permissionRestMethodEntity) {
        PermissionRestMethodInfo permissionRestMethodInfo = new PermissionRestMethodInfo();
        permissionRestMethodInfo.setRestRequestMethod(permissionRestMethodEntity.getRestRequestMethod());
        permissionRestMethodInfo.setRestRequestPath(permissionRestMethodEntity.getRestRequestPath());
        return permissionRestMethodInfo;
    }
}

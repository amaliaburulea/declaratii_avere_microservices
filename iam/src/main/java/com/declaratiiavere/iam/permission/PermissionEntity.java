package com.declaratiiavere.iam.permission;

import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity for RN_PERMISSION.
 *
 * @author Razvan Dani
 */
@Table(name = "permission")
@Entity
public class PermissionEntity extends EntityBase {
    @Column(name = "permission_id")
    @Id
    @GeneratedValue
    private Integer permissionId;

    @Column(name = "permission_name")
    private String permissionName;

    @Column(name = "permission_code")
    private String permissionCode;

    @Column(name = "permission_desc")
    private String permissionDesc;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "permissionEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePermissionEntity> rolePermissionEntitySet;

    @OneToMany(mappedBy = "permissionEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PermissionRestMethodEntity> permissionRestMethodEntitySet;

    public void addPermissionRestMethodEntities(Collection<PermissionRestMethodEntity> permissionRestMethodEntities) {
        if (permissionRestMethodEntitySet == null) {
            permissionRestMethodEntitySet = new HashSet<>();
        }

        for (PermissionRestMethodEntity permissionRestMethodEntity : permissionRestMethodEntities) {
            permissionRestMethodEntity.setPermissionEntity(this);
            permissionRestMethodEntitySet.add(permissionRestMethodEntity);
        }
    }

    public void removePermissionRestMethodEntities(Collection<PermissionRestMethodEntity> permissionRestMethodEntities) {
        if (permissionRestMethodEntitySet == null || permissionRestMethodEntities == null) {
            return;
        }

        for (PermissionRestMethodEntity permissionRestMethodEntity : permissionRestMethodEntities) {
            permissionRestMethodEntity.setPermissionEntity(null);
            permissionRestMethodEntitySet.remove(permissionRestMethodEntity);
        }
    }

    public void addRolePermissionEntities(Collection<RolePermissionEntity> rolePermissionEntities) {
        if (rolePermissionEntitySet == null) {
            rolePermissionEntitySet = new HashSet<>();
        }

        for (RolePermissionEntity rolePermissionEntity : rolePermissionEntities) {
            rolePermissionEntity.setPermissionEntity(this);
            rolePermissionEntitySet.add(rolePermissionEntity);
        }
    }

    public void removeRolePermissionEntities(Collection<RolePermissionEntity> rolePermissionEntities) {
        if (rolePermissionEntitySet == null || rolePermissionEntities == null) {
            return;
        }

        for (RolePermissionEntity rolePermissionEntity : rolePermissionEntities) {
            rolePermissionEntity.setPermissionEntity(null);
            rolePermissionEntitySet.remove(rolePermissionEntity);
        }
    }

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionDesc() {
        return permissionDesc;
    }

    public void setPermissionDesc(String permissionDesc) {
        this.permissionDesc = permissionDesc;
    }

    public Set<RolePermissionEntity> getRolePermissionEntitySet() {
        return rolePermissionEntitySet;
    }

    public void setRolePermissionEntitySet(Set<RolePermissionEntity> rolePermissionEntitySet) {
        this.rolePermissionEntitySet = rolePermissionEntitySet;
    }

    public Set<PermissionRestMethodEntity> getPermissionRestMethodEntitySet() {
        return permissionRestMethodEntitySet;
    }

    public void setPermissionRestMethodEntitySet(Set<PermissionRestMethodEntity> permissionRestMethodEntitySet) {
        this.permissionRestMethodEntitySet = permissionRestMethodEntitySet;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}

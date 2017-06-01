package com.declaratiiavere.iam.permission;

import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;

/**
 * JPA entity for RN_ROLES_PERMISSION table.
 */
@Table(name = "role_permission")
@Entity
public class RolePermissionEntity extends EntityBase {
    @Column(name = "role_perm_id")
    @Id
    @GeneratedValue
    private Integer rolePermId;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "permission_id", insertable = false, updatable = false)
    private Integer permissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private PermissionEntity permissionEntity;

    public Integer getRolePermId() {
        return rolePermId;
    }

    public void setRolePermId(Integer rolePermId) {
        this.rolePermId = rolePermId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public PermissionEntity getPermissionEntity() {
        return permissionEntity;
    }

    public void setPermissionEntity(PermissionEntity permissionEntity) {
        this.permissionEntity = permissionEntity;
    }
}

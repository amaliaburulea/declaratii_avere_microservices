package com.declaratiiavere.iam.user;

import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;

/**
 * JPA entity for role table.
 *
 * @author Razvan Dani
 */
@Table(name = "role")
@Entity
public class RoleEntity extends EntityBase {
    @Column(name = "role_id")
    @Id
    @GeneratedValue
    private Integer roleId;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_desc")
    private String roleDesc;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "is_predefined_role")
    private Boolean isPredefinedRole;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getIsPredefinedRole() {
        return isPredefinedRole;
    }

    public void setIsPredefinedRole(Boolean predefinedRole) {
        isPredefinedRole = predefinedRole;
    }
}

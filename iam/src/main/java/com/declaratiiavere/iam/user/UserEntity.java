package com.declaratiiavere.iam.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;
import java.util.Date;

/**
 * JPA entity for user table.
 *
 * @author Razvan Dani
 */
@Table(name = "user")
@Entity
public class UserEntity extends EntityBase {
    @Column(name = "userId")
    @Id
    @GeneratedValue
    private Integer userId;

    @Column(name ="userName")
    private String username;

    @Column(name ="password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name ="tempPassword")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String tempPassword;

    @Column(name = "roleId")
    private Integer roleId;

    @Column(name = "isActive")
    private Boolean isActive;

    @Column(name ="email")
    private String email;

    @Column(name ="createdDttm")
    private Date createdDttm;

    @Column(name ="lastLoginDttm")
    private Date lastLoginDttm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId", nullable = false, insertable = false, updatable = false)
    private RoleEntity roleEntity;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        this.isActive = active;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }

    public Date getCreatedDttm() {
        return createdDttm;
    }

    public void setCreatedDttm(Date createdDttm) {
        this.createdDttm = createdDttm;
    }

    public Date getLastLoginDttm() {
        return lastLoginDttm;
    }

    public void setLastLoginDttm(Date lastLoginDttm) {
        this.lastLoginDttm = lastLoginDttm;
    }

    public RoleEntity getRoleEntity() {
        return roleEntity;
    }

    public void setRoleEntity(RoleEntity roleEntity) {
        this.roleEntity = roleEntity;
    }
    }

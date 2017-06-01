package com.declaratiiavere.iam.permission;

import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;

/**
 * JPA entity for RN_PERMISSION_REST_METHOD table.
 */
@Table(name = "permission_rest_method")
@Entity
public class PermissionRestMethodEntity extends EntityBase {
    @Column(name = "permission_rest_method_id")
    @Id
    @GeneratedValue
    private Integer permissionRestMethodId;

    @Column(name = "permission_id", insertable = false, updatable = false)
    private Integer permissionId;

    @Column(name = "rest_request_path")
    private String restRequestPath;

    @Column(name = "rest_request_method")
    private String restRequestMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private PermissionEntity permissionEntity;

    public Integer getPermissionRestMethodId() {
        return permissionRestMethodId;
    }

    public void setPermissionRestMethodId(Integer permissionRestMethodId) {
        this.permissionRestMethodId = permissionRestMethodId;
    }

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public String getRestRequestPath() {
        return restRequestPath;
    }

    public void setRestRequestPath(String restRequestPath) {
        this.restRequestPath = restRequestPath;
    }

    public String getRestRequestMethod() {
        return restRequestMethod;
    }

    public void setRestRequestMethod(String restRequestMethod) {
        this.restRequestMethod = restRequestMethod;
    }

    public PermissionEntity getPermissionEntity() {
        return permissionEntity;
    }

    public void setPermissionEntity(PermissionEntity permissionEntity) {
        this.permissionEntity = permissionEntity;
    }
}

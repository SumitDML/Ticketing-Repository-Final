package com.ticketing.api.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@Setter
public class RoleEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "role_name")
    private String roleName;
    @Column(name = "role_description")
    private String roleDescription;
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "roles_has_roles_permissions", joinColumns = {
            @JoinColumn(name = "roles_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "roles_permissions_id", referencedColumnName = "id")})
    private Set<RolePermissionEntity> permissions;

}

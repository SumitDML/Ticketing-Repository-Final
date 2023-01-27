package com.ticketing.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", unique = true)
    private String userId;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true)
    @NotNull
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password")
    @NotNull
    private String password;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "roles_id")
    private RoleEntity roles;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assignedTo")
    private Set<TicketEntity> tickets;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "userEntity")
    private LoginEntity loginEntity;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY,mappedBy = "users")
    private Set<ProjectEntity> projects;


}

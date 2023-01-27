package com.ticketing.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "project")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "project_id",unique = true)
    private String projectId;
    @Column(name = "project_name",unique = true)
    private String projectName;
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "project")
    private Set<TicketEntity> tickets;
    @CreationTimestamp
    @Column(name = "starting_date")
    private Date startingDate;

    @Column(name = "type")
    private String type;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "project_users",
            joinColumns = {@JoinColumn(name = "project_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<UserEntity> users;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinTable(name = "project_manager",
            joinColumns = {@JoinColumn(name = "project_id",referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "manager_id",referencedColumnName = "id")})
    private UserEntity manager;



}

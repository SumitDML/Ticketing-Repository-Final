package com.ticketing.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ticketing.api.enums.PriorityEnums;
import com.ticketing.api.enums.StatusEnums;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ticket")
public class TicketEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.ORDINAL)
    private PriorityEnums priority;

    @Enumerated(EnumType.ORDINAL)
    private StatusEnums status;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "type")
    private String type;
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "closed_at")
    private Date closedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "due_date")
    private Timestamp dueDate;
    @Column(name = "ticket_id")
    private String ticketId;
    @Column(name = "is_deleted")
    private boolean isDeleted;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "user_ticket", joinColumns = {
            @JoinColumn(name = "ticket_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id")})
    private UserEntity assignedTo;

    @ManyToOne(cascade = CascadeType.MERGE,fetch = FetchType.LAZY)
    @JoinColumn(name = "project")
    private ProjectEntity project;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(name = "ticket_image", joinColumns = {
            @JoinColumn(name = "ticket_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "image_id", referencedColumnName = "id")})
    private List<ImageEntity> ticketImage;

}

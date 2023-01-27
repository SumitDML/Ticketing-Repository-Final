package com.ticketing.api.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ticketing.api.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestModel
{



    @NotBlank(message = " name cannot be null")
    private String name;

    @NotBlank(message = "Email cannot be null")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Phone number shouldn't be empty")
    private String phoneNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp dueDate;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "roles_id")
    private RoleEntity roles;
}
